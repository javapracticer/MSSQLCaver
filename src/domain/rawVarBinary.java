package domain;

public class rawVarBinary implements Ischema {
    private String name;
    int length = 0;
    int fixed = 0;
    private boolean isLOB = false;
    public rawVarBinary(String name1){
        this.name = name1;
    }
    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) {
        return bytes;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public int fixd() {
        return 0;
    }

    public boolean isLOB() {
        return isLOB;
    }

    @Override
    public Object getOverFlowValue(byte[] record, int startOffsetOfVariableColumn, int i) {
        return null;
    }
}
