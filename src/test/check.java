package test;

import util.PageUtils;
import util.checkSum;

public class check {
    public static void main(String[] args) {
        PageUtils.setfile("C:\\Users\\s6560\\Documents\\sqlsample\\sample.mdf");
        int i = 141;
        i=i<<8;
        i+=0;
        i=i<<8;
        i+=53;
        i=i<<8;
        i+=0;
        System.out.println(Integer.toBinaryString(i));
        boolean b = checkSum.pageCheckSum(PageUtils.getPagebyPageNum(0));
        System.out.println(b);
    }
}
