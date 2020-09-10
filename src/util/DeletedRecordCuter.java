package util;

import domain.PageHeader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeletedRecordCuter {
        public static List<byte[]> cutRrcord(byte[] page,int freeData){
            List<byte[]> records = new ArrayList<>();
            int startOffset = 96;
            int length = 0;
            byte[] record ;
            Set<Integer> startOffsetSet = new HashSet<>();
            while (startOffset < freeData) {
                try {
                    if (((page[startOffset] >> 0) & 0x1)==0) {
                        length = cutNormalDeleteRecord(page, startOffset);
                    }else{
                        length = cutRowCompressDeleteRecord(page,startOffset);
                    }
                }catch (Exception e){
                    PageHeader header = new PageHeader(page);
                    for (int i = 8190; i>=8192-header.getSlotCnt()*2 ; i=i-2){
                        if (startOffsetSet.contains(HexUtil.int2(page,i))){
                            continue;
                        }
                        //这是每行的开始位置
                        int tempstartOffset = HexUtil.int2(page,i);
                        if (tempstartOffset>8192){
                            continue;
                        }
                        if (tempstartOffset==0){
                            continue;
                        }
                        if (((page[tempstartOffset] >> 0) & 0x1)==0){
                            length = cutNormalDeleteRecord(page,tempstartOffset);
                        }else {
                            length = cutRowCompressDeleteRecord(page,tempstartOffset);
                        }
                        record = new byte[length];
                        //将record的字节数组拷贝出来
                        System.arraycopy(page, tempstartOffset, record, 0, length);
                        records.add(record);
                    }
                    return records;
                }
               startOffsetSet.add(startOffset);
                record = new byte[length];
                //将record的字节数组拷贝出来
                System.arraycopy(page, startOffset, record, 0, length);
                records.add(record);
                startOffset += length;
            }
            return records;
        }
    private static int cutNormalDeleteRecord(byte[] page,int startOffset){
        int endOffset = 0;
        //计算出储存行数的位置的偏移
        int offsetNumOfColum = HexUtil.int2(page, startOffset + 2);
        if (offsetNumOfColum==8481){
            return 0;
        }
        int numOfColumn = HexUtil.int2(page, startOffset + offsetNumOfColum);
        byte status = page[startOffset];
        int numOfVariable = 0;
        int length = 0;
        if (((status >> 5) & 0x1)==0){
            length = offsetNumOfColum+((numOfColumn-1)/8)+3;
            return length;
        }else {
            //可变长列的偏移位置开始点
            int variableOffset = startOffset+offsetNumOfColum+((numOfColumn-1)/8)+3;
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
            return length;
        }
    }
    private static  int cutRowCompressDeleteRecord(byte[] page, int startOffSet){
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
            return length;
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
        if (page[endOffset]!=1){
            int length = 9;
            return length;
        }
        if (page[endOffset]!=1&&shortLength==0){
            //如果列为0，则是固定九个字长度
            int length = 9;

            return length;
        }
        //长字段有几个字节
        int numOfLongRecord = 0;
        //当长字节为0的时候，有时候长字段可能会为65535
        int noLongRecord = 65535;
        if (((page[endOffset+1] >> 7) & 0x1)==0){
            lengthOffset = endOffset+3;
            numOfLongRecord = page[endOffset+1];
            endOffset+=2;
        }else {
            lengthOffset = endOffset+4;
            numOfLongRecord = HexUtil.int2(page,endOffset+1);
            if (numOfLongRecord==noLongRecord){
                //此处明明应该置0，但是为了能把记录剪切完，所以置为1
                numOfLongRecord=1;
            }
            endOffset+=3;

        }
        int longLength = 0;
        for (int i = lengthOffset; i <lengthOffset+numOfLongRecord*2 ; i=i+2) {
            int templength = HexUtil.int2(page, i);
            //如果长度大于32768，那么肯定是因为primary位为1，所以应该减去
            templength = ((templength << 17) >>> 17);
            //如果大于8192说明其为空
            if (templength>8192){
                templength=-1;
            }
            endOffset+=2;
            if (templength>longLength){
                longLength = templength;
            }
        }
        endOffset+=longLength+1;
        int length = endOffset-startOffSet;
        return length<9 ? 9:length;
    }
}
