package wowjoy.fruits.ms.dao;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.ExceptionSupport;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.Exception.Check;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * Created by wangziwen on 2017/8/24.
 */
public interface InterfaceDao {
    Logger logger = LoggerFactory.getLogger(InterfaceDao.class);
    Integer processorCount = Runtime.getRuntime().availableProcessors() * 2;
    Function<Date, LocalDateTime> ToLocalDate = date -> LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    Function<LocalDateTime, Date> ToDate = localDateTime -> Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

    Function<Integer, Executor> obtainExecutor = size -> Executors.newFixedThreadPool(Math.min(size, 100), r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);  //开启守护进程，不会阻止程序关闭
        return t;
    });

    @Deprecated
    class DaoThread {
        private final ExecutorService executorService;
        private final List<Future> futures = Lists.newLinkedList();

        public DaoThread(ExecutorService executorService) {
            this.executorService = executorService;
        }

        public static DaoThread getFixed() {
            return new DaoThread(Executors.newFixedThreadPool(processorCount));
        }

        public DaoThread execute(Callable callable) {
            futures.add(executorService.submit(callable));
            return this;
        }

        public <V> Future<V> executeFuture(Callable<V> callable) {
            return executorService.submit(callable);
        }

        public DaoThread get() {
            try {
                for (Future future : futures) future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                futures.forEach((i) -> i.cancel(true));
                throw new CheckException(Check.SYSTEM_THREAD_INTERRUPTED.name());
            } catch (ExceptionSupport exceptionSupport) {
                throw new CheckException(exceptionSupport.getMessage());
            } catch (ExecutionException e) {
                executorService.shutdownNow();
                e.printStackTrace();
                throw new CheckException(Check.SYSTEM_THREAD_EXECUTION.name());
            }
            return this;
        }

        public void shutdown() {
            try {
                executorService.shutdown();
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS))
                    throw new CheckException(Check.SYSTEM_THREAD_TIMEOUT.name());
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                throw new CheckException(Check.SYSTEM_THREAD_INTERRUPTED.name());
            }
        }

    }
}
