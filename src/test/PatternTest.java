import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.junit.Test;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.DateUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
        System.out.println(FruitDict.TransferDict.valueOf("NEW").name());
    }
}
