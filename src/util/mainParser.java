package util;

import domain.*;
import schema.schemaRecord;
import schema.schemeaPage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 这是一个整合类，将全自动的执行查询操作，然后返回一个表的map
 */
public class mainParser {
    static byte[][] read = pageSelecter.getPages();
    public static List<Map<String, String>> parsetTable(String tableid) throws IOException {
        Map<Long, schemaRecord> schemaMap = tableSchema(tableid);
        List<Ischema> schemaList = new ArrayList<>();
        String rowSetId = idobj5Page(tableid);
        String allocationUnitID = id7objPage(rowSetId);
        Map<Integer, Integer> colmap = id3objPage(rowSetId);
        Long aLong = Long.valueOf(allocationUnitID);
        Long indexID = aLong >> 48;
        Long idObj = (aLong - (indexID << 48)) >> 16;
        for (int i = 1; i <= schemaMap.size(); i++) {
            schemaRecord schemaRecord = schemaMap.get((long)i);
            schemaList.add(schemaBuilder(Integer.valueOf(schemaRecord.getType()), schemaRecord.getLength(), schemaRecord.getSchemaName()));
        }
        schemaSorter(schemaList,colmap);
        for (byte[] bytes : read) {
            pageHeader header = new pageHeader(bytes);
            if (header.getIndexId() == indexID.intValue() && header.getIdObj() == idObj.intValue() && header.getType() == 1) {
                List<byte[]> records = recordCuter.cutRrcord(bytes, header.getSlotCnt());
//                List<byte[]> records = deletedRecordCuter.cutRrcord(bytes, header.getFreeData());
                List<Map<String, String>> maps = rawColumnParser.prserRecord(records, schemaList);
                return maps;
            }
        }
        throw new RuntimeException("并未找到相关数据");
    }

    /**
     *  查找idobj为5的页里的数据表项
     * @param tableid
     * @return
     * @throws IOException
     */
    private static String idobj5Page(String tableid) throws IOException {
        List<Ischema> list = new ArrayList<>();
        list.add(new rawBigInt("rowsetid"));
        list.add(new rawTinyint("ownertype"));
        list.add(new rawInt("idmajor"));
        list.add(new rawInt("idminor"));
        list.add(new rawInt("numpart"));
        list.add(new rawInt("status"));
        list.add(new rawSmallInt("figidfs"));
        list.add(new rawBigInt("rcrows"));
        list.add(new rawTinyint("cmprlevel"));
        list.add(new rawTinyint("fillfact"));
        list.add(new rawSmallInt("maxnullbit"));
        list.add(new rawInt("maxleaf"));
        list.add(new rawSmallInt("maxint"));
        list.add(new rawSmallInt("minleaf"));
        list.add(new rawSmallInt("minint"));
        list.add(new rawVarBinary("rsguid",0));
        list.add(new rawVarBinary("lockres",0));
        list.add(new rawInt("dbfragid"));
        for (byte[] bytes : read) {
            pageHeader header = new pageHeader(bytes);
            if (header.getIdObj()==5&&header.getType()==1){
                List<byte[]> records = recordCuter.cutRrcord(bytes, header.getSlotCnt());
                List<Map<String, String>> maps = rawColumnParser.prserRecord(records, list);
                for (Map<String, String> map : maps) {
                    if (map.get("idmajor").equals(tableid)&&(map.get("idminor").equals("1")||map.get("idminor").equals("0"))){
                        return map.get("rowsetid");
                    }
                }
            }
        }
        return "";
    }

    /**
     * 查早id为7的页里数据
     * @param rowsetId
     * @return
     * @throws IOException
     */
    private static String id7objPage(String rowsetId) throws IOException {
        List<Ischema> list = new ArrayList<>();
        list.add(new rawBigInt("auid"));
        list.add(new rawTinyint("type"));
        list.add(new rawBigInt("ownerid"));
        list.add(new rawInt("status"));
        list.add(new rawSmallInt("fgid"));
        list.add(new rawBinary("pgfirst",6));
        list.add(new rawBinary("pgroot",6));
        list.add(new rawBinary("pgfirstiam",6));
        list.add(new rawBigInt("pcused"));
        list.add(new rawBigInt("pcdata"));
        list.add(new rawBigInt("pcreserved"));
        list.add(new rawInt("dbfragid"));
        for (byte[] bytes : read) {
            pageHeader header = new pageHeader(bytes);
            if (header.getIdObj()==7&&header.getType()==1){
                List<byte[]> records = recordCuter.cutRrcord(bytes, header.getSlotCnt());
                List<Map<String, String>> maps = rawColumnParser.prserRecord(records, list);
                for (Map<String, String> map : maps) {
                    if (map.get("ownerid").equals(rowsetId)&&map.get("type").equals("1")){
                        return map.get("auid");
                    }
                }
            }
        }
        return "";
    }

    /**
     * 查找id为3的页面里的行的物理顺序
     * @throws IOException
     */
    public static Map<Integer,Integer> id3objPage(String rowsetid) throws IOException {
        Map<Integer,Integer> colMap = new HashMap<>();
        List<Ischema> list = new ArrayList<>();
        list.add(new rawBigInt("rsid"));
        list.add(new rawInt("resolid"));
        list.add(new rawInt("hbcolid"));
        list.add(new rawBigInt("rcmodified"));
        list.add(new rawInt("ti"));
        list.add(new rawInt("cid"));
        list.add(new rawSmallInt("ordkey"));
        list.add(new rawSmallInt("maxinrowlen"));
        list.add(new rawInt("status"));
        list.add(new rawInt("offset"));
        list.add(new rawInt("nullbit"));
        list.add(new rawSmallInt("bitpos"));
        list.add(new rawVarBinary("colguid",16));
        byte[][] pages = pageSelecter.getPages();
        for (byte[] page : pages) {
            pageHeader header = new pageHeader(page);
            if (header.getIdObj()==3&&header.getType()==1){
                List<byte[]> records = recordCuter.cutRrcord(page,header.getSlotCnt());
                List<Map<String, String>> maps = rawColumnParser.prserRecord(records, list);
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
     * 找到表的schema
     * @param tableId
     * @return
     */
    public static  Map<Long, schemaRecord> tableSchema(String tableId){
        Map<Long,schemaRecord> recordMap = new HashMap<>();
        List<schemeaPage> list = new ArrayList<>();
        for (byte[] page : read) {
            pageHeader header = new pageHeader(page);
            if (header.getType()==1&&header.getIdObj()==41){
                schemeaPage sp = new schemeaPage(page);
                list.add(sp);
            }
        }
        for (schemeaPage schemeaPage : list) {
            List<schemaRecord> records = schemeaPage.getRecords();
            for (schemaRecord record : records) {
                long tableid = Long.parseLong(tableId);
                if (record.getTableId()== tableid){
                   recordMap.put(record.getColumnid(),record);
                }
            }
        }
        return recordMap;
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
                return new rawInt(name);
            case 175:
                return new rawChar(name,length);
            case 35:
                return new rawText(name);
            case 167:
                return new rawVarchar(name,length);
            case 61:
                return new rawDateTime(name);
            case 62:
                return new rawFloat(name,length);
            case 239:
                return new rawNChar(name,length);
        }
        throw new RuntimeException("并未找到对应的schema类");
    }

}
