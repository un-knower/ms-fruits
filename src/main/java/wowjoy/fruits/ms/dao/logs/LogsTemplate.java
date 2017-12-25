package wowjoy.fruits.ms.dao.logs;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.logs.FruitLogs;
import wowjoy.fruits.ms.module.notepad.FruitNotepadDao;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Map;

/**
 * Created by wangziwen on 2017/12/22.
 */
public abstract class LogsTemplate<T extends AbstractEntity> {
    private static final FruitUser CurrentUser = ApplicationContextUtils.getCurrentUser();
    private Map<FruitDict.LogsDict, String> templates = Maps.newLinkedHashMap();

    public void setTemplates(FruitDict.LogsDict logsDict, String template) {
        this.templates.put(logsDict, template);
    }

    public String getTemplates(FruitDict.LogsDict logsDict) {
        return templates.get(logsDict);
    }

    public String msg(FruitLogs logs) {
        T obj = fromJson(logs.getJsonObject());
        /*替换个性化参数*/
        String msg = replace(obj, getTemplates(logs.getOperateType()));
        /*替换全局参数*/
        return replace(CurrentUser, "user", msg);
    }

    public String replace(AbstractEntity obj, String msg) {
        return replace(obj, obj.getClass(), "", msg);
    }

    public String replace(AbstractEntity obj, String prefix, String msg) {
        return replace(obj, obj.getClass(), prefix, msg);
    }

    public String replace(AbstractEntity obj, Class aClass, String prefix, String msg) {
        String position;
        for (Field field : aClass.getDeclaredFields()) {
            position = "{" + MessageFormat.format("{0}{1}", StringUtils.isNotBlank(prefix) ? prefix + "." : "", field.getName()) + "}";
            if (msg.indexOf(position) == -1) continue;
            for (Method method : aClass.getDeclaredMethods()) {
                if (method.getName().toLowerCase().indexOf(field.getName().toLowerCase()) == -1) continue;
                try {
                    return msg.replace(position, new Gson().toJsonTree(method.invoke(obj, null)).getAsString());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new CheckException("获取占位值错误");
                }
            }
        }
        if (aClass.getSuperclass().getName().equals(Object.class.getName())) return msg;
        return replace(obj, aClass.getSuperclass(), prefix, msg);
    }

    public abstract T fromJson(String json);

    public static LogsTemplate newInstance(FruitDict.Parents parents) {
        switch (parents) {
            case NOTEPAD:
                return new NotepadTemplate<>();
            default:
                return null;
        }

    }

    public static class NotepadTemplate<T extends FruitNotepadDao> extends LogsTemplate {
        private final String ADD = "{user.userName} 添加了日报";
        private final String UPDATE = "{user.userName} 修改了日报";
        private final String DELETE = "{user.userName} 删除了日报（这特码谁给开放的接口）";

        public NotepadTemplate() {
            setTemplates(FruitDict.LogsDict.ADD, ADD);
            setTemplates(FruitDict.LogsDict.UPDATE, UPDATE);
            setTemplates(FruitDict.LogsDict.DELETE, DELETE);
        }

        @Override
        public T fromJson(String json) {
            return new Gson().fromJson(new JsonParser().parse(json), TypeToken.of(FruitNotepadDao.class).getType());
        }
    }
}
