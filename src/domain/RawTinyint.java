package domain;

public class RawTinyint implements Ischema {
    private String name;
    int length = 1;
    int fixed = 1;
    public RawTinyint(String name1){
        this.name = name1;
    }
    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) {
        return bytes[offset];
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
        return bytes[startOffset] & 0xff;
    }

    @Override
    public Object getOverFlowValue(byte[] record, int startOffsetOfVariableColumn, int i) {
        return null;
    }

    @Override
    public String getSqlSchema() {
        return "tinyint";
    }
}
