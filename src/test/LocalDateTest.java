import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import wowjoy.fruits.ms.module.relation.entity.PlanProjectRelation;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by wangziwen on 2017/8/24.
 */

public class LocalDateTest {
    @Test
    public void project() throws Exception {
        ConcurrentSkipListMap<Integer, Object> skipMap = new ConcurrentSkipListMap();
        long start = System.currentTimeMillis();
        for (Integer i = 0; i < 10000; i++)
            skipMap.put(i, i);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        ConcurrentHashMap hashMap = new ConcurrentHashMap();
        start = System.currentTimeMillis();
        for (Integer i = 0; i < 10000; i++)
            hashMap.put(i, i);
        end = System.currentTimeMillis();
        System.out.println(end - start);

        LinkedHashMap<Integer, Object> linkedHashMap = new LinkedHashMap<>();
        start = System.currentTimeMillis();
        for (Integer i = 0; i < 10000; i++)
            linkedHashMap.put(i, i);
        end = System.currentTimeMillis();
        System.out.println(end - start);

        start = System.nanoTime();
        for (Integer i = 0; i < 10000; i++)
            skipMap.get(i);
        end = System.nanoTime();
        System.out.println(end - start);

        start = System.nanoTime();
        for (Integer i = 0; i < 10000; i++)
            hashMap.get(i);
        end = System.nanoTime();
        System.out.println(end - start);

        start = System.nanoTime();
        for (Integer i = 0; i < 10000; i++)
            linkedHashMap.get(i);
        end = System.nanoTime();
        System.out.println(end - start);
    }

    @Test
    public void name() throws Exception {
        Date date = new Date();
        Thread.sleep(1000);
        Date date1 = new Date();

        System.out.println(date1.toInstant().compareTo(date.toInstant()));
    }

    @Test
    public void test1() {
        Map<FruitDict.Systems,List<PlanProjectRelation>> data = Maps.newLinkedHashMap();
        data.put(FruitDict.Systems.ADD, Lists.newLinkedList());
        System.out.println(new Gson().toJsonTree(data).isJsonObject());
    }

    private InputStreamReader urlClient() throws IOException {
        final HttpGet httpGet = new HttpGet("https://fruits.rubikstack.com/plan");
        httpGet.setHeader("Cookie", "JSESSIONID-CONF=5ec0d09c-ca49-41a9-b274-774082c46b53");
        final CloseableHttpResponse execute = HttpClients.createDefault().execute(httpGet);
        final InputStreamReader input = new InputStreamReader(execute.getEntity().getContent());
        return input;
    }

//    @Autowired
//    private RestTemplate restTemplate;
//
//    @Autowired
//    private PlanUserRelationMapper userRelationMapper;
//    @Autowired
//    private FruitPlanMapper fruitPlanMapper;
//
//    @Autowired
//    private PlanProjectRelationMapper planProjectRelationMapper;
//
//    @Autowired
//    private FruitPlanSummaryMapper summaryMapper;
//
//    @RequestMapping("add")
//    public void add(){
//        final JsonObject parse = new JsonParser().parse(urlClient()).getAsJsonObject().get("result").getAsJsonObject();
//        final List<FruitPlan> planInfo = Lists.newLinkedList();
//        final LinkedList<PlanProjectRelation> planProjectInfo = Lists.newLinkedList();
//        final LinkedList<List<PlanUserRelation>> planUserRelation = Lists.newLinkedList();
//        final List<FruitPlanSummary> fruitPlanSummaryInfo = Lists.newLinkedList();
//        parse.get("plan").getAsJsonArray().forEach((i) -> {
//            final FruitPlan fruitPlan = AbstractEntity.getFruitPlan();
//            fruitPlan.setUuid(i.getAsJsonObject().get("planId").getAsString());
//            fruitPlan.setEndDate(LocalDate.parse(i.getAsJsonObject().get("endTime").getAsString()));
//            fruitPlan.setPercent(Integer.parseInt(i.getAsJsonObject().get("percentage").getAsString()));
//            fruitPlan.setTitle(i.getAsJsonObject().get("content").getAsString());
//            fruitPlan.setDescription(i.getAsJsonObject().get("desc").getAsString());
//            fruitPlan.setParentId(i.getAsJsonObject().get("monthPlanId").getAsString());
//            final String state = i.getAsJsonObject().get("state").getAsString();
//            if (state.equals("1")) {
//                fruitPlan.setPlanStatus(FruitDict.PlanDict.PENDING.name());
//            } else if (state.equals("2")) {
//                fruitPlan.setPlanStatus(FruitDict.PlanDict.COMPLETE.name());
//            } else if (state.equals("3")) {
//                fruitPlan.setPlanStatus(FruitDict.PlanDict.END.name());
//            }
//            planProjectInfo.add(AbstractEntity.newPlanProjectRelation(fruitPlan.getUuid(),i.getAsJsonObject().get("projectId").getAsString()));
//            final List<PlanUserRelation> planUserRelations = Lists.newLinkedList();
//            for (JsonElement j : parse.get("relation").getAsJsonArray()) {
//                boolean boo = false;
//                if (i.getAsJsonObject().get("planId").getAsString().equals(j.getAsJsonObject().get("planId").getAsString())) {
//                    for (PlanUserRelation user : planUserRelations) {
//                        System.out.println(j.getAsJsonObject().get("teamId"));
//                        if (user.getUserId().equals(j.getAsJsonObject().get("teamId").getAsString()))
//                            boo = true;
//                    }
//                    if (!boo)
//                        planUserRelations.add(AbstractEntity.newPlanUserRelation(j.getAsJsonObject().get("teamId").getAsString(),fruitPlan.getUuid(),FruitDict.PlanUserDict.PRINCIPAL));
//                }
//            }
//            planUserRelation.add(planUserRelations);
//            planInfo.add(fruitPlan);
//        });
//        parse.get("desc").getAsJsonArray().forEach((i) -> {
//            final FruitPlanSummary fruitPlanSummary = AbstractEntity.getFruitPlanSummary();
//            fruitPlanSummary.setPlanId(i.getAsJsonObject().get("planId").getAsString());
//            fruitPlanSummary.setPercent(Integer.parseInt(!i.getAsJsonObject().has("percentage") ? "0" : i.getAsJsonObject().get("percentage").getAsString()));
//            fruitPlanSummary.setDescription(i.getAsJsonObject().get("complete").getAsString());
//            fruitPlanSummaryInfo.add(fruitPlanSummary);
//        });
//        System.out.println(planInfo);
//
//        planInfo.forEach((i)->{
//            fruitPlanMapper.insertSelective(i);
//        });
//
//        fruitPlanSummaryInfo.forEach((i)->{
//            summaryMapper.add(i);
//        });
//
//        planUserRelation.forEach((i)->{
//            i.forEach((j)->{
//                userRelationMapper.insertSelective(j);
//            });
//        });
//
//        planProjectInfo.forEach((i)->{
//            planProjectRelationMapper.insertSelective(i);
//        });
//    }
//
//    private String urlClient() {
//        final HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.set("Cookie","JSESSIONID-SSO=ba4f3133-8115-4364-a5fd-6003321c665a; JSESSIONID-CONF=dcc0d776-67cf-4bcf-b8a0-d36705b7b173");
//        final HttpEntity httpEntity = new HttpEntity(httpHeaders);
//        final ResponseEntity<String> exchange = restTemplate.exchange("https://fruits.rubikstack.com/plan", HttpMethod.GET, httpEntity, String.class);
//        return exchange.getBody();
//    }

}
