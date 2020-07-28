package util;

import domain.*;
import schema.SchemaRecord;
import schema.SchemeaPage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 这是一个整合类，将全自动的执行查询操作，然后返回一个表的map
 */
public class MainParserForce {
    static byte[][] read = PageSelecter.getPages();
    public static List<Map<String, String>> parsetTable(String tableid) throws IOException {
        Map<Long, SchemaRecord> schemaMap = tableSchema(tableid);
        List<Ischema> schemaList = new ArrayList<>();
        String rowSetId = idobj5Page(tableid);
        String allocationUnitID = id7objPage(rowSetId);
        Map<Integer, Integer> colmap = id3objPage(rowSetId);
        Long aLong = Long.valueOf(allocationUnitID);
        Long indexID = aLong >> 48;
        Long idObj = (aLong - (indexID << 48)) >> 16;
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
     *  查找idobj为5的页里的数据表项
     * @param tableid
     * @return
     * @throws IOException
     */
    private static String idobj5Page(String tableid) throws IOException {
        List<Ischema> list = new ArrayList<>();
        list.add(new RawBigInt("rowsetid"));
        list.add(new RawTinyint("ownertype"));
        list.add(new RawInt("idmajor"));
        list.add(new RawInt("idminor"));
        list.add(new RawInt("numpart"));
        list.add(new RawInt("status"));
        list.add(new RawSmallInt("figidfs"));
        list.add(new RawBigInt("rcrows"));
        list.add(new RawTinyint("cmprlevel"));
        list.add(new RawTinyint("fillfact"));
        list.add(new RawSmallInt("maxnullbit"));
        list.add(new RawInt("maxleaf"));
        list.add(new RawSmallInt("maxint"));
        list.add(new RawSmallInt("minleaf"));
        list.add(new RawSmallInt("minint"));
        list.add(new RawVarBinary("rsguid",0));
        list.add(new RawVarBinary("lockres",0));
        list.add(new RawInt("dbfragid"));
        for (byte[] bytes : read) {
            PageHeader header = new PageHeader(bytes);
            if (header.getIdObj()==5&&header.getType()==1){
                List<byte[]> records = RecordCuter.cutRrcord(bytes, header.getSlotCnt());
                List<Map<String, String>> maps = RawColumnParser.prserRecord(records, list);
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
        list.add(new RawBigInt("auid"));
        list.add(new RawTinyint("type"));
        list.add(new RawBigInt("ownerid"));
        list.add(new RawInt("status"));
        list.add(new RawSmallInt("fgid"));
        list.add(new RawBinary("pgfirst",6));
        list.add(new RawBinary("pgroot",6));
        list.add(new RawBinary("pgfirstiam",6));
        list.add(new RawBigInt("pcused"));
        list.add(new RawBigInt("pcdata"));
        list.add(new RawBigInt("pcreserved"));
        list.add(new RawInt("dbfragid"));
        for (byte[] bytes : read) {
            PageHeader header = new PageHeader(bytes);
            if (header.getIdObj()==7&&header.getType()==1){
                List<byte[]> records = RecordCuter.cutRrcord(bytes, header.getSlotCnt());
                List<Map<String, String>> maps = RawColumnParser.prserRecord(records, list);
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
        byte[][] pages = PageSelecter.getPages();
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
     * 找到表的schema
     * @param tableId
     * @return
     */
    public static  Map<Long, SchemaRecord> tableSchema(String tableId){
        Map<Long, SchemaRecord> recordMap = new HashMap<>();
        List<SchemeaPage> list = new ArrayList<>();
        for (byte[] page : read) {
            PageHeader header = new PageHeader(page);
            if (header.getType()==1&&header.getIdObj()==41){
                SchemeaPage sp = new SchemeaPage(page);
                list.add(sp);
            }
        }
        for (SchemeaPage SchemeaPage : list) {
            List<SchemaRecord> records = SchemeaPage.getRecords();
            for (SchemaRecord record : records) {
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
