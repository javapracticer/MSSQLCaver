package domain;

import util.HexUtil;
import util.OverFlowRecordParser;
import util.PageUtils;

import java.io.IOException;

public class RawNVarchar implements Ischema {
    private String name;
    int length = 0;
    private int fixed = 0;
    private boolean isLOB = false;
    public RawNVarchar(String name1,int length1){
        this.name = name1;
        this.length = length1;
    }

    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) throws IOException {

        return HexUtil.parseString(bytes,offset,endoffset);
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
    @Override
    public boolean isLOB() {
        return isLOB;
    }

    @Override
    public Object getRowCompressValue(byte[] bytes, int startOffset, int length, boolean isComplexRow) {
        return null;
    }

    @Override
    public Object getOverFlowValue(byte[] bytes, int offset, int endoffset) throws IOException {
        long pageid = HexUtil.int4(bytes, offset + 16);
        int slot = HexUtil.int2(bytes,offset+22);
        byte[] aimpage = PageUtils.getPagebyPageNum((int) pageid);
        Object result = OverFlowRecordParser.parserOverFlowRecord(aimpage, slot);
        return result;
    }
}
