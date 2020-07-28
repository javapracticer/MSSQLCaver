package domain;
import util.HexUtil;

public class RawInt implements Ischema {
    private String name;
    int length = 4;
    int fixed = 1;
    public RawInt(String Name){
        this.name = Name;
    }

    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) {
        return HexUtil.int4(bytes,offset);
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
    public Object getOverFlowValue(byte[] record, int startOffsetOfVariableColumn, int i) {
        return null;
    }
}
