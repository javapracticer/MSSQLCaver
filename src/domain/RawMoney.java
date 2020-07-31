package domain;

import util.HexUtil;

import java.io.IOException;
import java.math.BigInteger;

public class RawMoney implements Ischema {
    String name;
    int length=8;
    int fixd=1;
    public RawMoney(String name1){
        this.name = name1;
    }
    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) throws IOException {
        BigInteger bigInteger = HexUtil.int8(bytes, offset);
        String s = bigInteger.toString();
        Long aLong = Long.valueOf(s);

        return (double)aLong/10000;
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
    public int fixd() {
        return fixd;
    }

    @Override
    public boolean isLOB() {
        return false;
    }

    @Override
    public Object getOverFlowValue(byte[] record, int startOffsetOfVariableColumn, int i) throws IOException {
        return null;
    }
}
