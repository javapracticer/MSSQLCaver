package domain;

import util.hexUtil;
import util.overFlowRecordParser;
import util.pageSelecter;

import java.io.IOException;

public class rawVarchar implements Ischema {
    private String name;
    int length = 0;
    private int fixed = 0;
    private boolean isLOB = false;
    public rawVarchar(String name1,int length1){
        this.name = name1;
        this.length = length1;
    }
    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) throws IOException {
        return hexUtil.parseRecordString(bytes,offset,endoffset);
    }
    public Object getOverFlowValue(byte[] bytes, int offset, int endoffset) throws IOException {
        long pageid = hexUtil.int4(bytes, offset + 16);
        int slot = hexUtil.int2(bytes,offset+22);
        byte[] aimpage = pageSelecter.pageSelecterByid(pageid);
        Object result = overFlowRecordParser.parserOverFlowRecord(aimpage, slot);
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
