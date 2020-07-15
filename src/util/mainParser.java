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
        Long aLong = Long.valueOf(allocationUnitID);
        Long indexID = aLong >> 48;
        Long idObj = (aLong - (indexID << 48)) >> 16;
        for (int i = 1; i <= schemaMap.size(); i++) {
            schemaRecord schemaRecord = schemaMap.get((long)i);
            schemaList.add(schemaBuilder(Integer.valueOf(schemaRecord.getType()), schemaRecord.getLength(), schemaRecord.getSchemaName()));
        }
        for (byte[] bytes : read) {
            pageHeader header = new pageHeader(bytes);
            if (header.getIndexId() == indexID.intValue() && header.getIdObj() == idObj.intValue() && header.getType() == 1) {
                List<byte[]> records = recordCuter.cutRrcord(bytes, header.getSlotCnt());
//                List<byte[]> records = deletedRecordCuter.cutRrcord(bytes, header.getFreeData());
                List<Map<String, String>> maps = rawColumnParser.prserRecord(records, schemaList, read);
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
        list.add(new rawVarBinary("rsguid"));
        list.add(new rawVarBinary("lockres"));
        list.add(new rawInt("dbfragid"));
        for (byte[] bytes : read) {
            pageHeader header = new pageHeader(bytes);
            if (header.getIdObj()==5&&header.getType()==1){
                List<byte[]> records = recordCuter.cutRrcord(bytes, header.getSlotCnt());
                List<Map<String, String>> maps = rawColumnParser.prserRecord(records, list, read);
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
                List<Map<String, String>> maps = rawColumnParser.prserRecord(records, list, read);
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
        }
        throw new RuntimeException("并未找到对应的schema类");
    }
}
