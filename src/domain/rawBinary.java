package domain;

public class rawBinary implements Ischema{
    private String name;
    int length = 0;
    int fixed = 1;
    public rawBinary(String name1,int length1){
        this.length = length1;
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
        return length;
    }

    @Override
    public int fixd() {
        return fixed;
    }

}