package wowjoy.fruits.ms.dao.versions;

import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.user.example.FruitUserExample;
import wowjoy.fruits.ms.module.util.entity.FruitDict.Systems;
import wowjoy.fruits.ms.module.versions.FruitVersions;
import wowjoy.fruits.ms.module.versions.FruitVersionsExample;
import wowjoy.fruits.ms.util.GsonUtils;

import java.text.MessageFormat;
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

    protected abstract Page<FruitVersions> findByExampleAndPage(Consumer<FruitVersionsExample> exampleConsumer, int pageNum, int pageSize);

    protected abstract void update(FruitVersions.Update update, Consumer<FruitVersionsExample> exampleConsumer);

    protected abstract ArrayList<FruitVersions> findByExampleOrUserExample(Consumer<FruitVersionsExample> versionsExampleConsumer, Consumer<FruitUserExample> userExampleConsumer);

    protected abstract List<FruitUserDao> joinUser(List<String> userId);

    protected abstract List<FruitProject> joinProject(List<String> userId);

    /**
     * 添加版本
     *
     * @param insert
     */
    public void addVersion(FruitVersions.Insert insert) {
        this.checkVersions(insert);
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
                .orElseThrow(() -> new CheckException("versions no exists"));
        this.checkVersions(update);
        this.update(update, exampleConsumer);
    }

    /*检查版本信息*/
    public void checkVersions(final FruitVersions fruitVersions) {
        Optional.ofNullable(fruitVersions.getVersions())
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new CheckException("versions can't null"));
    }

    /**
     * @param search
     * @return
     */
    public Page<FruitVersions> findVersions(FruitVersions.Search search) {
        ArrayList<FruitVersions> allVersionsList = this.findByExampleOrUserExample(
                versionsExample -> {
                    FruitVersionsExample.Criteria criteria = versionsExample.createCriteria();
                    Optional.ofNullable(search)
                            .map(FruitVersions.Search::getVersions)
                            .filter(StringUtils::isNotBlank)
                            .ifPresent(versions -> criteria.andVersionsLike(MessageFormat.format("%{0}%", versions)));
                    Optional.ofNullable(search)
                            .map(FruitVersions.Search::getProjectId)
                            .filter(StringUtils::isNotBlank)
                            .ifPresent(criteria::andProjectIdEqualTo);
                    criteria.andIsDeletedEqualTo(Systems.N.name());
                },
                userExample -> {
                });
        return Optional.ofNullable(allVersionsList.stream().map(versions -> Optional.ofNullable(versions)
                .filter(one -> StringUtils.isNotBlank(one.getParentId()))
                .map(FruitVersions::getParentId)
                .orElseGet(versions::getUuid)).collect(toCollection(Lists::newArrayList)))
                .filter(ids -> !ids.isEmpty())
                .map(ids -> this.findByExampleAndPage(versionsExample -> {
                    versionsExample.createCriteria().andUuidIn(ids);
                    versionsExample.setOrderByClause("versions desc");
                }, search.getPageNum(), search.getPageSize()))
                .map(pageVersions -> {
                    LinkedList<FruitVersions> allVersions = Lists.newLinkedList();
                    pageVersions.forEach(parent -> allVersions.addAll(allVersionsList.stream().filter(son -> parent.getUuid().equals(son.getParentId())).collect(toCollection(ArrayList::new))));
                    allVersions.addAll(pageVersions);
                    ArrayList<FruitVersions.Info> intoVersions = allVersions.stream().map(versions -> (FruitVersions.Info) GsonUtils.newGson().fromJson(GsonUtils.newGson().toJsonTree(versions), TypeToken.of(FruitVersions.Info.class).getType())).collect(toCollection(ArrayList::new));
                    CompletableFuture.allOf(
                            CompletableFuture.supplyAsync(() -> this.joinUser(intoVersions.stream().map(FruitVersions::getUserId).distinct().collect(toCollection(ArrayList::new)))).thenAccept(userLists -> Optional.ofNullable(userLists)
                                    .filter(users -> !users.isEmpty())
                                    .map(users -> users.stream().collect(toMap(FruitUser::getUserId, user -> user)))
                                    .ifPresent(userMap -> intoVersions.stream().filter(versions -> userMap.containsKey(versions.getUserId())).forEach(versions -> versions.setUser(userMap.get(versions.getUserId()))))),
                            CompletableFuture.supplyAsync(() -> this.joinProject(intoVersions.stream().map(FruitVersions::getProjectId).distinct().collect(toCollection(ArrayList::new)))).thenAccept(projectLists ->
                                    Optional.ofNullable(projectLists)
                                            .filter(projects -> !projects.isEmpty())
                                            .map(projects -> projects.stream().collect(toMap(FruitProject::getUuid, project -> project)))
                                            .ifPresent(projectMap -> intoVersions.stream().filter(versions -> projectMap.containsKey(versions.getProjectId())).forEach(versions -> versions.setProject(projectMap.get(versions.getProjectId())))))
                    ).join();
                    Map<Boolean, List<FruitVersions.Info>> partitionVersions = intoVersions.stream().collect(partitioningBy(versions -> StringUtils.isNotBlank(versions.getParentId())));
                    partitionVersions.get(false).forEach(parent -> Optional.ofNullable(partitionVersions.get(true).stream().filter(son -> parent.getUuid().equals(son.getParentId())).collect(toCollection(ArrayList::new))).ifPresent(sons -> {
                        parent.setSons(new ArrayList<>());
                        parent.getSons().addAll(sons);
                    }));
                    pageVersions.clear();
                    pageVersions.addAll(partitionVersions.get(false));
                    return pageVersions;
                }).orElse(null);
    }
}
