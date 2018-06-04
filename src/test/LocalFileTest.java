import org.apache.commons.lang.StringUtils;
import org.apache.el.util.ConcurrentCache;
import org.junit.Test;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.MessageFormat;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
public class LocalFileTest {
    @Test
    public void fileHeader() throws IOException {
        RandomAccessFile rwFile = new RandomAccessFile("/Users/wangziwen/Downloads/求职者-甘圆圆-UI设计师.pdf", "rw");
        FileChannel channel = rwFile.getChannel();
        ByteBuffer allocate = ByteBuffer.allocate(10);
        int read = channel.read(allocate);
        if (read != -1) {
            for (int i = 0; i < allocate.array().length; i++) {
                System.out.print(Integer.toHexString(allocate.get(i) & 0xFF).toUpperCase());
            }
        }
    }

    @Test
    public void saveFile() throws IOException {
        RandomAccessFile w = new RandomAccessFile("/Users/wangziwen/Downloads/1", "rw");
        FileChannel channel = w.getChannel();
        ByteBuffer allocate = ByteBuffer.allocate(1024);
        allocate.clear();
        allocate.put("汪梓文".getBytes());
        allocate.flip();
        while (allocate.hasRemaining())
            channel.write(allocate);
        channel.close();
    }


    @Test
    public void findFile() throws Exception {
        RandomAccessFile r = new RandomAccessFile("/Users/wangziwen/Downloads/wangziwen.txt", "r");
        FileChannel channel = r.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        while (channel.read(byteBuffer) != -1) {
            byteBuffer.flip();
            stream.write(byteBuffer.array());
            byteBuffer.clear();
        }
    }

}
