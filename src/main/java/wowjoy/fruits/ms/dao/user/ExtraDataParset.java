package wowjoy.fruits.ms.dao.user;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import wowjoy.fruits.ms.module.user.FruitUser;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by wangziwen on 2017/8/25.
 * hoc-hr系统-用户信息解析类
 * 用户这一块改为直接调用第三方接口
 */
@Service
public class ExtraDataParset {

    private ConcurrentMap<String, FruitUser> data = null;

    public ConcurrentMap<String, FruitUser> getData() {
        return this.data == null ? (this.data = new ConcurrentHashMap<>()) : this.data;
    }

    public LinkedList<FruitUser> getList() {
        final LinkedList<FruitUser> result = Lists.newLinkedList();
        this.getData().forEach((k, v) -> {
            result.add(v);
        });

        return result;
    }

    private void addData(FruitUser user) {
        if (this.getData().containsKey(user.getUserId())) return;
        this.getData().put(user.getUserId(), user);
    }

    private void parset(JsonObject data) {
        for (JsonElement childs : data.get("children").getAsJsonArray()) {
            if (childs.getAsJsonObject().has("children"))
                parset(childs.getAsJsonObject());
            if (childs.getAsJsonObject().has("type"))
                if ("staff".equals(childs.getAsJsonObject().get("type").getAsString())) {
                    FruitUser fruitUser = new FruitUser();
                    fruitUser.setJobTitle(childs.getAsJsonObject().get("jobtitle").getAsString());
                    fruitUser.setUserEmail(childs.getAsJsonObject().get("email").getAsString());
                    fruitUser.setUserId(childs.getAsJsonObject().get("id").getAsString());
                    fruitUser.setUserName(childs.getAsJsonObject().get("mydesc").getAsString());
                    this.addData(fruitUser);
                }
        }
    }

    public ExtraDataParset build() {
//        this.parset(new JsonParser().parse(this.datas).getAsJsonObject().get("result").getAsJsonObject());
        return this;
    }

}