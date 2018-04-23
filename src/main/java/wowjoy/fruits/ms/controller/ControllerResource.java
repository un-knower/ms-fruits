package wowjoy.fruits.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import wowjoy.fruits.ms.dao.resource.ServiceResource;

import java.io.IOException;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
@RestController
@RequestMapping("/v1/resource")
public class ControllerResource {
    @Autowired
    private ServiceResource serviceResource;

    @RequestMapping(method = RequestMethod.POST)
    public void upload(@RequestPart("file") MultipartFile[] multipartFile) throws IOException {
        serviceResource.upload(multipartFile[0].getBytes());
    }
}
