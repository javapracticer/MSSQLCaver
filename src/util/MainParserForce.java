package util;

import domain.*;
import schema.SchemaRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 这是一个整合类，将全自动的执行查询操作，然后返回一个表的map
 */
public class MainParserForce {
    static byte[][] read = PageUtils.getPages();
    public static List<Map<String, String>> parsetTable(String tableid) throws IOException {
        //这个map按逻辑顺序储存了schema
        Map<Long, SchemaRecord> schemaMap = PageUtils.getTableSchema(tableid);
        List<Ischema> schemaList = new ArrayList<>();
        List<Map<String, String>> maps = PageUtils.getRowSetIdByTableId(tableid);
        String rowSetId ="";
        for (Map<String, String> map : maps) {
            if (map.get("idmajor").equals(tableid)&&(map.get("idminor").equals("1")||map.get("idminor").equals("0"))){
                rowSetId= map.get("rowsetid");
            }
        }
        String allocationUnitID = PageUtils.getId7ObjPage(rowSetId).get("auid");
        Map<Integer, Integer> colmap = PageUtils.id3objPageRecords(rowSetId);
        Long aLong = Long.valueOf(allocationUnitID);
        Long indexID = aLong >> 48;
        Long idObj = (aLong - (indexID << 48)) >> 16;
        //将map里的schema按物理顺序拿出
        for (int i = 1; i <= schemaMap.size(); i++) {
            SchemaRecord SchemaRecord = schemaMap.get((long)i);
            schemaList.add(PageUtils.schemaBuilder(Integer.valueOf(SchemaRecord.getType()), SchemaRecord.getLength(), SchemaRecord.getSchemaName()));
        }
        PageUtils.schemaSorter(schemaList,colmap);
        List<Map<String,String>> result = new ArrayList<Map<String, String>>();
        for (byte[] bytes : read) {
            PageHeader header = new PageHeader(bytes);
            if (header.getIndexId() == indexID.intValue() && header.getIdObj() == idObj.intValue() && header.getType() == 1) {
                List<byte[]> records = RecordCuter.cutRrcord(bytes, header.getSlotCnt());
//                List<byte[]> records = DeletedRecordCuter.cutRrcord(bytes, header.getFreeData());
                result.addAll(RawColumnParser.prserRecord(records, schemaList));

            }

        }
        return result;
    }



}
