package com.fc.javacode.dataStructure.lru;

/**
 *
 * 测试MyLRUCache的功能是否可用
 * @author fangyuan
 */
public class  MyLRUCacheTest{

    public static void main(String[] args) {

        int maxSize = 6 ;

        MyLRUCache myLRUCache = new MyLRUCache(maxSize);

        //构建测试数据
        for (int i=0;i<6;i++){
            myLRUCache.putCaceh("test_key_"+i,i);
        }

        myLRUCache.getCache("test_key_1");
        myLRUCache.getCache("test_key_5");

        myLRUCache.putCaceh("test_key_7",7);
        myLRUCache.putCaceh("test_key_8",8);
        myLRUCache.getCache("test_key_1");
        myLRUCache.getCache("test_key_5");
        myLRUCache.putCaceh("test_key_9",9);
        myLRUCache.putCaceh("test_key_10",10);
        myLRUCache.getCache("test_key_1");
        myLRUCache.getCache("test_key_5");
        myLRUCache.getCache("test_key_8");
        myLRUCache.putCaceh("test_key_5",55);


        //按照LRU算法 最终的结果为 55(最新数据),8,1,10,9,7(最old数据)
        myLRUCache.forEach((k,v)->{
            System.out.print(v+",");
        });

    }
}
