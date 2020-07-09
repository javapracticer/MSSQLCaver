package domain;

public class rawText implements Ischema {
    private String name;
    int length = 16;
    int fixed = 0;
    public rawText(String name1){
        this.name = name1;
    }

    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) {
        return null;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int getLength() {
        return 16;
    }

    @Override
    public int fixd() {
        return fixed;
    }
}
