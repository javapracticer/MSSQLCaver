package domain;

import java.io.IOException;

public class RawBit implements Ischema {
    private String name;
    int length = 1;
    int fixed = 1;
    public RawBit(String name1){
        this.name = name1;
    }
    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) throws IOException {
        int bit = bytes[offset] & 0xff;
        if ((byte)((bit >>0 ) & 0x1)==1){
            return true;
        }else {
            return false;
        }
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
