package domain;
import util.hexUtil;
public class rawBigInt implements Ischema {
    String name;
    int length=8;
    int fixd=1;
    public rawBigInt(String name1){
        this.name = name1;
    }

    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) {
        return hexUtil.int8(bytes,offset);
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
