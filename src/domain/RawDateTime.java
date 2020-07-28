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
    private static double CLOCK_TICK_MS = 10d/3d;
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
    public Object getOverFlowValue(byte[] record, int startOffsetOfVariableColumn, int i) {
        return null;
    }
}
