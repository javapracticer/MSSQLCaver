package domain;

import util.hexUtil;

import java.io.IOException;
import java.util.Date;

public class rawDateTime implements Ischema {
    String name;
    int length=8;
    int fixd=1;
    private static double CLOCK_TICK_MS = 10d / 3d;
    public rawDateTime(String name1){
        this.name = name1;
    }
    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) throws IOException {
        long time = hexUtil.int4(bytes, offset);
        long date = hexUtil.int4(bytes, offset + 4);
        StringBuilder sb = new StringBuilder();
        sb.append(date);
        sb.append(time);
        return sb;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public int fixd() {
        return 0;
    }
}
