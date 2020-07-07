package domain;
import util.hexUtil;
public class rawInt implements Ischema {
    private String name;
    int length = 4;
    int fixed = 1;
    public rawInt(String Name){
        this.name = Name;
    }

    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) {
        return hexUtil.int4(bytes,offset);
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
