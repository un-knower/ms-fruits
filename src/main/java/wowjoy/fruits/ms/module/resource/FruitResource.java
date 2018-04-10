package wowjoy.fruits.ms.module.resource;

import wowjoy.fruits.ms.module.AbstractEntity;

public class FruitResource extends AbstractEntity {

    private String originName;

    private String nowName;

    private Long size;

    private String type;

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getNowName() {
        return nowName;
    }

    public void setNowName(String nowName) {
        this.nowName = nowName;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}