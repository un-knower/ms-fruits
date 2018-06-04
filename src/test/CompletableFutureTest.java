import com.netflix.infix.TimeUtil;
import org.junit.Test;
import sun.misc.ThreadGroupUtils;
import wowjoy.fruits.ms.module.defect.FruitDefect;

import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
public class CompletableFutureTest {

    public void test() {
        CompletableFuture.allOf(
                CompletableFuture.supplyAsync(() -> {
                    sleep(600);
                    return "1";
                }).thenAccept(str -> {
                    System.out.print(str);
                }),
                CompletableFuture.supplyAsync(() -> {
                    sleep(500);
                    return "2";
                }).thenAccept(str -> {
                    System.out.print(str);
                }),
                CompletableFuture.supplyAsync(() -> {
                    sleep(400);
                    return "3";
                }).thenAccept(str -> {
                    System.out.print(str);
                }),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return "4";
                }).thenAccept(str -> {
                    System.out.print(str);
                }),
                CompletableFuture.supplyAsync(() -> {
                    sleep(200);
                    return "5";
                }).thenAccept(str -> {
                    System.out.print(str);
                }),
                CompletableFuture.supplyAsync(() -> {
                    sleep(100);
                    return "6";
                }).thenAccept(str -> {
                    System.out.print(str);
                })
        ).join();
        System.out.print("7");
        System.out.println();
    }

    public void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void name() {
        IntStream.range(0,100).forEach(i->test());
    }
}
