package util;

public class recordCuter {
    public static byte[][] cutRrcord(byte[] page,int recordCounter){

        int j = 0;
        int endOffset = 0;
        byte[][] records = new byte[recordCounter][];
        for (int i = 8190; i>=8192-recordCounter*2 ; i=i-2){
            int startOffset =hexUtil.int2(page,i);
            if (startOffset>8192){
                continue;
            }
            int offsetNumOfColum = hexUtil.int2(page,startOffset+2);
            int numOfColumn = hexUtil.int2(page,startOffset+offsetNumOfColum);
            byte status = page[startOffset];
            int numOfVariable = 0;
            int length = 0;
            if (((status >> 5) & 0x1)==0){
                length = offsetNumOfColum+(numOfColumn/8)+3;
                records[j] = new byte[length];
                System.arraycopy(page, startOffset, records[j], 0, length);//将record的字节数组拷贝出来
            }else {
                int variableOffset = startOffset+offsetNumOfColum+1+(1+numOfColumn/8)+1; //可变长列的偏移位置开始点
                numOfVariable = hexUtil.int2(page,variableOffset);
                for (int k = numOfVariable; k >0 ; k--) {
                    int temp = hexUtil.int2(page,variableOffset+2);
                    if (temp>8192){
                        endOffset = page[variableOffset+2];
                    }else {
                        endOffset=temp;
                    }
                    variableOffset+=2;
                }
                endOffset+=startOffset;
                length = endOffset - startOffset;
                records[j] = new byte[length];
                System.arraycopy(page, startOffset, records[j], 0, length);
            }
            j++;
        }


        return records;
    }
}
