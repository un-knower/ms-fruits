package wowjoy.fruits.ms.dao.resource;

import org.springframework.stereotype.Service;
import wowjoy.fruits.ms.module.resource.FruitResource;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 * 本地上传工具
 */
@Service
public class LocalFileUtils implements InterfaceFile {

    public Boolean upload(FruitResource.Upload upload, String jwt) {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile("/Users/wangziwen/Downloads/resource/" + upload.getNowName(), "rw");
            ByteBuffer byteBuffer = ByteBuffer.allocate(upload.getOutputStream().size());
            byteBuffer.put(upload.getOutputStream().toByteArray());
            byteBuffer.flip();
            FileChannel channel = randomAccessFile.getChannel();
            while (byteBuffer.hasRemaining()) channel.write(byteBuffer);
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
