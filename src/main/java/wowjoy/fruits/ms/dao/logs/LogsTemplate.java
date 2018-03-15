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
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.LogsDict;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by wangziwen on 2017/12/22.
 * Tips:
 *  wangziwen-2018年03月08日09:54:55：2.5.0版本中保留，预计2.6.0版本移除
 */
@Deprecated
public abstract class LogsTemplate<T extends AbstractEntity> {
    private Map<LogsDict, String> templates = Maps.newLinkedHashMap();
    private final static LinkedHashMap<FruitDict.Parents, Supplier<LogsTemplate>> instanceFactory = Maps.newLinkedHashMap();

    static {
        instanceFactory.put(FruitDict.Parents.NOTEPAD, NotepadTemplate::new);
        instanceFactory.put(FruitDict.Parents.PLAN, PlanTemplate::new);
        instanceFactory.put(FruitDict.Parents.TASK, TaskTemplate::new);
    }

    public FruitLogsDao msg(FruitLogsDao logs, TemplateFunction<AbstractEntity, String> template) {
        /*替换个性化参数*/
        String msg = replace(fromJson(logs), template.apply(logs, getTemplates(logs.getOperateType())));
        /*替换全局参数*/
        msg = replace(logs.getUser(), "user", msg);
        logs.setMsg(msg);
        return logs;
    }

    public static LogsTemplate newInstance(FruitDict.Parents parents) {
        return instanceFactory.get(parents).get();
    }

    public String getTemplates(LogsDict logsDict) {
        return templates.get(logsDict);
    }

    public void setTemplates(LogsDict logsDict, String msgSupplier) {
        this.templates.put(logsDict, msgSupplier);
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
            if (!msg.contains(position)) continue;
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
        private final String DELETE = "{user.userName} 删除了日报";

        public NotepadTemplate() {
            super.setTemplates(LogsDict.ADD, ADD);
            super.setTemplates(LogsDict.UPDATE, UPDATE);
            super.setTemplates(LogsDict.DELETE, DELETE);
        }

        @Override
        public T fromJson(FruitLogsDao log) {
            return new Gson().fromJson(new JsonParser().parse(log.getJsonObject()), TypeToken.of(FruitNotepadDao.class).getType());
        }
    }

    protected static class PlanTemplate<T extends FruitPlanDao> extends LogsTemplate {
        private final String ADD = "{user.userName} 添加了计划";
        private final String UPDATE = "{user.userName} 修改了计划";
        private final String DELETE = "{user.userName} 删除了计划";
        private final String COMPLETE = "{user.userName} 按时完成了目标";
        private final String DELAY_COMPLETE = "{user.userName} 延期 {daysTemplate} 天完成了目标，延期原因：{statusDescription}";
        private final String END = "{user.userName} 标记目标为已终止，终止理由：{statusDescription}";
        private final String PENDING = "{user.userName} 标记目标为进行中";

        public PlanTemplate() {
            super.setTemplates(LogsDict.ADD, ADD);
            super.setTemplates(LogsDict.UPDATE, UPDATE);
            super.setTemplates(LogsDict.DELETE, DELETE);
            super.setTemplates(LogsDict.COMPLETE, COMPLETE);
            super.setTemplates(LogsDict.DELAY_COMPLETE, DELAY_COMPLETE);
            super.setTemplates(LogsDict.END, END);
            super.setTemplates(LogsDict.PENDING, PENDING);
        }

        @Override
        public T fromJson(FruitLogsDao log) {
            T plan = new Gson().fromJson(new JsonParser().parse(log.getJsonObject()), TypeToken.of(FruitPlanDao.class).getType());
            plan.computeDays();
            if (plan.getPlanStatus().equals(FruitDict.PlanDict.COMPLETE.name()) && plan.getDays() < 0)
                log.setOperateType(LogsDict.valueOf(FruitDict.PlanDict.DELAY_COMPLETE.name()));
            return plan;
        }
    }

    protected static class TaskTemplate<T extends FruitTaskDao> extends LogsTemplate {
        private final String ADD = "{user.userName} 添加了任务";
        private final String UPDATE = "{user.userName} 修改了任务";
        private final String DELETE = "{user.userName} 删除了任务";
        private final String MOVE_TASK = "{user.userName} 改变了任务所在列表";
        private final String START = "{user.userName} 启动了任务";
        private final String COMPLETE = "{user.userName} 完成了任务";
        private final String END = "{user.userName} 终止了任务，终止理由：{statusDescription}";
        private final String HANDOVER = "{user.userName} 改变了任务执行人";

        public TaskTemplate() {
            super.setTemplates(LogsDict.ADD, ADD);
            super.setTemplates(LogsDict.UPDATE, UPDATE);
            super.setTemplates(LogsDict.DELETE, DELETE);
            super.setTemplates(LogsDict.COMPLETE, COMPLETE);
            super.setTemplates(LogsDict.START, START);
            super.setTemplates(LogsDict.MOVE_TASK, MOVE_TASK);
            super.setTemplates(LogsDict.END, END);
            super.setTemplates(LogsDict.STAFF_CHANGE, HANDOVER);
        }

        @Override
        public T fromJson(FruitLogsDao log) {
            return new Gson().fromJson(new JsonParser().parse(log.getJsonObject()), TypeToken.of(FruitTaskDao.class).getType());
        }
    }
}
