package util;

import domain.pageHeader;

import java.io.IOException;

public class pageSelecter {
    public static byte[] pageSelecterByid(long pageid) throws IOException {
        byte[][] read = pageCuter.read("C:\\Users\\s6560\\Documents\\sqlsample\\sample.mdf");
        for (byte[] page : read) {
            pageHeader header = new pageHeader(page);
            if (header.getPageId()==pageid){
                return page;
            }
        }
        return null;
    }
}
