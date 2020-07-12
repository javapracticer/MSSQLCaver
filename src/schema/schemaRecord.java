package schema;
import domain.schemaType;
import util.hexUtil;
public class schemaRecord {
    String type;
    Long tableId;
    String schemaName;
    Long columnid;
    int length;
    public schemaRecord(byte[] page,int preRecord,int size){
        this.columnid = hexUtil.int4(page,preRecord+10);
        this.tableId = hexUtil.int4(page,preRecord+4);
        this.type = String.valueOf(page[preRecord+14] & 0xff);
        if (hexUtil.int2(page,preRecord+47)!=0){
            this.schemaName = hexUtil.parseString(page,preRecord+52,preRecord+size);
        }else {
            this.schemaName = hexUtil.parseString(page,preRecord+54,preRecord+size);
        }

        this.length = hexUtil.int2(page,preRecord+19);
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

    @Override
    public String toString() {
        return "schemaRecord{" +
                "type='" + type + '\'' +
                ", tableId=" + tableId +
                ", schemaName='" + schemaName + '\'' +
                ", columnid=" + columnid +
                ", length=" + length +
                '}';
    }
}
