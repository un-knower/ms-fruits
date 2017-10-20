import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.elasticsearch.client.transport.TransportClient;
import org.junit.Test;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.util.JwtUtils;

import java.net.UnknownHostException;
import java.time.LocalDateTime;
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
    public void preparIndex() throws ExecutionException, InterruptedException {
        FruitProject fruitProject = new FruitProject();
        JsonElement jsonElement = new Gson().toJsonTree(fruitProject);
        System.out.println(esClient.admin().indices().prepareTypesExists("ms_fruits").setTypes("s").execute().get().isExists());
    }

    @Test
    public void name() throws Exception {
        FruitProject project = new FruitProject();
        System.out.println(new Gson().toJsonTree(project).toString());
    }

    @Test
    public void sss() throws Exception {
        String token = JwtUtils.token(JwtUtils.newHeader(), JwtUtils.newPayLoad("111", LocalDateTime.now().plusDays(1), LocalDateTime.now()));
        System.out.println(token);
    }
}
