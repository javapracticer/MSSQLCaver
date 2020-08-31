package util;
import domain.Ischema;
import schema.SchemaRecord;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OutPutRecord {
    public static String tableName = "";
    private  static StringBuilder sb = new StringBuilder();
    private static List<Ischema> schemalist = new ArrayList<>();
    public static void outPutRecordAsSql(List<Ischema> schemas){
        StringBuilder sb2 = new StringBuilder();
        sb.append("CREATE TABLE "+tableName);
        sb.append("(\n");
        schemalist.addAll(schemas);
        for (Ischema schema : schemas) {
            sb.append(schema.name()+" "+schema.getSqlSchema()+",\n");
        }
        sb.deleteCharAt(sb.length()-2);
        sb.append(")\n");
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
                System.out.println("表格输出完成");
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    public static void outPutRecord(List<Map<String, String>> records){
        StringBuilder sb = new StringBuilder();
        for (Map<String, String> record : records) {
            sb.append("insert into "+tableName+" values(");
            for (Ischema schema : schemalist) {
                if (schema.getType()==175||schema.getType()==35||schema.getType()==239){
                    sb.append("'"+record.get(schema.name())+"',");
                }else {
                    sb.append(record.get(schema.name())+",");
                }
            }
            sb.deleteCharAt(sb.length()-1);
            sb.append(")\n");
        }
        File file = new File("C:\\Users\\s6560\\Documents\\sqlsample\\test.sql");
        try {

            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile(),true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(sb.toString());
            bufferedWriter.close();
            System.out.println("记录输出完成");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
