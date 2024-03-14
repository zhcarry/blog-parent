import java.util.HashMap;
import java.util.Map;

//前缀树类
public class TrieNode {
    //子节点
    private Map<Character, TrieNode> subNodes = new HashMap<>();
    //敏感词结束标记
    private boolean isWordEnd = false;

    //获取结束标记
    public boolean isWordEnd() {
        return isWordEnd;
    }

    //设置结束标记
    public void setWordEnd(boolean wordEnd) {
        isWordEnd = wordEnd;
    }

    //添加子节点
    public void addSubNode(Character c, TrieNode node) {
        subNodes.put(c, node);
    }

    //获取子节点
    public TrieNode getSubNode(Character c) {
        return subNodes.get(c);
    }
}
