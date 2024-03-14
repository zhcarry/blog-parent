import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@SpringBootTest
@Slf4j
public class SensitiveFilter {
    // 根节点
    private TrieNode rootNode = new TrieNode();
    // 替换符
    private static final String REPLACEMENT = "***";

    @Test
    public void sensitiveFilter() {
        String text = "小明在赌场卖毒品，这样违法！";
        SensitiveFilter filter = new SensitiveFilter();
        filter.init();
        System.out.println(filter.filter(text));
    }

    //初始化前缀树
    public void init() {
        try (
                //使用类加载器读取 敏感词库 并缓存
                InputStream is = this.getClass().getClassLoader().
                        getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {

            String SensitiveWord;
            while ((SensitiveWord = reader.readLine()) != null) {
                // 添加到前缀树
                this.addSensitiveWord(SensitiveWord);
            }
        } catch (IOException e) {
            log.error("加载敏感词文件失败: " + e.getMessage());
        }
    }

    // 将一个敏感词添加到前缀树中
    private void addSensitiveWord(String sensitiveWord) {
        //根节点
        TrieNode tempNode = rootNode;
        //遍历敏感词 获取每一个字符
        for (int i = 0; i < sensitiveWord.length(); i++) {
            char c = sensitiveWord.charAt(i);
            //根据字符获取子节点
            TrieNode subNode = tempNode.getSubNode(c);
            //如果子节点为空 创建
            if (subNode == null) {
                // 初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }

            // 指向子节点,进入下一轮循环
            tempNode = subNode;

            // 设置结束标识
            if (i == sensitiveWord.length() - 1) {
                tempNode.setWordEnd(true);
            }
        }
    }

    // 判断是否为符号
    private boolean isSymbol(Character c) {
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        // 当前节点
        TrieNode tempNode = rootNode;
        // text字符开始位置
        int begin = 0;
        // text字符当前位置
        int position = 0;
        // 结果
        StringBuilder sb = new StringBuilder();

        while (position < text.length()) {
            char c = text.charAt(position);
            // 跳过符号
            if (isSymbol(c)) {
                // 若当前节点处于根节点,将此符号计入结果,取下一个text字符
                if (tempNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                // 无论符号在开头或中间,当前位置都向下走一步
                position++;
                continue;
            }

            // 检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                // 以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                // 进入下一个位置
                position = ++begin;
                // 重新指向根节点
                tempNode = rootNode;
            } else if (tempNode.isWordEnd()) {
                // 发现敏感词,将begin~position字符串替换掉
                sb.append(REPLACEMENT);
                // 进入下一个位置
                begin = ++position;
                // 重新指向根节点
                tempNode = rootNode;
            } else {
                // 检查下一个字符
                position++;
            }
        }
        // 将最后一批字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }
}
