package test;

import util.PageUtils;
import util.CheckSum;

public class check {
    public static void main(String[] args) {
        PageUtils.setfile("C:\\Users\\s6560\\Documents\\sqlsample\\sample.mdf");
        boolean b = CheckSum.pageCheckSum(PageUtils.getPagebyPageNum(448));
        System.out.println(b);
    }
}
