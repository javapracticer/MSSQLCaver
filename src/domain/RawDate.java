package domain;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RawDate implements Ischema {
    String name;
    int length=3;
    int fixd=1;
    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) throws IOException {
        int day = ((bytes[offset+2]& 0xff) << 16) + ((bytes[offset+1]& 0xff) << 8) + (bytes[offset]& 0xff);
        Calendar calendar = Calendar.getInstance();
        calendar.set(1,0,3);
        calendar.add(Calendar.DATE,day);
        Date date = calendar.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
    public RawDate(String name1){
        this.name = name1;
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
