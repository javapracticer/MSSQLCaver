package util;

import java.io.IOException;

public class overFlowRecordParser {
    public static Object parserOverFlowRecord(byte[] page, int slot) throws IOException {
        int preRecord = hexUtil.int2(page,8190-slot*2);
        if (preRecord ==0){
            return "行溢出列暂时无法解析";
        }
        int startOffset = preRecord+14;
        int length = hexUtil.int2(page,preRecord+2);
        String result = hexUtil.parseRecordString(page, startOffset, startOffset + length - 15); //15 = 头14+1，因为行溢出的长度包含了头
        return result;
    }
}
