package schema;

import domain.PageHeader;

import java.util.List;

public class SchemeaPage {
    PageHeader header;
    List<SchemaRecord> records;
    public SchemeaPage(byte[] page){
        header = new PageHeader(page);
        records = SchemaRecordUtil.parsePageRecord(page,header);
    }

    public PageHeader getHeader() {
        return header;
    }

    public void setHeader(PageHeader header) {
        this.header = header;
    }

    public List<SchemaRecord> getRecords() {
        return records;
    }

    public void setRecords(List<SchemaRecord> list) {
        this.records = list;
    }

    @Override
    public String toString() {
        return "SchemeaPage{" +
                "header=" + header +
                ", records=" + records +
                '}';
    }
}
