import com.google.common.collect.Lists;
import org.junit.Test;
import wowjoy.fruits.ms.module.resource.FruitResource;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * Created by wangziwen on 2017/9/15.
 */
public class PatternTest {
//    @Test
//    public void test() throws Exception {
//        ExecutorService executorService = Executors.newFixedThreadPool(3);
//        executorService.submit(() -> {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//        executorService.shutdown();
//        long start = System.currentTimeMillis();
//        executorService.awaitTermination(60, TimeUnit.SECONDS);
//        long end = System.currentTimeMillis();
//        System.out.println(end - start);
//    }


    @Test
    public void test() throws Exception {
        List<String> collect = IntStream.range(0, 100).boxed().map(i -> Integer.toString(i)).collect(toList());
        collect.sort((l, r) -> "99".contains(l) ? -1 : 1);
        System.out.println(collect.stream().collect(joining(",")));
    }

    @Test
    public void pattern() throws Exception {
        String text = "<img src=\"data:image/png;base64,123123123123123123123123123213\"><img src=\"data:image/png;base64,123123123123123123123123123213\"><img src=\"data:image/png;base64,123123123123123123123123123213\">";
        Matcher matcherDescription = Pattern.compile("data:(.*?)\">", Pattern.CASE_INSENSITIVE).matcher(text);
        LinkedList<FruitResource.Upload> files = new LinkedList();
        StringBuffer replace = new StringBuffer();
        String[] split;
        FruitResource.Upload upload;
        while (matcherDescription.find()) {
            upload = new FruitResource.Upload();
            split = matcherDescription.group().split(",");
            upload.setEncodeData(split[1].substring(0, split[1].length() - 2));
            upload.setOriginName(split[0]);
            matcherDescription.appendReplacement(replace, MessageFormat.format("[{0}]\">", upload.getUuid()));
            files.add(upload);
        }
        matcherDescription.appendTail(replace);
        System.out.println(replace);

    }

    @Test
    public void pattern02() throws Exception {
        String text = "<img src=\"[7202c760db0b46da95d55d07c041283c]\">";
        Matcher matcher = Pattern.compile("src=\"\\[(.*?)\\]\"", Pattern.CASE_INSENSITIVE).matcher(text);
        LinkedList<String> resourceIds = Lists.newLinkedList();
        while (matcher.find()) {
            String roughId = matcher.group();
            resourceIds.add(roughId.substring(6, roughId.length() - 2));
        }
    }

    @Test
    public void fileName() throws Exception {
        String fileName = "123.456.456.jpg";
        Matcher matcher = Pattern.compile("456", Pattern.CASE_INSENSITIVE).matcher(fileName);
        while (matcher.find()) {
            System.out.println(matcher.group());
        }

    }

    @Test
    public void requestStatic() {
        System.out.println(Pattern.compile("/static/(.*?)",Pattern.CASE_INSENSITIVE).matcher("/static1/qrqwrqweqweqwe/qweqweqweqwe").find());;
    }
}
