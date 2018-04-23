package wowjoy.fruits.ms.dao.resource;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 * 本地上传工具
 */
public class FileUtils implements InterfaceFile {

    public static void upload(byte[] fileStream, String fileName) {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile("/Users/wangziwen/Downloads/resource/" + fileName, "rw");
            ByteBuffer byteBuffer = ByteBuffer.allocate(fileStream.length);
            byteBuffer.put(fileStream);
            byteBuffer.flip();
            FileChannel channel = randomAccessFile.getChannel();
            while (byteBuffer.hasRemaining()) channel.write(byteBuffer);
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void download() {

    }
}
