package domain;

import util.HexUtil;

public class PageHeader {
    private int headerVersion;
    private int type;
    private int typeFlagBits;
    private int level;
    private int flagBits;
    private int indexId;
    private long prevPage;
    private int pminlen;
    private long nextPage;
    private int slotCnt;
    private long idObj;
    private int freeCnt;
    private int freeData;
    private long pageId;
    private short pageFileId;
    private int reservedCnt;
    private int lsn1;
    private int lsn2;
    private int lsn3;
    private String lsn;
    private int xactReserved;
    private long xdesId;
    private int ghostRecnt;
    private Long tornBits;
    private int fileId;
    public PageHeader(byte[] page){
        this.headerVersion = page[0];
        this.type = page[1];
        this.flagBits = page[2];
        this.level = page[3];
        this.flagBits = HexUtil.int2(page,4);
        this.indexId = HexUtil.int2(page,6);
        this.prevPage = HexUtil.int4(page,8);
        this.pminlen = HexUtil.int2(page,14);
        this.nextPage = HexUtil.int4(page,16);
        this.slotCnt = HexUtil.int2(page,22);
        this.idObj = HexUtil.int4(page,24);
        this.freeCnt = HexUtil.int2(page,28);
        this.freeData = HexUtil.int2(page,30);
        this.pageId = HexUtil.int4(page,32);
        this.fileId = HexUtil.int2(page,36);
        this.reservedCnt = HexUtil.int2(page,38);
        this.lsn = HexUtil.int4(page,40)+":"+ HexUtil.int4(page,44)+":"+ HexUtil.int2(page,48);
        this.xactReserved = HexUtil.int2(page,50);
        this.xdesId = HexUtil.int6(page,52);
        this.ghostRecnt = HexUtil.int2(page,58);
        this.tornBits = HexUtil.int4(page,60);
    }

    public int getHeaderVersion() {
        return headerVersion;
    }

    public void setHeaderVersion(int headerVersion) {
        this.headerVersion = headerVersion;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTypeFlagBits() {
        return typeFlagBits;
    }

    public void setTypeFlagBits(int typeFlagBits) {
        this.typeFlagBits = typeFlagBits;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getFlagBits() {
        return flagBits;
    }

    public void setFlagBits(int flagBits) {
        this.flagBits = flagBits;
    }

    public int getIndexId() {
        return indexId;
    }

    public void setIndexId(int indexId) {
        this.indexId = indexId;
    }

    public long getPrevPage() {
        return prevPage;
    }

    public void setPrevPage(long prevPage) {
        this.prevPage = prevPage;
    }

    public int getPminlen() {
        return pminlen;
    }

    public void setPminlen(int pminlen) {
        this.pminlen = pminlen;
    }

    public long getNextPage() {
        return nextPage;
    }

    public void setNextPage(long nextPage) {
        this.nextPage = nextPage;
    }

    public int getSlotCnt() {
        return slotCnt;
    }

    public void setSlotCnt(int slotCnt) {
        this.slotCnt = slotCnt;
    }

    public Long getIdObj() {
        return idObj;
    }

    public void setIdObj(Long idObj) {
        this.idObj = idObj;
    }

    public int getFreeCnt() {
        return freeCnt;
    }

    public void setFreeCnt(int freeCnt) {
        this.freeCnt = freeCnt;
    }

    public int getFreeData() {
        return freeData;
    }

    public void setFreeData(int freeData) {
        this.freeData = freeData;
    }

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }


    public void setPageFileId(short pageFileId) {
        this.pageFileId = pageFileId;
    }

    public int getReservedCnt() {
        return reservedCnt;
    }

    public void setReservedCnt(int reservedCnt) {
        this.reservedCnt = reservedCnt;
    }

    public int getLsn1() {
        return lsn1;
    }

    public void setLsn1(int lsn1) {
        this.lsn1 = lsn1;
    }

    public int getLsn2() {
        return lsn2;
    }

    public void setLsn2(int lsn2) {
        this.lsn2 = lsn2;
    }

    public int getLsn3() {
        return lsn3;
    }

    public void setLsn3(int lsn3) {
        this.lsn3 = lsn3;
    }

    public String getLsn() {
        return lsn;
    }

    public void setLsn(String lsn) {
        this.lsn = lsn;
    }

    public int getXactReserved() {
        return xactReserved;
    }

    public void setXactReserved(int xactReserved) {
        this.xactReserved = xactReserved;
    }

    public long getXdesId() {
        return xdesId;
    }

    public void setXdesId(long xdesId) {
        this.xdesId = xdesId;
    }

    public int getGhostRecnt() {
        return ghostRecnt;
    }

    public void setGhostRecnt(int ghostRecnt) {
        this.ghostRecnt = ghostRecnt;
    }

    public Long getTornBits() {
        return tornBits;
    }

    public void setTornBits(Long tornBits) {
        this.tornBits = tornBits;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    @Override
    public String toString() {
        return "PageHeader{" +
                "headerVersion=" + headerVersion +
                ", type=" + type +
                ", typeFlagBits=" + typeFlagBits +
                ", level=" + level +
                ", flagBits=" + flagBits +
                ", indexId=" + indexId +
                ", prevPage=" + prevPage +
                ", pminlen=" + pminlen +
                ", nextPage=" + nextPage +
                ", slotCnt=" + slotCnt +
                ", idObj=" + idObj +
                ", freeCnt=" + freeCnt +
                ", freeData=" + freeData +
                ", pageId=" + pageId +
                ", reservedCnt=" + reservedCnt +
                ", lsn1=" + lsn1 +
                ", lsn2=" + lsn2 +
                ", lsn3=" + lsn3 +
                ", lsn='" + lsn + '\'' +
                ", xactReserved=" + xactReserved +
                ", xdesId=" + xdesId +
                ", ghostRecnt=" + ghostRecnt +
                ", tornBits=" + tornBits +
                ", fileId=" + fileId +
                '}';
    }
}
