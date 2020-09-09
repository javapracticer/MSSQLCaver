package util;

public class CheckSum {
    /**
     * 检查页面是否受损
     * @param page 需要检查的页面
     * @return 检查无错误返回true否则返回false
     */
    public static boolean pageCheckSum(byte[] page){
        int seed = 15;
        int[][] pagebuf = new int[16][128];
        int overall;
        int checkSum = 0;
        int point = 0;
        //将页面分为16份，每份128段，每段四个字节（小端）
        for (int i = 0 ;i<16;i++) {
            for (int j=0;j<128;j++) {
                pagebuf[i][j]  = HexUtil.binaryInt4(page,point);
                point+=4;
            }
        }
        pagebuf[0][15]=0x00000000;
        for (int j = 0;j<16;j++) {
            overall = 0;
            for (int i = 0; i <128 ; i++) {
                overall = overall ^ (pagebuf[j][i]);
            }
            checkSum = checkSum ^ rol(overall,seed-j);
        }

        int tronBit = HexUtil.binaryInt4(page, 60);

        return checkSum==tronBit;
    }
    private static int rol(int value,int rotation){
        int charBit = 8;
        return (value) << (rotation) | (value) >> (4 * charBit - rotation) & ( (1 << rotation) -1);

    }
}
