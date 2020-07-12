import domain.*;

import org.junit.jupiter.api.Test;
import schema.schemaRecord;
import title.titlePage;
import title.titleRecord;
import util.pageCuter;
import schema.schemeaPage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import util.pageSelecter;
import util.rawColumnParser;
import util.recordCuter;

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
                long tableid = 430624577L;
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
                byte[][] records = recordCuter.cutRrcord(bytes, header.getSlotCnt());
                List<Map<String, String>> maps = rawColumnParser.prserRecord(records, list, read);
                for (Map<String, String> map : maps) {
                    if (map.get("ownerid").equals("72057594044350464")){
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
        byte[][] read = pageCuter.read("C:\\Users\\s6560\\Documents\\sqlsample\\sample.mdf");
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
                byte[][] records = recordCuter.cutRrcord(bytes, header.getSlotCnt());
                List<Map<String, String>> maps = rawColumnParser.prserRecord(records, list, read);
                for (Map<String, String> map : maps) {
                    if (map.get("idmajor").equals("430624577")){
                        System.out.println("Rowsetid:"+map.get("rowsetid")+"|"+"ObjectID:"+map.get("idmajor")+"|"+"IndexID:"+map.get("idminor"));
                    }
                    }
                }
            }
        }
        @Test
        public void testBinaryShift(){
        Long allocationUnitID = 72057594051231744L;
        Long indexID= allocationUnitID>>48;
        Long idObj = (allocationUnitID - (indexID << 48)) >> 16;
            System.out.println("indxeID="+indexID);
            System.out.println("idObj="+idObj);
        }
        @Test
        public void testParseRecord() throws IOException {
            byte[][] read = pageSelecter.getPages();
            List<Ischema> list = new ArrayList<>();
            list.add(new rawVarchar("a",3000));
            list.add(new rawVarchar("b",8000));
            list.add(new rawVarchar("c",8000));
            list.add(new rawVarchar("d",8000));

            System.out.printf("%-20s","a");
            System.out.printf("%-20s","b");
            System.out.printf("%-20s","c");
            System.out.printf("%-20s","d");
            System.out.println();

            for (byte[] bytes : read) {
                pageHeader header = new pageHeader(bytes);
                if (header.getIndexId()==256&&header.getIdObj()==203&&header.getType()==1){
                    byte[][] records = recordCuter.cutRrcord(bytes, header.getSlotCnt());
                    List<Map<String, String>> maps = rawColumnParser.prserRecord(records, list,read);
                    for (Map<String, String> map : maps) {
                        if (map.get("a").length()>5){
                            String secondColumn = map.get("a");
                            System.out.printf("%-20s",secondColumn.substring(0,3)+"...."+secondColumn.length());
                        }else {
                            System.out.printf("%-20s",map.get("a"));
                        }
                        if (map.get("b").length()>5){
                            String secondColumn = map.get("b");
                            System.out.printf("%-20s",secondColumn.substring(0,3)+"...."+secondColumn.length());
                        }else {
                            System.out.printf("%-20s",map.get("b"));
                        }
                        if (map.get("c").length()>5){
                            String secondColumn = map.get("c");
                            System.out.printf("%-15s",secondColumn.substring(0,3)+"...."+secondColumn.length());
                        }else {
                            System.out.printf("%-15s",map.get("c"));
                        }
                        if (map.get("d").length()>5){
                            String secondColumn = map.get("d");
                            System.out.printf("%-20s",secondColumn.substring(0,3)+"...."+secondColumn.length());
                        }else {
                            System.out.printf("%-20s",map.get("d"));
                        }
                        System.out.println();
                    }
                }
            }
        }
        @Test
        public void testBinary(){
            byte b = Byte.parseByte(String.valueOf(30),16);
            System.out.println(b);
            System.out.println((byte) ((b >> 6) & 0x1));
            System.out.println(7/8);
        }
        @Test
        public void caculate(){
            Long l = 11990400L;
            int ll = (int) (l/300);
            int lll = ll%60;
            System.out.println(lll);
        }
    }

