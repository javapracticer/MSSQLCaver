package util;

import domain.PageHeader;

import java.io.IOException;

public class PageSelecter {
    private static byte[][] read;

    static {
        try {
            read = PageCuter.read("C:\\Users\\s6560\\Documents\\sqlsample\\sample.mdf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static byte[][] getPages(){
        return read;
    }
    public static byte[] pageSelecterByid(long pageid) throws IOException {
        for (byte[] page : read) {
            PageHeader header = new PageHeader(page);
            if (header.getPageId()==pageid){
                return page;
            }
        }
        return null;
    }
}
