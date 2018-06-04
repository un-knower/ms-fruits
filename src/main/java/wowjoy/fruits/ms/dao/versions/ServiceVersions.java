package wowjoy.fruits.ms.dao.versions;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.Systems;
import wowjoy.fruits.ms.module.versions.FruitVersions;
import wowjoy.fruits.ms.module.versions.FruitVersionsExample;
import wowjoy.fruits.ms.util.ApplicationContextUtils;
import wowjoy.fruits.ms.util.GsonUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static java.util.stream.Collectors.*;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
public abstract class ServiceVersions {

    protected abstract void insert(FruitVersions.Insert insert);

    public abstract List<FruitVersions> findByExample(Consumer<FruitVersionsExample> exampleConsumer);

    protected abstract Page<FruitVersions> findByVersionAndPage(String projectId, String version, int pageNum, int pageSize);

    protected abstract ArrayList<FruitVersions> findByProjectAndParentIds(String projectId, LinkedList<String> parentIds, String version);

    protected abstract void update(FruitVersions.Update update, Consumer<FruitVersionsExample> exampleConsumer);

    protected abstract List<FruitUserDao> joinUser(List<String> userId);

    protected abstract List<FruitProject> joinProject(List<String> userId);

    protected abstract Map<String, String> findJoinDefect(ArrayList<String> versionIds);

    public abstract void remove(String versionId);

    /**
     * 添加版本
     *
     * @param insert
     */
    public void addVersion(FruitVersions.Insert insert) {
        this.checkVersions(insert);
        /*默认创建人为当前登录用户*/
        insert.setUserId(ApplicationContextUtils.getCurrentUser().getUserId());
        this.insert(insert);
    }

    /**
     * 修改版本
     *
     * @param update
     */
    public void updateVersion(FruitVersions.Update update) {
        /*局部FruitsVersionExample*/
        Consumer<FruitVersionsExample> exampleConsumer = example -> example.createCriteria().andIsDeletedEqualTo(Systems.N.name()).andUuidEqualTo(update.getUuid());
        Optional.ofNullable(update)
                .map(FruitVersions.Update::getUuid)
                .filter(StringUtils::isNotBlank)
                .filter(uuid -> !this.findByExample(exampleConsumer).isEmpty())
                .filter(projects -> !projects.isEmpty())
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_NOT_EXISTS.name()));
        this.checkVersions(update);
        this.update(update, exampleConsumer);
    }

    /*检查版本信息*/
    private void checkVersions(final FruitVersions fruitVersions) {
        Optional.ofNullable(fruitVersions)
                .filter(version -> version.getVersions() == null || version.getVersions().length() > 0)
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_NULL.name()));
    }


    /**
     * @param search versions：后缀模糊匹配
     *               projectId：根据项目查询
     */
    public PageInfo<FruitVersions> findPage(FruitVersions.Search search) {
        Optional.ofNullable(search.getProjectId())
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_NULL.name()));
        return Optional.ofNullable(this.findByVersionAndPage(search.getProjectId(), search.getVersions(), search.getPageNum(), search.getPageSize()))
                .filter(page -> !page.isEmpty())
                .map(page -> Optional.of(this.toInfo(page.getResult()))
                        .map(infos -> {
                            page.getResult().clear();
                            page.getResult().addAll(infos);
                            return page;
                        }).orElse(page))
                .map(page -> {
                    ArrayList<FruitVersions.Info> sonsVersions = this.toInfo(this.findByProjectAndParentIds(search.getProjectId(), page.getResult().stream().map(FruitVersions::getUuid).collect(toCollection(LinkedList::new)), search.getVersions()));
                    ArrayList<FruitVersions> collectVersions = Lists.newArrayListWithExpectedSize(sonsVersions.size() + page.getResult().size());
                    collectVersions.addAll(sonsVersions);
                    collectVersions.addAll(page.getResult());
                    CompletableFuture.allOf(
                            CompletableFuture.supplyAsync(() -> this.joinUser(collectVersions.stream().map(FruitVersions::getUserId).distinct().collect(toCollection(ArrayList::new)))).thenAccept(userLists -> Optional.ofNullable(userLists)
                                    .filter(users -> !users.isEmpty())
                                    .map(users -> users.stream().collect(toMap(FruitUser::getUserId, user -> user)))
                                    .ifPresent(userMap -> collectVersions.stream().filter(versions -> userMap.containsKey(versions.getUserId())).map(version -> (FruitVersions.Info) version).forEach(versions -> versions.setUser(userMap.get(versions.getUserId()))))),
                            CompletableFuture.supplyAsync(() -> this.joinProject(collectVersions.stream().map(FruitVersions::getProjectId).distinct().collect(toCollection(ArrayList::new)))).thenAccept(projectLists ->
                                    Optional.ofNullable(projectLists)
                                            .map(projects -> projects.stream().collect(toMap(FruitProject::getUuid, project -> project)))
                                            .ifPresent(projectMap -> collectVersions.stream().filter(versions -> projectMap.containsKey(versions.getProjectId())).map(version -> (FruitVersions.Info) version).forEach(versions -> versions.setProject(projectMap.get(versions.getProjectId()))))),
                            CompletableFuture.supplyAsync(() -> this.findJoinDefect(collectVersions.stream().map(FruitVersions::getUuid).collect(toCollection(ArrayList::new))))
                                    .thenAccept(versionMap -> collectVersions.stream().map(version -> (FruitVersions.Info) version).forEach(version -> {
                                        Optional.ofNullable(versionMap.get(version.getUuid()))
                                                .ifPresent(versionId -> version.setUse(true));
                                    }))
                    ).join();
                    Optional.ofNullable(sonsVersions.stream().collect(groupingBy(FruitVersions::getParentId, toCollection(ArrayList::new))))
                            .ifPresent(sonsMap -> page.getResult().stream().filter(versions -> sonsMap.containsKey(versions.getUuid())).map(version -> (FruitVersions.Info) version).forEach(version -> {
                                Optional.ofNullable(sonsMap.get(version.getUuid()))
                                        .ifPresent(version::setSons);
                            }));
                    return page.toPageInfo();
                }).orElse(null);
    }

    private ArrayList<FruitVersions.Info> toInfo(List<FruitVersions> intoVersions) {
        return Optional.ofNullable(intoVersions)
                .map(versions -> versions.stream().map(version -> (FruitVersions.Info) GsonUtils.newGson().fromJson(GsonUtils.newGson().toJsonTree(version), TypeToken.of(FruitVersions.Info.class).getType())).collect(toCollection(ArrayList::new)))
                .orElseGet(Lists::newArrayList);
    }


    /*根据项目查询所有子版本信息*/
    public List<FruitVersions> findSons(String projectId) {
        return this.findByExample(versionsExample -> {
            versionsExample.createCriteria().andProjectIdEqualTo(projectId).andParentIdIsNotNull().andIsDeletedEqualTo(Systems.N.name());
            versionsExample.setOrderByClause("versions desc");
        });
    }

    public FruitVersions find(String versionId) {
        return this.findByExample(versionsExample -> versionsExample.createCriteria().andUuidEqualTo(versionId).andIsDeletedEqualTo(Systems.N.name())).stream().findAny().orElse(null);
    }


}
