package domain;

import util.HexUtil;

public class RawBinary implements Ischema{
    private String name;
    int length = 0;
    int fixed = 1;
    public RawBinary(String name1, int length1){
        this.length = length1;
        this.name = name1;
    }
    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) {
        String normalHex = HexUtil.getNormalHex(bytes, offset, endoffset);
        normalHex = "0x"+normalHex;
        return normalHex;
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
        return getValue(bytes,startOffset,startOffset+length-1);
    }
    @Override
    public Object getOverFlowValue(byte[] record, int startOffsetOfVariableColumn, int i) {
        return null;
    }

    @Override
    public String getSqlSchema() {
        return "binary("+length+")";
    }

    @Override
    public int getType() {
        return 173;
    }

}
