package domain;

public interface Ischema {
    Object getValue(byte[] bytes,int offset,int endoffset);
    String name();
    int getLength();
    int fixd();
}
