package util;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

/**
 * 工具类，主要是按字节来处理mdf文件里的数据
 */
public class HexUtil {
    /**
     * 处理两个字节的数据，将其转为int，由于数据有大小端的问题
     * 所以需要倒着拼接
     *
     * @param bytes
     * @param offset
     * @return
     */
    public static int int2(byte[] bytes, int offset) {
        String int2 = "";
        for (int i = offset + 1; i >= offset; i--) {
            String s = Integer.toHexString(bytes[i] & 0xff);
            if ((bytes[i] & 0xff) < 16) {
                s = "0" + s;
            }
            int2 += s;
        }
        return Integer.valueOf(int2, 16);
    }

    /**
     * 处理四个字节的数据，将其转为int，由于数据有大小端的问题
     * 所以需要倒着拼接
     *
     * @param bytes
     * @param offset
     * @return
     */
    public static long int4(byte[] bytes, int offset) {
        String int4 = "";
        for (int i = offset + 3; i >= offset; i--) {
            String s = Integer.toHexString(bytes[i] & 0xff);
            if ((bytes[i] & 0xff) < 16) {
                s = "0" + s;
            }
            int4 += s;
        }
        return Long.valueOf(int4, 16);
    }

    /**
     * 处理六个字节的数据，将其转为int，由于数据有大小端的问题
     * 所以需要倒着拼接，且由于其是6字节，要用long类型储存
     *
     * @param bytes
     * @param offset
     * @return
     */
    public static long int6(byte[] bytes, int offset) {
        String int6 = "";
        for (int i = offset + 5; i >= offset; i--) {
            String s = Integer.toHexString(bytes[i] & 0xff);
            if ((bytes[i] & 0xff) <= 16) {
                s = "0" + s;
            }
            int6 += s;
        }
        return Long.valueOf(int6, 16);
    }

    /**
     * 处理8字节的数据，转为int
     *
     * @param bytes
     * @param offset
     * @return
     */
    public static BigInteger int8(byte[] bytes, int offset) {
        String int8 = "";
        for (int i = offset + 7; i >= offset; i--) {
            String s = Integer.toHexString(bytes[i] & 0xff);
            if ((bytes[i] & 0xff) < 16) {
                s = "0" + s;
            }
            int8 += s;
        }
        BigInteger bigInteger = new BigInteger(int8, 16);
        return bigInteger;
    }

    /**
     * 读取两个字节的内容，将其转为16进制，并且补齐0
     *
     * @param bytes
     * @param offset
     * @return
     */
    public static String hex2(byte[] bytes, int offset) {
        String hex2 = "";
        for (int i = offset + 1; i >= offset; i--) {
            String s = Integer.toHexString(bytes[i] & 0xff);
            if ((bytes[i] & 0xff) <= 16) {
                s = "0" + s;
            }
            hex2 += s;
        }
        return hex2;
    }

    public static String recordHex2(byte[] bytes, int offset) {
        String hex2 = "";
        for (int i = offset; i <= offset + 1; i++) {
            String s = Integer.toHexString(bytes[i] & 0xff);
            if ((bytes[i] & 0xff) <= 16) {
                s = "0" + s;
            }
            hex2 += s;
        }
        return hex2;
    }

    /**
     * 解析ascii码
     *
     * @param page
     * @param start ascii开始
     * @param end   ascii 结束
     * @return
     */
    public static String parseString(byte[] page, int start, int end) {
        StringBuilder hex = new StringBuilder("");
        for (int i = start; i < end; i = i + 2) {
            hex.append(HexUtil.getNormalHex(page,i,i+1));
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += 4) {
            String h = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(h, 16);
            sb.append((char) decimal);
        }
        return sb.toString();
    }

    /**
     * 解析记录当中的字符
     *
     * @param page
     * @param start
     * @param end
     * @return
     */
    public static String parseRecordString(byte[] page, int start, int end) throws UnsupportedEncodingException {
        byte[] baKeyword = new byte[end - start + 1];
        int j = 0;
        for (int i = start; i <= end; i++) {
            baKeyword[j] = page[i];
            j++;
        }
        String result = new String(baKeyword,"GBK");
        return result;
//        StringBuilder hex = new StringBuilder("");
//        for (int i = start; i <=end; i=i+2) {
//            hex.append(HexUtil.recordHex2(page, i));
//        }
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < hex.length() - 1; i += 2) {
//            String h = hex.substring(i, (i + 2));
//            if (h.equals("20")){
//                continue;
//            }
//            int decimal = Integer.parseInt(h, 16);
//            sb.append((char) decimal);
//        }
//        return sb.toString();
    }

    public static void recordIndexParser(byte[] record, int startoffset, int endoffset) throws IOException {
        long pageId = HexUtil.int6(record, startoffset + 8);
        int slot = HexUtil.int2(record, startoffset + 14);
        byte[] page = PageUtils.pageSelecterByObjid(pageId);
        LobRecordParser.parserLobRecord(page, slot);
    }

    /**
     * 获取16进制形式的数据，由于大小端问题，所以是倒着获取
     * @param record
     * @param start
     * @param end
     * @return
     */
    public static String getHex(byte[] record, int start, int end) {
        StringBuilder hex = new StringBuilder("");
        for (int i = end; i >= start; i--) {
            String s = Integer.toHexString(record[i] & 0xff);
            if ((record[i] & 0xff) < 16) {
                s = "0" + s;
            }
            hex.append(s);
        }
        return hex.toString();
    }

    /**
     * 获取一个字节低位四位的数
     * @param aByte
     * @return
     */
    public static int getLow4Bit(byte aByte){
        int low;
        low = (aByte & 0x0f);
        return low;
    }

    /**
     * 获取一个字节高四位的数
     * @param aByte
     * @return
     */
    public static int getHeight4Bit(byte aByte){//获取高四位
        int height;
        height = ((aByte & 0xf0) >> 4);
        return height;
    }

    /**
     * 获取正着读取的int2数据
     * @param data
     * @param offset
     * @return
     */
    public static int normalInt2(byte[] data, int offset){
        String int2 = "";
        for (int i = offset; i <= offset+1; i++) {
            String s = Integer.toHexString(data[i] & 0xff);
            if ((data[i] & 0xff) < 16) {
                s = "0" + s;
            }
            int2 += s;
        }
        return Integer.valueOf(int2, 16);
    }

    /**
     * 获取正着读取的int4数据
     * @param data
     * @param offset
     * @return
     */
    public static int normalInt4(byte[] data, int offset){
        String int4 = "";
        for (int i = offset; i <= offset+3; i++) {
            String s = Integer.toHexString(data[i] & 0xff);
            if ((data[i] & 0xff) < 16) {
                s = "0" + s;
            }
            int4 += s;
        }
        System.out.println(Integer.valueOf(int4,16));
        return Integer.valueOf(int4, 16);
    }

    /**
     * 获取从左到右读的hex数据
     * @param record
     * @param start
     * @param end
     * @return
     */
    public static String getNormalHex(byte[] record, int start, int end) {
        StringBuilder hex = new StringBuilder("");
        for (int i = start; i <= end; i++) {
            String s = Integer.toHexString(record[i] & 0xff);
            if ((record[i] & 0xff) < 16) {
                s = "0" + s;
            }
            hex.append(s);
        }
        return hex.toString();
    }

    /**
     * 此方法用来操作输入一个固定byte数组，将其倒序拼借起来
     * @return
     */
    public static long ToUIntX(byte[] bytes){
        String hexx = "";
        for (int i = bytes.length-1; i >= 0; i--) {
            String s = Integer.toHexString(bytes[i] & 0xff);
            if ((bytes[i] & 0xff) <= 16) {
                s = "0" + s;
            }
            hexx += s;
        }
        return Integer.valueOf(hexx,16);
    }

    /**
     *  返回小端四字节的二进制
     * @param data
     * @param offset
     * @return
     */
    public static int binaryInt4(byte[] data,int offset){
        int[] nums = new int[4];
        int k = 0;
        for (int i = 0; i <4 ; i++) {
            nums[i] = data[offset+i] & 0xff;
        }
        k = nums[3];
        for (int i = 2; i>=0 ; i--) {
            k=k<<8;
            k += nums[i];
        }
        return k;
    }
}
