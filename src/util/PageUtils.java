package util;

import domain.*;
import schema.SchemaRecord;
import schema.SchemeaPage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageUtils {
    private static byte[][] read;
    private static List<byte[]> idobj7Pages = new ArrayList<>();
    private static List<byte[]> idobj5Pages = new ArrayList<>();
    private static List<SchemeaPage> schemaPages = new ArrayList<>();
    static {
        System.out.println("文件载入初始化...");
        try {
            read = PageCuter.read("C:\\Users\\s6560\\Documents\\sqlsample\\sample.mdf");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (byte[] page : read) {
            PageHeader header = new PageHeader(page);
            if (header.getIdObj()==7&&header.getType()==1){
                idobj7Pages.add(page);
            }else if (header.getIdObj()==5&&header.getType()==1){
                idobj5Pages.add(page);
            }else if (header.getType()==1&&header.getIdObj()==41){
                SchemeaPage sp = new SchemeaPage(page);
                schemaPages.add(sp);
            }
        }
    }
    public static byte[][] getPages(){
        return read;
    }
    public static byte[] pageSelecterByObjid(long pageid) throws IOException {
        for (byte[] page : read) {
            PageHeader header = new PageHeader(page);
            if (header.getPageId()==pageid){
                return page;
            }
        }
        return null;
    }

    /**
     * 返回指定页码的页面
     * @param num 指定的页码
     * @return
     */
    public static byte[] getPagebyPageNum(int num){
        return read[num];
    }

    /**
     * 返回文件的页数
     * @return
     */
    public static int getPageNumber(){
        return read.length;
    }
    public static  List<Map<String, String>> getRowSetIdByTableId(String tableId) throws IOException {
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
        for (byte[] idobj5Page : idobj5Pages) {
            PageHeader header = new PageHeader(idobj5Page);
            List<byte[]> records = RecordCuter.cutRrcord(idobj5Page, header.getSlotCnt());
            List<Map<String, String>> maps = RawColumnParser.prserRecord(records, list);
            for (Map<String, String> map : maps) {
                if( map.get("idmajor").equals(tableId)){
                    return maps;
                }
            }
        }
        throw new RuntimeException("未找到指定表 ");
    }
    public static  Map<String,String> getId7ObjPage(String rowsetId) throws IOException {
        List<Ischema> list = new ArrayList<>();
        list.add(new RawBigInt("auid"));
        list.add(new RawTinyint("type"));
        list.add(new RawBigInt("ownerid"));
        list.add(new RawInt("status"));
        list.add(new RawSmallInt("fgid"));
        list.add(new RawBinary("pgfirst",6));
        list.add(new RawBinary("pgroot",6));
        list.add(new RawInt("firstIAMpage"));
        list.add(new RawSmallInt("firstIAMFileId"));
        list.add(new RawBigInt("pcused"));
        list.add(new RawBigInt("pcdata"));
        list.add(new RawBigInt("pcreserved"));
        list.add(new RawInt("dbfragid"));
        for (byte[] idobj7Page : idobj7Pages) {
            PageHeader header = new PageHeader(idobj7Page);
            if (header.getIdObj()==7&&header.getType()==1){
                List<byte[]> records = RecordCuter.cutRrcord(idobj7Page, header.getSlotCnt());
                List<Map<String, String>> maps = RawColumnParser.prserRecord(records, list);
                for (Map<String, String> map : maps) {
                    if (map.get("ownerid").equals(rowsetId)&&map.get("type").equals("1")){
                        return map;
                    }
                }
            }
        }
        throw new RuntimeException("并未在OBJ7页里找到相关的记录");
    }
    public static  Map<Long, SchemaRecord> getTableSchema(String tableId){
        Map<Long, SchemaRecord> schemaMap = new HashMap<>(16);
        for (SchemeaPage SchemeaPage : schemaPages) {
            List<SchemaRecord> records = SchemeaPage.getRecords();
            for (SchemaRecord record : records) {
                long tableid = Long.parseLong(tableId);
                if (record.getTableId()== tableid){
                    schemaMap.put(record.getColumnid(),record);
                }
            }
        }
        return schemaMap;
    }
}
