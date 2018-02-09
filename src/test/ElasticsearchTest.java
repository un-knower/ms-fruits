import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.elasticsearch.client.transport.TransportClient;
import org.junit.Test;
import wowjoy.fruits.ms.module.project.FruitProject;

import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by wangziwen on 2017/9/26.
 */
public class ElasticsearchTest {
    private static TransportClient esClient;

    public ElasticsearchTest() throws UnknownHostException {
//        esClient = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddresses(new InetSocketTransportAddress(InetAddress.getByName("effiy.cn"), 9300));
    }

    @Test
    public void dateUtils() throws Exception {
//        DateUtils.Month<DateUtils.Week.WeekChinese> monthByYearMonth = DateUtils.getMonthByYearMonth(2018, 2);
//        System.out.println(monthByYearMonth.getStartDate());
//        System.out.println(monthByYearMonth.getEndDate());
//        LocalDateTime localDateTime1 = LocalDateTime.of(2018, 2, 8, 23, 59, 59);
//        LocalDateTime localDateTime = LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault()).withHour(0).withMinute(0).withSecond(0);
//        System.out.println(localDateTime.toString());
//        System.out.println(localDateTime1.toString());
//        System.out.println(Duration.between(localDateTime, localDateTime1).toDays());
    }

    @Test
    public void preparIndex() throws ExecutionException, InterruptedException {
        FruitProject fruitProject = new FruitProject();
        JsonElement jsonElement = new Gson().toJsonTree(fruitProject);
        System.out.println(esClient.admin().indices().prepareTypesExists("ms_fruits").setTypes("s").execute().get().isExists());
    }

    @Test
    public void name() throws Exception {
        ArrayList<Object> objects = new ArrayList<>(Arrays.asList(new Object[10]));
        System.out.println(objects.size());
    }

    @Test
    public void test() {
        LinkedList<String> objects = Lists.newLinkedList();
        objects.add("1");
        objects.add("1");
        objects.add("1");
        objects.add("1");
        objects.add("1");
        objects.add("1");
        objects.add("1");
        objects.add("1");
        objects.add("1");
        objects.add("1");
        objects.add("1");
        objects.add("1");
        objects.add("1");
        Optional<String> reduce = objects.stream().reduce((old, news) -> old + news);
        System.out.println(reduce);
        System.out.println(reduce.get());
    }

}
