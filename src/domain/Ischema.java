package domain;

import java.io.IOException;

public interface Ischema {
    Object getValue(byte[] bytes,int offset,int endoffset) throws IOException;
    String name();
    int getLength();
    int fixd();
}
