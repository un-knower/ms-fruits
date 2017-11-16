package wowjoy.fruits.ms.module.elastic;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.elasticsearch.search.SearchHit;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

/**
 * Created by wangziwen on 2017/9/27.
 */
public class GlobalSearch<T> extends AbstractEntity {
    private String title;
    private T entity;
    private String content;
    private FruitDict.Parents type;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
        this.content = new Gson().toJsonTree(entity).toString();
    }

    public FruitDict.Parents getType() {
        return type;
    }

    public void setType(FruitDict.Parents type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static <T extends AbstractEntity> GlobalSearch getInstance(Class<T> tClass) {
        return new GlobalSearch<T>();
    }

    public static GlobalSearchDao newDao(SearchHit hit) {
        GlobalSearchDao searchDao = new Gson().fromJson(new Gson().toJsonTree(hit.getSource()), TypeToken.of(GlobalSearchDao.class).getType());
        searchDao.putAllHighlight(hit.getHighlightFields());
        return searchDao;
    }

    public static JsonObject mappings() {
        JsonObject result = new JsonObject();
        result.add("properties", new JsonObject());
        JsonObject propertied = new JsonObject();
        propertied.addProperty("type", "text");
        propertied.addProperty("analyzer", "ik_max_word");
        JsonObject properties = result.get("properties").getAsJsonObject();
        properties.add("content", propertied);
        return result;
    }

}
