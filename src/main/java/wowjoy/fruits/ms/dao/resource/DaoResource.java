package wowjoy.fruits.ms.dao.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.module.resource.FruitResource;
import wowjoy.fruits.ms.module.resource.FruitResourceExample;
import wowjoy.fruits.ms.module.resource.mapper.FruitResourceMapper;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
@Service
@Transactional
public class DaoResource extends ServiceResource {

    private final FruitResourceMapper resourceMapper;
    private final InterfaceFile interfaceFile;

    @Autowired
    public DaoResource(LocalFileUtils interfaceFile, FruitResourceMapper resourceMapper) {
        this.interfaceFile = interfaceFile;
        this.resourceMapper = resourceMapper;
    }

    @Override
    public void insert(FruitResource.Upload upload) {
        resourceMapper.insertSelective(upload);
    }

    @Override
    public Boolean upload(FruitResource.Upload update, String jwt) {
        return interfaceFile.upload(update, jwt);
    }

    @Override
    public List<FruitResource> findExample(Consumer<FruitResourceExample> exampleConsumer) {
        FruitResourceExample example = new FruitResourceExample();
        exampleConsumer.accept(example);
        return resourceMapper.selectByExample(example);
    }
}
