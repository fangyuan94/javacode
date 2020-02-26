package com.fc.javacode.dataStructure.lru;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *使用LinkedHashMap实现LRU算法
 *
 * @author fangyuan
 *
 */
public class LRUCache  extends LinkedHashMap {

    /**
     * 使用LRU算法构建的缓存中保存的最大数据
     */
    private Integer maxSize;

    public LRUCache( Integer maxSize){
        super(16,0.75F,true);
        this.maxSize = maxSize;
    }

    /**
     * 重写removeEldestEntry，按照LRU算法，当达到内存最大深度时，移除最年长的数据(不经常被访问的数据)
     * @param eldest
     * @return
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return this.maxSize<this.size();
    }

}
