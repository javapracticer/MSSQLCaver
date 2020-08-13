package util;

import domain.*;
import schema.SchemaRecord;
import schema.SchemeaPage;
import test.CompleteTest;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static util.PageCuter.BORDERPAGE;
import static util.PageCuter.file;

public class PageUtils {
    private static byte[][] read;
    private static List<byte[]> idobj7Pages = new ArrayList<>();
    private static List<byte[]> idobj5Pages = new ArrayList<>();
    private static List<SchemeaPage> schemaPages = new ArrayList<>();
    private static List<byte[]> idobj3Pages = new ArrayList<>();
    static {
        long startTime = System.currentTimeMillis();
        System.out.println("文件载入初始化...");
        try {
            read = PageCuter.read(CompleteTest.mkdir);
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
            }else if (header.getIdObj()==3&&header.getType()==1){
                idobj3Pages.add(page);
            }
        }
        //获取读取了的最后一页,查看是否还有后续页，如果有就读取。
        byte[] tempPage = idobj7Pages.get(idobj7Pages.size() - 1);
        PageHeader header = new PageHeader(tempPage);
        try {
            while (header.getNextPage() != 0) {
                byte[] pagebyPageNum = getPagebyPageNum((int) header.getNextPage());
                idobj7Pages.add(pagebyPageNum);
                header = new PageHeader(pagebyPageNum);

            }
            tempPage = idobj5Pages.get(idobj5Pages.size() - 1);
            header = new PageHeader(tempPage);
            while (header.getNextPage() != 0) {
                byte[] pagebyPageNum = getPagebyPageNum((int) header.getNextPage());
                idobj5Pages.add(pagebyPageNum);
                header = new PageHeader(pagebyPageNum);
            }
            tempPage = idobj3Pages.get(idobj3Pages.size() - 1);
            header = new PageHeader(tempPage);
            while (header.getNextPage() != 0) {
                byte[] pagebyPageNum = getPagebyPageNum((int) header.getNextPage());
                idobj3Pages.add(pagebyPageNum);
                header = new PageHeader(pagebyPageNum);
            }
            SchemeaPage tempSchemaPage = schemaPages.get(schemaPages.size() - 1);
            header = tempSchemaPage.getHeader();
            while (header.getNextPage() != 0) {
                byte[] pagebyPageNum = getPagebyPageNum((int) header.getNextPage());
                tempSchemaPage = new SchemeaPage(pagebyPageNum);
                schemaPages.add(tempSchemaPage);
                header = tempSchemaPage.getHeader();
            }
        }catch (Exception e){
            throw new RuntimeException("初始化失败");
        }
        long endTime = System.currentTimeMillis();
        System.out.println("文件初始化耗时:"+(endTime-startTime)+"ms");
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
    public static byte[] getPagebyPageNum(int num)  {
        try {
            if (num< BORDERPAGE){
                return read[num];
            }else {
                //直接按流读取
                FileInputStream fileInputStream = new FileInputStream(file);
                fileInputStream.skip((long)num*8192L);
                byte[] aimPage = new byte[8192];
                fileInputStream.read(aimPage);
                fileInputStream.close();
                return aimPage;
            }
        }catch (Exception e){
            e.printStackTrace();

        }
        throw new RuntimeException("查找页面出错");
    }

    /**
     * 返回文件的页数
     * @return
     */
    public static int getPageNumber(){
        int pagenumber = (int) (file.length()/8192);
        return pagenumber;
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
            List<Map<String, String>> maps = RawColumnParser.parserRecord(records, list);
            for (Map<String, String> map : maps) {
                if( map.get("idmajor").equals(tableId)){
                    return maps;
                }
            }
        }
        return null;
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
                List<Map<String, String>> maps = RawColumnParser.parserRecord(records, list);
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
    public static Map<Integer,Integer> id3objPageRecords(String rowsetid) throws IOException {
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
        for (byte[] idobj3Page : idobj3Pages) {
            PageHeader header = new PageHeader(idobj3Page);
            List<byte[]> records = RecordCuter.cutRrcord(idobj3Page,header.getSlotCnt());
            List<Map<String, String>> maps = RawColumnParser.parserRecord(records, list);
            for (Map<String, String> map : maps) {
                if (map.get("rsid").equals(rowsetid)){
                    colMap.put(Integer.valueOf(map.get("resolid")),Integer.valueOf(map.get("hbcolid")));
                }
            }
        }
        return colMap;
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
            case 60:
                return new RawMoney(name);
            case 231:
                return new RawVarchar(name,length);
            case 99:
                return new RawNText(name);
            case 40:
                return new RawDate(name);
            case 58:
                return new RawSmallDateTime(name);
            case 104:
                return new RawBit(name);
            case 48:
                return new RawTinyint(name);
            case 173:
                return new RawBinary(name,length);
            case 165:
                return new RawVarBinary(name,length);
            case 52:
                return new RawSmallInt(name);
            case 36:
                return new RawUniqueidentifier(name);
            default:
                throw new RuntimeException( "类型"+code+"暂时不被支持");
        }
    }
    /**
     * 将schema列表的逻辑顺序调整为物理顺序
     * @param ischemaList
     * @param sortmap
     */
    public static void schemaSorter(List<Ischema> ischemaList, Map<Integer, Integer> sortmap) {
        Ischema[] ischemas = new Ischema[ischemaList.size()];
        for (int i = 1; i <= sortmap.size(); i++) {
            try {
                ischemas[sortmap.get(i) - 1] = ischemaList.get(i - 1);
            }catch (Exception e){
                System.out.println("正在修正行数量");
            }

        }
        ischemaList.clear();
        for (Ischema ischema : ischemas) {
            ischemaList.add(ischema);
        }
    }
    public static List<byte[]> findAllIamPage(byte[] iamPage) throws IOException {
        List<byte[]> allIamPage = new ArrayList<>();
        PageHeader header = new PageHeader(iamPage);
        allIamPage.add(iamPage);
        while (header.getNextPage()!=0){
            iamPage = getPagebyPageNum((int) header.getNextPage());
            header = new PageHeader(iamPage);
            allIamPage.add(iamPage);
        }
        return allIamPage;
    }
}
