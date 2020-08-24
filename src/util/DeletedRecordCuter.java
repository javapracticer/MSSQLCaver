package util;

import java.util.ArrayList;
import java.util.List;

public class DeletedRecordCuter {
        public static List<byte[]> cutRrcord(byte[] page,int freeData){
            List<byte[]> records = new ArrayList<>();
            int startOffset = 96;
            int j = 0;
            int endOffset = 0;
            while (startOffset < freeData) {
                //计算出储存行数的位置的偏移
                int offsetNumOfColum = HexUtil.int2(page, startOffset + 2);
                int numOfColumn = HexUtil.int2(page, startOffset + offsetNumOfColum);
                byte status = page[startOffset];
                int numOfVariable = 0;
                int length = 0;
                if (((status >> 5) & 0x1)==0){
                    length = offsetNumOfColum+(numOfColumn/8)+3;
                     byte[] record = new byte[length];
                     //将record的字节数组拷贝出来
                    System.arraycopy(page, startOffset, record, 0, length);
                    records.add(record);
                    startOffset+=length;
                }else {
                    //可变长列的偏移位置开始点
                    int variableOffset = startOffset+offsetNumOfColum+1+(1+numOfColumn/8)+1;
                    //可变长列的数量
                    numOfVariable = HexUtil.int2(page,variableOffset);
                    for (int k = numOfVariable; k >0 ; k--) {
                        int temp = HexUtil.int2(page,variableOffset+2);
                        if (temp>8192) {
                            temp -= 32768;

                        }
                        temp += startOffset;
                        if (temp>endOffset){
                            endOffset =temp;
                        }

                        variableOffset+=2;
                    }
                    length = endOffset - startOffset;
                    byte[] record = new byte[length];
                    System.arraycopy(page, startOffset, record, 0, length);
                    records.add(record);
                    startOffset+=length;
                }
            }
            return records;
        }
}
