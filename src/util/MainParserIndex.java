package util;

import domain.Ischema;
import schema.SchemaRecord;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 这是一个通过索引查早数据的方法，对于数据多的情况，这个方法
 * 应该比之前所写的暴力解析的方法速度更快,由于精力有限，而且经过
 * 测试发现，解析表名和schema并不需要多少时间，所以这两项我直接用了
 * 强制解析的方法
 */
public class MainParserIndex {
    public static List<Map<String,String>> parserTable(String tableId) throws IOException {
        Map<Long, SchemaRecord> SchemaRecordMap = MainParserForce.tableSchema(tableId);
        List<Ischema> schemaList = new ArrayList<>();
        String rowSetId=MainParserForce.id5objPage(tableId);
        Map<String, String> id7PageRecord = MainParserForce.id7objPage(rowSetId);
        String allocationUnitID = id7PageRecord.get("auid");
        Map<Integer, Integer> colmap = MainParserForce.id3objPage(rowSetId);
        byte[] page2 = PageSelecter.getPagebyPageNum(2);
        int counter = countPages(page2);
        int firstIamPageNum = Integer.valueOf(id7PageRecord.get("firstIAMpage"));
        byte[] IamPage = PageSelecter.getPagebyPageNum(firstIamPageNum);
        List<Integer> list = recordArea(counter,IamPage);
        System.out.println(list);
        return null;
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
    public static List<Integer> recordArea(int counter,byte[] iamPage){
        List<Integer> list = new ArrayList<>();
        for (int i = 194; i <counter+194 ; i++) {
            if(iamPage[i]!=0) {
               int precout = (i-194)*8;
               for(int j = 0 ;j<8;j++){
                   if ((byte)((iamPage[i] >>j ) & 0x1)==1){
                       list.add((precout+j)*8);
                   }
               }
            }
        }
        return list;
    }
}
