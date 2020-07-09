package domain;

import util.hexUtil;
import util.lobRecordParser;
import util.pageSelecter;

import java.io.IOException;

public class rawText implements Ischema {
    private String name;
    int length = 16;
    private int fixed = 0;
    public rawText(String name1){
        this.name = name1;
    }

    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) throws IOException {
        long pageid = hexUtil.int6(bytes, offset + 8);
        int slot = hexUtil.int2(bytes,offset+12);
        byte[] aimpage = pageSelecter.pageSelecterByid(pageid);
        Object textResult = lobRecordParser.parserLobRecord(aimpage, slot);
        return textResult;
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
