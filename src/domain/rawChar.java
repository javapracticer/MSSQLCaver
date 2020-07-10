package domain;
import util.hexUtil;

import java.io.UnsupportedEncodingException;

public class rawChar implements Ischema {
    private String name;
    int length = 0;
    int fixed = 1;
    public rawChar(String name1,int length1){
        this.name = name1;
        this.length = length1;
    }
    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) throws UnsupportedEncodingException {
        return hexUtil.parseRecordString(bytes,offset,endoffset);
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
