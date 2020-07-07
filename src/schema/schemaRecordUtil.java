package schema;

import domain.pageHeader;
import title.titleRecord;
import util.hexUtil;

import java.util.ArrayList;
import java.util.List;

public class schemaRecordUtil {
    public static List<schemaRecord> parsePageRecord(byte[] page, pageHeader header){
        List<schemaRecord> list = new ArrayList<>();
        int recordCount = header.getSlotCnt();
        for (int i = 8190; i>=8192-recordCount*2 ; i=i-2) {
            int preRecord = hexUtil.int2(page,i);
            //此处为了程序能运行，舍弃了一些跨页数据，之后再研究
            if (preRecord>8192) {
                continue;
            }
            int size = hexUtil.int2(page,preRecord+51)-1;
            //舍弃一些跨页数据
            if (size>8192){
                continue;
            }
            schemaRecord sr = new schemaRecord(page,preRecord,size);
            list.add(sr);
        }
        return list;
    }
}
