package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 是一个腐败类，主要作用用于随机的腐蚀数据库中的一些页面
 * 请勿在生产数据库中使用此类
 */
public class CorruptUtils {
    public static void destoryedPageIDs(String filePath,double rate,int fileNum) throws IOException {
        File file = new File(filePath);
        int fileSize = 0;
        fileSize = (int) file.length();

        FileInputStream fi = new FileInputStream(file);
        byte[] buffer = new byte[ fileSize];
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
        int totalNum = (fileSize/8192);
        int corruptNum = (int) (totalNum*rate);
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i <corruptNum ; i++) {
            list.add(new Random().nextInt(totalNum));
        }
        for (Integer integer : list) {
            int start = integer*8192;
            for (int i = start; i <start+8192 ; i++) {
                buffer[i] = 0X00;
            }
        }
        File file2 = new File("C:\\Users\\s6560\\Documents\\sqlsample\\corruptsample\\"+fileNum+".mdf");
        try {

            if (!file.exists()) {
                file.createNewFile();
            }
            OutputStream out = new BufferedOutputStream(new FileOutputStream(file2));
            out.write(buffer);
            out.close();
            System.out.println("输出完成");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}