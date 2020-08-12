package domain;

import util.HexUtil;
import util.OverFlowRecordParser;
import util.PageUtils;

import java.io.IOException;

public class RawVarBinary implements Ischema {
    private String name;
    int length = 0;
    int fixed = 0;
    private boolean isLOB = false;
    private boolean changeToLob = false;
    public RawVarBinary(String name1, int length1){
        this.name = name1;
        this.length = length1;
        if (length==65535){
            changeToLob = true;
        }
    }
    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) {
        String normalHex = HexUtil.getNormalHex(bytes, offset, endoffset);
        normalHex = "0x"+normalHex;
        return normalHex;
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
        return 0;
    }
    @Override
    public boolean isLOB() {
        return isLOB;
    }

    @Override
    public Object getRowCompressValue(byte[] bytes, int startOffset, int length, boolean isComplexRow) throws IOException {
        return null;
    }

    @Override
    public Object getOverFlowValue(byte[] bytes, int offset, int endoffset) throws IOException {
        if (changeToLob){
            return parserChangeLob(bytes,offset,endoffset);
        }else {
            long pageid = HexUtil.int4(bytes, offset + 16);
            int slot = HexUtil.int2(bytes,offset+22);
            byte[] aimpage = PageUtils.getPagebyPageNum((int) pageid);
            String result = OverFlowRecordParser.parserOverFlowRecord(aimpage, slot);
            char[] chars = result.toCharArray();
            result = chars.toString();
            return result;
        }
    }

    public Object parserChangeLob(byte[] bytes, int offset, int endoffset) throws IOException {
        return null;
    }
}
