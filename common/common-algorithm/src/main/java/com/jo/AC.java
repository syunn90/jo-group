package com.jo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xtc
 * @date 2024/4/12
 */
public class AC {
    public static void main(String[] args) {
        List<String> strings = new ArrayList<>();
        strings.add("a");
        strings.add("b");
        strings.add("abc");
        strings.add("ac");
        strings.add("ad");
        ACTree tree = new ACTree(strings.toArray(String[]::new));
        String a = new ACFilter(tree).filter("ab");

        System.out.println(a);
    }
    static class ACFilter {

        private static final String regEx = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        private static ACTree tree;

        public ACFilter(ACTree tree) {
            this.tree = tree;
        }

        /**
         * 过滤
         */
        public  String filter(String word) {
            if (ObjectUtils.isEmpty(word)) {
                return "";
            }
            // 过滤结果
            StringBuilder sb = new StringBuilder();

            word = match(word);

            char[] words = word.toLowerCase().toCharArray();
            char[] result = null;
            ACNode curACNode = tree.getRootACNode();
            ACNode subACNode;
            Character c;
            int fromPos = 0;
            for (int i = 0; i < words.length; i++) {
                c = words[i];
                subACNode = curACNode.getSubNode(c);
                while (subACNode == null && curACNode != tree.getRootACNode()) {
                    curACNode = curACNode.getFailACNode();
                    subACNode = curACNode.getSubNode(c);
                }
                if (subACNode != null) {
                    curACNode = subACNode;
                }
                if (curACNode.isTerminal()) {
                    int pos = i - curACNode.getLevel() + 1;
                    if (pos < fromPos) {
                        pos = fromPos;
                    }
                    if (result == null) {
                        result = word.toLowerCase().toCharArray();
                    }
                    if (result.length > 0) {
                        sb.append("[");
                        for (; pos <= i; pos++) {
                            sb.append(result[pos]);
                        }
                        sb.append("]");
                    }

                    fromPos = i + 1;
                }
            }
            return String.valueOf(sb);
        }

        private static String match(String str) {
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(str);
            return m.replaceAll("").trim().replaceAll(" ", "");
        }




    }
    @Getter
    static class ACTree {
        // getter
        private ACNode rootACNode;

        public ACTree(String[] keyWords) {
            // 初始树
            initTree(keyWords);
            // 构建失败跳转
            buildFailLink();
        }

        /**
         * 初始树
         */
        private void initTree(String[] keyWords) {
            rootACNode = new ACNode();
            rootACNode.setSubNodes(new HashMap<Character, ACNode>());
            char[] charArray;
            for (String keyWord : keyWords) {
                if (keyWord.isEmpty()) {
                    continue;
                }
                charArray = keyWord.toLowerCase().toCharArray();
                buildKeyMap(charArray);
            }
        }

        /**
         * 构建指定字符数组的结点
         */
        private void buildKeyMap(char[] charArray) {
            Character c;
            ACNode curACNode = rootACNode;
            ACNode ACNode;
            for (int i = 0; i < charArray.length; i++) {
                c = charArray[i];
                if (curACNode.containSubNode(c)) {
                    ACNode = curACNode.getSubNode(c);
                } else {
                    ACNode = new ACNode();
                    ACNode.setLevel(i + 1);
                    curACNode.addSubNode(c, ACNode);
                }
                if (i == charArray.length - 1) {
                    ACNode.setTerminal(true);
                }
                curACNode = ACNode;
            }
        }

        /**
         * 构建失败跳转
         */
        private void buildFailLink() {
            buildFirstLevelFailLink();
            buildOtherLevelFailLink();
        }

        /**
         * 根结点的所有第一级子结点，失败跳转均为根结点
         */
        private void buildFirstLevelFailLink() {
            Collection<ACNode> ACNodes = rootACNode.getSubNodes().values();
            for (ACNode ACNode : ACNodes) {
                ACNode.setFailACNode(rootACNode);
            }
        }

        /**
         * 根结点、第一级结点以外的所有结点，失败跳转均为其父结点的失败结点的对应子结点
         */
        private void buildOtherLevelFailLink() {
            Queue<ACNode> queue = new LinkedList<>(rootACNode.getSubNodes().values());
            ACNode ACNode;
            while (!queue.isEmpty()) {
                ACNode = queue.remove();
                buildNodeFailLink(ACNode, queue);
            }
        }

        /**
         * 构建指定结点的下一层结点的失败跳转
         *
         */
        private void buildNodeFailLink(ACNode ACNode, Queue<ACNode> queue) {
            if (ACNode.getSubNodes().isEmpty()) {
                return;
            }
            queue.addAll(ACNode.getSubNodes().values());
            ACNode failACNode = ACNode.getFailACNode();
            Set<Character> subNodeKeys = ACNode.getSubNodes().keySet();
            ACNode subFailACNode;
            for (Character key : subNodeKeys) {
                subFailACNode = failACNode;
                while (subFailACNode != rootACNode && !subFailACNode.containSubNode(key)) {
                    subFailACNode = subFailACNode.getFailACNode();
                }
                subFailACNode = subFailACNode.getSubNode(key);
                if (subFailACNode == null) {
                    subFailACNode = rootACNode;
                }
                ACNode.getSubNode(key).setFailACNode(subFailACNode);
            }
        }

    }


    @Setter
    static class ACNode {
        // getter & setter
        // getter & setter
        // 当前结点的层级
        @Getter
        private int level;
        // 当前结点后子结点，Key为小写字母
        private Map<Character, ACNode> subNodes;
        // 当前结果匹配失败时的跳转结点
        @Getter
        private ACNode failACNode;
        // 当前结点是否是终结结点
        @Getter
        private boolean terminal;

        /**
         * 当前结点是否已包含指定Key值的子结点
         */
        public boolean containSubNode(Character c) {
            if (this.subNodes == null || this.subNodes.isEmpty()) {
                return false;
            }
            return subNodes.containsKey(c);
        }

        /**
         * 获取指定Key值的子结点
         */
        public ACNode getSubNode(Character c) {
            if (this.subNodes == null || this.subNodes.isEmpty()) {
                return null;
            }
            return subNodes.get(c);
        }

        /**
         * 添加子结点
         */
        public void addSubNode(Character c, ACNode ACNode) {
            if (this.subNodes == null) {
                this.subNodes = new HashMap<Character, ACNode>();
            }
            this.subNodes.put(c, ACNode);
        }

        public Map<Character, ACNode> getSubNodes() {
            return subNodes == null ? Collections.emptyMap() : subNodes;
        }

    }
}