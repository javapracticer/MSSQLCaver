package title;

import domain.pageHeader;
import util.hexUtil;

import java.util.ArrayList;
import java.util.List;

public class titleRecordUtil {
    public static List<titleRecord> parsePageRecord(byte[] page, pageHeader header) {
        List<titleRecord> list = new ArrayList<>();
        int recordCount = header.getSlotCnt();
        for (int i = 8190; i>=8192-recordCount*2 ; i=i-2) {
            int preRecord = hexUtil.int2(page,i);
            //此处为了程序能运行，舍弃了一些跨页数据，之后再研究
            if (preRecord>8192){
                continue;
            }
            int size = hexUtil.int2(page,preRecord+54)-1;
            if (size>8192){
                continue;
            }
            titleRecord tr = new titleRecord(page,preRecord,size);
            list.add(tr);
        }
        return list;
    }

}
