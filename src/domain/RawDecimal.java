package domain;

import util.HexUtil;

import java.io.IOException;
import java.math.BigDecimal;

public class RawDecimal implements Ischema {
    private String name;
    int length = 0;
    int fixed = 1;
    short scale = 0;
    public  RawDecimal(String name1,int length1,short scale1){
        this.length = length1;
        this.name = name1;
        this.scale = scale1;

    }

    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) throws IOException {
        String hex = HexUtil.getHex(bytes, offset + 1, endoffset);
        Long aLong = Long.valueOf(hex, 16);
        BigDecimal bigDecimal = BigDecimal.valueOf(aLong, scale);
        return bigDecimal;

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
        return null;
    }

    @Override
    public Object getOverFlowValue(byte[] record, int startOffsetOfVariableColumn, int i) throws IOException {
        return null;
    }
}
