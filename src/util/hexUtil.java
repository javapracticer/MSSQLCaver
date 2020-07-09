package util;

import java.math.BigInteger;

/**
 * 工具类，主要是按字节来处理mdf文件里的数据
 */
public class hexUtil {
    /**
     * 处理两个字节的数据，将其转为int，由于数据有大小端的问题
     * 所以需要倒着拼接
     * @param head
     * @param offset
     * @return
     */
    public static int int2(byte[] head, int offset) {
        String int2 = "";
        for (int i = offset + 1; i >= offset; i--) {
            String s = Integer.toHexString(head[i] & 0xff);
            if ((head[i] & 0xff) < 16) {
                s = "0" + s;
            }
            int2 += s;
        }
        return Integer.valueOf(int2, 16);
    }
    /**
     * 处理四个字节的数据，将其转为int，由于数据有大小端的问题
     * 所以需要倒着拼接
     * @param head
     * @param offset
     * @return
     */
    public static long int4(byte[] head, int offset) {
        String int4 = "";
        for (int i = offset + 3; i >= offset; i--) {
            String s = Integer.toHexString(head[i] & 0xff);
            if ((head[i] & 0xff) <= 16) {
                s = "0" + s;
            }
            int4 += s;
        }
        return Long.valueOf(int4, 16);
    }
    /**
     * 处理六个字节的数据，将其转为int，由于数据有大小端的问题
     * 所以需要倒着拼接，且由于其是6字节，要用long类型储存
     * @param head
     * @param offset
     * @return
     */
    public static long int6(byte[] head, int offset) {
        String int6 = "";
        for (int i = offset + 5; i >= offset; i--) {
            String s = Integer.toHexString(head[i] & 0xff);
            if ((head[i] & 0xff) <= 16) {
                s = "0" + s;
            }
            int6 += s;
        }
        return Long.valueOf(int6, 16);
    }

    /**
     * 处理8字节的数据，转为int
     * @param head
     * @param offset
     * @return
     */
    public static BigInteger int8(byte[] head, int offset) {
        String int8 = "";
        for (int i = offset + 7; i >= offset; i--) {
            String s = Integer.toHexString(head[i] & 0xff);
            if ((head[i] & 0xff) <= 16) {
                s = "0" + s;
            }
            int8 += s;
        }
        BigInteger bigInteger = new BigInteger(int8,16);
        return bigInteger;
    }

    /**
     * 读取两个字节的内容，将其转为16进制，并且补齐0
     * @param head
     * @param offset
     * @return
     */
    public static String hex2(byte[] head, int offset) {
        String hex2 = "";
        for (int i = offset + 1; i >= offset; i--) {
            String s = Integer.toHexString(head[i] & 0xff);
            if ((head[i] & 0xff) <= 16) {
                s = "0" + s;
            }
            hex2 += s;
        }
        return hex2;
    }
    public static String recordHex2(byte[] head, int offset) {
        String hex2 = "";
        for (int i = offset; i <= offset+1; i++) {
            String s = Integer.toHexString(head[i] & 0xff);
            if ((head[i] & 0xff) <= 16) {
                s = "0" + s;
            }
            hex2 += s;
        }
        return hex2;
    }

    /**
     * 解析ascii码
     * @param page
     * @param start ascii开始
     * @param end ascii 结束
     * @return
     */
    public static String parseString(byte[] page, int start, int end) {
        String hex = "";
        for (int i = start; i < end; i = i + 2) {
            hex += hexUtil.hex2(page, i);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += 2) {
            String h = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(h, 16);
            sb.append((char) decimal);
        }
        return sb.toString();
    }

    /**
     * 解析记录当中的字符
     * @param page
     * @param start
     * @param end
     * @return
     */
    public static String parseRecordString(byte[] page, int start, int end) {
        String hex = "";
        for (int i = start; i <=end; i=i+2) {
            hex += hexUtil.recordHex2(page, i);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += 2) {
            String h = hex.substring(i, (i + 2));
            if (h.equals("20")){
                continue;
            }
            int decimal = Integer.parseInt(h, 16);
            sb.append((char) decimal);
        }
        return sb.toString();
    }
    public static void recordIndexParser(){

    }
}