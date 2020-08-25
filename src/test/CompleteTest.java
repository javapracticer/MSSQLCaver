package test;

import domain.PageHeader;
import title.TitlePage;
import util.MainParserIndex;
import util.PageUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CompleteTest {
    public static String mkdir;
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
                if (split[1].equals("mdf")||split[1].equals("ndf")){
                    flage = false;
                }else {
                    System.out.println("请输入正确的MDF文件的路径！");
                }
            }catch (Exception e){
                System.out.println(e);
            }
        }
        byte[][] read = PageUtils.getPages();
        List<Map<String, String>> list = new ArrayList<>();
        for (byte[] bytes : read) {
            PageHeader header = new PageHeader(bytes);
            if (header.getType()==1&&header.getIdObj()==34){
                list.addAll(TitlePage.parserTitle(bytes));
            }
        }
        int i = 0;
        List<Map<String,String>> titles = new ArrayList<>();
        for (Map<String, String> map : list) {
            //将type为U的表打印出来
            if (map.get("type").equals("U ")){
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
            if(tableId.equals("quit")) {break;}
            long startTime = System.currentTimeMillis();
            try {
                //进入主解析类
                List<Map<String, String>> maps = MainParserIndex.parserTable(tableId);
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
            if (s.equals("quit")){
                break;
            }
        }
    }

}
