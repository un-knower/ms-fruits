package wowjoy.fruits.ms.module.util.entity;


import wowjoy.fruits.ms.module.AbstractEntity;

/**
 * Created by wangziwen on 2017/8/29.
 * 全局排序表管理
 */
public class FruitSort extends AbstractEntity {
    private String tableId;
    private String sortCode;

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }
}
