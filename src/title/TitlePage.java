package title;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.prism.impl.Disposer;
import domain.*;
import util.HexUtil;
import util.PageUtils;
import util.RawColumnParser;
import util.RecordCuter;

/**
 * 数据表头名页
 */
public  class TitlePage {
    public static List<Map<String,String>> parserTitle(byte[] bytes) throws IOException {
        List<Ischema> list = new ArrayList<>();
        list.add(new RawInt("id"));
        list.add(new RawNVarchar("name",0));
        list.add(new RawInt("nsid"));
        list.add(new RawTinyint("nsclass"));
        list.add(new RawInt("status"));
        list.add(new RawChar("type",2));
        list.add(new RawInt("pid"));
        list.add(new RawTinyint("pclass"));
        list.add(new RawInt("intprop"));
        list.add(new RawDateTime("created"));
        list.add(new RawDateTime("modified"));
        if (PageUtils.getVersionNum()<=665){
            list.add(new RawInt("status2"));
        }
        PageHeader header = new PageHeader(bytes);
        List<byte[]> record = RecordCuter.cutRrcord(bytes, header.getSlotCnt());
        return RawColumnParser.parserRecord(record,list);
    }
}
