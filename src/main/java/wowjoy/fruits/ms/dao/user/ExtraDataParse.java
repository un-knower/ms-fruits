package wowjoy.fruits.ms.dao.user;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.user.FruitAccount;
import wowjoy.fruits.ms.module.user.FruitAccountDao;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by wangziwen on 2017/8/25.
 * hoc-hr系统-用户信息解析类
 * 用户这一块改为直接调用第三方接口
 */
@Service
public class ExtraDataParse {

    private final ConcurrentMap<String, FruitUserDao> users = new ConcurrentHashMap<>();
    private final List<FruitAccountDao> accounts = Lists.newLinkedList();

    public ConcurrentMap<String, FruitUserDao> getUserMap() {
        return this.users;
    }

    public LinkedList<FruitUserDao> getUserList() {
        final LinkedList<FruitUserDao> result = Lists.newLinkedList();
        this.getUserMap().forEach((k, v) -> result.add(v));
        return result;
    }

    private void addUser(FruitUserDao user) {
        if (this.getUserMap().containsKey(user.getUserId())) return;
        this.getUserMap().put(user.getUserId(), user);
    }

    public List<FruitAccountDao> getAccounts() {
        return accounts;
    }

    public ExtraDataParse parseTree(JsonObject data) {
        for (JsonElement childs : data.get("children").getAsJsonArray()) {
            if (childs.getAsJsonObject().has("children"))
                parseTree(childs.getAsJsonObject());
            if (childs.getAsJsonObject().has("type"))
                if ("staff".equals(childs.getAsJsonObject().get("type").getAsString())) {
                    FruitUserDao fruitUser = FruitUser.getDao();
                    fruitUser.setUuid(AbstractEntity.UUID());
                    fruitUser.setJobTitle(childs.getAsJsonObject().get("jobtitle").getAsString());
                    fruitUser.setUserEmail(childs.getAsJsonObject().has("email") ? childs.getAsJsonObject().get("email").getAsString() : null);
                    fruitUser.setUserId(childs.getAsJsonObject().get("id").getAsString());
                    fruitUser.setUserName(childs.getAsJsonObject().get("mydesc").getAsString());
                    this.addUser(fruitUser);
                }
        }
        return this;
    }

    public ExtraDataParse parseAccount(JsonArray data) {
        data.forEach((i) -> {
            JsonObject account = i.getAsJsonObject();
            FruitAccountDao dao = FruitAccount.getDao();
            dao.setUuid(FruitAccount.getVo().getUuid());
            dao.setPrincipal(account.get("account_name").getAsString());
            dao.setUserId(account.get("user_id").getAsString());
            dao.setType(accountType(account.get("account_type").getAsInt()));
            FruitUserDao user = this.getUserMap().get(dao.getUserId());
            if (user != null) {
                user.getAccounts().add(dao);
                accounts.add(dao);
            }
        });
        return this;
    }

    private String accountType(Integer accountType) {
        switch (accountType) {
            case 0:
                return FruitDict.AccountDict.TEMP.getParentCode();
            case 1:
                return FruitDict.AccountDict.PRIVATE_EMAIL.getParentCode();
            case 2:
                return FruitDict.AccountDict.PHONE.getParentCode();
            case 3:
                return FruitDict.AccountDict.JOB_NUMBER.getParentCode();
            case 4:
                return FruitDict.AccountDict.COMPANY_EMAIL.getParentCode();
            default:
                return "-1";
        }
    }

    public static ExtraDataParse getInstance() {
        return new ExtraDataParse();
    }

}