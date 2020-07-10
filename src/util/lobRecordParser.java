package util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class lobRecordParser {
    /**
     * lob记录一般有一个固定头，固定头之后再根据其类型判断有没有类型头
     * @param page 储存lob数据的页面
     * @param slot 储存的槽位
     * @return 返回lob数据
     * @throws IOException 错误
     */
    public static Object parserLobRecord(byte[] page,int slot) throws IOException {
        //找到记录地址的初始偏移量
        Object result = null;
        int preRecord = hexUtil.int2(page,8190-slot*2);
        int recordType = hexUtil.int2(page,preRecord+12);
        if (recordType==5){
            result = parserType5(page, preRecord + 14); //直接从跳过固定记录头，从记录开始
        }else if (recordType==3){
            result=parserType3(page,preRecord); //因为要分析记录头长度，所以不跳过固定记录头
        }else if (recordType==0){
            result =parserType0(page,preRecord+14);//不分析固定头，跳过固定头
        }
        return result;
    }
    private static Object parserType5(byte[] page,int startOffset) throws IOException {
        int curLinks = hexUtil.int2(page,startOffset+2);  //找出数据内部存了几条数据
        StringBuilder lobrecord = new StringBuilder("");//新建String类储存数据
        int prerecord = startOffset+10;//跳过固定头，进入type头
        for (int i = 0; i <=curLinks ; i++) {
            Long pageid = hexUtil.int6(page,prerecord+4);
            byte[] aimPage = pageSelecter.pageSelecterByid(pageid);
            int aimslot = hexUtil.int2(page,prerecord+10);
            Object s = parserLobRecord(aimPage, aimslot);
            lobrecord.append(s);
        }
        return lobrecord.toString();
    }
    private static Object parserType3(byte[] page, int startOffset) throws UnsupportedEncodingException {
        int length = hexUtil.int2(page,startOffset+2);
        String result = hexUtil.parseRecordString(page, startOffset + 14, startOffset + length - 1);
        return result;
    }
    public static void parserType2(){

    }
    private static Object parserType0(byte[] page, int startOffset) throws UnsupportedEncodingException {
        int length = hexUtil.int2(page,startOffset);
        String resulptPart = hexUtil.parseRecordString(page, startOffset + 6, startOffset +6 + length - 1);
        return resulptPart;
    }
}
