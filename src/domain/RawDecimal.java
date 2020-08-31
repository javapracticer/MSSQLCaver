package domain;

import util.HexUtil;

import java.io.IOException;
import java.math.BigDecimal;

public class RawDecimal implements Ischema {
    private String name;
    int length = 0;
    int fixed = 1;
    int prec = 0;
    private short scale = 0;
    public  RawDecimal(String name1,int length1,short prec1,short scale1){
        this.length = length1;
        this.name = name1;
        this.scale = scale1;
        this.prec = prec1;
        if (this.length!=5&&this.length!=9&&this.length!=13&&this.length!=17){
            this.fixed=0;
        }

    }

    @Override
    public Object getValue(byte[] bytes, int offset, int endoffset) throws IOException {
        if (fixed==1){
            String hex = HexUtil.getHex(bytes, offset + 1, endoffset);
            Long aLong = Long.valueOf(hex, 16);
            return BigDecimal.valueOf(aLong, scale);

        }else {
            return getRowCompressValue(bytes, offset, endoffset - offset + 1, false);
        }

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
        return fixed;
    }

    @Override
    public boolean isLOB() {
        return false;
    }

    @Override
    public Object getRowCompressValue(byte[] bytes, int startOffset, int length, boolean isComplexRow) throws IOException {
        //首先读取第7位，若第7位为1则是正数，为0则是负数
        boolean positive = true;
        int firstByte = bytes[startOffset] & 0xff;
        if (((firstByte >> 7) & 0x1)!=1){
            positive=false;
        }
        String normalHex = HexUtil.getNormalHex(bytes, startOffset+1, startOffset + length - 1);
        long aLong = Long.valueOf(normalHex, 16);
        //总共有多少bit
        int totalBit = (length-1)*8;
        //有多少个10位
        int numOfChunk = (int) Math.ceil( (double) totalBit/10);
        //尾数
        StringBuilder result = new StringBuilder();
        for (int i = 0; i <numOfChunk ; i++) {
            StringBuilder temp = new StringBuilder();
            for (int j = totalBit-1-i*10; j >totalBit-1-(i+1)*10 ; j--) {
                if (((aLong>>j) & 0x1)==1){
                    temp.append("1");
                }else {
                    temp.append("0");
                }
                if(j==0){
                    break;
                }
            }
            result.append(Long.valueOf(temp.toString(),2));
        }
        double aLong1 = Long.valueOf(result.toString());
        if (result.length()>prec){
           aLong1 = aLong1/Math.pow(10,result.length()-prec);
        }
        return BigDecimal.valueOf((long) aLong1,scale);
    }

    @Override
    public Object getOverFlowValue(byte[] record, int startOffsetOfVariableColumn, int i) throws IOException {
        return null;
    }

    @Override
    public String getSqlSchema() {
        return "decimal("+prec+","+scale+")";
    }

    @Override
    public int getType() {
        return 106;
    }
}
