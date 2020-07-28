package domain;

public class RecordPage {
    PageHeader header;
    public RecordPage(byte[] page){
        header = new PageHeader(page);

    }
}
