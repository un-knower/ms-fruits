package wowjoy.fruits.ms.dao.resource;

import wowjoy.fruits.ms.module.resource.FruitResource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
public interface InterfaceFile {
    Boolean upload(FruitResource.Upload upload, String jwt);

}
