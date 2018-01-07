package wowjoy.fruits.ms.dao.logs;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.logs.FruitLogsDao;
import wowjoy.fruits.ms.module.notepad.FruitNotepadDao;
import wowjoy.fruits.ms.module.plan.FruitPlanDao;
import wowjoy.fruits.ms.module.task.FruitTaskDao;
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

    public String msg(FruitLogsDao logs) {
        T obj = fromJson(logs);
        /*替换个性化参数*/
        String msg = replace(obj, getTemplates(logs.getOperateType()));
        /*替换全局参数*/
        return replace(CurrentUser, "user", msg);
    }

    public static LogsTemplate newInstance(FruitDict.Parents parents) {
        switch (parents) {
            case NOTEPAD:
                return new NotepadTemplate<>();
            case PLAN:
                return new PlanTemplate<>();
            case TASK:
                return new TaskTemplate<>();
            default:
                return null;
        }

    }

    protected void setTemplates(FruitDict.LogsDict logsDict, String template) {
        this.templates.put(logsDict, template);
    }

    protected String getTemplates(FruitDict.LogsDict logsDict) {
        return templates.get(logsDict);
    }

    private String replace(AbstractEntity obj, String msg) {
        return replace(obj, obj.getClass(), "", msg);
    }

    private String replace(AbstractEntity obj, String prefix, String msg) {
        return replace(obj, obj.getClass(), prefix, msg);
    }

    private String replace(AbstractEntity obj, Class aClass, String prefix, String msg) {
        String position;
        for (Field field : aClass.getDeclaredFields()) {
            position = "{" + MessageFormat.format("{0}{1}", StringUtils.isNotBlank(prefix) ? prefix + "." : "", field.getName()) + "}";
            if (msg.indexOf(position) == -1) continue;
            for (Method method : aClass.getDeclaredMethods()) {
                if (!method.getName().toLowerCase().equals("get" + field.getName().toLowerCase())) continue;
                try {
                    Object methodResult = method.invoke(obj, null);
                    JsonElement asString = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJsonTree(methodResult);
                    msg = msg.replace(position, asString.isJsonNull() ? "" : asString.getAsString());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new CheckException("获取占位值错误");
                }
            }
        }
        if (aClass.getSuperclass().getName().equals(Object.class.getName())) return msg;
        return replace(obj, aClass.getSuperclass(), prefix, msg);
    }

    protected abstract T fromJson(FruitLogsDao log);

    protected static class NotepadTemplate<T extends FruitNotepadDao> extends LogsTemplate {
        private final String ADD = "{user.userName} 添加了日报";
        private final String UPDATE = "{user.userName} 修改了日报";
        private final String DELETE = "{user.userName} 删除了日报（这特码谁给开放的接口）";

        public NotepadTemplate() {
            super.setTemplates(FruitDict.LogsDict.ADD, ADD);
            super.setTemplates(FruitDict.LogsDict.UPDATE, UPDATE);
            super.setTemplates(FruitDict.LogsDict.DELETE, DELETE);
        }

        @Override
        public T fromJson(FruitLogsDao log) {
            return new Gson().fromJson(new JsonParser().parse(log.getJsonObject()), TypeToken.of(FruitNotepadDao.class).getType());
        }
    }

    protected static class PlanTemplate<T extends FruitPlanDao> extends LogsTemplate {
        private final String ADD = "{user.userName} 添加了计划";
        private final String UPDATE = "{user.userName} 修改了计划";
        private final String DELETE = "{user.userName} 删除了计划（这特码谁给开放的接口）";
        private final String COMPLETE = "{user.userName} 按时完成了目标";
        private final String DELAY_COMPLETE = "{user.userName} 延期 {daysTemplate} 天完成了目标，延期原因：{statusDescription}";
        private final String END = "{user.userName} 标记目标为已终止，终止理由：{statusDescription}";

        public PlanTemplate() {
            super.setTemplates(FruitDict.LogsDict.ADD, ADD);
            super.setTemplates(FruitDict.LogsDict.UPDATE, UPDATE);
            super.setTemplates(FruitDict.LogsDict.DELETE, DELETE);
            super.setTemplates(FruitDict.LogsDict.COMPLETE, COMPLETE);
            super.setTemplates(FruitDict.LogsDict.DELAY_COMPLETE, DELAY_COMPLETE);
            super.setTemplates(FruitDict.LogsDict.END, END);
        }

        @Override
        public T fromJson(FruitLogsDao log) {
            T plan = new Gson().fromJson(new JsonParser().parse(log.getJsonObject()), TypeToken.of(FruitPlanDao.class).getType());
            plan.computeDays();
            if (plan.getPlanStatus().equals(FruitDict.PlanDict.COMPLETE.name()) && plan.getDays() < 0)
                log.setOperateType(FruitDict.LogsDict.valueOf(FruitDict.PlanDict.DELAY_COMPLETE.name()));
            return plan;
        }
    }

    protected static class TaskTemplate<T extends FruitTaskDao> extends LogsTemplate {
        private final String ADD = "{user.userName} 添加了任务";
        private final String UPDATE = "{user.userName} 修改了任务";
        private final String DELETE = "{user.userName} 删除了任务（这特码谁给开放的接口）";
        private final String MOVE_TASK = "{user.userName} 改变了任务所在列表";
        private final String START = "{user.userName} 启动了任务";
        private final String END = "{user.userName} 结束了任务";

        public TaskTemplate() {
            super.setTemplates(FruitDict.LogsDict.ADD, ADD);
            super.setTemplates(FruitDict.LogsDict.UPDATE, UPDATE);
            super.setTemplates(FruitDict.LogsDict.DELETE, DELETE);
            super.setTemplates(FruitDict.LogsDict.END, END);
            super.setTemplates(FruitDict.LogsDict.START, START);
            super.setTemplates(FruitDict.LogsDict.MOVE_TASK, MOVE_TASK);
        }

        @Override
        public T fromJson(FruitLogsDao log) {
            return new Gson().fromJson(new JsonParser().parse(log.getJsonObject()), TypeToken.of(FruitTaskDao.class).getType());
        }
    }
}
