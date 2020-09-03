package util;

public class CheckSum {
    /**
     * 计算页面校验和，并返回布尔类型，true为无错
     * false为页面有错误
     * @return
     */
    static int seed = 15;
    static int charBit = 8;
    public static boolean pageCheckSum(byte[] page){
        int[][] pagebuf = new int[16][128];
        int overall = 0;
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
    public static int rol(int value,int rotation){
        return (value) << (rotation) | (value) >> (4 * charBit - rotation) & ( (1 << rotation) -1);

    }
}
