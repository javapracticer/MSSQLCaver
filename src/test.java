import domain.*;

import org.junit.jupiter.api.Test;
import schema.schemaRecord;
import title.titlePage;
import title.titleRecord;
import util.*;
import schema.schemeaPage;
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
        byte[][] read = pageCuter.read("C:\\Users\\s6560\\Documents\\sqlsample\\2012.mdf");
        List<titlePage> list = new ArrayList<>();
        for (byte[] bytes : read) {
            pageHeader header = new pageHeader(bytes);
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
        byte[][] read = pageCuter.read("C:\\Users\\s6560\\Documents\\sqlsample\\sample.mdf");
        List<titlePage> list = new ArrayList<>();
        for (byte[] bytes : read) {
            pageHeader header = new pageHeader(bytes);
            if (header.getType()==1&&header.getIdObj()==34){
                titlePage tp = new titlePage(bytes);
                list.add(tp);
            }
        }
        int i = 0;
        for (title.titlePage titlePage : list) {
            List<titleRecord> list1 = titlePage.getList();
            for (title.titleRecord titleRecord : list1) {
                if (titleRecord.getType()==8277){
                    System.out.println(titleRecord);
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
        byte[][] pages = pageCuter.read("C:\\Users\\s6560\\Documents\\sqlsample\\sample.mdf");
        List<schemeaPage> list = new ArrayList<>();
        for (byte[] page : pages) {
            pageHeader header = new pageHeader(page);
            if (header.getType()==1&&header.getIdObj()==41){
                schemeaPage sp = new schemeaPage(page);
                list.add(sp);
            }
        }
        for (schemeaPage schemeaPage : list) {
            List<schemaRecord> records = schemeaPage.getRecords();
            for (schemaRecord record : records) {
                long tableid = 1954106002L;
                if (record.getTableId()== tableid){
                    System.out.println("Type:"+schemaType.codeOf(Integer.parseInt(record.getType())).getFiled()+"\n"+record);
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
        byte[][] read = pageCuter.read("C:\\Users\\s6560\\Documents\\sqlsample\\sample.mdf");
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
                    if (map.get("ownerid").equals("72057594043760640")){
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
        byte[][] read = pageCuter.read("C:\\Users\\s6560\\Documents\\sqlsample\\sample2.mdf");
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
                    if (map.get("idmajor").equals("1221579390")){
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
            byte[][] read = pageSelecter.getPages();
            List<Ischema> list = new ArrayList<>();
            list.add(new rawInt("dog"));
            list.add(new rawChar("cat",10));
            list.add(new rawText("butterfly"));
            System.out.printf("%-20s","dog");
            System.out.printf("%-20s","cat");
            System.out.printf("%-20s","butterfly");
            System.out.println();

            for (byte[] bytes : read) {
                pageHeader header = new pageHeader(bytes);
                if (header.getIndexId()==256&&header.getIdObj()==135&&header.getType()==1){
                    List<byte[]> records = recordCuter.cutRrcord(bytes, header.getSlotCnt());
//                    List<byte[]> records = deletedRecordCuter.cutRrcord(bytes, header.getFreeData());
                    List<Map<String, String>> maps = rawColumnParser.prserRecord(records, list);
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
            System.out.println((byte)((0xf0 >>1 ) & 0x1)==1);
            System.out.println("");
        }
        @Test
        public void deletedRecordCut(){
            byte[][] pages = pageSelecter.getPages();
            for (byte[] page : pages) {
                pageHeader header = new pageHeader(page);
                if (header.getPageId()==464){
                    List<byte[]> list = deletedRecordCuter.cutRrcord(page, header.getFreeData());
                    for (byte[] bytes : list) {
                        System.out.println(bytes);
                    }
                }
            }
        }
        @Test
        public void testmainPaarser() throws IOException {
            long startTime = System.currentTimeMillis();
            List<Map<String, String>> maps = mainParser.parsetTable(String.valueOf(1298103665));
            for (Map<String, String> map : maps) {
                System.out.println(map);
            }
            long endTime = System.currentTimeMillis();
            System.out.println(endTime-startTime);
        }
    }

