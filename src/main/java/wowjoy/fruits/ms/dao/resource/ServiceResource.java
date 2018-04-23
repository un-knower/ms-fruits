package wowjoy.fruits.ms.dao.resource;

import java.util.UUID;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
public abstract class ServiceResource {
    public void upload(byte[] bytes) {
        FileUtils.upload(bytes, UUID.randomUUID().toString());
    }
}
