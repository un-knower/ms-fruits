import org.junit.Test;

import java.util.List;
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
}
