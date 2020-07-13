package domain;

import util.hexUtil;

import java.io.IOException;

public class rawNVarchar implements Ischema {
    private String name;
    int length = 16;
    private int fixed = 0;
    private boolean isLOB = false;
    public rawNVarchar(String name1){
        this.name = name1;
    }

    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) throws IOException {

        return hexUtil.parseString(bytes,offset,endoffset);
    }
    @Override
    public String name() {
        return name;
    }

    @Override
    public int getLength() {
        return 16;
    }

    @Override
    public int fixd() {
        return fixed;
    }

    public boolean isLOB() {
        return isLOB;
    }

    @Override
    public Object getOverFlowValue(byte[] record, int startOffsetOfVariableColumn, int i) {
        return null;
    }
}