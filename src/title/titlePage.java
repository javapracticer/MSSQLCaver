package title;

import java.util.ArrayList;
import java.util.List;
import domain.pageHeader;

/**
 * 数据表头名页
 */
public class titlePage {
    pageHeader header; //页头
    List<titleRecord> list = new ArrayList<>(); //页里包含的文件
    public titlePage(byte[] page){
        header=new pageHeader(page);
        list = titleRecordUtil.parsePageRecord(page,header);
    }

    public pageHeader getHeader() {
        return header;
    }

    public void setHeader(pageHeader header) {
        this.header = header;
    }

    public List<titleRecord> getList() {
        return list;
    }

    public void setList(List<titleRecord> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "title.titlePage{" +
                "header=" + header +
                ", list=" + list +
                '}';
    }
}
