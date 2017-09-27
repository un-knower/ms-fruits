import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Test;
import wowjoy.fruits.ms.module.project.FruitProject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

/**
 * Created by wangziwen on 2017/9/26.
 */
public class ElasticsearchTest {
    private static TransportClient client;

    public ElasticsearchTest() throws UnknownHostException {
        client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddresses(new InetSocketTransportAddress(InetAddress.getByName("effiy.cn"), 9300));
    }

    @Test
    public void preparIndex() throws ExecutionException, InterruptedException {
        FruitProject fruitProject = new FruitProject();
        JsonElement jsonElement = new Gson().toJsonTree(fruitProject);
//        client.prepareIndex("ms_fruits", "project", "1").setSource(jsonElement.toString(), XContentType.JSON).execute();
//        ListenableActionFuture<IndicesExistsResponse> fruits_test = client.admin().indices().prepareExists("fruits_test").execute();
//        System.out.println(fruits_test.get().isExists());
//        ListenableActionFuture<IndicesExistsResponse> fruits_test1 = client.admin().indices().prepareExists("fruits_tesst").execute();
//        System.out.println(fruits_test1.get().isExists());
    }
}
