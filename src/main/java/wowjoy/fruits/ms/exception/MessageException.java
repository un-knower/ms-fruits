package wowjoy.fruits.ms.exception;

/**
 * Created by wangziwen on 2018/2/8.
 */
public class MessageException extends ExceptionSupport {
    public MessageException(String msg) {
        super(msg);
    }

    public static class RefuseToRemoveUser {
        private String userName;
        private long planCount;
        private long taskCount;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public long getPlanCount() {
            return planCount;
        }

        public void setPlanCount(long planCount) {
            this.planCount = planCount;
        }

        public long getTaskCount() {
            return taskCount;
        }

        public void setTaskCount(long taskCount) {
            this.taskCount = taskCount;
        }

        public static RefuseToRemoveUser newInstance(String userName, long planCount, long taskCount) {
            RefuseToRemoveUser refuseToRemoveUser = new RefuseToRemoveUser();
            refuseToRemoveUser.setUserName(userName);
            refuseToRemoveUser.setPlanCount(planCount);
            refuseToRemoveUser.setTaskCount(taskCount);
            return refuseToRemoveUser;
        }
    }

    public static class RefuseToCompletePlan {
        private String title;
        private String userName;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public static RefuseToCompletePlan newInstance(String title, String userName) {
            RefuseToCompletePlan refuseToCompletePlan = new RefuseToCompletePlan();
            refuseToCompletePlan.setTitle(title);
            refuseToCompletePlan.setUserName(userName);
            return refuseToCompletePlan;
        }
    }
}