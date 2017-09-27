package wowjoy.fruits.ms.elastic;

import com.google.gson.Gson;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wowjoy.fruits.ms.module.AbstractEntity;

/**
 * Created by wangziwen on 2017/9/26.
 */
@Service
public class GlobalSearchEs extends AbstractElastic {
    @Autowired
    private TransportClient esClient;

    /**
     * 添加全局搜索
     *
     * @param entity
     */
    public void insert(AbstractEntity entity) {
        esClient.prepareIndex(Index, Type, entity.getUuid())
                .setSource(new Gson().toJsonTree(entity).toString(), XContentType.JSON)
                .execute();
    }
}
