package util;

import domain.pageHeader;

import java.io.IOException;

public class pageSelecter {
    private static byte[][] read;

    static {
        try {
            read = pageCuter.read("C:\\Users\\s6560\\Documents\\sqlsample\\sample.mdf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static byte[][] getPages(){
        return read;
    }
    public static byte[] pageSelecterByid(long pageid) throws IOException {
        for (byte[] page : read) {
            pageHeader header = new pageHeader(page);
            if (header.getPageId()==pageid){
                return page;
            }
        }
        return null;
    }
}
