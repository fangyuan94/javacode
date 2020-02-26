package com.fc.javacode.dataStructure.tree;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

/**
 *
 *相对于TrieTree 优化子节点的寻址 使得整个Trie树的时间复杂度为O(n) n为树的深度
 * @author fangyuan
 */
public class TrieHashTree  implements Serializable, Iterable<String> {

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
    private TrieHashTree.TrieTreeNode root;

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
    public TrieHashTree(){
        this.size=0;
        this.length=1;
        this.root = new TrieHashTree.TrieTreeNode('/',white);
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
        private Map<Character,TrieTreeNode> nexts;


        public  TrieTreeNode(char ele,int type){
            this.ele = ele;
            this.type=type;
            this.nexts = new HashMap<>(minTreeLevel);
        }

    }

    /**
     * 新增一个元素
     * @param str 被写入的元素字符串
     */
    public void addNewEle(String str){

        char ele;
        TrieHashTree.TrieTreeNode current = this.root;
        Map<Character,TrieTreeNode> trieTreeNodes = this.root.nexts;
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

            //遍历此层节点是否已包含了该字母
            TrieTreeNode trieTreeNode ;
            if((trieTreeNode = trieTreeNodes.get(ele))!=null){
                //说明当前节点已经存在 处理下前缀词
                trieTreeNode.type =nodeType;
                current = trieTreeNode;
                trieTreeNodes = trieTreeNode.nexts;
                continue;
            }else{
                trieTreeNode =  new TrieHashTree.TrieTreeNode(ele,nodeType);
                trieTreeNodes.put(ele,trieTreeNode);
                current = trieTreeNode;
                trieTreeNodes = trieTreeNode.nexts;
                this.length++;
            }
        }
        //创建完成
        this.size++;
        eles.add(str);
    }


    /**
     * 查询的字符串是否在trie字典中存在
     * @param str 被查询的元素字符串
     * @return
     */
    public  boolean search(String str) {

        char ele;
        TrieHashTree.TrieTreeNode current = this.root;
        Map<Character,TrieTreeNode> trieTreeNodes = this.root.nexts;
        //将当前词解析为char字符数组
        char[] chars = str.toCharArray();
        //将chars以节点的形式写入到Trie树中
        for (int i = 0; i < chars.length; i++) {

            ele = chars[i];
            //若词中包含空格直接去除
            if(ele ==' ') {
                continue;
            }

            //遍历此层节点是否已包含了该字母
            TrieTreeNode trieTreeNode ;
            if((trieTreeNode = trieTreeNodes.get(ele))!=null){
                //若当前字符为该单词的最好一个字母 为避免前缀词的影响 需再次确认下 尾节点的类型
                if(i==chars.length-1 && trieTreeNode.type == yellow){
                    return true;
                }
                current = trieTreeNode;
                trieTreeNodes = trieTreeNode.nexts;
                continue;
            }else{
                break;
            }
        }
        return false;
    }

    /**
     * 删除一个元素
     * @param str 被删除的元素字符串
     */
    public void remove(String str){

        char ele;
        TrieHashTree.TrieTreeNode current = this.root;
        Map<Character,TrieTreeNode> trieTreeNodes = this.root.nexts;
        //将当前词解析为char字符数组
        char[] chars = str.toCharArray();

        LinkedHashMap<Map, Character> delQueue = new LinkedHashMap<>(chars.length);

        //遍历trie树
        for (int i = 0; i < chars.length; i++) {

            ele = chars[i];

            //若词中包含空格直接去除
            if (ele == ' ') {
                continue;
            }

            //遍历此层节点是否已包含了该字母
            TrieTreeNode trieTreeNode ;
            if((trieTreeNode = trieTreeNodes.get(ele))!=null){
                //若当前字符为该单词的最后一个字母 若该单词为一个前缀词只需要变更节点属性即可
                if(i==chars.length-1 && trieTreeNode.type == yellow && trieTreeNode.nexts.size()>0){
                    //变更节点类型
                    trieTreeNode.type=white;
                    this.size--;
                    this.eles.remove(str);
                    return;
                }
                //记录当前节点到队列中 为后续删除做准备
                delQueue.put(trieTreeNodes,ele);
                current = trieTreeNode;
                trieTreeNodes = trieTreeNode.nexts;
                continue;
            }else{
                return;
            }
        }

        boolean flag = true;

        //移除节点
        for(Map.Entry<Map, Character> entry: delQueue.entrySet()){
            Map k = entry.getKey();
            Character v = entry.getValue();
            if(k.size()<=1){
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

        TrieHashTree trieTree = new TrieHashTree();

        String[] test = {"fuck","bitch","bullshit","bull","suck","婊子"};

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
