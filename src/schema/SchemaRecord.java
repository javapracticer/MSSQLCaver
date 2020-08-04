package schema;
import util.HexUtil;
public class SchemaRecord {
    String type;
    Long tableId;
    String schemaName;
    Long columnid;
    int length;
    public SchemaRecord(byte[] page, int preRecord, int size){
        this.columnid = HexUtil.int4(page,preRecord+10);
        this.tableId = HexUtil.int4(page,preRecord+4);
        this.type = String.valueOf(page[preRecord+14] & 0xff);
        if (HexUtil.int2(page,preRecord+47)!=0){
            this.schemaName = HexUtil.parseString(page,preRecord+52,preRecord+size);
        }else {
            this.schemaName = HexUtil.parseString(page,preRecord+54,preRecord+size);
        }

        this.length = HexUtil.int2(page,preRecord+19);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Long getColumnid() {
        return columnid;
    }

    public void setColumnid(Long columnid) {
        this.columnid = columnid;
    }

    @Override
    public String toString() {
        return "SchemaRecord{" +
                "type='" + type + '\'' +
                ", tableId=" + tableId +
                ", schemaName='" + schemaName + '\'' +
                ", columnid=" + columnid +
                ", length=" + length +
                '}';
    }
}