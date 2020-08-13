package util;

import java.util.ArrayList;
import java.util.List;


public class RecordCuter {
    /**
     * 这个方法负责切割页面里的记录，使其变为一条一条的
     * @param page 拥有记录的页面
     * @param recordCounter 当前页面里有几条记录
     * @return 返回值为一条一条的单个记录
     */
    public static List<byte[]> cutRrcord(byte[] page,int recordCounter){
        List<byte[]> records = new ArrayList<>();
        //依次读取每个slot槽
        for (int i = 8190; i>=8192-recordCounter*2 ; i=i-2){
            //这是每行的开始位置
            int startOffset = HexUtil.int2(page,i);
            if (startOffset>8192){
                continue;
            }
            if (startOffset==0){
                continue;
            }
            if (((page[startOffset] >> 0) & 0x1)==0){
                records.add(cutNoramalRecord(page,startOffset));
            }else {
                records.add(cutRowCompressRecord(page,startOffset));
            }
        }
        return records;
    }

    /**
     * 切割普通的数据
     * @param page
     * @param startOffSet
     * @return
     */
    private static byte[] cutNoramalRecord(byte[] page,int startOffSet){
        //这个代表的是每行的结束位置
        int endOffset = 0;
        //每条记录行数的offset
        int offsetNumOfColum = HexUtil.int2(page,startOffSet+2);
        //每条记录的行数
        int numOfColumn = HexUtil.int2(page,startOffSet+offsetNumOfColum);
        //每条记录第一位状态为，通过其可以判断是否
        byte status = page[startOffSet];
        int numOfVariable = 0;
        int length = 0;
        if (((status >> 5) & 0x1)==0){
            length = offsetNumOfColum+(numOfColumn/8)+3;
            byte[] record = new byte[length];
            //将record的字节数组拷贝出来
            System.arraycopy(page, startOffSet, record, 0, length);
            return record;
        }else {
            //可变长列的偏移位置开始点
            int variableOffset = startOffSet+offsetNumOfColum+1+(1+(numOfColumn-1)/8)+1;
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
            endOffset+=startOffSet;
            length = endOffset - startOffSet;
            byte[] record = new byte[length];
            System.arraycopy(page, startOffSet, record, 0, length);
            return record;
        }
    }

    /**
     * 切割行压缩数据
     * @param page
     * @param startOffSet
     * @return
     */
    private static  byte[] cutRowCompressRecord(byte[] page, int startOffSet){
        //这个代表的是每行的结束位置
        int endOffset = 0;
        //记录总共有多少列
        int numOfColumn =0;
        //行长度记录部位的offset
        int lengthOffset = 0;
        if (((page[startOffSet+1] >> 7) & 0x1)==0){
            numOfColumn=page[startOffSet+1];
            endOffset = startOffSet+2;
            lengthOffset = startOffSet+2;
        }else {
            numOfColumn = HexUtil.int2(page,startOffSet+1);
            endOffset = startOffSet+3;
            lengthOffset = startOffSet+3;
        }
        if (numOfColumn==0){
            //如果列为0，则是固定九个字长度
            int length = 9;
            byte[] record = new byte[length];
            System.arraycopy(page, startOffSet, record, 0, length);
            return record;
        }
        int bytesNumOfLengthOffset = (int)Math.ceil((double)numOfColumn/2);
        endOffset+=bytesNumOfLengthOffset;
        int shortLength = 0;
        for (int i = lengthOffset ;i<lengthOffset+bytesNumOfLengthOffset ; i++) {
            int low4Bit = HexUtil.getLow4Bit(page[i]);
            if (2<=low4Bit&&low4Bit<=9){
                shortLength+=(low4Bit-1);
            }else if(low4Bit>=11){
                //表示页压缩中的一个值，暂时不打算支持
            }
            int height4Bit = HexUtil.getHeight4Bit(page[i]);
            if (2<=height4Bit&&height4Bit<=9){
                shortLength+=(height4Bit-1);
            }else if(height4Bit>=11){
                //表示页压缩中的一个值，暂时不打算支持
            }
        }
        //此时endOffset来到了变长列的开端
        endOffset+=shortLength;
        if (page[endOffset]!=1&&shortLength==0){
            //如果列为0，则是固定九个字长度
            int length = 9;
            byte[] record = new byte[length];
            System.arraycopy(page, startOffSet, record, 0, length);
            return record;
        }
        //长字段有几个字节
        int numOfLongRecord = 0;
        //当长字节为0的时候，有时候长字段可能会为65535
        int noLongRecord = 65535;
        if (((page[endOffset+1] >> 7) & 0x1)==0){
            lengthOffset = endOffset+2;
             numOfLongRecord = page[endOffset+1];
            endOffset+=2;
        }else {
            lengthOffset = endOffset+3;
            numOfLongRecord = HexUtil.int2(page,endOffset+1);
            if (numOfLongRecord==noLongRecord){
                //此处明明应该置0，但是为了能把记录剪切完，所以置为1
                numOfLongRecord=1;
            }
            endOffset+=3;

        }
        int longLength = 0;
        for (int i = lengthOffset; i <lengthOffset+numOfLongRecord*2 ; i=i+2) {
            int templength = HexUtil.normalInt2(page, i);
            //如果长度大于32768，那么肯定是因为primary位为1，所以应该减去
            templength = ((templength << 17) >>> 17);
            //如果大于8192说明其为空
            if (templength>8192){
                templength=-1;
            }
            endOffset+=2;
            longLength+=templength;
        }
        endOffset+=longLength+1;
        int length = endOffset-startOffSet;
        byte[] record = new byte[length];
        System.arraycopy(page, startOffSet, record, 0, length);
        return record;
    }
}
