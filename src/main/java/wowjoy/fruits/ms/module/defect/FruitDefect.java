package wowjoy.fruits.ms.module.defect;

import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Lists;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.EntityUtils;
import wowjoy.fruits.ms.module.comment.FruitComment;
import wowjoy.fruits.ms.module.logs.FruitLogs;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.resource.FruitResource;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.DefectDict;
import wowjoy.fruits.ms.module.versions.FruitVersions;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;

public class FruitDefect extends AbstractEntity {
    private Integer number;

    private String projectId;

    private String beforeVersionId;

    private String afterVersionId;

    private String defectName;

    private String userId;

    private String handlerUserId;

    private DefectDict.Type defectType;

    private DefectDict.Level defectLevel;

    private DefectDict.Index riskIndex;

    private DefectDict.Status defectStatus;

    private Date endDateTime;

    private Date closedDateTime;

    private String duplicate;       //字段综合查询

    public static class Insert extends FruitDefect implements EntityUtils {
        public Insert() {
            setUuid(obtainUUID());
        }

        private ArrayList<Upload> upload;

        public ArrayList<Upload> getUpload() {
            return upload;
        }

        public void setUpload(Upload intoUpload) {
            if (upload == null)
                upload = Lists.newArrayList();
            upload.add(intoUpload);
        }
    }

    public static class Update extends FruitDefect implements EntityUtils {
        public Update() {
            setUuid(null);
            setIsDeleted(null);
        }

        private ArrayList<Upload> upload;
        private String removeResource;

        public ArrayList<Upload> getUpload() {
            return upload;
        }

        public void setUpload(ArrayList<Upload> upload) {
            this.upload = upload;
        }

        public ArrayList<String> getRemoveResources() {
            return Optional.ofNullable(removeResource).filter(StringUtils::isNotBlank).map(str -> Stream.of(str.split(",")).filter(StringUtils::isNotBlank).collect(toCollection(ArrayList::new))).orElseGet(Lists::newArrayList);
        }

        public String getRemoveResource() {
            return removeResource;
        }

        public void setRemoveResource(String removeResource) {
            this.removeResource = removeResource;
        }
    }

    public static class Upload extends FruitResource.Upload {

        private FruitDict.Resource drType;

        public FruitDict.Resource getDrType() {
            return drType;
        }

        public void setDrType(FruitDict.Resource drType) {
            this.drType = drType;
        }

    }

    public static class Search extends FruitDefect {
        public Search() {
            setUuid(null);
            setIsDeleted(null);
        }

        private int pageNum;
        private int pageSize;
        private String startTime;
        private String endTime;
        private String status;  //状态
        private String level;   //优先级
        private String type;    //类型
        private String index;   //严重程度

        private ArrayList<String> statusIn;  //状态
        private ArrayList<String> levelIn;   //优先级
        private ArrayList<String> typeIn;    //类型
        private ArrayList<String> indexIn;   //严重程度
        private ArrayList<String> handlerUserIdIn;    //处理人集合
        private ArrayList<String> userIdIn;    //创建人集合
        private ArrayList<String> beforeVersionIdIn;    //创建人集合
        private ArrayList<String> projectIdIn;    //创建人集合
        private String orderByClause;

        public ArrayList<String> getHandlerUserIdIn() {
            return handlerUserIdIn;
        }

        public void setHandlerUserIdIn(ArrayList<String> handlerUserIdIn) {
            this.handlerUserIdIn = handlerUserIdIn;
        }

        public ArrayList<String> getUserIdIn() {
            return userIdIn;
        }

        public void setUserIdIn(ArrayList<String> userIdIn) {
            this.userIdIn = userIdIn;
        }

        public ArrayList<String> getBeforeVersionIdIn() {
            return beforeVersionIdIn;
        }

        public void setBeforeVersionIdIn(ArrayList<String> beforeVersionIdIn) {
            this.beforeVersionIdIn = beforeVersionIdIn;
        }

        public ArrayList<String> getProjectIdIn() {
            return projectIdIn;
        }

        public void setProjectIdIn(ArrayList<String> projectIdIn) {
            this.projectIdIn = projectIdIn;
        }

        public String getOrderByClause() {
            return orderByClause;
        }

        public ArrayList<String> getStatusIn() {
            return statusIn;
        }

        public void setStatusIn(ArrayList<String> statusIn) {
            this.statusIn = statusIn;
        }

        public ArrayList<String> getLevelIn() {
            return levelIn;
        }

        public void setLevelIn(ArrayList<String> levelIn) {
            this.levelIn = levelIn;
        }

        public ArrayList<String> getTypeIn() {
            return typeIn;
        }

        public void setTypeIn(ArrayList<String> typeIn) {
            this.typeIn = typeIn;
        }

        public ArrayList<String> getIndexIn() {
            return indexIn;
        }

        public void setIndexIn(ArrayList<String> indexIn) {
            this.indexIn = indexIn;
        }

        public void setOrderByClause(String orderByClause) {
            this.orderByClause = orderByClause;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }
    }

    public static class Info extends FruitDefect {
        public Info() {
            setUuid(null);
            setIsDeleted(null);
        }

        private FruitProject project;   //项目信息
        private FruitVersions beforeVersion;  //影响版本
        private FruitVersions afterVersion;  //解决版本
        private FruitUser createUser;   //创建用户
        private FruitUser handlerUser;  //处理用户
        private ArrayList<FruitDefectResource> files; //文件集合，不包含富文本图片
        private ArrayList<FruitLogs.Info> logs;     //缺陷日志
        private ArrayList<? extends FruitComment.Info> comments;  //评论列表
        private int reOpenCount;    //重开次数

        public ArrayList<? extends FruitComment.Info> getComments() {
            return comments;
        }

        public void setComments(ArrayList<? extends FruitComment.Info> comments) {
            this.comments = comments;
        }

        public int getReOpenCount() {
            return reOpenCount;
        }

        public void setReOpenCount(int reOpenCount) {
            this.reOpenCount = reOpenCount;
        }

        public ArrayList<FruitLogs.Info> getLogs() {
            return logs;
        }

        public void setLogs(ArrayList<FruitLogs.Info> logs) {
            this.logs = logs;
        }

        public ArrayList<FruitDefectResource> getFiles() {
            return files;
        }

        public void setFiles(ArrayList<FruitDefectResource> files) {
            this.files = files;
        }

        public FruitProject getProject() {
            return project;
        }

        public void setProject(FruitProject project) {
            this.project = project;
        }

        public FruitVersions getBeforeVersion() {
            return beforeVersion;
        }

        public void setBeforeVersion(FruitVersions beforeVersion) {
            this.beforeVersion = beforeVersion;
        }

        public FruitVersions getAfterVersion() {
            return afterVersion;
        }

        public void setAfterVersion(FruitVersions afterVersion) {
            this.afterVersion = afterVersion;
        }

        public FruitUser getCreateUser() {
            return createUser;
        }

        public void setCreateUser(FruitUser createUser) {
            this.createUser = createUser;
        }

        public FruitUser getHandlerUser() {
            return handlerUser;
        }

        public void setHandlerUser(FruitUser handlerUser) {
            this.handlerUser = handlerUser;
        }

    }

    /*改变缺陷信息时日志记录信息，例如描述内容是不需要单独保存在一张表中的，可以直接保存在日志记录中*/
    public static class ChangeInfo extends FruitDefect {
        public ChangeInfo() {
            setUuid(null);
            setIsDeleted(null);
        }

        /*备注、描述*/
        private String comment;

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    public Date getClosedDateTime() {
        return closedDateTime;
    }

    public void setClosedDateTime(Date closedDateTime) {
        this.closedDateTime = closedDateTime;
    }

    public String getDuplicate() {
        return duplicate;
    }

    public void setDuplicate(String duplicate) {
        this.duplicate = duplicate;
    }

    public DefectDict.Level getDefectLevel() {
        return defectLevel;
    }

    public void setDefectLevel(DefectDict.Level defectLevel) {
        this.defectLevel = defectLevel;
    }

    public DefectDict.Index getRiskIndex() {
        return riskIndex;
    }

    public void setRiskIndex(DefectDict.Index riskIndex) {
        this.riskIndex = riskIndex;
    }

    public DefectDict.Status getDefectStatus() {
        return defectStatus;
    }

    public void setDefectStatus(DefectDict.Status defectStatus) {
        this.defectStatus = defectStatus;
    }

    public DefectDict.Type getDefectType() {
        return defectType;
    }

    public void setDefectType(DefectDict.Type defectType) {
        this.defectType = defectType;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getBeforeVersionId() {
        return beforeVersionId;
    }

    public void setBeforeVersionId(String beforeVersionId) {
        this.beforeVersionId = beforeVersionId;
    }

    public String getAfterVersionId() {
        return afterVersionId;
    }

    public void setAfterVersionId(String afterVersionId) {
        this.afterVersionId = afterVersionId;
    }

    public String getDefectName() {
        return defectName;
    }

    public void setDefectName(String defectName) {
        this.defectName = defectName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHandlerUserId() {
        return handlerUserId;
    }

    public void setHandlerUserId(String handlerUserId) {
        this.handlerUserId = handlerUserId;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }
}