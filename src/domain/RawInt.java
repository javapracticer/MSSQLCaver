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
    public Object getRowCompressValue(byte[] bytes, int startOffset, int length, boolean isComplexRow) {
        String hex = HexUtil.getNormalHex(bytes, startOffset, startOffset + length-1);
        Integer integer = Integer.valueOf(hex, 16);
        switch (length) {
            case 1:
                integer = (integer << 25) >> 25;
                break;
            case 2:
                integer = (integer << 17) >> 17;
                break;
            case 3:
                integer = (integer << 9) >> 9;
                break;
            case 4:
                integer = (integer << 1) >> 1;
                break;
            default:
                integer = 0;
        }
        return integer;
    }

    @Override
    public Object getOverFlowValue(byte[] record, int startOffsetOfVariableColumn, int i) {
        return null;
    }
}
