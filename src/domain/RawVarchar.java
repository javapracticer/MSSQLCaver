package domain;

import util.HexUtil;
import util.OverFlowRecordParser;
import util.PageSelecter;

import java.io.IOException;

public class RawVarchar implements Ischema {
    private String name;
    int length = 0;
    private int fixed = 0;
    private boolean isLOB = false;
    public RawVarchar(String name1, int length1){
        this.name = name1;
        this.length = length1;
    }
    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) throws IOException {
        return HexUtil.parseRecordString(bytes,offset,endoffset);
    }
    @Override
    public Object getOverFlowValue(byte[] bytes, int offset, int endoffset) throws IOException {
        long pageid = HexUtil.int4(bytes, offset + 16);
        int slot = HexUtil.int2(bytes,offset+22);
        byte[] aimpage = PageSelecter.getPagebyPageNum((int) pageid);
        Object result = OverFlowRecordParser.parserOverFlowRecord(aimpage, slot);
        return result;
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

    public boolean isLOB() {
        return isLOB;
    }

}
