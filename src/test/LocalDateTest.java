import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.relation.entity.UserProjectRelation;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Stack;

/**
 * Created by wangziwen on 2017/8/24.
 */

public class LocalDateTest {
    @Test
    public void project() throws Exception {
        final JsonArray parse = new JsonParser().parse(urlClient()).getAsJsonObject().get("result").getAsJsonArray();
        Stack<FruitProject> stackProject = new Stack<FruitProject>();
        Stack<UserProjectRelation> userProjectRelations = new Stack<>();
        parse.forEach((i) -> {
            final FruitProject project = new FruitProject();
            project.setUuid(i.getAsJsonObject().get("projectId").getAsString());
            project.setPredictEndDate(Date.from(LocalDate.parse(i.getAsJsonObject().get("endTime").getAsString()).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            project.setProjectStatus(FruitDict.ProjectDict.UNDERWAY.name());
            project.setTitle(i.getAsJsonObject().get("name").getAsString());
            stackProject.add(project);
            final UserProjectRelation leader = new UserProjectRelation();
            leader.setProjectId(project.getUuid());
            leader.setUserId(i.getAsJsonObject().get("leader").getAsString());
            leader.setUpRole(FruitDict.UserProjectDict.PRINCIPAL.name());
            userProjectRelations.add(leader);
            System.out.println(leader.getUserId());
            i.getAsJsonObject().get("userList").getAsJsonArray().forEach((j)->{
                if (!j.getAsJsonObject().get("userId").getAsString().equals(leader.getUserId())) {
                    final UserProjectRelation cyz = new UserProjectRelation();
                    cyz.setUpRole(FruitDict.UserProjectDict.PARTICIPANT.name());
                    cyz.setUserId(j.getAsJsonObject().get("userId").getAsString());
                    cyz.setProjectId(project.getUuid());
                    userProjectRelations.add(cyz);
                    System.out.println(cyz.getUserId());
                }
            });

        });

    }

    private InputStreamReader urlClient() throws IOException {
        final HttpGet httpGet = new HttpGet("https://fruits.rubikstack.com/project/getList");
        httpGet.setHeader("Cookie", "JSESSIONID-SSO=7955bb8f-0370-488f-a525-3a0b1770f10b; JSESSIONID-CONF=eeeb525a-9ffc-49ab-aa69-87a62e0dc4bf");
        final CloseableHttpResponse execute = HttpClients.createDefault().execute(httpGet);
        final InputStreamReader input = new InputStreamReader(execute.getEntity().getContent());
        StringBuffer result = new StringBuffer();
//        while (input.ready()){
//            result.append((char) input.read());
//        }
//        return result.toString();
        return input;
    }
}
