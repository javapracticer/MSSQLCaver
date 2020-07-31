package util;

import domain.Ischema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RawColumnParser {
    /**
     * 这个方法专门负责解析记录
     * @param records 这是切割好了的列表
     * @param list 这是已经解析好了的类的shcema
     * @return 返回值是用map记录好了记录的list集合
     * @throws IOException
     */
    public static List<Map<String,String>> prserRecord(List<byte[]> records, List<Ischema> list) throws IOException {
        List<Map<String,String>> recordList = new ArrayList<>();
        int j = 0;

        for (byte[] record : records) {
            if (j==records.size()||record==null){
                break;
            }
            //固定长段的开始点
            int fixdOffset = 4;
            //记录总共有几列记录的offset
            int columnsOffset = HexUtil.int2(record, 2);
            //总共有几列记录
            int numOfColumns = HexUtil.int2(record, columnsOffset);
            int endOffsetPointer =0;
            //记录可变长列结尾的offset
            int variableEndOffsetPointer = 0;
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
                variableEndOffsetPointer = HexUtil.int2(record,endOffsetPointer);
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
                        if (variableEndOffsetPointer>8192){
                            variableEndOffsetPointer -= 32768;
                            overFlowOrLob = true;
                        }
                        if (ischema.isLOB()){
                            //如果是LOB
                            value = ischema.getValue(record, startOffsetOfVariableColumn, variableEndOffsetPointer-1);
                        }else if (overFlowOrLob){
                            //如果不是LOB但是是overFlow
                            value  = ischema.getOverFlowValue(record, startOffsetOfVariableColumn, variableEndOffsetPointer-1);
                        }else {
                            //既不是LOB也不是overFlow
                            value = ischema.getValue(record, startOffsetOfVariableColumn, variableEndOffsetPointer-1);
                        }

                        if (startOffsetOfVariableColumn==variableEndOffsetPointer&&(byte) ((b >> bit) & 0x1)==1){
                            value = "NULL";
                        }else if (startOffsetOfVariableColumn==variableEndOffsetPointer&&(byte) ((b >> bit) & 0x1)!=1){
                            value = "NULL";
                        }
                        recordmap.put(ischema.name(), String.valueOf(value));
                        //开始下一轮的解析2
                        startOffsetOfVariableColumn = variableEndOffsetPointer;
                        variableEndOffsetPointer = HexUtil.int2(record,endOffsetPointer+2);
                        endOffsetPointer+=2;
                        variableHasParase++;
                        i++;
                    }else {
                        recordmap.put(ischema.name(), "NULL");
                        i++;
                    }
                }
            }
            j++;
            recordList.add(recordmap);
        }
        return recordList;
    }
}
