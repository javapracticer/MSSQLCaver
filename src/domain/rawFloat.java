package domain;

import util.hexUtil;

import java.io.IOException;

public class rawFloat implements Ischema {
    private String name;
    int length = 0;
    int fixed = 1;

    public rawFloat(String name1,int length1){
        this.length = length1;
        this.name = name1;
    }
    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) throws IOException {
        String hex = hexUtil.getHex(bytes, offset, endoffset);
        Long aLong = Long.parseLong(hex,16);
        double v = Double.longBitsToDouble(aLong);
        return v;
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
        return fixed;
    }

    @Override
    public boolean isLOB() {
        return false;
    }

    @Override
    public Object getOverFlowValue(byte[] record, int startOffsetOfVariableColumn, int i) throws IOException {
        return null;
    }
}
