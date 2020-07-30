package util;

import domain.*;
import schema.SchemaRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 这是一个整合类，将全自动的执行查询操作，然后返回一个表的map
 */
public class MainParserForce {
    static byte[][] read = PageUtils.getPages();
    public static List<Map<String, String>> parsetTable(String tableid) throws IOException {
        //这个map按逻辑顺序储存了schema
        Map<Long, SchemaRecord> schemaMap = PageUtils.getTableSchema(tableid);
        List<Ischema> schemaList = new ArrayList<>();
        List<Map<String, String>> maps = PageUtils.getRowSetIdByTableId(tableid);
        String rowSetId ="";
        for (Map<String, String> map : maps) {
            if (map.get("idmajor").equals(tableid)&&(map.get("idminor").equals("1")||map.get("idminor").equals("0"))){
                rowSetId= map.get("rowsetid");
            }
        }
        String allocationUnitID = PageUtils.getId7ObjPage(rowSetId).get("auid");
        Map<Integer, Integer> colmap = id3objPage(rowSetId);
        Long aLong = Long.valueOf(allocationUnitID);
        Long indexID = aLong >> 48;
        Long idObj = (aLong - (indexID << 48)) >> 16;
        //将map里的schema按物理顺序拿出
        for (int i = 1; i <= schemaMap.size(); i++) {
            SchemaRecord SchemaRecord = schemaMap.get((long)i);
            schemaList.add(schemaBuilder(Integer.valueOf(SchemaRecord.getType()), SchemaRecord.getLength(), SchemaRecord.getSchemaName()));
        }
        schemaSorter(schemaList,colmap);
        List<Map<String,String>> result = new ArrayList<Map<String, String>>();
        for (byte[] bytes : read) {
            PageHeader header = new PageHeader(bytes);
            if (header.getIndexId() == indexID.intValue() && header.getIdObj() == idObj.intValue() && header.getType() == 1) {
                List<byte[]> records = RecordCuter.cutRrcord(bytes, header.getSlotCnt());
//                List<byte[]> records = DeletedRecordCuter.cutRrcord(bytes, header.getFreeData());
                result.addAll(RawColumnParser.prserRecord(records, schemaList));

            }

        }
        return result;
    }
    /**
     * 查找id为3的页面里的行的物理顺序
     * @throws IOException
     */
    public static Map<Integer,Integer> id3objPage(String rowsetid) throws IOException {
        Map<Integer,Integer> colMap = new HashMap<>();
        List<Ischema> list = new ArrayList<>();
        list.add(new RawBigInt("rsid"));
        list.add(new RawInt("resolid"));
        list.add(new RawInt("hbcolid"));
        list.add(new RawBigInt("rcmodified"));
        list.add(new RawInt("ti"));
        list.add(new RawInt("cid"));
        list.add(new RawSmallInt("ordkey"));
        list.add(new RawSmallInt("maxinrowlen"));
        list.add(new RawInt("status"));
        list.add(new RawInt("offset"));
        list.add(new RawInt("nullbit"));
        list.add(new RawSmallInt("bitpos"));
        list.add(new RawVarBinary("colguid",16));
        byte[][] pages = PageUtils.getPages();
        for (byte[] page : pages) {
            PageHeader header = new PageHeader(page);
            if (header.getIdObj()==3&&header.getType()==1){
                List<byte[]> records = RecordCuter.cutRrcord(page,header.getSlotCnt());
                List<Map<String, String>> maps = RawColumnParser.prserRecord(records, list);
                for (Map<String, String> map : maps) {
                    if (map.get("rsid").equals(rowsetid)){
                        colMap.put(Integer.valueOf(map.get("resolid")),Integer.valueOf(map.get("hbcolid")));
                    }
                }
            }
        }
        return colMap;
    }

    /**
     * 将schema列表的逻辑顺序调整为物理顺序
     * @param ischemaList
     * @param sortmap
     */
    public static void schemaSorter(List<Ischema> ischemaList, Map<Integer, Integer> sortmap) {
        Ischema[] ischemas = new Ischema[ischemaList.size()];
        for (int i = 1; i <= sortmap.size(); i++) {
            ischemas[sortmap.get(i) - 1] = ischemaList.get(i - 1);
        }
        ischemaList.clear();
        for (Ischema ischema : ischemas) {
            ischemaList.add(ischema);
        }
    }

    /**
     * 通过code生成schema类
     * @param code
     * @param length
     * @return
     */
    public static Ischema schemaBuilder(int code,int length,String name){
        switch (code){
            case 56:
                return new RawInt(name);
            case 175:
                return new RawChar(name,length);
            case 35:
                return new RawText(name);
            case 167:
                return new RawVarchar(name,length);
            case 61:
                return new RawDateTime(name);
            case 62:
                return new RawFloat(name,length);
            case 239:
                return new RawNChar(name,length);
            case 127:
                return new RawBigInt(name);
        }
        throw new RuntimeException("并未找到对应的schema类");
    }

}
