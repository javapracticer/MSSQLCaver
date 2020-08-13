package domain;

import util.HexUtil;
import util.LobRecordParser;
import util.OverFlowRecordParser;
import util.PageUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

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
            int preRecord = HexUtil.int2(aimpage,8190-slot*2);
            if (preRecord ==0){
                return "行溢出列删除数据暂时无法恢复";
            }
            int startOffset = preRecord+14;
            int length = HexUtil.int2(aimpage,preRecord+2);
            //15 = 头14+1，因为行溢出的长度包含了头
            String result = HexUtil.getNormalHex(aimpage, startOffset, startOffset + length - 15);
            return result;

        }
    }

    public Object parserChangeLob(byte[] bytes, int offset, int endoffset) throws IOException {
        int preoffset = offset+16;
        StringBuilder lobrecord = new StringBuilder("");
        while ( preoffset<endoffset){
            long pageId = HexUtil.int4(bytes, preoffset);
            int fileId = HexUtil.int2(bytes, preoffset + 4);
            int slotId = HexUtil.int2(bytes, preoffset + 6);
            byte[] aimpage = PageUtils.getPagebyPageNum((int) pageId);
            Object o = parserBinaryLob(aimpage, slotId);
            lobrecord.append(o);
            preoffset+=12;
        }
        return lobrecord.toString();
    }
    private String parserBinaryLob(byte[] page,int slot) throws IOException {
        //找到记录地址的初始偏移量
        String result = null;
        int preRecord = HexUtil.int2(page,8190-slot*2);
        if (preRecord ==0){
            return "lob数据暂时无法解析";
        }
        int recordType = HexUtil.int2(page,preRecord+12);
        if (recordType==5){
            //直接从跳过固定记录头，从记录开始
            result = parserBinaryType5(page, preRecord + 14);
        }else if (recordType==3){
            //因为要分析记录头长度，所以不跳过固定记录头
            result=parserBinaryType3(page,preRecord);
        }else if (recordType==0){
            //不分析固定头，跳过固定头
            result =parserBinaryType0(page,preRecord+14);
        }else if (recordType==2){
            //跳过固定头
            result = parserBinaryType2(page,preRecord+14);
        }
        return result;
    }
    private  String parserBinaryType5(byte[] page,int startOffset) throws IOException {
        //找出数据内部存了几条数据
        int curLinks = HexUtil.int2(page,startOffset+2);
        //新建String类储存数据
        StringBuilder lobrecord = new StringBuilder("");
        //跳过type头
        int prerecord = startOffset+10;
        for (int i = 0; i <curLinks ; i++) {
            long pageid = HexUtil.int4(page,prerecord+4);
            byte[] aimPage = PageUtils.getPagebyPageNum((int)pageid);
            int aimslot = HexUtil.int2(page,prerecord+10);
            Object s = parserBinaryLob(aimPage, aimslot);
            lobrecord.append(s);
            prerecord+=12;
        }
        return lobrecord.toString();
    }
    private  String parserBinaryType3(byte[] page, int startOffset) throws UnsupportedEncodingException {
        int length = HexUtil.int2(page,startOffset+2);
        String result = HexUtil.getNormalHex(page, startOffset + 14, startOffset + length - 1);
        return result;
    }
    public  String parserBinaryType2(byte[] page, int startOffset) throws IOException {
        int curLinks = HexUtil.int2(page,startOffset+2);
        StringBuilder lobrecord = new StringBuilder("");
        //跳过type头
        int prerecord = startOffset+14;
        for (int i = 0; i <curLinks ; i++) {
            long pageid = HexUtil.int4(page,prerecord);
            byte[] aimPage = PageUtils.getPagebyPageNum((int)pageid);
            int aimslot = HexUtil.int2(page,prerecord+6);
            Object s = parserBinaryLob(aimPage, aimslot);
            lobrecord.append(s);
            prerecord+=16;
        }
        return lobrecord.toString();
    }
    private  String parserBinaryType0(byte[] page, int startOffset) throws UnsupportedEncodingException {
        int length = HexUtil.int2(page,startOffset);
        String resulptPart = HexUtil.getNormalHex(page, startOffset + 6, startOffset +6 + length - 1);
        return resulptPart;
    }
}
