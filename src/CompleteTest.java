import domain.PageHeader;
import title.TitlePage;
import title.TitleRecord;
import util.MainParserIndex;
import util.PageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CompleteTest {
    public static void main(String[] args) throws IOException {
        byte[][] read = PageUtils.getPages();
        List<TitlePage> list = new ArrayList<>();
        for (byte[] bytes : read) {
            PageHeader header = new PageHeader(bytes);
            if (header.getType()==1&&header.getIdObj()==34){
                TitlePage tp = new TitlePage(bytes);
                list.add(tp);
            }
        }
        int i = 0;
        for (TitlePage titlePage : list) {
            List<TitleRecord> list1 = titlePage.getList();
            for (TitleRecord titleRecord : list1) {
                if (titleRecord.getType()==8277){
                    System.out.println(titleRecord);
                }
            }
        }
        while (true){
            Scanner sc = new Scanner(System.in);
            System.out.println("请输入要查早的表id:                              (退出请输入quit)");
            String tableId = sc.nextLine();
            if(tableId.equals("quit")) {break;}
            long startTime = System.currentTimeMillis();
            try {
                List<Map<String, String>> maps = MainParserIndex.parserTable(tableId);
                for (Map<String, String> map : maps) {
                    System.out.println(map);
                }
            }catch (Exception e){
                System.out.println(e);
            }
            long endTime = System.currentTimeMillis();
            System.out.println("总共耗时:"+(endTime-startTime));
        }
    }

}
