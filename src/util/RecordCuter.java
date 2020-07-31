package util;

import java.util.ArrayList;
import java.util.List;


public class RecordCuter {
    /**
     * 这个方法负责切割页面里的记录，使其变为一条一条的
     * @param page 拥有记录的页面
     * @param recordCounter 当前页面里有几条
     * @return 返回值为一条一条的单个记录
     */
    public static List<byte[]> cutRrcord(byte[] page,int recordCounter){
        int j = 0;
        List<byte[]> records = new ArrayList<>();
        //依次读取每个slot槽
        for (int i = 8190; i>=8192-recordCounter*2 ; i=i-2){
            //这个代表的是每行的结束位置
            int endOffset = 0;
            //这是每行的开始位置
            int startOffset = HexUtil.int2(page,i);
            if (startOffset>8192){
                continue;
            }
            if (startOffset==0){
                continue;
            }
            //每条记录行数的offset
            int offsetNumOfColum = HexUtil.int2(page,startOffset+2);
            //每条记录的行数
            int numOfColumn = HexUtil.int2(page,startOffset+offsetNumOfColum);
            //每条记录第一位状态为，通过其可以判断是否
            byte status = page[startOffset];
            int numOfVariable = 0;
            int length = 0;
            if (((status >> 5) & 0x1)==0){
                length = offsetNumOfColum+(numOfColumn/8)+3;
               byte[] record = new byte[length];
               //将record的字节数组拷贝出来
                System.arraycopy(page, startOffset, record, 0, length);
                records.add(record);
            }else {
                //可变长列的偏移位置开始点
                int variableOffset = startOffset+offsetNumOfColum+1+(1+(numOfColumn-1)/8)+1;
                //可变长度的列数
                numOfVariable = HexUtil.int2(page,variableOffset);
                for (int k = numOfVariable; k >0 ; k--) {
                    int temp = HexUtil.int2(page,variableOffset+2);
                    if (temp>8192){
                        temp -= 32768;
                    }
                    if (temp>endOffset){
                        endOffset =temp;
                    }
                    //指针向前移
                    variableOffset+=2;
                }
                //计算出此记录的结尾点
                endOffset+=startOffset;
                length = endOffset - startOffset;
                byte[] record = new byte[length];
                System.arraycopy(page, startOffset, record, 0, length);
                records.add(record);
            }
            j++;
        }


        return records;
    }
}
