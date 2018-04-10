import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.elasticsearch.client.transport.TransportClient;
import org.junit.Test;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.task.FruitTask;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

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
        List<Integer> collect = IntStream.range(0, 100).boxed().collect(toList());
        collect.sort((l,r)-> l > r ? -1: Objects.equals(l, r) ?0:1);
        collect.stream().peek(System.out::println).collect(toList());
    }

    public FruitTask createTask(FruitDict.TaskDict taskDict) {
        FruitTask fruitTask = new FruitTask();
        fruitTask.setTaskStatus(taskDict.name());
        return fruitTask;
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

    }

}
