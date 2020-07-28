package domain;
import util.HexUtil;

public class RawBigInt implements Ischema {
    String name;
    int length=8;
    int fixd=1;
    public RawBigInt(String name1){
        this.name = name1;
    }

    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) {
        return HexUtil.int8(bytes,offset);
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
    public int fixd(){
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
