package domain;

import util.HexUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RawDateTime implements Ischema {
    String name;
    int length=8;
    int fixd=1;
    private static double CLOCK_TICK_MS = 3.33333;
    public RawDateTime(String name1){
        this.name = name1;
    }
    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) throws IOException {
        long time = HexUtil.int4(bytes, offset);
        long day = HexUtil.int4(bytes, offset + 4);
        StringBuilder sb = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        calendar.set(1900,0,1,0,0,0);
        int Hour = (int) (time/300/60/60);
        int Min = (int) (time/300/60%60);
        int sec = (int) (time/300%60);
        int MillSec = (int) Math.round(time%300*CLOCK_TICK_MS);
          calendar.add(Calendar.DATE, (int) day);
          calendar.add(Calendar.HOUR_OF_DAY, Hour);
          calendar.add(Calendar.MINUTE, Min);
          calendar.add(Calendar.SECOND, sec);
          calendar.add(Calendar.MILLISECOND, MillSec);

        Date date = calendar.getTime();

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(date);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public int fixd() {
        return fixd;
    }

    @Override
    public boolean isLOB() {
        return false;
    }

    @Override
    public Object getRowCompressValue(byte[] bytes, int startOffset, int length, boolean isComplexRow) {
        // 首先初始化一个基准时间
        Calendar calendar = Calendar.getInstance();
        calendar.set(1900,0,1,0,0,0);
        if(length>4){

            switch (length){
                case 5:
                    calendar.add(Calendar.DATE, Math.toIntExact(-128 + bytes[startOffset]));
                    break;
                case 6:
                    calendar.add(Calendar.DATE, Math.toIntExact(-32768 + HexUtil.ToUIntX(new byte[]{bytes[startOffset + 1], bytes[startOffset]})));
                    break;
                case 7:
                    calendar.add(Calendar.DATE, Math.toIntExact(-8388608 + HexUtil.ToUIntX(new byte[]{bytes[startOffset + 2], bytes[startOffset + 1], bytes[startOffset], 0})));
                    break;
                case 8:
                    calendar.add(Calendar.DATE, Math.toIntExact(-2147483648 + HexUtil.ToUIntX(new byte[]{bytes[startOffset + 3], bytes[startOffset + 2], bytes[startOffset + 1], bytes[startOffset]})));
                    break;
                default:
                    break;
            }
            calendar.add(Calendar.MILLISECOND, (int) (Math.toIntExact(HexUtil.ToUIntX(new byte[]{bytes[startOffset+length-1],bytes[startOffset+length-2],bytes[startOffset+length-3],bytes[startOffset+length-4]}))*CLOCK_TICK_MS));

        }else if (length>0){
            switch (length){
                case 1:
                    calendar.add(Calendar.MILLISECOND, (int) (bytes[startOffset]*CLOCK_TICK_MS));
                    break;
                case 2:
                    calendar.add(Calendar.MILLISECOND, Math.toIntExact(-32768 + HexUtil.ToUIntX(new byte[]{bytes[startOffset + 1], bytes[startOffset]})));
                    break;
                case 3:
                    calendar.add(Calendar.MILLISECOND, Math.toIntExact(-8388608 + HexUtil.ToUIntX(new byte[]{bytes[startOffset + 2], bytes[startOffset+1],bytes[startOffset],0})));
                    break;
                case 4:
                    calendar.add(Calendar.MILLISECOND, Math.toIntExact(-2147483648 + HexUtil.ToUIntX(new byte[]{bytes[startOffset + 3], bytes[startOffset+2],bytes[startOffset+1],bytes[startOffset]})));
                    break;
                default:
                    System.out.println("时间异常");
            }
        }
        Date date = calendar.getTime();

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(date);
    }

    @Override
    public Object getOverFlowValue(byte[] record, int startOffsetOfVariableColumn, int i) {
        return null;
    }
}
