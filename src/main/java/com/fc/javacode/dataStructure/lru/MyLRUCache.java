package com.fc.javacode.dataStructure.lru;

import java.util.HashMap;
import java.util.function.BiConsumer;

/**
 * 基于HashMap+双向链表
 * @author fangyuan
 */
public class MyLRUCache<K,V> {

    /**
     * 用于存储所有节点数据【利用HashMap中读写为O(1)特性】可以快速获取节点
     */
    private HashMap<K,Node<K,V>> nodes;

    /**
     * 用于存储双向链表头
     */
    private Node<K, V> head;

    /**
     * 用于存储双向链尾
     */
    private Node<K, V> tail;

    /**
     * 缓存中最大数据深度
     */
    private Integer maxSize;


    public MyLRUCache(Integer maxSize){

        if( ( maxSize =(maxSize ==null ? 16:maxSize))<6){

            throw new RuntimeException("缓存最大过小，最小值为6");
        }

        nodes = new HashMap<K,Node<K,V>>(maxSize,0.75F);

        this.maxSize = maxSize;

        //初始化
        this.head = null;
        this.tail = null;
    }

    /**
     * 双向链表
     */
    class Node<K, V> {

        private K key;

        private V value;

        private Node<K, V> before;

        private Node<K, V> after;

        public Node(K k,V v){
            this.key = k;
            this.value =v;
        }
    }

    public  void putCaceh(K k,V v){

        //创建数据节点
        Node<K,V> node = new Node<>(k,v);

        //将当前节点加入双向链表头
        this.addNewNode(node);
        //判断是否内存缓存超过最大深度

        if(nodes.size()>this.maxSize){
            //移除尾部数据
            removeTailNode();
        }
    }

    public Integer size(){
        return nodes.size();
    }

    private void removeTailNode() {

        this.tail.before.after = null;
        this.nodes.remove(tail.key);
        this.tail = tail.before;

    }

    private void addNewNode(Node<K,V> node) {

        if(nodes.containsKey(node.key)){

            //该key已经存在 替换值并
            Node cnode = nodes.get(node.key);

            cnode.value = node.value;

            afterNodeAccess(cnode);

        }else {
            //是一个新值只需要追加到双向链表头即可
            if(head != null){

                this.head.before = node;
                node.after = this.head;

                if(tail == null ){
                    tail = head;
                }
                this.head = node;

            }else {
                this.head = node;
                this.tail = this.head;
            }
        }

        nodes.put(node.key,node);
    }

    /**
     * 获取元素
     * @param t
     * @return
     */
    public V getCache(K t){

        Node<K,V> node = nodes.get(t);

        if(node != null){
            V v =  node.value;
            //将当前节点放到双向链表头部
            this.afterNodeAccess(node);
            return v;
        }
        return null;
    }

    /**
     * 将node写入双向链表头
     * @param node
     */
    private void afterNodeAccess(Node<K,V> node) {

        Node p = node,a = p.after, b=p.before;

        p.before = null;
        //进行替换

        //说明当前节点是head
        if(b == null){
            return;
        }else {
            b.after = a;
        }
        //说明当前节点为tail
        if(a == null ){
           this.tail = b;
        }else{
            a.before = b;
        }
        head.before = p;
        p.after = head;
        head = p;
    }

    /**
     * 循环迭代
     */
    public void forEach(BiConsumer<? super K, ? super V> action) {

        if (action == null) {
            throw new NullPointerException();
        }
        for (Node<K,V> e = head; e != null; e = e.after) {
            action.accept(e.key, e.value);
        }

    }

}
