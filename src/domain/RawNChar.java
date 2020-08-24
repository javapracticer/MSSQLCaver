package domain;

import util.HexUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class RawNChar implements Ischema {
    private String name;
    int length = 0;
    int fixed = 1;
    public RawNChar(String name1, int length1){
        this.name = name1;
        this.length = length1;
    }
    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) throws IOException {
        String s = HexUtil.parseString(bytes, offset, endoffset);
        return s;
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
    public Object getRowCompressValue(byte[] bytes, int startOffset, int length, boolean isComplexRow) throws UnsupportedEncodingException {
        String result = HexUtil.parseRecordString(bytes, startOffset, startOffset + length - 1);
        return result;
    }

    @Override
    public Object getOverFlowValue(byte[] record, int startOffsetOfVariableColumn, int i) throws IOException {
        return null;
    }

    @Override
    public String getSqlSchema() {
        return "nchar("+length/2+")";
    }
}
