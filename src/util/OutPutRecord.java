package util;
import domain.Ischema;
import schema.SchemaRecord;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class OutPutRecord {
    public static String tableName = "";
    private  static StringBuilder sb = new StringBuilder();

    public static void outPutRecordAsSql(List<Ischema> schemas){
        sb.append("CREATE TABLE "+tableName);
        sb.append("(\n");
        for (Ischema schema : schemas) {
            sb.append(schema.name()+" "+schema.getSqlSchema()+",\n");
        }
        sb.deleteCharAt(sb.length()-2);
        sb.append(")");
        String sql = sb.toString();
        File file = new File("C:\\Users\\s6560\\Documents\\sqlsample\\test.sql");
            try {

                if (!file.exists()) {
                    file.createNewFile();
                }
                FileWriter fileWriter = new FileWriter(file.getAbsoluteFile(),true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(sql);
                bufferedWriter.close();
                System.out.println("输出完成");
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    public static void outPutRecord(){

    }
}
