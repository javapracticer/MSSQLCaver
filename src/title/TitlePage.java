package title;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.prism.impl.Disposer;
import domain.*;
import util.*;

/**
 * 数据表头名页
 */
public  class TitlePage {
    public static List<Map<String,String>> parserTitle(List<byte[]> titlePages) throws IOException {
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
        //根据版本判断是否需要加入status2，在2008R2之后不需要再添加
        if (PageUtils.getVersionNum()<=665){
            list.add(new RawInt("status2"));
        }
        PageHeader header;
        List<byte[]> records;
        List<Map<String,String>> result = new ArrayList<>();
        for (byte[] titlePage : titlePages) {
            header = new PageHeader(titlePage);
            records = RecordCuter.cutRrcord(titlePage, header.getSlotCnt());
            result.addAll(RawColumnParser.parserRecord(records,list, CheckSum.pageCheckSum(titlePage)));
        }
        return result;
    }
}
