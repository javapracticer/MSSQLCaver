package util;

import domain.Ischema;
import domain.PageHeader;
import schema.SchemaRecord;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static util.MainParserForce.schemaBuilder;


/**
 * 这是一个通过索引查早数据的方法，对于数据多的情况，这个方法
 * 应该比之前所写的暴力解析的方法速度更快,由于精力有限，而且经过
 * 测试发现，解析表名和schema并不需要多少时间，所以这两项我直接用了
 * 强制解析的方法
 */
public class MainParserIndex {
    public static List<Map<String,String>> parserTable(String tableId) throws IOException {
        List<Map<String,String>> result;
        Map<Long, SchemaRecord> schemaRecordMap = PageUtils.getTableSchema(tableId);
        List<Ischema> schemaList = new ArrayList<>();
        List<Map<String, String>> maps = PageUtils.getRowSetIdByTableId(tableId);
        String rowSetId ="";
        for (Map<String, String> map : maps) {
            if (map.get("idmajor").equals(tableId)&&(map.get("idminor").equals("1")||map.get("idminor").equals("0"))){
                rowSetId= map.get("rowsetid");
                break;
            }
        }
        //获得id为7的表的值为rowset的record
        Map<String, String> id7PageRecord = PageUtils.getId7ObjPage(rowSetId);
        //获取每个字段物理位置和逻辑位置的对应关系
        Map<Integer, Integer> colmap = MainParserForce.id3objPage(rowSetId);
        //获取IAM页面的第一页
        int firstIamPageNum = Integer.valueOf(id7PageRecord.get("firstIAMpage"));
        byte[] iamPage = PageUtils.getPagebyPageNum(firstIamPageNum);
        //获取表记录所在区的首页的list
        List<Integer> indexUnitAreaList = recordUnitArea(iamPage);
        List<byte[]> records;
        //获取混合区的指针
        List<Integer> mixPointer = recordMixPointer(iamPage);
        //遍历统一区的开始节点
        records = addRecords(mixPointer, indexUnitAreaList);
        for (int i = 1; i <= schemaRecordMap.size(); i++) {
            SchemaRecord schemaRecord = schemaRecordMap.get((long)i);
            schemaList.add(schemaBuilder(Integer.valueOf(schemaRecord.getType()), schemaRecord.getLength(), schemaRecord.getSchemaName()));
        }
        MainParserForce.schemaSorter(schemaList,colmap);
        result = RawColumnParser.prserRecord(records, schemaList);
        return result;
    }

    /**
     * 计算page有多少正在使用
     * @param page2
     * @return
     */
    public static int countPages(byte[] page2){
        int counter = 0;
        for(int i = 194;i<8192;i++){
            if ((page2[i] & 0xff)==255){
                break;
            }
            counter++;
        }
        return counter;
    }

    /**
     * 通过IAM页面查找数据所在的页面，区间
     * 由于在2016之后的IAM不会先进入混合区
     * 所以这里我暂时先做大于2016版本的适配
     * 如果小于2016，就直接用暴力解法吧
     * @return
     */
    public static List<Integer> recordUnitArea( byte[] iamPage){
        List<Integer> list = new ArrayList<>();
        //统一区开始的偏移
        int startOffSet = 194;
        int size = PageUtils.getPageNumber();
        int block = size/8;
        int gamSize = 64000;
        while (block>gamSize){
            block-=gamSize;
        }
        for (int i = startOffSet; i <block+startOffSet; i++) {
            if(iamPage[i]!=0) {
               int precount = (i-194)*8;
               for(int j = 0 ;j<8;j++){
                   if ((byte)((iamPage[i] >>j ) & 0x1)==1){
                       list.add((precount+j)*8);
                   }
               }
            }
        }
        return list;
    }
    public static List<Integer> recordMixPointer(byte[] iamPage){
        List<Integer> result = new ArrayList<>();
        //混合区指针开始的地方
        int startOffset = 142;
        //一共有8条记录在混合区
        int count = 8;
        for (int i = 0; i < count ; i++) {
            startOffset+=i*6;
            long pageid = HexUtil.int4(iamPage, startOffset);
            int fileid = HexUtil.int2(iamPage, startOffset + 4);
            if (pageid!=0&&fileid!=0){
                result.add((int) pageid);
            }
        }
        return result;
    }
    public static List<byte[]> addRecords(List<Integer> mixs,List<Integer> units){
        List<byte[]> records = new ArrayList<>();
        for (Integer mix : mixs) {
            byte[] pagebyPageNum = PageUtils.getPagebyPageNum(mix);
            PageHeader header = new PageHeader(pagebyPageNum);
            records.addAll(RecordCuter.cutRrcord(pagebyPageNum,header.getSlotCnt()));
        }

        for (Integer unit : units) {
            for (int i = unit; i <unit+8 ; i++) {
                byte[] pagebyPageNum = PageUtils.getPagebyPageNum(i);
                PageHeader header = new PageHeader(pagebyPageNum);
                if (header.getSlotCnt()==0){
                    break;
                }
                records.addAll(RecordCuter.cutRrcord(PageUtils.getPagebyPageNum(i),header.getSlotCnt()));
            }
        }
        return records;
    }
}
