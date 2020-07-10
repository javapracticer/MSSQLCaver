package domain;

import util.hexUtil;
import util.lobRecordParser;
import util.pageSelecter;

import java.io.IOException;

public class rawNVarchar implements Ischema {
    private String name;
    int length = 16;
    private int fixed = 0;
    public rawNVarchar(String name1){
        this.name = name1;
    }

    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) throws IOException {

        return hexUtil.parseString(bytes,offset,endoffset);
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
