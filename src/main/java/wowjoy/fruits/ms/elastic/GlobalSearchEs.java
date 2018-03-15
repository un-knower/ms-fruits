package wowjoy.fruits.ms.elastic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;
import wowjoy.fruits.ms.dao.project.AbstractDaoProject;
import wowjoy.fruits.ms.dao.project.ProjectDaoImpl;
import wowjoy.fruits.ms.dao.team.TeamDaoImpl;
import wowjoy.fruits.ms.dao.user.AbstractDaoUser;
import wowjoy.fruits.ms.dao.user.UserDaoImpl;
import wowjoy.fruits.ms.exception.ServiceException;
import wowjoy.fruits.ms.module.elastic.GlobalSearch;
import wowjoy.fruits.ms.module.elastic.GlobalSearchDao;
import wowjoy.fruits.ms.module.elastic.GlobalSearchVo;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.project.FruitProjectDao;
import wowjoy.fruits.ms.module.team.FruitTeamDao;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by wangziwen on 2017/9/26.
 */
@Service
public class GlobalSearchEs extends AbstractElastic {

    private final String Index;
    private final ArrayList<String> Types = Lists.newArrayList(FruitDict.Parents.PROJECT.name().toLowerCase(), FruitDict.Parents.TEAM.name().toLowerCase());

    public GlobalSearchEs() {
        this.Index = MessageFormat.format("{0}_{1}", super.Index, "global");
    }

    public SearchHits finds(GlobalSearchVo vo) {
        final String globalField = "content";
        final LinkedList<GlobalSearchDao> result = Lists.newLinkedList();
        final SearchRequestBuilder searchBuilder = esClient.prepareSearch(this.Index);

        searchBuilder.setTypes(Types.toArray(new String[Types.size()]));
        searchBuilder.setFetchSource(null, globalField);
        searchBuilder.setQuery(QueryBuilders.matchQuery(globalField, vo.getContent()));
        searchBuilder.setSize(vo.getSize());
        searchBuilder.setFrom((vo.getPage() - 1) * vo.getSize());
        searchBuilder.highlighter(SearchSourceBuilder.highlight().field(globalField));
        try {
            return searchBuilder.execute().actionGet().getHits();
        } catch (Exception e) {
            throw new ServiceException("服务器异常，查询失败。");
        }
    }

    /**
     * 添加全局搜索数据
     * 初始化和填充数据需要使用【消费者-生产者模式】
     */
    public void loading() {
        /*清楚数据*/
        esClient.admin().indices().prepareDelete(this.Index).execute();
        /*填充数据*/
        CreateIndexRequestBuilder builder = esClient.admin().indices().prepareCreate(this.Index);
        Types.forEach((i) -> {
            builder.addMapping(i, GlobalSearch.mappings().toString(), XContentType.JSON);
        });
        builder.execute().actionGet();

        BulkRequestBuilder bulkRequest = esClient.prepareBulk();
        DataSource.getInstance(esClient, this.Index, Types).execute().forEach((global) ->
                bulkRequest.add(
                        esClient.prepareIndex(this.Index, global.getType().name().toLowerCase(), global.getUuid())
                                .setSource(new Gson().toJsonTree(global).toString(), XContentType.JSON)
                )
        );
        if (bulkRequest.get().hasFailures())
            throw new ServiceException("全局索引数据添加失败");
    }

    /**
     * 读取整站数据
     */
    private static class DataSource {
        /**
         * 线程数=当前内核数+1
         */
        private final int threadNum = Runtime.getRuntime().availableProcessors() + 1;
        /**
         * executorService 提供了完整的生命周期：运行、关闭、已终止
         */
        private final ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        /**
         * 融合了Executor和BlockingQueue的功能。
         */
        private final CompletionService<List<GlobalSearch>> initESExecutor = new ExecutorCompletionService<>(executorService);
        private final Map<FruitDict.Parents, Callable> callables;
        private final TransportClient thisClient;
        private final String index;
        private final List<String> types;

        public DataSource(TransportClient thisClient, String index, List<String> types) {
            callables = Maps.newLinkedHashMap();
            callables.put(FruitDict.Parents.PROJECT, extractProjects);
            callables.put(FruitDict.Parents.TEAM, extractTeam);
            callables.put(FruitDict.Parents.USER, extractUser);
            this.thisClient = thisClient;
            this.index = index;
            this.types = types;
        }

        public static DataSource getInstance(TransportClient thisClient, String index, List<String> types) {
            return new DataSource(thisClient, index, types);
        }

        public LinkedList<GlobalSearch> execute() {
            LinkedList<GlobalSearch> result = Lists.newLinkedList();
            types.forEach((type) -> initESExecutor.submit(callables.get(type)));
            types.forEach((i) -> {
                try {
                    result.addAll(initESExecutor.take().get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
            return result;
        }

        /**
         * Loading project data
         */
        private final Callable extractProjects = () -> {
            final LinkedList<GlobalSearch> result = Lists.newLinkedList();
            final AbstractDaoProject dao = ApplicationContextUtils.getContext().getBean(ProjectDaoImpl.class);
            /*查询代码已被删除*/
            final List<FruitProjectDao> projects = Lists.newLinkedList();
            projects.forEach((projectDao) -> {
                GlobalSearch global = GlobalSearch.getInstance(FruitProjectDao.class);
                global.setTitle(projectDao.getTitle());
                global.setEntity(projectDao);
                global.setType(FruitDict.Parents.PROJECT);
                result.add(global);
            });
            return result;
        };

        /**
         * Loading team data
         */
        private final Callable extractTeam = () -> {
            final LinkedList<GlobalSearch> result = Lists.newLinkedList();
            TeamDaoImpl dao = ApplicationContextUtils.getContext().getBean(TeamDaoImpl.class);
            List<FruitTeamDao> teams = dao.findTeamByExample(null);
            teams.forEach((i) -> {
                GlobalSearch global = GlobalSearch.getInstance(FruitTeamDao.class);
                global.setTitle(i.getTitle());
                global.setEntity(i);
                global.setType(FruitDict.Parents.TEAM);
                result.add(global);
            });
            return result;
        };
        /**
         * Loading user data
         */
        private final Callable extractUser = () -> {
            final LinkedList<GlobalSearch> result = Lists.newLinkedList();
            final AbstractDaoUser dao = ApplicationContextUtils.getContext().getBean(UserDaoImpl.class);
            final List<FruitUserDao> users = dao.finds(FruitUser.getVo());
            users.forEach((user) -> {
                GlobalSearch global = GlobalSearch.getInstance(FruitUser.class);
                global.setTitle(user.getUserName());
                global.setEntity(user);
                global.setType(FruitDict.Parents.USER);
                result.add(global);
            });
            return result;
        };

    }
}
