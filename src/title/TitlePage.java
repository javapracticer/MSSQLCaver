package title;

import java.util.ArrayList;
import java.util.List;

import domain.PageHeader;

/**
 * 数据表头名页
 */
public class TitlePage {
    PageHeader header; //页头
    List<TitleRecord> list = new ArrayList<>(); //页里包含的文件
    public TitlePage(byte[] page){
        header=new PageHeader(page);
        list = TitleRecordUtil.parsePageRecord(page,header);
    }

    public PageHeader getHeader() {
        return header;
    }

    public void setHeader(PageHeader header) {
        this.header = header;
    }

    public List<TitleRecord> getList() {
        return list;
    }

    public void setList(List<TitleRecord> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "title.TitlePage{" +
                "header=" + header +
                ", list=" + list +
                '}';
    }
}
