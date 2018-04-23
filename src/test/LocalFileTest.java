import org.assertj.core.util.Sets;
import org.junit.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
public class LocalFileTest {
    @Test
    public void fileHeader() throws IOException {
        RandomAccessFile rwFile = new RandomAccessFile("/Users/wangziwen/Downloads/封面.pdf", "rw");
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
}
