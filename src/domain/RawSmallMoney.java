package domain;

import util.HexUtil;

import java.io.IOException;

public class RawSmallMoney implements Ischema {
    String name;
    int length=4;
    int fixd=1;
    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) throws IOException {
        long l = HexUtil.int4(bytes, offset);
        int i = Math.toIntExact(l);
        return (double)i/10000;
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
        return fixd;
    }

    @Override
    public boolean isLOB() {
        return false;
    }

    @Override
    public Object getRowCompressValue(byte[] bytes, int startOffset, int length, boolean isComplexRow) throws IOException {
        String normalHex = HexUtil.getNormalHex(bytes, startOffset, startOffset + length - 1);
        Integer integer = Integer.valueOf(normalHex, 16);
        switch (length) {
            case 1:
                integer = (integer << 25) >>> 25;
                break;
            case 2:
                integer = (integer << 17) >>> 17;
                break;
            case 3:
                integer = (integer << 9) >>> 9;
                break;
            case 4:
                integer = (integer << 1) >>> 1;
                break;
            default:
                integer = 0;
        }
        return (double)integer/10000;
    }

    @Override
    public Object getOverFlowValue(byte[] record, int startOffsetOfVariableColumn, int i) throws IOException {
        return null;
    }

    @Override
    public String getSqlSchema() {
        return "smallmoney";
    }
}
