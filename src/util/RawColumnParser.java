package util;

import domain.Ischema;
import domain.PageHeader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 这是一个按行解析数据的类
 */
public class RawColumnParser {
    /**
     * 这是解析数据的主方法，其主要判断数据是否行压缩，并将其传入相应的解析方法内
     * @param records 传入的切割好的数据数组
     * @param list schema的列表
     * @param unbroken 页面是否错误的的布尔值
     * @return 将解析结果作为map并存入list中返回
     * @throws IOException 遇到错误直接向上抛出
     */
    public static List<Map<String,String>> parserRecord(List<byte[]> records, List<Ischema> list,boolean unbroken) throws IOException {
        List<Map<String,String>> recordList = new ArrayList<>();
        int j = 0;
        for (byte[] record : records) {
            if (j == records.size() || record == null) {
                break;
            }
            try {
                if (((record[0] >> 0) & 0x1) == 0) {
                    recordList.add(parserNormalRecord(record, list, unbroken));
                } else {
                    recordList.add(parserRowCompressRecord(record, list, unbroken));
                }
            }catch (Exception e){
                continue;
            }
            j++;
        }
        return recordList;
}

    /**
     * 解析普通的数据
     * @param record 已经切割好的数据
     * @param list schema列表
     * @return 返回一个键值对map
     * @throws IOException
     */
    private static Map<String,String> parserNormalRecord(byte[] record, List<Ischema> list,boolean flag) throws IOException {
        //固定长段的开始点
        int fixdOffset = 4;
        //记录总共有几列记录的offset
        int columnsOffset = HexUtil.int2(record, 2);
        //总共有几列记录
        int numOfColumns = HexUtil.int2(record, columnsOffset);
        int endOffsetPointer =0;
        //记录可变长列结尾的offset
        int variableEndLocation = 0;
        //可变长列的数量
        int variableColumns = 0;
        //可变长列的开始位置
        int startOffsetOfVariableColumn = 0;
        byte b = 0x00;
        int variableHasParase = 0;
        if (((record[0] >> 5) & 0x1)==1){
            //可变长列数
            variableColumns = HexUtil.int2(record, columnsOffset +3+(numOfColumns-1)/8);
            //判断可变长列是否为0
            // 指向可变长结尾的偏移量的偏移量
            endOffsetPointer = columnsOffset +5+(numOfColumns-1)/8;
            //可变长结束的点
            variableEndLocation = HexUtil.int2(record,endOffsetPointer);
            //可变长度的开始节点
            startOffsetOfVariableColumn = endOffsetPointer + variableColumns * 2;
        }
        //读取零位图
        byte[]  nullBitMap = new byte[1+(numOfColumns-1)/8];
        int counter = 0;
        for (int i = columnsOffset +2; i <columnsOffset+3+(numOfColumns-1)/8; i++) {
            nullBitMap[counter] = record[i];
            counter++;
        }
        //用于存放记录的map
        Map<String,String> recordmap = new HashMap<>();
        int i = 0;
        /**
         * 接下来就是激动人心的记录解析部分
         */
        for (Ischema ischema : list) {
            //这一个if语句是专门针对后续临时增加锅行的表
            if (i+1>numOfColumns){
                recordmap.put(ischema.name(), "NULL");
                continue;
            }
            boolean overFlowOrLob = false;
            //以是否是变长字段为分界点
            if (ischema.fixd()==1){
                b = nullBitMap[(int) Math.floor(i/8)];
                int bit = i%8;
                //通过零位图来判断是否是null
                if ((byte) ((b >> bit) & 0x1)==1){
                    recordmap.put(ischema.name(),"NULL");
                    fixdOffset=fixdOffset+ischema.getLength();
                    i++;
                }else {
                    recordmap.put(ischema.name(), String.valueOf(ischema.getValue(record,fixdOffset,fixdOffset+ischema.getLength()-1)));
                    fixdOffset=fixdOffset+ischema.getLength();
                    i++;
                }
            }else {
                Object value = null;
                //先把nullbitmap祭出
                b = nullBitMap[(int) Math.floor(i/8)];
                int bit = i%8;
                //以下是变长字段解析
                //如果还没有到可变长度的头
                if (variableHasParase<variableColumns){
                    //去除地址的大端
                    if (variableEndLocation>8192){
                        variableEndLocation -= 32768;
                        overFlowOrLob = true;
                    }
                    if (ischema.isLOB()){
                        //如果是LOB
                        value = ischema.getValue(record, startOffsetOfVariableColumn, variableEndLocation-1);
                    }else if (overFlowOrLob){
                        //如果不是LOB但是是overFlow
                        value  = ischema.getOverFlowValue(record, startOffsetOfVariableColumn, variableEndLocation-1);
                    }else {
                        //既不是LOB也不是overFlow
                        value = ischema.getValue(record, startOffsetOfVariableColumn, variableEndLocation-1);
                    }

                    if (startOffsetOfVariableColumn==variableEndLocation&&(byte) ((b >> bit) & 0x1)==1){
                        value = "NULL";
                    }else if (startOffsetOfVariableColumn==variableEndLocation&&(byte) ((b >> bit) & 0x1)!=1){
                        value = "NULL";
                    }
                    recordmap.put(ischema.name(), String.valueOf(value));
                    //开始下一轮的解析2
                    startOffsetOfVariableColumn = variableEndLocation;
                    variableHasParase++;
                    endOffsetPointer+=2;
                    if (variableHasParase<variableColumns){
                        variableEndLocation = HexUtil.int2(record,endOffsetPointer);
                    }
                    i++;
                }else {
                    recordmap.put(ischema.name(), "NULL");
                    i++;
                }
            }
        }
        recordmap.put("unbroken", String.valueOf(flag));
        return recordmap;
    }

    /**
     * 解析进行了行压缩的数据
     * @param record 已经切割好的数据
     * @param list schema列表
     * @return 返回一个键值对map
     *
     */
    private static Map<String,String> parserRowCompressRecord(byte[] record, List<Ischema> list,boolean flag) throws IOException {
        //此变量用来记录短记录的offset
        int shortRecordOffset = 0;
        //此变量用来记录全局长度offset
        int allLengthOffset = 0;
        //此变量用来记录长记录的长度offset
        int longLengthOffset = 0;
        //此变量用来记录长记录的开始offset
        int longRecordOffset = 0;
        //长记录开始位置的相对位置
        int longRecordRelativeOffset=0;
        //总共有多少列数据
        int numOfAllRecord = 0;
        //有多少列长数据
        int numOfLongRecord = 0;
        //表明记录全局长度的长度占多少个字节
        int bytesNumOfLengthOffset = 0;
        //记录每一列长度的一个list
        List<Integer> lengths = new ArrayList<>();
        //此变量作为一个临时指针遍历数据的同时把上面的指针指定到正确的位置
        int temp = 1;
        if(((record[temp] >> 7) & 0x1)==0){
            allLengthOffset += 2;
            numOfAllRecord = record[temp] & 0xff;
            bytesNumOfLengthOffset = (int)Math.ceil((double)numOfAllRecord/2);
            temp += bytesNumOfLengthOffset+1;
        }else {
            allLengthOffset+=3;
            numOfAllRecord = HexUtil.normalInt2(record,temp);
            bytesNumOfLengthOffset = (int)Math.ceil((double)numOfAllRecord/2);
            temp+=bytesNumOfLengthOffset+2;
        }
        shortRecordOffset = temp;
        //短数据占的总长度
        int shortLength = 0;
        //获取低4位和高4位的值
        for (int i = allLengthOffset; i <allLengthOffset+bytesNumOfLengthOffset ; i++) {
            int low4Bit = HexUtil.getLow4Bit(record[i]);
            if (1<=low4Bit&&low4Bit<10){
                shortLength+=(low4Bit-1);
                lengths.add(low4Bit-1);
            }else if (low4Bit>=10){
                lengths.add(low4Bit-1);
            }else if (low4Bit==0){
                lengths.add(low4Bit-1);
            }
            int height4Bit = HexUtil.getHeight4Bit(record[i]);
            if (1<=height4Bit&&height4Bit<10){
                shortLength+=(height4Bit-1);
                lengths.add(height4Bit-1);
            }else if (height4Bit==10){
                lengths.add(height4Bit-1);
            }else if (height4Bit==0){
                lengths.add(height4Bit-1);
            }
        }
        //此时temp到达了长数据的列数位置
        temp+=shortLength+1;
        //判断是否有长数据
        if (temp<record.length-1&&record[temp-1]==1){
            if(((record[temp] >> 7) & 0x1)==0){
                numOfLongRecord=record[temp] & 0xff;
                longLengthOffset = temp+2;
                //最后会多出一个字节
                longRecordOffset =temp+2+numOfLongRecord*2;
            }else {
                numOfLongRecord = HexUtil.normalInt2(record,temp);
                longLengthOffset = temp+3;
                longRecordOffset = temp+3+numOfLongRecord*2;
            }
        }
        /**
         * 这个时候，所有的指针都已经就位，就可以开始解析数据了
         * 由于要同时读两个指针，所以用索引来读取
         */
        //用于存放记录的map
        Map<String,String> recordmap = new HashMap<>();
        for (int i = 0; i <list.size() ; i++) {
            Ischema ischema = list.get(i);
            Integer length = lengths.get(i);
            if (length <=8&&length >=1){
                Object rowCompressValue = ischema.getRowCompressValue(record, shortRecordOffset, length,false );
                recordmap.put(ischema.name(), String.valueOf(rowCompressValue));
                shortRecordOffset+=length;
            }else if (length==9){
                //长数据从长数据开始部位开始的相对结束偏移
                int longRecordRelativeEndOffset = HexUtil.int2(record,longLengthOffset);
                longLengthOffset+=2;
                //如果长度大于这个数，说明其primary位为1，应该去掉,且这是个复杂列
                if (longRecordRelativeEndOffset>32768){
                    longRecordRelativeEndOffset-=32768;
                    Object rowCompressValue = ischema.getRowCompressValue(record, longRecordOffset, longRecordRelativeEndOffset-longRecordRelativeOffset,true);
                    recordmap.put(ischema.name(), String.valueOf(rowCompressValue));
                    longRecordOffset+=longRecordRelativeEndOffset-longRecordRelativeOffset;
                    longRecordRelativeOffset+=longRecordRelativeEndOffset-longRecordRelativeOffset;
                }else{
                    Object rowCompressValue = ischema.getRowCompressValue(record, longRecordOffset, longRecordRelativeEndOffset-longRecordRelativeOffset, false);
                    recordmap.put(ischema.name(), String.valueOf(rowCompressValue));
                    longRecordOffset+=longRecordRelativeEndOffset-longRecordRelativeOffset;
                    longRecordRelativeOffset+=longRecordRelativeEndOffset-longRecordRelativeOffset;
                }
            }else if (length==0){
                recordmap.put(ischema.name(),String.valueOf(ischema.getRowCompressValue(record,shortRecordOffset,length,false)));
            }else if (length==-1){
                recordmap.put(ischema.name(),"NULL");
            }else if (length==10){
                recordmap.put(ischema.name(), String.valueOf(ischema.getRowCompressValue(record,shortRecordOffset,length,false)));
            }
        }
        recordmap.put("unbroken", String.valueOf(flag));
        return recordmap;
    }
}
