package util;

import domain.*;
import schema.SchemaRecord;
import schema.SchemeaPage;

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
    private static int versionNum = 0;

    /**
     * 设置文件路径,并对文件进行初始化操作
     * @param file
     */
    public static void setfile(String file) {
        idobj7Pages.clear();
        idobj5Pages.clear();
        schemaPages.clear();
        idobj3Pages.clear();
        boolean idobjget7 = false;
        boolean idobjget5 = false;
        boolean idobjget3 = false;
        boolean idobjgetSchema = false;
        long startTime = System.currentTimeMillis();
        System.out.println("文件载入初始化...");
        try {
            read = PageCuter.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //高效链表式遍历,
        for (byte[] page : read) {
            PageHeader header = new PageHeader(page);
            if (header.getIdObj() == 7 && header.getType() == 1 && !idobjget7) {
                idobj7Pages.add(page);
                getAllObjectNPage(header);
                idobjget7 = true;
            } else if (header.getIdObj() == 5 && header.getType() == 1 &&!idobjget5) {
                idobj5Pages.add(page);
                getAllObjectNPage(header);
                idobjget5 = true;
            } else if (header.getType() == 1 && header.getIdObj() == 41 &&!idobjgetSchema) {
                SchemeaPage sp = new SchemeaPage(page);
                schemaPages.add(sp);
                getAllObjectNPage(header);
                idobjgetSchema = true;
            } else if (header.getIdObj() == 3 && header.getType() == 1&&!idobjget3) {
                idobj3Pages.add(page);
                getAllObjectNPage(header);
                idobjget3 = true;
            }
            if (idobjget3&&idobjget5&&idobjget7&&idobjgetSchema){
                break;
            }
        }
        versionNum = HexUtil.int2(read[9], 100);
        System.out.println("内部版本号为" + versionNum);
        long endTime = System.currentTimeMillis();
        System.out.println("文件初始化耗时:" + (endTime - startTime) + "ms");
    }

    public static byte[][] getPages() {
        return read;
    }

    /**
     * 通过objectID获取所需页面，十分低效
     * @param pageid
     * @return
     * @throws IOException
     */
    public static byte[] pageSelecterByObjid(long pageid) throws IOException {
        for (byte[] page : read) {
            PageHeader header = new PageHeader(page);
            if (header.getPageId() == pageid) {
                return page;
            }
        }
        return null;
    }

    /**
     * 返回指定页码的页面，当页面在读入内存的部分时就直接读取，否则就通过
     * 文件偏移读取
     *
     * @param num 指定的页码
     * @return
     */
    public static byte[] getPagebyPageNum(int num) {
        try {
            if (num < BORDERPAGE) {
                return read[num];
            } else {
                //直接按流读取
                FileInputStream fileInputStream = new FileInputStream(file);
                fileInputStream.skip((long) num * 8192L);
                byte[] aimPage = new byte[8192];
                fileInputStream.read(aimPage);
                fileInputStream.close();
                return aimPage;
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        throw new RuntimeException("查找页面出错");
    }

    /**
     * 返回文件的页数
     *
     * @return
     */
    public static int getPageNumber() {
        int pagenumber = (int) (file.length() / 8192);
        return pagenumber;
    }

    /**
     * 通过Tableid获取rowsetid
     * @param tableId
     * @return
     * @throws IOException
     */
    public static List<Map<String, String>> getRowSetIdByTableId(String tableId) throws IOException {
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
        List<Map<String, String>> maps = new ArrayList<>();
        PageHeader header;
        List<byte[]> records;
        for (byte[] idobj5Page : idobj5Pages) {
            header = new PageHeader(idobj5Page);
            records = RecordCuter.cutRrcord(idobj5Page,header.getSlotCnt());
            maps = RawColumnParser.parserRecord(records, list,CheckSum.pageCheckSum(idobj5Page));
        }

        for (Map<String, String> map : maps) {
            if (map.get("idmajor").equals(tableId)) {
                return maps;
            }
        }
        return null;
    }

    /**
     * 通过ownerid（也就是rowsetid）查找auid
     * @param rowsetId
     * @return
     * @throws IOException
     */
    public static Map<String, String> getId7ObjPage(String rowsetId) throws IOException {
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
        List<Map<String, String>> maps = new ArrayList<>();
        PageHeader header;
        List<byte[]> records;
        for (byte[] idobj7Page : idobj7Pages) {
            header = new PageHeader(idobj7Page);
            records = RecordCuter.cutRrcord(idobj7Page,header.getSlotCnt());
            maps = RawColumnParser.parserRecord(records, list,CheckSum.pageCheckSum(idobj7Page));
        }
                for (Map<String, String> map : maps) {
                    if (map.get("ownerid").equals(rowsetId) && map.get("type").equals("1")) {
                        return map;
                    }
                }
        throw new RuntimeException("并未在OBJ7页里找到相关的记录");
    }

    /**
     * 通过tableID查找到对应的schema
     * @param tableId
     * @return
     */
    public static Map<Long, SchemaRecord> getTableSchema(String tableId) {
        Map<Long, SchemaRecord> schemaMap = new HashMap<>(16);
        for (schema.SchemeaPage SchemeaPage : schemaPages) {
            List<SchemaRecord> records = SchemeaPage.getRecords();
            for (SchemaRecord record : records) {
                long tableid = Long.parseLong(tableId);
                if (record.getTableId() == tableid) {
                    schemaMap.put(record.getColumnid(), record);
                }
            }
        }
        return schemaMap;
    }

    /**
     * 通过rowsetid查找到对应每个字段的相关信息
     * @param rowsetid
     * @return
     * @throws IOException
     */
    public static Map<Integer, Integer> id3objPageRecords(String rowsetid) throws IOException {
        Map<Integer, Integer> colMap = new HashMap<>();
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
        list.add(new RawVarBinary("colguid", 16));
        List<Map<String, String>> maps = new ArrayList<>();
        PageHeader header;
        List<byte[]> records;
        for (byte[] idobj3Page : idobj3Pages) {
            header = new PageHeader(idobj3Page);
            records = RecordCuter.cutRrcord(idobj3Page,header.getSlotCnt());
            maps = RawColumnParser.parserRecord(records, list,CheckSum.pageCheckSum(idobj3Page));
        }
            for (Map<String, String> map : maps) {
                if (map.get("rsid").equals(rowsetid)) {
                    //key是逻辑顺序，value是物理顺序
                    colMap.put(Integer.valueOf(map.get("resolid")), Integer.valueOf(map.get("hbcolid")));
                }
            }
        return colMap;
    }

    /**
     * 通过code生成schema类
     *
     * @param code
     * @param schemaRecord
     * @return
     */
    public static Ischema schemaBuilder(int code, SchemaRecord schemaRecord) {
        switch (code) {
            case 56:
                return new RawInt(schemaRecord.getSchemaName());
            case 175:
                return new RawChar(schemaRecord.getSchemaName(), schemaRecord.getLength());
            case 35:
                return new RawText(schemaRecord.getSchemaName());
            case 167:
                return new RawVarchar(schemaRecord.getSchemaName(), schemaRecord.getLength());
            case 61:
                return new RawDateTime(schemaRecord.getSchemaName());
            case 62:
                return new RawFloat(schemaRecord.getSchemaName(), schemaRecord.getLength());
            case 239:
                return new RawNChar(schemaRecord.getSchemaName(), schemaRecord.getLength());
            case 127:
                return new RawBigInt(schemaRecord.getSchemaName());
            case 60:
                return new RawMoney(schemaRecord.getSchemaName());
            case 231:
                return new RawNVarchar(schemaRecord.getSchemaName(), schemaRecord.getLength());
            case 99:
                return new RawNText(schemaRecord.getSchemaName());
            case 40:
                return new RawDate(schemaRecord.getSchemaName());
            case 58:
                return new RawSmallDateTime(schemaRecord.getSchemaName());
            case 104:
                return new RawBit(schemaRecord.getSchemaName());
            case 48:
                return new RawTinyint(schemaRecord.getSchemaName());
            case 173:
                return new RawBinary(schemaRecord.getSchemaName(), schemaRecord.getLength());
            case 165:
                return new RawVarBinary(schemaRecord.getSchemaName(), schemaRecord.getLength());
            case 52:
                return new RawSmallInt(schemaRecord.getSchemaName());
            case 36:
                return new RawUniqueidentifier(schemaRecord.getSchemaName());
            case 106:
                return new RawDecimal(schemaRecord.getSchemaName(), schemaRecord.getLength(), schemaRecord.getPrec(), schemaRecord.getScale());
            default:
                throw new RuntimeException("类型" + code + "暂时不被支持");
        }
    }

    /**
     * 将schema列表的逻辑顺序调整为物理顺序
     *
     * @param ischemaList
     * @param sortmap
     */
    public static void schemaSorter(List<Ischema> ischemaList, Map<Integer, Integer> sortmap) {
        Ischema[] ischemas = new Ischema[ischemaList.size()];
        for (int i = 1; i <= sortmap.size(); i++) {
            try {
                ischemas[sortmap.get(i) - 1] = ischemaList.get(i - 1);
            } catch (Exception e) {
                System.out.println("正在修正行数量");
            }

        }
        ischemaList.clear();
        for (Ischema ischema : ischemas) {
            ischemaList.add(ischema);
        }
    }

    /**
     * 通过IAM页的第一页获取之后的所有页面
     * @param iamPage
     * @return
     * @throws IOException
     */
    public static List<byte[]> findAllIamPage(byte[] iamPage) throws IOException {
        List<byte[]> allIamPage = new ArrayList<>();
        PageHeader header = new PageHeader(iamPage);
        allIamPage.add(iamPage);
        while (header.getNextPage() != 0) {
            iamPage = getPagebyPageNum((int) header.getNextPage());
            header = new PageHeader(iamPage);
            allIamPage.add(iamPage);
        }
        return allIamPage;
    }

    /**
     * 获取内部版本号
     * @return
     */
    public static int getVersionNum() {
        return versionNum;
    }

    /**
     * 顺着链式结构寻找所有页面
     * @param header
     */
    public static void getAllObjectNPage(PageHeader header) {
        PageHeader temp = header;
        byte[] pagebyPageNum;
        while (header.getPrevPage() != 0) {
            pagebyPageNum = PageUtils.getPagebyPageNum((int) header.getPrevPage());
            switch (header.getIdObj().intValue()) {
                case 7:
                    idobj7Pages.add(pagebyPageNum);
                    break;
                case 5:
                    idobj5Pages.add(pagebyPageNum);
                    break;
                case 41:
                    schemaPages.add(new SchemeaPage(pagebyPageNum));
                    break;
                case 3:
                    idobj3Pages.add(pagebyPageNum);
                    break;
                default:
                    System.out.println("初始化出错");
            }
            header = new PageHeader(pagebyPageNum);
        }
        header = temp;
        while (header.getNextPage() != 0) {
            pagebyPageNum = PageUtils.getPagebyPageNum((int) header.getNextPage());
            switch (header.getIdObj().intValue()) {
                case 7:
                    idobj7Pages.add(pagebyPageNum);
                    break;
                case 5:
                    idobj5Pages.add(pagebyPageNum);
                    break;
                case 41:
                    schemaPages.add(new SchemeaPage(pagebyPageNum));
                    break;
                case 3:
                    idobj3Pages.add(pagebyPageNum);
                    break;
                default:
                    System.out.println("初始化出错");
            }
            header = new PageHeader(pagebyPageNum);
        }
    }
}
