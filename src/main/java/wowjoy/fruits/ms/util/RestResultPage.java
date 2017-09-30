package wowjoy.fruits.ms.util;

/**
 * Created by wangziwen on 2017/9/29.
 */
public class RestResultPage<T> extends RestResult {
    private final int pageNum;
    private final int pageSize;
    private final long total;
    private final int pages;

    public RestResultPage(int pageNum, int pageSize, long total, T data) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.pages = (int) ((total - 1) / pageSize + 1);
        this.setData(data);
    }

    public int getPageNum() {
        return pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotal() {
        return total;
    }

    public int getPages() {
        return pages;
    }

}
