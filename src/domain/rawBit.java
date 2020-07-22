package domain;

import java.io.IOException;

public class rawBit implements Ischema {
    private String name;
    int length = 1;
    int fixed = 1;
    public rawBit(String name1){
        this.name = name1;
    }
    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) throws IOException {
        return "暂不支持bit";
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int getLength() {
        return 1;
    }

    @Override
    public int fixd() {
        return 1;
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
