package wowjoy.fruits.ms.module.util.entity;


import com.google.common.collect.Maps;
import wowjoy.fruits.ms.exception.ServiceException;
import wowjoy.fruits.ms.module.AbstractEntity;

import java.util.HashMap;
import java.util.function.Predicate;

/**
 * Created by wangziwen on 2017/8/24.
 * 数据字典
 */
public class FruitDict extends AbstractEntity {

    /**
     * 系统字典
     */
    public enum Systems {
        N("NO"),

        Y("YES"),

        MAN("男"),

        GIRLS("女"),

        BEFORE("提交前记录"),

        DELETE("删除标识"), ADD("添加标识"), SEARCH("查询"), UPDATE("更新"),

        DESC("倒序"), ASC("升序"),

        TEST("测试-数据Key");

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        Systems(String value) {
            this.value = value;
        }
    }

    public enum LogsDict {
        DELETE("删除"),
        ADD("添加"),
        UPDATE("修改"),
        MOVE_TASK("移动任务"),
        PENDING("进行中"),
        COMPLETE("完成"),
        DELAY_COMPLETE("延期完成"),
        START("开始"),
        END("结束"),
        CLOSE("关闭"),
        TRANSFER("转移"),
        /*缺陷处理*/
        TO_NEW("新开"),
        TO_SOLVED("已解决"),
        TO_CLOSED("已关闭"),
        TO_DELAY("延迟处理"),
        TO_DISREGARD("不予处理"),
        TO_REOPEN("重新打开");

        private String parentCode;
        private String value;

        private void setParentCode(String parentCode) {
            this.parentCode = parentCode;
        }

        private void setValue(String value) {
            this.value = value;
        }

        public String getParentCode() {
            return parentCode;
        }

        public String getValue() {
            return value;
        }

        LogsDict(String value) {
            this.setParentCode(Parents.PLAN.name());
            this.setValue(value);
        }

    }

    /**
     * dict 管理项目所有父节点，便于父节点的管理和维护。
     */
    public enum Parents {
        TRANSFER("转交"),
        PROJECT("项目"),
        SUMMARY("小结"),
        NOTEPAD("日报"),
        PLAN("计划"),
        TASK("任务"),
        USER("用户"),
        TEAM("团队"),
        List("列表"),
        VERSIONS("版本"),
        DEFECT("缺陷"),
        PLANUSER("计划-用户"),
        TASKUSER("任务-用户"),
        MEILESTONE("里程碑"),
        USERTEAM("用户-团队"),
        USERPROJECT("用户-项目"),
        MS_FRUITS("Elasticsearch-INDEX-TYPE"),
        MIME("MIME");

        private String value;

        private void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        Parents(String value) {
            this.setValue(value);
        }
    }

    /**
     * plan 状态管理
     */
    public enum PlanDict {
        STAY_PENDING("待执行"),
        PENDING("进行中"),
        COMPLETE("已完成"),
        END("已终止"),
        /*下列字段只用于数据统计，不在数据库中真实存放*/
        DELAY("延期"),
        DELAY_COMPLETE("延期完成");


        private String parentCode;
        private String value;

        private void setParentCode(String parentCode) {
            this.parentCode = parentCode;
        }

        private void setValue(String value) {
            this.value = value;
        }

        public String getParentCode() {
            return parentCode;
        }

        public String getValue() {
            return value;
        }

        PlanDict(String value) {
            this.setParentCode(Parents.PLAN.name());
            this.setValue(value);
        }
    }

    /**
     * project 状态管理
     */
    public enum ProjectDict {
        UNDERWAY("进行中"),
        COMPLETE("已完成");

        private String parentCode;
        private String value;

        private void setParentCode(String parentCode) {
            this.parentCode = parentCode;
        }

        private void setValue(String value) {
            this.value = value;
        }

        public String getParentCode() {
            return parentCode;
        }

        public String getValue() {
            return value;
        }


        ProjectDict(String value) {
            this.setParentCode(Parents.PROJECT.name());
            this.setValue(value);
        }
    }

    /**
     * task 状态管理
     */
    public enum TaskDict {
        /**
         * 任务优先级
         */
        LOW("低"),
        CENTRE("中"),
        HEIGHT("高"),
        /**
         * 任务状态
         */
        START("进行中"),
        COMPLETE("完成"),
        END("终止"),
        DENIAL("拒绝");


        private String parentCode;
        private String value;

        private void setParentCode(String parentCode) {
            this.parentCode = parentCode;
        }

        private void setValue(String value) {
            this.value = value;
        }

        public String getParentCode() {
            return parentCode;
        }

        public String getValue() {
            return value;
        }

        TaskDict(String value) {
            this.setParentCode(Parents.TASK.name());
            this.setValue(value);
        }
    }

    /**
     * milestone 里程碑管理
     */
    public enum MilestoneDict {
        UNDONE("未完成"),
        COMPLETE("已完成");


        private String parentCode;
        private String value;

        private void setParentCode(String parentCode) {
            this.parentCode = parentCode;
        }

        private void setValue(String value) {
            this.value = value;
        }

        public String getParentCode() {
            return parentCode;
        }

        public String getValue() {
            return value;
        }

        MilestoneDict(String value) {
            this.setParentCode(Parents.MEILESTONE.name());
            this.setValue(value);
        }
    }

    /************
     * relation *
     ************/

    /**
     * taskUserRelation 任务用户关联字典管理
     */
    public enum TaskUserDict {
        EXECUTOR("执行人");


        private String parentCode;
        private String value;

        private void setParentCode(String parentCode) {
            this.parentCode = parentCode;
        }

        private void setValue(String value) {
            this.value = value;
        }

        public String getParentCode() {
            return parentCode;
        }

        public String getValue() {
            return value;
        }

        TaskUserDict(String value) {
            this.setParentCode(Parents.TASKUSER.name());
            this.setValue(value);
        }
    }

    /**
     * planUserRelation 计划用户关联字典管理
     */
    public enum PlanUserDict {
        PRINCIPAL("负责人");

        private String parentCode;
        private String value;

        private void setParentCode(String parentCode) {
            this.parentCode = parentCode;
        }

        private void setValue(String value) {
            this.value = value;
        }

        public String getParentCode() {
            return parentCode;
        }

        public String getValue() {
            return value;
        }

        PlanUserDict(String value) {
            this.setParentCode(Parents.PLANUSER.name());
            this.setValue(value);
        }
    }

    /**
     * userTeamRelation 用户团队关联字典管理
     */
    public enum UserTeamDict {
        LEADER("领导者"),
        EXECUTOR("执行者");


        private String parentCode;
        private String value;

        private void setParentCode(String parentCode) {
            this.parentCode = parentCode;
        }

        private void setValue(String value) {
            this.value = value;
        }

        public String getParentCode() {
            return parentCode;
        }

        public String getValue() {
            return value;
        }

        UserTeamDict(String value) {
            this.setParentCode(Parents.USERTEAM.name());
            this.setValue(value);
        }
    }

    /**
     * userProjectRelation 用户项目关联字典管理
     */
    public enum UserProjectDict {
        PRINCIPAL("负责人"),
        PARTICIPANT("参与者");


        private String parentCode;
        private String value;

        private void setParentCode(String parentCode) {
            this.parentCode = parentCode;
        }

        private void setValue(String value) {
            this.value = value;
        }

        public String getParentCode() {
            return parentCode;
        }

        public String getValue() {
            return value;
        }

        UserProjectDict(String value) {
            this.setParentCode(Parents.USERTEAM.name());
            this.setValue(value);
        }
    }

    /**
     * ProjectTeamRelation 项目团队关联字典管理
     */
    public enum ProjectTeamDict {
        PRINCIPAL("负责团队"),
        PARTICIPANT("参与团队"),
        /*不记录数据库，中用在和前端数据交接时使用*/
        OTHER("其他团队");


        private String parentCode;
        private String value;

        private void setParentCode(String parentCode) {
            this.parentCode = parentCode;
        }

        private void setValue(String value) {
            this.value = value;
        }

        public String getParentCode() {
            return parentCode;
        }

        public String getValue() {
            return value;
        }

        ProjectTeamDict(String value) {
            this.setParentCode(Parents.USERPROJECT.name());
            this.setValue(value);
        }

    }

    /**
     * AccountDict 账户字典
     */
    public enum AccountDict {
        COMPANY_EMAIL("公司邮箱"),
        PRIVATE_EMAIL("私人邮箱"),
        PHONE("电话"),
        JOB_NUMBER("工号"),
        TEMP("临时用户");

        private String parentCode;
        private String value;

        private void setParentCode(String parentCode) {
            this.parentCode = parentCode;
        }

        private void setValue(String value) {
            this.value = value;
        }

        public String getParentCode() {
            return parentCode;
        }

        public String getValue() {
            return value;
        }

        AccountDict(String value) {
            this.setParentCode(Parents.USERPROJECT.name());
            this.setValue(value);
        }

    }

    /**
     * Notepad 记事本状态
     */
    public enum NotepadDict {
        PUNCTUAL_SUBMIT("准时提交"),
        PAY_SUBMIT("补交"),
        NOT_SUBMIT("未提交");

        private String parentCode;
        private String value;

        private void setParentCode(String parentCode) {
            this.parentCode = parentCode;
        }

        private void setValue(String value) {
            this.value = value;
        }

        public String getParentCode() {
            return parentCode;
        }

        public String getValue() {
            return value;
        }

        NotepadDict(String value) {
            this.setParentCode(Parents.NOTEPAD.name());
            this.setValue(value);
        }

    }

    public enum TransferDict {
        OLD("旧负责人"),
        NEW("新负责人");
        private String parentCode;
        private String value;

        public String getParentCode() {
            return parentCode;
        }

        private void setParentCode(String parentCode) {
            this.parentCode = parentCode;
        }

        public String getValue() {
            return value;
        }

        private void setValue(String value) {
            this.value = value;
        }

        TransferDict(String value) {
            this.parentCode = Parents.TRANSFER.name();
            this.value = value;
        }
    }

    public enum UserDict {
        ACTIVE("在职"),
        STILL("离职");
        private String parentCode;
        private String value;

        public String getParentCode() {
            return parentCode;
        }

        private void setParentCode(String parentCode) {
            this.parentCode = parentCode;
        }

        public String getValue() {
            return value;
        }

        private void setValue(String value) {
            this.value = value;
        }

        UserDict(String value) {
            this.parentCode = Parents.USER.name();
            this.value = value;
        }
    }

    public static class DefectDict {
        /*缺陷资源类型*/
        public enum Resource {
            DESCRIPTION("描述附带资源"),
            FORM("随表单上传资源");
            private String parentCode;
            private String value;

            Resource(String value) {
                this.parentCode = Parents.DEFECT.name();
                this.value = value;
            }
        }

        /*缺陷类型*/
        public enum Type {
            FUNCTION("功能"),
            API("接口"),
            LOGIC("逻辑"),
            DATA("数据"),
            COMPUTE("计算"),
            INTERFACE("界面"),
            DOCUMENT("文档"),
            PERFORMANCE("性能"),
            DEPLOY("配置"),
            CRITERIA("标准"),
            ENVIRONMENT("环境"),
            COMPATIBLE("兼容"),
            OTHER("其他");

            private String parentCode;
            private String value;

            public String getValue() {
                return value;
            }

            Type(String value) {
                this.parentCode = Parents.DEFECT.name();
                this.value = value;
            }
        }

        /*缺陷等级*/
        public enum Level {
            EMERGENCY("紧急"),
            HIGH("高"),
            CENTER("中"),
            LOW("低"),
            ADVICE("建议");

            private String parentCode;
            private String value;

            public String getValue() {
                return value;
            }

            Level(String value) {
                this.parentCode = Parents.DEFECT.name();
                this.value = value;
            }
        }

        /*危险系数 / 严重程度*/
        public enum Index {
            DEADLY("致命"),
            SERIOUS("严重"),
            IMPORTANT("重要"),
            COMMON("一般"),
            SLIGHT("轻微"),
            IMPROVE("有待改进");

            private String parentCode;
            private String value;

            public String getValue() {
                return value;
            }

            Index(String value) {
                this.parentCode = Parents.DEFECT.name();
                this.value = value;
            }
        }

        /*缺陷状态*/
        public enum Status {
            NEW("新开"),
            SOLVED("已解决"),
            CLOSED("已关闭"),
            DELAY("延迟处理"),
            DISREGARD("不予处理"),
            REOPEN("重新打开");

            private String parentCode;
            private String value;

            Status(String value) {
                this.parentCode = Parents.DEFECT.name();
                this.value = value;
            }
        }
    }

    public static class Mime {
        private static final HashMap<String, String> keyValue = Maps.newHashMapWithExpectedSize(3);

        static {
            keyValue.put(Image.PNG.key, Image.PNG.value);
            keyValue.put(Image.JPEG.key, Image.JPEG.value);
            keyValue.put(Image.GIF.key, Image.GIF.value);
        }

        /**
         * 前20位字节即可
         *
         * @param header
         * @return
         */
        public static String obtainMimeType(byte[] header) {
            if (header.length > 20) throw new ServiceException("file length can't more than 20");
            StringBuffer headerStr = new StringBuffer();
            for (int i = 0; i < header.length; i++)
                headerStr.append(" ").append(Integer.toHexString(header[i] & 0xFF));
            Predicate<String> predicate = (key) -> headerStr.toString().trim().toUpperCase().contains(key);
            return keyValue.keySet().stream().filter(predicate).map(keyValue::get).findAny().orElse("");
        }

        public enum Image {
            PNG("89 50 4E 47 D A 1A A", "image/png"),
            JPEG("FF D8 FF E0 0 10 4A 46 49 46", "image/jpeg"),
            GIF("47 49 46 38 39 61", "image/gif");

            private String parentCode;
            private String key;
            private String value;

            Image(String key, String value) {
                this.parentCode = Parents.MIME.name();
                this.key = key;
                this.value = value;
            }
        }
    }
}
