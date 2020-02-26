package com.fc.javacode.java;

import java.util.LinkedHashMap;

/**
 * LinkedHashMap demo测试
 */
public class LinkedHashMapTest {

    public static void main(String[] args) throws Exception {

        //正常按照插入的顺序迭代
        LinkedHashMap<String,Integer> linkedHashMapSort = new LinkedHashMap(6,0.75f,false);

        linkedHashMapSort.put("张三",1);
        linkedHashMapSort.put("李四",2);
        linkedHashMapSort.put("王五",3);
        linkedHashMapSort.put("李六",4);
        linkedHashMapSort.put("赵七",5);

        linkedHashMapSort.get("张三");
        linkedHashMapSort.get("李六");
        //
        linkedHashMapSort.forEach((k,v)->{
            System.out.println(k+"------------"+v);
        });

        System.out.println("----------------------------分割线--------------------------");

        //按照访问的顺序迭代
        LinkedHashMap<String,Integer> linkedHashMapAccessSort = new LinkedHashMap(6,0.75f,true);

        linkedHashMapAccessSort.put("张三",1);
        linkedHashMapAccessSort.put("李四",2);
        linkedHashMapAccessSort.put("王五",3);
        linkedHashMapAccessSort.put("李六",4);
        linkedHashMapAccessSort.put("赵七",5);
        linkedHashMapAccessSort.get("张三");
        linkedHashMapAccessSort.get("李六");

        //
        linkedHashMapAccessSort.forEach((k,v)->{
            System.out.println(k+"------------"+v);
        });

    }
}
