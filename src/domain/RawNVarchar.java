package domain;

import util.HexUtil;
import util.LobRecordParser;
import util.OverFlowRecordParser;
import util.PageUtils;

import java.io.IOException;

public class RawNVarchar implements Ischema {
    private String name;
    int length = 0;
    private int fixed = 0;
    private boolean isLOB = false;
    private boolean changeToLob = false;
    public RawNVarchar(String name1,int length1){
        this.name = name1;
        this.length = length1;
        if (length==65535){
            changeToLob = true;
        }
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
    public Object getRowCompressValue(byte[] bytes, int startOffset, int length, boolean isComplexRow) throws IOException {
        if (isComplexRow){
            if (changeToLob){
                return parserChangeLob(bytes,startOffset,startOffset+length-1);
            }else {
                return getOverFlowValue(bytes,startOffset,startOffset+length-1);
            }
        }
        return getValue(bytes,startOffset,startOffset+length-1);
    }

    @Override
    public Object getOverFlowValue(byte[] bytes, int offset, int endOffset) throws IOException {
        long pageid = HexUtil.int4(bytes, offset + 16);
        int slot = HexUtil.int2(bytes,offset+22);
        byte[] aimpage = PageUtils.getPagebyPageNum((int) pageid);
        Object result = OverFlowRecordParser.parserOverFlowRecord(aimpage, slot);
        return result;
    }

    @Override
    public String getSqlSchema() {
        if (changeToLob){
            return "nvarchar(max)";
        }
        return "nvarchar("+length/2+")";
    }

    @Override
    public int getType() {
        return 231;
    }

    public Object parserChangeLob(byte[] bytes, int offset, int endoffset) throws IOException {
        int preoffset = offset+16;
        StringBuilder lobrecord = new StringBuilder("");
        while ( preoffset<endoffset){
            long pageId = HexUtil.int4(bytes, preoffset);
            int fileId = HexUtil.int2(bytes, preoffset + 4);
            int slotId = HexUtil.int2(bytes, preoffset + 6);
            byte[] aimpage = PageUtils.getPagebyPageNum((int) pageId);
            Object o = LobRecordParser.parserLobRecord(aimpage, slotId);
            lobrecord.append(o);
            preoffset+=12;
        }
        return lobrecord.toString();
    }
}
