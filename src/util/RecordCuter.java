package util;

import java.util.ArrayList;
import java.util.List;

public class RecordCuter {
    public static List<byte[]> cutRrcord(byte[] page,int recordCounter){
        int j = 0;

        List<byte[]> records = new ArrayList<>();
        for (int i = 8190; i>=8192-recordCounter*2 ; i=i-2){
            int endOffset = 0;
            int startOffset = HexUtil.int2(page,i);
            if (startOffset>8192){
                continue;
            }
            if (startOffset==0){
                continue;
            }
            int offsetNumOfColum = HexUtil.int2(page,startOffset+2);
            int numOfColumn = HexUtil.int2(page,startOffset+offsetNumOfColum);
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
                numOfVariable = HexUtil.int2(page,variableOffset);
                for (int k = numOfVariable; k >0 ; k--) {
                    int temp = HexUtil.int2(page,variableOffset+2);
                    if (temp>8192){
                        temp -= 32768;
                    }
                    if (temp>endOffset){
                        endOffset =temp;
                    }
                    variableOffset+=2;
                }
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
