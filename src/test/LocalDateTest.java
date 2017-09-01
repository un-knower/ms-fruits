import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by wangziwen on 2017/8/24.
 */

public class LocalDateTest {
    public static void main(String[] args) {
        final String USERID = "63fe9ea2d1e14caab616b983df68880c";

        final FruitList fruitList = new FruitList();
        fruitList.setTitle("新需求");
        fruitList.setDescription("存放新的产品需求、产品bug");
        System.out.println(gson(fruitList));

        final ListUserRelation listUserRelation = new ListUserRelation();
        listUserRelation.setListId(fruitList.getUuid());
        listUserRelation.setUserId(USERID);
        System.out.println(gson(listUserRelation));

        final FruitTask fruitTask = new FruitTask();
        fruitTask.setListId(fruitList.getUuid());
        fruitTask.setTitle("创建模拟数据");
        fruitTask.setStartDate(LocalDate.now());
        fruitTask.setEndDate(LocalDate.now());
        fruitTask.setTaskLevel(FruitDict.TaskDict.HEIGH.name());
        fruitTask.setTaskStatus(FruitDict.TaskDict.START.name());
        System.out.println(gson(fruitTask));

        final TaskUserRelation taskUserRelation = new TaskUserRelation();
        taskUserRelation.setUserRole(FruitDict.TaskUserDict.PRINCIPAL.name());
        taskUserRelation.setTaskId(fruitTask.getUuid());
        taskUserRelation.setUserId(USERID);
        System.out.println(gson(taskUserRelation));

        final FruitPlan fruitPlan = new FruitPlan();
        fruitPlan.setTitle("重构智果后台");
        fruitPlan.setPlanContent("重构智果后台");
        fruitPlan.setPlanStatus(FruitDict.PlanDict.PENDING.name());
        fruitPlan.setStartDateTime(LocalDate.now());
        fruitPlan.setEndDateTime(LocalDate.now());
        System.out.println(gson(fruitPlan));

        final PlanUserRelation planUserRelation = new PlanUserRelation();
        planUserRelation.setPlanId(fruitPlan.getUuid());
        planUserRelation.setUserRole(FruitDict.PlanUserDict.PARTICIPANT.name());
        planUserRelation.setUserId(USERID);
        System.out.println(gson(fruitList));

        final TaskPlanRelation taskPlanRelation = new TaskPlanRelation();
        taskPlanRelation.setPlanId(fruitPlan.getUuid());
        taskPlanRelation.setTaskId(fruitTask.getUuid());
        System.out.println(gson(taskPlanRelation));

    }

    public static String gson(AbstractEntity abstractEntity) {
        return new Gson().toJsonTree(abstractEntity).toString();
    }
}
