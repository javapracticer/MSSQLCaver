package domain;

import util.HexUtil;

import java.io.IOException;

public class RawUniqueidentifier implements Ischema {
    private String name;
    int length = 16;
    int fixed = 1;

    public RawUniqueidentifier(String name1){
        this.name =name1;
    }
    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(HexUtil.getHex(bytes,offset,offset+3));
        builder.append("-");
        builder.append(HexUtil.getHex(bytes,offset+4,offset+5));
        builder.append("-");
        builder.append(HexUtil.getHex(bytes,offset+6,offset+7));
        builder.append("-");
        builder.append(HexUtil.getNormalHex(bytes,offset+8,offset+9));
        builder.append("-");
        builder.append(HexUtil.getNormalHex(bytes,offset+10,endoffset));
        return builder.toString();
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
    public Object getRowCompressValue(byte[] bytes, int startOffset, int length, boolean isComplexRow) throws IOException {
        return getValue(bytes,startOffset,startOffset+length);
    }

    @Override
    public Object getOverFlowValue(byte[] record, int startOffsetOfVariableColumn, int i) throws IOException {
        return null;
    }
}
