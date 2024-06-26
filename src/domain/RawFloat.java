package domain;

import util.HexUtil;

import java.io.IOException;

public class RawFloat implements Ischema {
    private String name;
    int length = 0;
    int fixed = 1;

    public RawFloat(String name1, int length1){
        this.length = length1;
        this.name = name1;
    }
    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) throws IOException {
        String hex = HexUtil.getHex(bytes, offset, endoffset);
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
    public Object getRowCompressValue(byte[] bytes, int startOffset, int length, boolean isComplexRow) {
        String hex = HexUtil.getHex(bytes, startOffset, startOffset+length-1);
        while (length<this.length){
            hex+="00";
            length++;
        }
        Long aLong = Long.parseLong(hex,16);
        double v = Double.longBitsToDouble(aLong);
        return v;
    }

    @Override
    public Object getOverFlowValue(byte[] record, int startOffsetOfVariableColumn, int endOffset) throws IOException {
        return null;
    }

    @Override
    public String getSqlSchema() {
        return "float";
    }

    @Override
    public int getType() {
        return 62;
    }
}
