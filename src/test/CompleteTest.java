package test;

import domain.PageHeader;
import title.TitlePage;
import util.MainParserForce;
import util.PageUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CompleteTest {
    private static String mkdir;
    public static void main(String[] args) throws IOException {
        boolean flage = true;
        while (flage){
            Scanner dir = new Scanner(System.in);
            System.out.println("请输入文件路径");
            mkdir = dir.nextLine();
            try {
                File file = new File(mkdir);
                String name = file.getName();
                String[] split = name.split("\\.");
                if ("mdf".equals(split[1])||"ndf".equals(split[1])){
                    flage = false;
                }else {
                    System.out.println("请输入正确的MDF文件的路径！");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        PageUtils.setfile(mkdir);
        byte[][] read = PageUtils.getPages();
        List<byte[]> titlePages = new ArrayList<>();
        for (byte[] bytes : read) {
            PageHeader header = new PageHeader(bytes);
            if (header.getType()==1&&header.getIdObj()==34){
                titlePages.add(bytes);
            }
        }
        List<Map<String, String>> titleRecoreds = TitlePage.parserTitle(titlePages);
        List<Map<String,String>> titles = new ArrayList<>();
        for (Map<String, String> map : titleRecoreds) {
            //将type为U的表打印出来
            if ("U ".equals(map.get("type"))){
                titles.add(map);
            }
        }
        while (true){
            for (Map<String, String> title : titles) {
                System.out.println(title);
            }
            Scanner sc = new Scanner(System.in);
            System.out.println("请输入要查找的表id:                              (退出请输入quit)");
            String tableId = sc.nextLine();
            if("quit".equals(tableId)) {break;}
            long startTime = System.currentTimeMillis();
            try {
//                for (Map<String, String> title : titles) {
//                    if (title.get("id").equals(tableId)){
//                        OutPutRecord.tableName = title.get("name");
//                    }
//                }
                //进入主解析类
                List<Map<String, String>> maps = MainParserForce.parsetTable(tableId);
//                OutPutRecord.outPutRecord(maps);
                for (Map<String, String> map : maps) {
                    System.out.println(map);
                }
                System.out.println(maps.size());
            }catch (Exception e){
                e.printStackTrace();
            }
            long endTime = System.currentTimeMillis();
            System.out.println("总共耗时:"+(endTime-startTime));
            System.out.println("继续查询请按回车，结束请输入quit");
            Scanner sc2 = new Scanner(System.in);
            String s = sc2.nextLine();
            if ("quit".equals(s)){
                break;
            }
        }
    }

}
