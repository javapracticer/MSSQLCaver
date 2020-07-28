package domain;

public enum SchemaType {
    Char(175,"char"),
    Int(56,"int"),
    Text(35,"text"),
    Nchar(239,"nchar"),
    Bit(98,"bit"),
    Money(60,"money"),
    Bigint(127,"bigint"),
    Datetime(61,"datetime"),
    Decimal(106,"decimal"),
    Float(62,"float"),
    Image(34,"image"),
    Numeric(108,"numeric"),
    Real(59,"real"),
    SmallDatatime(58,"samlldatetime"),
    SmallInt(52,"smallint"),
    SmallMoney(122,"smallmoney"),
    TimeStamp(189,"timeStamp"),
    TimyInt(48,"timtint"),
    UniqueIdentifier(36,"uniqueidentifier"),
    Varchar(167,"varchar"),
    Ntext(99,"ntext"),
    Nvarchar(231,"nvarchar"),
    Varbinary(165,"varbinary"),
    Xml(241,"xml");


    private final Integer value;
    private final String filed;

    SchemaType(int i, String s) {
        value = i;
        filed = s;
    }
    public Integer getValue() {
        return value;
    }

    public String getFiled() {
        return filed;
    }

    public static SchemaType codeOf(int code) {
        for (SchemaType types : values()) {
            if (types.getValue() == code) {
                return types;
            }
        }
        throw new RuntimeException("没有找到对应的枚举");
    }

    public static void main(String[] args) {
        System.out.println(SchemaType.codeOf(175).getFiled());
    }
}
