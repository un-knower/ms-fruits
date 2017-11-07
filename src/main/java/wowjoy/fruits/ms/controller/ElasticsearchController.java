package wowjoy.fruits.ms.controller;

import com.google.common.collect.Lists;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.elastic.GlobalSearchEs;
import wowjoy.fruits.ms.module.elastic.GlobalSearch;
import wowjoy.fruits.ms.module.elastic.GlobalSearchDao;
import wowjoy.fruits.ms.module.elastic.GlobalSearchVo;
import wowjoy.fruits.ms.util.JsonArgument;
import wowjoy.fruits.ms.util.RestResult;

import java.util.LinkedList;

/**
 * Created by wangziwen on 2017/9/27.
 */
@RequestMapping("/v1/elastic")
@RestController
public class ElasticsearchController {


    @Qualifier("globalSearchEs")
    @Autowired
    private GlobalSearchEs globalSearchEs;

    /*临时提供接口加载全局数据*/
    @RequestMapping(value = "/global", method = RequestMethod.POST)
    public RestResult globalDateLoading() {
        globalSearchEs.loading();
        return RestResult.getInstance().setMsg("加载完成");
    }

    /**
     * 提供es全局搜索接口
     *
     * @param vo
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public RestResult globalSearch(@JsonArgument(type = GlobalSearchVo.class) GlobalSearchVo vo) {
        final SearchHits data = globalSearchEs.finds(vo);
        final LinkedList<GlobalSearchDao> globals = Lists.newLinkedList();
        for (SearchHit hit : data.getHits())
            globals.add(GlobalSearch.newDao(hit));
        return RestResult.newPage(vo.getPage(), vo.getSize(), data.getTotalHits(), globals);
    }

}
