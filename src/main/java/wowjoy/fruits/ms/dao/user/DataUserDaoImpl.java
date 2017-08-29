package wowjoy.fruits.ms.dao.user;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import wowjoy.fruits.ms.module.user.entity.FruitUser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wangziwen on 2017/8/25.
 */
@Service
@PropertySource(value = "classpath:/application.properties")
public class DataUserDaoImpl extends AbstractUser {
    @Value("${department.tree}")
    private String datas;

    private List<FruitUser> users;

    private void addUsers(FruitUser user) {
        boolean boo = true;
        for (FruitUser fruitUser : this.getUsers()) {
            if (fruitUser.getUserId().equals(user.getUserId()))
                boo = false;
        }
        if (boo) this.getUsers().add(user);
    }

    private List<FruitUser> getUsers() {
        if (users == null)
            users = Lists.newLinkedList();
        return users;
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
                    this.addUsers(fruitUser);
                }
        }
    }

    private void loading() {
        this.parset(new JsonParser().parse(this.datas).getAsJsonObject().get("result").getAsJsonObject());
    }

    private boolean classMethod(FruitUser user, FruitUser each) {
        for (Method method : FruitUser.class.getDeclaredMethods()) {
            if (method.getName().indexOf("get") == -1) continue;
            try {
                if (method.invoke(user) == null) continue;
                if (!each.getClass().getDeclaredMethod(method.getName()).invoke(each).equals(method.invoke(user)))
                    return false;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public List<FruitUser> findByUser(FruitUser user) {
        this.loading();
        final LinkedList<FruitUser> result = Lists.newLinkedList();
        this.getUsers().forEach((i) -> {
            if (this.classMethod(user, i))
                result.add(i);
        });
        return result;

    }


}