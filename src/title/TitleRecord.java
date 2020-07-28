package title;

import util.HexUtil;

public class TitleRecord {
    Long tableId;
    String tablename;
    int type;
    int unknown;
    public TitleRecord(byte[] page, int preRecord, int size) {
        this.tableId = HexUtil.int4(page,preRecord+4);
        this.tablename = HexUtil.parseString(page,preRecord+56,preRecord+size);
        this.type = HexUtil.int2(page,preRecord+17);
        this.unknown = HexUtil.int2(page,preRecord+15);
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


    @Override
    public String toString() {
        return "TitleRecord{" +
                "tableId=" + tableId +
                ", tablename='" + tablename + '\'' +
                ", type=" + type +
                ", unknown=" + unknown +
                '}';
    }
}
