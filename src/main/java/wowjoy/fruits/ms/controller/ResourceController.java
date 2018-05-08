package wowjoy.fruits.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.dao.resource.ServiceResource;
import wowjoy.fruits.ms.util.RestResult;

import java.io.IOException;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
@RestController
@RequestMapping("/v1/resource")
public class ResourceController {

    @Autowired
    private ServiceResource resource;

    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
    public RestResult image(@PathVariable("uuid") String uuid) throws IOException {
        return RestResult.newSuccess().setData(resource.download(uuid));
    }
}
