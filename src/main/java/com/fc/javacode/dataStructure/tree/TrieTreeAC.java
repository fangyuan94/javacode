package com.fc.javacode.dataStructure.tree;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

/**
 * 使用AC算法结合Trie来做到高效的多文本匹配
 *
 */
public class TrieTreeAC implements Serializable, Iterable<String> {

    /**
     * 当前Trie树中包含元素个数
     */
    private int size;

    /**
     * 当前Trie树中包含节点数量
     */
    private long length;

    /**
     * 目前最大level深度 为了应对多层词结构(例如存储AB，ABC都是词典中一员需要将两者都处理)
     */
    private int maxLevel;

    /**
     * 定义根节点
     */
    private TrieTreeAC.TrieTreeNode root;

    /**
     * 用于存储当前树中元素
     */
    private List<String> eles;

    String xh = "";

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
    public TrieTreeAC(){
        this.size=0;
        this.length=1;
        this.root = new TrieTreeAC.TrieTreeNode('/',white,0);
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
        private Map<Character, TrieTreeAC.TrieTreeNode> nexts;

        //当前节点对应level深度
        private int level;

        //当查询失败时跳到的节点
        private TrieTreeNode failTo;

        public  TrieTreeNode(char ele,int type,int level){
            this.ele = ele;
            this.type=type;
            this.level=level;
            this.nexts = new HashMap<>(minTreeLevel);
        }

    }

    /**
     * 记录字符串中
     */
    class Position{

        int startCursor;

        int endCursor;

        public  Position(int startCursor,int endCursor){
            this.startCursor = startCursor;
            this.endCursor = endCursor;
        }
    }

    /**
     * 初始化
     * @param entitys
     */
    public  void init(List<String> entitys){
        this.buildTrieTree(entitys);
        this.buildFailTo();
    }

    /**
     * 根据词典创建一个新的字典树
     */
    private void buildTrieTree(List<String> entitys){

        //将数据写入到Trie树
        entitys.forEach(str->{
            this.addNewEle(str);
        });

    }

    /**
     * 为每个节点构建匹配失败时的跳转-失败结点(即匹配失败时，跳转到哪个结点继续匹配)
     * 其思想为：第一层子节点的fail节点直接指定是根节点
     * 其它的子节点的fail节点是其父节点的失败点中查找相应路径 如果查询不到则继续在当前节点的失败节点中查找相应的路径
     * 直到找相应的节点或失败节点为根节点
     */
    private void buildFailTo() {
        //这里使用算法类似于BFS算法(核心是一样的，从根节点向外部一层一层处理)
        Queue<TrieTreeNode> queue = new LinkedList<TrieTreeNode>();

        queue.add(this.root);

        while(!queue.isEmpty()){

            TrieTreeNode  currentNode =  queue.poll();

            TrieTreeNode failTo = currentNode.failTo;

            //处理当前节点的子节点
            for (Map.Entry<Character, TrieTreeNode> entry :currentNode.nexts.entrySet()) {

                Character childEle = entry.getKey();
                TrieTreeNode childNode = entry.getValue();

                queue.add(childNode);
                //循环一直找到该节点的回退点
                while(true){
                    if(failTo == null){
                        //如果当前节点无回退点 直接以根节点作为回退点
                        childNode.failTo=this.root;
                        break;
                    }
                    //判断fail节点是其父节点的失败点中查找相应路径
                    if(failTo.nexts.containsKey(childEle)){
                        childNode.failTo = failTo.nexts.get(childEle);
                        break;
                    }else{
                        //继续在当前节点的失败节点中查找相应的路径
                        failTo = failTo.failTo;
                    }
                }
            }
        }

        //根路径fail指向root节点
        this.root.failTo = this.root;
    }

    /**
     * 将一段文字中敏感字符进行替换为*号格式
     * @param str 需要被替换的字符
     */
    public String replace(String str){
        List<Position> positions = getPositions(str);
        //获取对应位置上字符串
        StringBuilder sb = new StringBuilder(str);
        positions.forEach(position -> {
            sb.replace(position.startCursor,position.endCursor+1,this.xh.substring(0,position.endCursor-position.startCursor+1));
        });
        return sb.toString();
    }

    /**
     *
     * @param str
     * @return
     */
    public List<String> filter(String str){

        List<Position> positions = getPositions(str);

        List<String> rs = new ArrayList<>(positions.size());

        positions.forEach(position -> {
            rs.add(str.substring(position.startCursor,position.endCursor+1));
        });
        return rs;
    }

    /**
     *
     * @param str
     * @return
     */
    private List<Position> getPositions(String str){

        char ele;
        char[] chars = str.toLowerCase().toCharArray();
        //定义起点游标 遍历字符串起点游标
        int startCursor = 0;
        //定义end游标
        int endCurson = 0;

        List<Position> positions = new ArrayList<>(8);
        TrieTreeAC.TrieTreeNode current = this.root;

        //开始进行替换
        for(int i=0;i<chars.length;i++){

            endCurson = i;
            ele = chars[i];
            //遍历此层节点是否已包含了该字母
            TrieTreeAC.TrieTreeNode trieTreeNode ;

            if((trieTreeNode = current.nexts.get(ele))!=null){
                //处理AC中goto
                if( trieTreeNode.type == yellow ){
                    if(trieTreeNode.nexts.size()>0 &&trieTreeNode.level<this.maxLevel){
                        //虽然找到end节点 避免前缀词的影响继续向下迭代
                        //记录对应位置 继续向下寻找
                        Position position = new Position(startCursor,endCurson);
                        positions.add(position);
                        current = trieTreeNode;
                    }else{
                        //说明已找到词典 记录对应字典的位置
                        Position position = new Position(startCursor,endCurson);
                        positions.add(position);
                        //开始下一轮的词典的寻找
                        startCursor = i;
                        current = this.root;
                    }
                }else{
                    //继续下一个字符的匹配
                    current = trieTreeNode;
                    continue;
                }
            }else{
                //匹配失败 尝试进行跳转到失败节点进行下一次匹配
                //重置startCursor位置
                if(current.failTo.equals(this.root)){
                    //当前节点fialTo的节点为root根节点
                    //开始下一轮的词典的寻找

                    //重置位置
                    if(i <chars.length-1 && current.level>0){
                        startCursor = i;
                        i = i-1;
                    }else{
                        startCursor = i+1;
                    }
                }else{
                    //计算当前对应start位置 并更新
                    startCursor +=(i+1-startCursor - current.failTo.level);
                }
                current = current.failTo;
            }
        }
        return positions;
    }

    /**
     * 新增一个元素
     * @param str 被写入的元素字符串
     */
    public void addNewEle(String str){

        char ele;
        TrieTreeAC.TrieTreeNode current = this.root;
        Map<Character, TrieTreeAC.TrieTreeNode> trieTreeNodes = this.root.nexts;
        //将当前词解析为char字符数组
        char[] chars = str.toLowerCase().toCharArray();
        //将chars以节点的形式写入到Trie树中
        for (int i = 0; i < chars.length; i++) {

            ele = chars[i];
            //若词中包含空格直接去除
            if(ele ==' ') {
                continue;
            }
            //当前节点type
            int nodeType = i==chars.length-1?yellow:white;
            //若当前类型为end节点 用以计算最大深度
            if(nodeType == yellow && maxLevel<i+1){
                //构建*库
                for(int n=0;n<(i+1)-maxLevel;n++){
                    this.xh+="*";
                }
                maxLevel = i+1 ;
            }
            //遍历此层节点是否已包含了该字母
            TrieTreeAC.TrieTreeNode trieTreeNode ;
            if((trieTreeNode = trieTreeNodes.get(ele))!=null){
                //说明当前节点已经存在 处理下前缀词
                trieTreeNode.type =nodeType;
                current = trieTreeNode;
                trieTreeNodes = trieTreeNode.nexts;
                continue;
            }else{
                trieTreeNode =  new TrieTreeAC.TrieTreeNode(ele,nodeType,i+1);
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
        TrieTreeAC.TrieTreeNode current = this.root;
        Map<Character, TrieTreeAC.TrieTreeNode> trieTreeNodes = this.root.nexts;
        //将当前词解析为char字符数组
        char[] chars = str.toLowerCase().toCharArray();
        //将chars以节点的形式写入到Trie树中
        for (int i = 0; i < chars.length; i++) {

            ele = chars[i];
            //若词中包含空格直接去除
            if(ele ==' ') {
                continue;
            }

            //遍历此层节点是否已包含了该字母
            TrieTreeAC.TrieTreeNode trieTreeNode ;
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
        TrieTreeAC.TrieTreeNode current = this.root;
        Map<Character, TrieTreeAC.TrieTreeNode> trieTreeNodes = this.root.nexts;
        //将当前词解析为char字符数组
        char[] chars = str.toLowerCase().toCharArray();

        LinkedHashMap<Map, Character> delQueue = new LinkedHashMap<>(chars.length);

        //遍历trie树
        for (int i = 0; i < chars.length; i++) {

            ele = chars[i];

            //若词中包含空格直接去除
            if (ele == ' ') {
                continue;
            }

            //遍历此层节点是否已包含了该字母
            TrieTreeAC.TrieTreeNode trieTreeNode ;
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

        TrieTreeAC trieTreeAC = new TrieTreeAC();

        List<String> test = new ArrayList<>();

        test.add("fuck");
        test.add("bitch");
        test.add("bullshit");
        test.add("bull");
        test.add("suck");
        test.add("婊子");
        test.add("我草");
        test.add("草");
        test.add("我草你");

        trieTreeAC.init(test);
        //
        String testStr = "你好你好啊，我bullshitbitc哈哈卧槽我草你呀啊呀呀发疯";
        System.out.println("=======原语句为=======>"+testStr);
        //测试替换功能
        String rs = trieTreeAC.replace(testStr);

        trieTreeAC.filter(testStr).forEach(str->{
            System.out.println(str);
        });

        System.out.println("=======处理过后的语句为=======>"+rs);


        //测试迭代功能
        trieTreeAC.forEach(str->{
            System.out.println("-----初始元素集合------"+str);
        });

        //检测单词
        System.out.println("-------bitch------>"+trieTreeAC.search("bitch"));

        System.out.println("------suck------->"+trieTreeAC.search("suck"));

        System.out.println("------bull------->"+trieTreeAC.search("bull"));


        trieTreeAC.remove("bitch");

        trieTreeAC.remove("suck");

        trieTreeAC.remove("bull");

        //测试迭代功能
        trieTreeAC.forEach(str->{
            System.out.println("-----被删除后的元素集合------"+str);
        });

        System.out.println("-------bitch------>"+trieTreeAC.search("bitch"));

        System.out.println("------suck------->"+trieTreeAC.search("suck"));

        System.out.println("------bull------->"+trieTreeAC.search("bull"));

    }

}