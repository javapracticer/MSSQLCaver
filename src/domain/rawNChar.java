package domain;

import util.hexUtil;

import java.io.IOException;

public class rawNChar implements Ischema {
    private String name;
    int length = 0;
    int fixed = 1;
    public rawNChar(String name1,int length1){
        this.name = name1;
        this.length = length1;
    }
    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) throws IOException {
        String s = hexUtil.parseString(bytes, offset, endoffset);
        return s;
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
