package wowjoy.fruits.ms.elastic;

import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

/**
 * Created by wangziwen on 2017/9/26.
 */
public abstract class AbstractElastic {
    @Autowired
    protected TransportClient esClient;
    protected final String Index = FruitDict.Parents.MS_FRUITS.name().toLowerCase();
}
