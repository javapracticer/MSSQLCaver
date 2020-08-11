package domain;

import util.HexUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RawSmallDateTime implements Ischema {
    String name;
    int length=4;
    int fixd=1;
    public RawSmallDateTime(String name1){
        this.name = name1;
    }
    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) throws IOException {
        int time = HexUtil.int2(bytes,offset);
        int date = HexUtil.int2(bytes,offset+2);
        Calendar calendar = Calendar.getInstance();
        calendar.set(1900,0,1,time/60,time%60,0);
        calendar.add(Calendar.DATE,date);
        Date result = calendar.getTime();

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(result);
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
        return null;
    }

    @Override
    public Object getOverFlowValue(byte[] record, int startOffsetOfVariableColumn, int i) throws IOException {
        return null;
    }
}
