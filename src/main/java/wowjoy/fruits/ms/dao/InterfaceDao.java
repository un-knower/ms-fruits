package wowjoy.fruits.ms.dao;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wowjoy.fruits.ms.exception.CheckException;

import java.util.List;
import java.util.concurrent.*;

/**
 * Created by wangziwen on 2017/8/24.
 */
public interface InterfaceDao {
    Logger logger = LoggerFactory.getLogger(InterfaceDao.class);
    Integer processorCount = Runtime.getRuntime().availableProcessors() + 1;

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

        public void get() {
            try {
                for (Future future : futures) future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                futures.forEach((i) -> i.cancel(true));
                throw new CheckException("强制中断线程");
            } catch (ExecutionException e) {
                executorService.shutdownNow();
                e.printStackTrace();
                throw new CheckException("获取线程数据异常，线程已终止");
            }
        }

        public void shutdown() {
            try {
                executorService.shutdown();
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS))
                    throw new CheckException("关闭线程超时，请重试");
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                throw new CheckException("等待关闭时，被提前终止");
            }
        }

    }
}
