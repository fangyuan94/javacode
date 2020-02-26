package com.fc.javacode.dataStructure.lru;

/**
 * LRUCache测试
 * @author fangyuan
 */
public class LRUCacheTest {

    public static void main(String[] args) {

        int maxSize = 6 ;

        LRUCache lruCache = new LRUCache(maxSize);

        //构建测试数据
        for (int i=0;i<6;i++){
            lruCache.put("test_key_"+i,i);
        }

        lruCache.get("test_key_1");
        lruCache.get("test_key_5");

        lruCache.put("test_key_7",7);
        lruCache.put("test_key_8",8);
        lruCache.get("test_key_1");
        lruCache.get("test_key_5");
        lruCache.put("test_key_9",9);
        lruCache.put("test_key_10",10);
        lruCache.get("test_key_1");
        lruCache.get("test_key_5");
        lruCache.get("test_key_8");

        //按照LRU算法 最终的结果为 7(最old数据),9,10,1,5,8(最新数据)
        lruCache.forEach((k,v)->{
            System.out.print(v+",");
        });

    }

}
