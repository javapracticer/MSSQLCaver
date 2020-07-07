package domain;

public class recordPage {
    pageHeader header;
    public recordPage(byte[] page){
        header = new pageHeader(page);

    }
}
