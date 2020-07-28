import domain.*;

import org.junit.jupiter.api.Test;
import schema.SchemeaPage;
import schema.SchemaRecord;
import title.TitlePage;
import title.TitleRecord;
import util.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class test {
    /**
     * 测试读取header
     * @throws IOException
     */
    @Test
    public void headReader() throws IOException {
        byte[][] read = PageCuter.read("C:\\Users\\s6560\\Documents\\sqlsample\\2012.mdf");
        List<TitlePage> list = new ArrayList<>();
        for (byte[] bytes : read) {
            PageHeader header = new PageHeader(bytes);
            if (header.getIdObj()==7){
                System.out.println(header);
            }
        }
    }

    /**
     * 测试解析表名
     * @throws IOException
     */
    @Test
    public void testTitleName() throws IOException {
        byte[][] read = PageCuter.read("C:\\Users\\s6560\\Documents\\sqlsample\\sample.mdf");
        List<TitlePage> list = new ArrayList<>();
        for (byte[] bytes : read) {
            PageHeader header = new PageHeader(bytes);
            if (header.getType()==1&&header.getIdObj()==34){
                TitlePage tp = new TitlePage(bytes);
                list.add(tp);
            }
        }
        int i = 0;
        for (TitlePage TitlePage : list) {
            List<TitleRecord> list1 = TitlePage.getList();
            for (TitleRecord TitleRecord : list1) {
                if (TitleRecord.getType()==8277){
                    System.out.println(TitleRecord);
                    i++;
                }
            }
        }
        System.out.println(i);
    }

    /**
     * 测试解析特定表的schema
     * @throws IOException
     */
    @Test
    public void testTableSchema() throws IOException {
        byte[][] pages = PageCuter.read("C:\\Users\\s6560\\Documents\\sqlsample\\cx_data1.mdf");
        List<SchemeaPage> list = new ArrayList<>();
        for (byte[] page : pages) {
            PageHeader header = new PageHeader(page);
            if (header.getType()==1&&header.getIdObj()==41){
                SchemeaPage sp = new SchemeaPage(page);
                list.add(sp);
            }
        }
        for (SchemeaPage SchemeaPage : list) {
            List<SchemaRecord> records = SchemeaPage.getRecords();
            for (SchemaRecord record : records) {
                long tableid = 1294679710L;
                if (record.getTableId()== tableid){
                    System.out.println("Type:"+ SchemaType.codeOf(Integer.parseInt(record.getType())).getFiled()+"\n"+record);
                }
            }
        }
    }

    /**
     * 测试解析idobj为7的页
     * @throws IOException
     */
    @Test
    public void testid7() throws IOException {
        byte[][] read = PageCuter.read("C:\\Users\\s6560\\Documents\\sqlsample\\sample.mdf");
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
        for (byte[] bytes : read) {
            PageHeader header = new PageHeader(bytes);
            if (header.getIdObj()==7&&header.getType()==1){
                List<byte[]> records = RecordCuter.cutRrcord(bytes, header.getSlotCnt());
                List<Map<String, String>> maps = RawColumnParser.prserRecord(records, list);
                for (Map<String, String> map : maps) {
                    if (map.get("ownerid").equals("72057594044481536")){
                        System.out.println(map);
                    }
                }
            }
        }
    }

    /**
     * 测试解析idobj为5的页
     * @throws IOException
     */
    @Test
    public void testid5() throws IOException {
        byte[][] read = PageCuter.read("C:\\Users\\s6560\\Documents\\sqlsample\\sample.mdf");
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
                    if (map.get("idmajor").equals("1954106002")){
                        System.out.println("Rowsetid:"+map.get("rowsetid")+"|"+"ObjectID:"+map.get("idmajor")+"|"+"IndexID:"+map.get("idminor"));
                    }
                    }
                }
            }
        }
        @Test
        public void testBinaryShift(){
        Long allocationUnitID = 72057594043170816L;
        Long indexID= allocationUnitID>>48;
        Long idObj = (allocationUnitID - (indexID << 48)) >> 16;
            System.out.println("indxeID="+indexID);
            System.out.println("idObj="+idObj);
        }
        @Test
        public void testParseRecord() throws IOException {
            byte[][] read = PageSelecter.getPages();
            List<Ischema> list = new ArrayList<>();
            list.add(new RawInt("dog"));
            list.add(new RawChar("cat",10));
            list.add(new RawText("butterfly"));
            System.out.printf("%-20s","dog");
            System.out.printf("%-20s","cat");
            System.out.printf("%-20s","butterfly");
            System.out.println();

            for (byte[] bytes : read) {
                PageHeader header = new PageHeader(bytes);
                if (header.getIndexId()==256&&header.getIdObj()==135&&header.getType()==1){
                    List<byte[]> records = RecordCuter.cutRrcord(bytes, header.getSlotCnt());
//                    List<byte[]> records = DeletedRecordCuter.cutRrcord(bytes, header.getFreeData());
                    List<Map<String, String>> maps = RawColumnParser.prserRecord(records, list);
                    for (Map<String, String> map : maps) {
                        System.out.printf("%-20s",map.get("dog"));
                        System.out.printf("%-20s",map.get("cat"));
                        System.out.printf("%-20s",map.get("butterfly"));
                        System.out.println();
                    }
                }
            }
        }
        @Test
        public void testBinary(){
            System.out.println((byte)((1 >>0 ) & 0x1)==1);
            System.out.println("");
        }
        @Test
        public void deletedRecordCut(){
            byte[][] pages = PageSelecter.getPages();
            for (byte[] page : pages) {
                PageHeader header = new PageHeader(page);
                if (header.getPageId()==464){
                    List<byte[]> list = DeletedRecordCuter.cutRrcord(page, header.getFreeData());
                    for (byte[] bytes : list) {
                        System.out.println(bytes);
                    }
                }
            }
        }
        @Test
        public void testmainPaarserForce() throws IOException {
            long startTime = System.currentTimeMillis();
            List<Map<String, String>> maps = MainParserForce.parsetTable(String.valueOf(1298103665));
            for (Map<String, String> map : maps) {
                System.out.println(map);
            }
            long endTime = System.currentTimeMillis();
            System.out.println(endTime-startTime);
        }
        @Test
        public void testMainParserIndex() throws IOException {
            MainParserIndex.parserTable(String.valueOf(1954106002));
        }
    }

