package schema;

import domain.pageHeader;

import java.util.List;

public class schemeaPage {
    pageHeader header;
    List<schemaRecord> records;
    public schemeaPage(byte[] page){
        header = new pageHeader(page);
        records = schemaRecordUtil.parsePageRecord(page,header);
    }

    public pageHeader getHeader() {
        return header;
    }

    public void setHeader(pageHeader header) {
        this.header = header;
    }

    public List<schemaRecord> getRecords() {
        return records;
    }

    public void setRecords(List<schemaRecord> list) {
        this.records = list;
    }

    @Override
    public String toString() {
        return "schemeaPage{" +
                "header=" + header +
                ", records=" + records +
                '}';
    }
}
