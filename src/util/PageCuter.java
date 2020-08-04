package util;

import java.io.*;
import java.util.Arrays;

public class PageCuter {
    /**
     * 这是一个边界值，是我自己设定的，实际可以根据内存大小自己设定
     * 当文件大小大于此边界值的时候，就会先只读取这么多文件进入内存
     * 换算为数据页面数位71680页
     */
    public static final int borderSize = 587202560;
    public static final int borderPage= 71680;
    public static  File file = null;
    /**
     * 读取数据
     * @param path 文件的路径
     * @return
     * @throws IOException
     */
    public static byte[][] read(String path) throws IOException {
       file = new File(path);

        long fileSize=0;
        if (file.length()> borderSize){
            fileSize = borderSize;
        }else {
            fileSize = file.length();
        }

        FileInputStream fi = new FileInputStream(file);
        byte[] buffer = new byte[(int) fileSize];
        int offset = 0;
        int numRead = 0;
        while (offset < buffer.length
                && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
            offset += numRead;
        }
        // 确保所有数据均被读取
        if (offset != buffer.length) {
            throw new IOException("Could not completely read file "
                    + file.getName());
        }
        fi.close();
        byte[][] pages = splitBytes(buffer, 8192);
        return pages;
    }

    /**
     * 将mdf文件按8192字节大小分割
     * @param bytes
     * @param size
     * @return
     */
    public static byte[][] splitBytes(byte[] bytes, int size) {
        double splitLength = Double.parseDouble(size + "");
        int arrayLength = (int) Math.ceil(bytes.length / splitLength);
        byte[][] result = new byte[arrayLength][];
        int from, to;
        for (int i = 0; i < arrayLength; i++) {

            from = (int) (i * splitLength);
            to = (int) (from + splitLength);
            if (to > bytes.length){
                to = bytes.length;}
            result[i] = Arrays.copyOfRange(bytes, from, to);
        }
        return result;
    }
}
