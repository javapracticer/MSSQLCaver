package domain;
import util.HexUtil;

import java.io.IOException;

public class RawBigInt implements Ischema {
    String name;
    int length=8;
    int fixd=1;
    public RawBigInt(String name1){
        this.name = name1;
    }

    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) {
        return HexUtil.int8(bytes,offset);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int getLength() {
        return length;
    }
    @Override
    public int fixd(){
        return fixd;
    }

    @Override
    public boolean isLOB() {
        return false;
    }

    @Override
    public Object getRowCompressValue(byte[] bytes, int startOffset, int length, boolean isComplexRow) {
        String hex = HexUtil.getNormalHex(bytes, startOffset, startOffset + length-1);
        Long aLong = Long.valueOf(hex, 16);
        switch (length) {
            case 1:
                aLong = (aLong << 57) >>> 57;
                break;
            case 2:
                aLong = (aLong << 49) >>> 49;
                break;
            case 3:
                aLong = (aLong << 41) >>> 41;
                break;
            case 4:
                aLong = (aLong << 33) >>> 33;
                break;
            case 5:
                aLong =(aLong << 25) >>> 25;
                break;
            case 6:
                aLong =(aLong << 17) >>> 17;
                break;
            case 7:
                aLong =(aLong << 9) >>> 9;
                break;
            case 8:
                aLong =(aLong << 1) >>> 1;
                break;
            default:
                aLong = 0L;
        }
        return aLong;
    }

    @Override
    public Object getOverFlowValue(byte[] record, int startOffsetOfVariableColumn, int i) throws IOException {
        return null;
    }

    @Override
    public String getSqlSchema() {
        return "bigint";
    }

    @Override
    public int getType() {
        return 127;
    }


}
