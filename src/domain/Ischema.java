package domain;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface Ischema {
    /**
     * 这个方法用于解析最简单的普通数据
     * @param bytes
     * @param offset
     * @param endoffset
     * @return
     * @throws IOException
     */
    Object getValue(byte[] bytes,int offset,int endoffset) throws IOException;

    /**
     * 这个方法用于返回shcema的名字
     * @return
     */
    String name();

    /**
     * 这个方法用于返回schema的长度
     * @return
     */
    int getLength();

    /**
     * 这个方法用于返回是否是变长列
     * @return
     */
    int fixd();

    /**
     * 这个方法用于返回是否是LOB数据
     * @return
     */
    boolean isLOB();

    /**
     * 解析行压缩数据用
     * @param bytes
     * @param startOffset
     * @param length
     * @param isComplexRow 这一项是判断其是否为复杂列
     * @return
     * @throws IOException
     */
    Object getRowCompressValue(byte[] bytes, int startOffset, int length, boolean isComplexRow) throws IOException;

    Object getOverFlowValue(byte[] record, int startOffsetOfVariableColumn, int i) throws IOException;
}
