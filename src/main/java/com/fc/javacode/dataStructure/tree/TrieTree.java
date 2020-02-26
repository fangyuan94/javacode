package com.fc.javacode.dataStructure.tree;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

/**
 * java实现Trie树数据结构
 * 此树为不安全
 * @author fangyuan
 */
public class TrieTree implements Serializable, Iterable<String> {

    private static final long serialVersionUID = 8386482531322992189L;

    /**
     * 当前Trie树中包含元素个数
     */
    private int size;

    /**
     * 当前Trie树中包含节点数量
     */
    private long length;

    /**
     * 定义根节点
     */
    private TrieTreeNode root;

    /**
     * 用于存储当前树中元素
     */
    private List<String> eles;

    //定义节点类型
    /**
     * 标示该节点字符串应该在trie树中
     */
    static final int yellow = 1;

    /**
     * 普通节点
     */
    static final int white = 0;

    /**
     * 每一层树默认分支数
     */
    static final int minTreeLevel = 2;

    /**
     * 构建函数 初始化trie树
     */
    public TrieTree(){
        this.size=0;
        this.length=1;
        this.root = new TrieTreeNode('/',white);
        this.eles = new ArrayList<>(8);
    }


    /**
     * 定义Trie节点
     */
    class TrieTreeNode {

        //当前节点对应的字母
        private char ele;

        //定义该节点类型
        private int type;

        //当前节点所包含的字节点 后续使用其它结构对其进行优化
        private List<TrieTreeNode> nexts;

        public  TrieTreeNode(char ele,int type){
            this.ele = ele;
            this.type=type;
            this.nexts = new ArrayList<>(minTreeLevel);
        }

    }

    /**
     * 新增一个元素
     * @param str 被写入的元素字符串
     */
    public void addNewEle(String str){

        char ele;
        TrieTreeNode current = this.root;
        List<TrieTreeNode> trieTreeNodes = this.root.nexts;
        //将当前词解析为char字符数组
        char[] chars = str.toCharArray();
        //将chars以节点的形式写入到Trie树中
        for (int i = 0; i < chars.length; i++) {

            ele = chars[i];
            //若词中包含空格直接去除
            if(ele ==' ') {
                continue;
            }
            //当前节点type
           int nodeType = i==chars.length-1?yellow:white;

            if(trieTreeNodes.size() == 0){
                //直接新建一个新节点
                TrieTreeNode trieTreeNode =  new TrieTreeNode(ele,nodeType);
                trieTreeNodes.add(trieTreeNode);
                current = trieTreeNode;
                trieTreeNodes = trieTreeNode.nexts;
                this.length++;
            }else{
                boolean flag = true;
                //遍历此层节点是否已包含了该字母
                for (int j=0;j<trieTreeNodes.size();j++) {
                    TrieTreeNode trieTreeNode  = trieTreeNodes.get(j);
                    if(trieTreeNode.ele == ele){
                        //说明当前节点已经存在 处理下前缀词
                        trieTreeNode.type =nodeType;
                        current = trieTreeNode;
                        trieTreeNodes = trieTreeNode.nexts;
                        flag = false;
                        break;
                    }
                }

                //若当前节点不包含则重新创建
                if(flag){
                    TrieTreeNode trieTreeNode =  new TrieTreeNode(ele,nodeType);
                    trieTreeNodes.add(trieTreeNode);
                    current = trieTreeNode;
                    trieTreeNodes = trieTreeNode.nexts;
                    this.length++;
                }
            }
        }
        //创建完成
        this.size++;

        eles.add(str);
    }

    /**
     * 删除一个元素
     * @param str 被删除的元素字符串
     */
    public void remove(String str){

        char ele;
        TrieTreeNode current = this.root;
        List<TrieTreeNode> trieTreeNodes = this.root.nexts;
        //将当前词解析为char字符数组
        char[] chars = str.toCharArray();

        boolean flag = true;

        LinkedHashMap<List<TrieTreeNode>,TrieTreeNode> delQueue = new LinkedHashMap<>(chars.length);

        //遍历trie树
        for (int i = 0; i < chars.length; i++) {

            ele = chars[i];

            //若词中包含空格直接去除
            if (ele == ' ') {
                continue;
            }

            //遍历此层节点是否已包含了该字母
            for (int j=0;j<trieTreeNodes.size();j++) {
                TrieTreeNode trieTreeNode  = trieTreeNodes.get(j);
                if(trieTreeNode.ele == ele){
                    //若当前字符为该单词的最后一个字母 若该单词为一个前缀词只需要变更节点属性即可
                    if(i==chars.length-1 && trieTreeNode.type == yellow && trieTreeNode.nexts.size()>0){
                        //变更节点类型
                        trieTreeNode.type=white;
                        this.size--;
                        this.eles.remove(str);
                        return;
                    }
                    //记录当前节点到队列中 为后续删除做准备
                    delQueue.put(trieTreeNodes,trieTreeNode);
                    current = trieTreeNode;
                    trieTreeNodes = trieTreeNode.nexts;
                    break;
                }

                if(j==trieTreeNodes.size()-1){
                    //当前字符串在Trie中不存在 直接跳出
                    return;
                }
            }
        }

        //移除节点
        for(Map.Entry<List<TrieTreeNode>, TrieTreeNode> entry: delQueue.entrySet()){
            List<TrieTreeNode> k = entry.getKey();
            TrieTreeNode v = entry.getValue();
            if(v.nexts.size()<=1){
                if(flag) {
                    k.remove(v);
                    flag = false;
                }
                this.length--;
            }
        }
        //参数递减
        this.size--;
        this.eles.remove(str);
    }

    /**
     * 包含元素总数
     * @return
     */
    public int size(){
        return this.size;
    }

    /**
     * 查询的字符串是否在trie字典中存在
     * @param str 被查询的元素字符串
     * @return
     */
    public  boolean search(String str) {

        char ele;
        TrieTreeNode current = this.root;
        List<TrieTreeNode> trieTreeNodes = this.root.nexts;
        //将当前词解析为char字符数组
        char[] chars = str.toCharArray();
        boolean flag = false;

        //遍历trie树
        for (int i = 0; i < chars.length; i++) {

            ele = chars[i];

            //若词中包含空格直接去除
            if(ele ==' ') {
                continue;
            }
            //遍历此层节点是否已包含了该字母
            for (int j=0;j<trieTreeNodes.size();j++) {
                TrieTreeNode trieTreeNode  = trieTreeNodes.get(j);
                if(trieTreeNode.ele == ele){
                    //若当前字符为该单词的最好一个字母 为避免前缀词的影响 需再次确认下 尾节点的类型
                    if(i==chars.length-1 && trieTreeNode.type == yellow){
                        return true;
                    }
                    current = trieTreeNode;
                    trieTreeNodes = trieTreeNode.nexts;
                    break;
                }

                if(j==trieTreeNodes.size()-1){
                    flag = true;
                }

            }
            if(flag){
                break;
            }
        }
        return false;
    }

    @Override
    public Iterator<String> iterator() {
        return this.eles.iterator();
    }

    /**
     * 实际调用list中接口处理
     * @param action
     */
    @Override
    public void forEach(Consumer<? super String> action) {
        this.eles.forEach(action);
    }

    public static void main(String[] args) {

        TrieTree trieTree = new TrieTree();

        String[] test = {"fuck","bitch","bullshit","bull","suck"};

        for(int i=0;i<test.length;i++){

            trieTree.addNewEle(test[i]);
        }

        //测试迭代功能
        trieTree.forEach(str->{
            System.out.println("-----初始元素集合------"+str);
        });

        //检测单词
        System.out.println("-------bitch------>"+trieTree.search("bitch"));

        System.out.println("------suck------->"+trieTree.search("suck"));

        System.out.println("------bull------->"+trieTree.search("bull"));


        trieTree.remove("bitch");

        trieTree.remove("suck");

        trieTree.remove("bull");

        //测试迭代功能
        trieTree.forEach(str->{
            System.out.println("-----被删除后的元素集合------"+str);
        });

        System.out.println("-------bitch------>"+trieTree.search("bitch"));

        System.out.println("------suck------->"+trieTree.search("suck"));

        System.out.println("------bull------->"+trieTree.search("bull"));

    }
}
