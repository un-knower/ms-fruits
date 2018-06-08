package wowjoy.fruits.ms.dao.resource;

import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.resource.FruitResource;
import wowjoy.fruits.ms.module.resource.FruitResourceExample;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
public abstract class ServiceResource {

    public abstract void insert(FruitResource.Upload upload);

    public abstract Boolean upload(FruitResource.Upload update, String jwt);

    public abstract List<FruitResource> findExample(Consumer<FruitResourceExample> exampleConsumer);

    public void upload(FruitResource.Upload upload) {
        Optional.ofNullable(upload)
                .filter(resource -> this.upload(resource, ApplicationContextUtils.getCurrentJwt().getJwt()))
                .ifPresent(this::insert);
    }

    public FruitResource download(String uuid) {
        return findExample(fruitResourceExample -> fruitResourceExample.createCriteria().andIsDeletedEqualTo(FruitDict.Systems.N.name()).andUuidEqualTo(uuid))
                .stream()
                .findAny().orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_NOT_EXISTS.name()));
    }
}
