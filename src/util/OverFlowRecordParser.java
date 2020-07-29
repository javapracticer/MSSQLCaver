package util;

import java.io.IOException;

public class OverFlowRecordParser {
    public static Object parserOverFlowRecord(byte[] page, int slot) throws IOException {
        int preRecord = HexUtil.int2(page,8190-slot*2);
        if (preRecord ==0){
            return "行溢出列删除数据暂时无法恢复";
        }
        int startOffset = preRecord+14;
        int length = HexUtil.int2(page,preRecord+2);
        //15 = 头14+1，因为行溢出的长度包含了头
        String result = HexUtil.parseRecordString(page, startOffset, startOffset + length - 15);
        return result;
    }
}
