package wowjoy.fruits.ms.module.elastic;

import com.google.common.collect.Maps;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by wangziwen on 2017/9/28.
 */
public class GlobalSearchDao extends GlobalSearch {
    private final Map<String, String> highlight = Maps.newLinkedHashMap();

    public Map<String, String> getHighlight() {
        return highlight;
    }

    public void putAllHighlight(Map<String, HighlightField> highlight) {
        highlight.forEach((k, v) -> this.highlight.put(k, Arrays.toString(v.getFragments()).replace("\"", "'")));
    }
    
}
