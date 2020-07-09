package title;

import util.hexUtil;

public class titleRecord {
    Long tableId;
    String tablename;
    int type;
    int unknown;
    int mightBeType;
    public titleRecord(byte[] page, int preRecord, int size) {
        this.tableId = hexUtil.int4(page,preRecord+4);
        this.tablename = hexUtil.parseString(page,preRecord+56,preRecord+size);
        this.type = hexUtil.int2(page,preRecord+17);
        this.unknown = hexUtil.int2(page,preRecord+15);
        this.mightBeType = hexUtil.int2(page,preRecord+41);
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableid) {
        this.tableId = tableid;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUnknown() {
        return unknown;
    }

    public void setUnknown(int unknown) {
        this.unknown = unknown;
    }

    public int getMightBeType() {
        return mightBeType;
    }

    public void setMightBeType(int mightBeType) {
        this.mightBeType = mightBeType;
    }

    @Override
    public String toString() {
        return "titleRecord{" +
                "tableId=" + tableId +
                ", tablename='" + tablename + '\'' +
                ", type=" + type +
                ", unknown=" + unknown +
                ", mightBeType=" + mightBeType +
                '}';
    }
}