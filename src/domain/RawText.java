package domain;

import util.HexUtil;
import util.LobRecordParser;
import util.PageUtils;

import java.io.IOException;

public class RawText implements Ischema {
    private String name;
    int length = 16;
    private int fixed = 0;
    private boolean isLOB = true;
    public RawText(String name1){
        this.name = name1;
    }

    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) throws IOException {
        long pageid = HexUtil.int4(bytes, offset + 8);
        int slot = HexUtil.int2(bytes,offset+14);
        byte[] aimpage = PageUtils.getPagebyPageNum((int) pageid);
        Object textResult = LobRecordParser.parserLobRecord(aimpage, slot);
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
    @Override
    public boolean isLOB() {
        return isLOB;
    }

    @Override
    public Object getRowCompressValue(byte[] bytes, int startOffset, int length, boolean isComplexRow) throws IOException {
        return getValue(bytes,startOffset,startOffset+length);
    }

    @Override
    public Object getOverFlowValue(byte[] record, int startOffsetOfVariableColumn, int i) {
        return null;
    }
}
