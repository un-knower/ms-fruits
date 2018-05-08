package wowjoy.fruits.ms.dao.resource;

import com.google.common.reflect.TypeToken;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.resource.FruitResource;
import wowjoy.fruits.ms.module.resource.FruitResourceExample;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.ApplicationContextUtils;
import wowjoy.fruits.ms.util.GsonUtils;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
public abstract class ServiceResource {

    public abstract void insert(FruitResource.Upload upload);

    public abstract Boolean upload(FruitResource.Upload update, String jwt);

    public abstract ByteArrayOutputStream download(String nowName, String jwt);

    public abstract List<FruitResource> findExample(Consumer<FruitResourceExample> exampleConsumer);

    public void upload(FruitResource.Upload upload) {
        Optional.ofNullable(upload)
                .filter(resource -> this.upload(resource, ApplicationContextUtils.getCurrentJwt().getJwt()))
                .ifPresent(this::insert);
    }

    public FruitResource.Download download(String uuid) {
        return findExample(fruitResourceExample -> fruitResourceExample.createCriteria().andIsDeletedEqualTo(FruitDict.Systems.N.name()).andUuidEqualTo(uuid))
                .stream()
                .findAny()
                .map(resource -> {
                    ByteArrayOutputStream download = download(resource.getNowName(), ApplicationContextUtils.getCurrentJwt().getJwt());
                    FruitResource.Download exportResource = GsonUtils.newGson().fromJson(GsonUtils.newGson().toJsonTree(resource), TypeToken.of(FruitResource.Download.class).getType());
                    Optional.ofNullable(download)
                            .filter(stream -> stream.size() >= 20)
                            .ifPresent(stream -> {
                                ByteBuffer header = ByteBuffer.allocate(20);
                                header.put(stream.toByteArray(), 0, 20);
                                exportResource.setMimeType(FruitDict.Mime.obtainMimeType(header.array()));
                            });

                    exportResource.setEncodeData(Base64.getEncoder().encodeToString(download.toByteArray()));
                    return exportResource;
                }).orElseThrow(() -> new CheckException("resource not exists"));
    }
}
