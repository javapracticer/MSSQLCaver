package title;

import domain.PageHeader;
import util.HexUtil;

import java.util.ArrayList;
import java.util.List;

public class TitleRecordUtil {
    public static List<TitleRecord> parsePageRecord(byte[] page, PageHeader header) {
        List<TitleRecord> list = new ArrayList<>();
        int recordCount = header.getSlotCnt();
        for (int i = 8190; i>=8192-recordCount*2 ; i=i-2) {
            int preRecord = HexUtil.int2(page,i);
            //此处为了程序能运行，舍弃了一些跨页数据，之后再研究
            if (preRecord>8192){
                continue;
            }
            int size = HexUtil.int2(page,preRecord+54)-1;
            if (size>8192){
                continue;
            }
            TitleRecord tr = new TitleRecord(page,preRecord,size);
            list.add(tr);
        }
        return list;
    }

}
