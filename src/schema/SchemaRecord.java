package schema;
import util.HexUtil;
public class SchemaRecord {
    private String type;
    private Long tableId;
    private String schemaName;
    private Long columnid;
    private int length;
    private short prec;
    private short scale;
    public SchemaRecord(byte[] page, int preRecord, int size){
        this.columnid = HexUtil.int4(page,preRecord+10);
        this.tableId = HexUtil.int4(page,preRecord+4);
        this.type = String.valueOf(page[preRecord+14] & 0xff);
        if (HexUtil.int2(page,preRecord+47)!=0){
            this.schemaName = HexUtil.parseString(page,preRecord+53,preRecord+size);
        }else {
            this.schemaName = HexUtil.parseString(page,preRecord+55,preRecord+size);
        }

        this.length = HexUtil.int2(page,preRecord+19);
        this.prec = page[preRecord+21];
        this.scale = page[preRecord+22];
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

    public short getPrec() {
        return prec;
    }

    public void setPrec(short prec) {
        this.prec = prec;
    }

    public short getScale() {
        return scale;
    }

    public void setScale(short scale) {
        this.scale = scale;
    }

    @Override
    public String toString() {
        return "SchemaRecord{" +
                "type='" + type + '\'' +
                ", tableId=" + tableId +
                ", schemaName='" + schemaName + '\'' +
                ", columnid=" + columnid +
                ", length=" + length +
                ", prec=" + prec +
                ", scale=" + scale +
                '}';
    }
}
