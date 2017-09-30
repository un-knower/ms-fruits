package wowjoy.fruits.ms.module.elastic;

/**
 * Created by wangziwen on 2017/9/28.
 */
public class GlobalSearchVo extends GlobalSearch {
    private Integer size = 10;
    private Integer page = 1;

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}
