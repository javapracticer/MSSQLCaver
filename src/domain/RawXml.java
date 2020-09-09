package domain;


import util.HexUtil;

import java.io.IOException;

/**
 * XML对应的java类
 *
 * @author s6560 longxingli
 * @create 2020-09-08 17:41
 **/
public class RawXml implements Ischema {
    private String name;
    int length = 0;
    private int fixed = 0;
    private boolean isLOB = false;
    private boolean changeToLob = false;
    public RawXml(String name1){
        this.name = name1;
    }
    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) throws IOException {
        byte[] temp = new byte[endoffset-offset];
        System.arraycopy(bytes,offset,temp,0,endoffset-offset);

        return "暂不支持XML";
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public int fixd() {
        return fixed;
    }

    @Override
    public boolean isLOB() {
        return false;
    }

    @Override
    public Object getRowCompressValue(byte[] bytes, int startOffset, int length, boolean isComplexRow) throws IOException {
        return null;
    }

    @Override
    public Object getOverFlowValue(byte[] record, int startOffsetOfVariableColumn, int endOffset) throws IOException {
        return null;
    }

    @Override
    public String getSqlSchema() {
        return "xml";
    }

    @Override
    public int getType() {
        return 0;
    }
}
