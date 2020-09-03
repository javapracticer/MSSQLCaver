package test;

import domain.*;

import java.io.*;

import org.junit.jupiter.api.Test;
import schema.SchemeaPage;
import schema.SchemaRecord;
import title.TitlePage;
import util.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 这个类中是开发时的一些零散的工具类
 */
public class test {
    /**
     * 测试读取header
     *
     * @throws IOException
     */
    @Test
    public void headReader() throws IOException {
        byte[][] read = PageCuter.read("C:\\Users\\s6560\\Documents\\sqlsample\\2012.mdf");
        List<TitlePage> list = new ArrayList<>();
        for (byte[] bytes : read) {
            PageHeader header = new PageHeader(bytes);
            if (header.getIdObj() == 7) {
                System.out.println(header);
            }
        }
    }


    /**
     * 测试解析特定表的schema
     *
     * @throws IOException
     */
    @Test
    public void testTableSchema() throws IOException {
        byte[][] pages = PageCuter.read("C:\\Users\\s6560\\Documents\\sqlsample\\sample.mdf");
        List<SchemeaPage> list = new ArrayList<>();
        for (byte[] page : pages) {
            PageHeader header = new PageHeader(page);
            if (header.getType() == 1 && header.getIdObj() == 7) {
                System.out.println(header);
            }
        }
//        for (SchemeaPage SchemeaPage : list) {
//            List<SchemaRecord> records = SchemeaPage.getRecords();
//            for (SchemaRecord record : records) {
//                long tableid = 658101385L;
//                if (record.getTableId()== tableid){
//                    System.out.println("Type:"+ SchemaType.codeOf(Integer.parseInt(record.getType())).getFiled()+"\n"+record);
//                }
//            }
//        }
    }

    /**
     * 测试解析idobj为7的页
     *
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
        list.add(new RawBinary("pgfirst", 6));
        list.add(new RawBinary("pgroot", 6));
        list.add(new RawInt("firstIAMpage"));
        list.add(new RawSmallInt("firstIAMFileId"));
        list.add(new RawBigInt("pcused"));
        list.add(new RawBigInt("pcdata"));
        list.add(new RawBigInt("pcreserved"));
        list.add(new RawInt("dbfragid"));
        for (byte[] bytes : read) {
            PageHeader header = new PageHeader(bytes);
            if (header.getIdObj() == 7 && header.getType() == 1) {
                List<byte[]> records = RecordCuter.cutRrcord(bytes, header.getSlotCnt());
                List<Map<String, String>> maps = RawColumnParser.parserRecord(records, list);
                for (Map<String, String> map : maps) {
                    if (map.get("ownerid").equals("72057594044743680")) {
                        System.out.println(map);
                    }
                }
            }
        }
    }

    /**
     * 测试解析idobj为5的页
     *
     * @throws IOException
     */
    @Test
    public void testid5() throws IOException {
        byte[][] read = PageCuter.read("C:\\Users\\s6560\\Documents\\sqlsample\\samplexp.mdf");
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
        list.add(new RawVarBinary("rsguid", 0));
        list.add(new RawVarBinary("lockres", 0));
        list.add(new RawInt("dbfragid"));
        for (byte[] bytes : read) {
            PageHeader header = new PageHeader(bytes);
            if (header.getIdObj() == 5 && header.getType() == 1) {
                List<byte[]> records = RecordCuter.cutRrcord(bytes, header.getSlotCnt());
                List<Map<String, String>> maps = RawColumnParser.parserRecord(records, list);
                for (Map<String, String> map : maps) {
                    if (map.get("idmajor").equals("2105058535")) {
                        System.out.println("Rowsetid:" + map.get("rowsetid") + "|" + "ObjectID:" + map.get("idmajor") + "|" + "IndexID:" + map.get("idminor"));
                    }
                }
            }
        }
    }

    @Test
    public void testBinaryShift() {
        Long allocationUnitID = 72057594043170816L;
        Long indexID = allocationUnitID >> 48;
        Long idObj = (allocationUnitID - (indexID << 48)) >> 16;
        System.out.println("indxeID=" + indexID);
        System.out.println("idObj=" + idObj);
    }

    @Test
    public void testBinary() {
        String hex = "01312d00";
        Long aLong = Long.parseLong(hex, 16);
        double v = Double.longBitsToDouble(aLong);
        System.out.println(v);
    }

    @Test
    public void deletedRecordCut() {
        byte[][] pages = PageUtils.getPages();
        for (byte[] page : pages) {
            PageHeader header = new PageHeader(page);
            if (header.getPageId() == 464) {
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
        System.out.println(endTime - startTime);
    }

    @Test
    public void testMainParserIndex() throws IOException {
        long startTime = System.currentTimeMillis();
        List<Map<String, String>> maps = MainParserIndex.parserTable(String.valueOf(1710629137));
        if (maps != null) {
            for (Map<String, String> map : maps) {
                System.out.println(map);
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("总共耗时:" + (endTime - startTime));
    }

    @Test
    public void testLarge() throws IOException {
        long startTime = System.currentTimeMillis();
        File file = new File("C:\\Users\\s6560\\Documents\\sqlsample\\sample2.mdf");
        FileInputStream fileInputStream = new FileInputStream(file);
        long length = file.length();
        System.out.println(length);
        long test = 262488L * 8192L;
        fileInputStream.skip(test);
        byte[] bs = new byte[8192];
        fileInputStream.read(bs);
        fileInputStream.close();
        PageHeader header = new PageHeader(bs);
        System.out.println(header);
        long endTime = System.currentTimeMillis();
        System.out.println("总共耗时:" + (endTime - startTime) / 1000);
    }

    @Test
    public void getLow4() {
        String s = "110";
        System.out.println(Long.valueOf(s, 2));
    }

    @Test
    public void testCompressRowRecordCuter() {
        BigDecimal bigDecimal = BigDecimal.valueOf(12345L, 2);
        System.out.println(bigDecimal);
    }

    @Test
    public void testCorrupt() throws IOException {
        for (int i = 0; i <1 ; i++) {
            CorruptUtils.destoryedPageIDs("F:\\bigdate\\testSSBM.mdf", 0.15, i);
            System.out.println("文件" + i + "腐蚀完成");
        }
    }

}

