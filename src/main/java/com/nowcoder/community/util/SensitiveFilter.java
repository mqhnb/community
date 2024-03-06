package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    // 换字符
    private static final String REPLACEMENT = "*";


    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    // 根节点
    private TrieNode trieNode = new TrieNode();

    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败: " + e.getMessage());
        }
    }

//    @PostConstruct
//    public void init()
//    {
//            try (
//                    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//                    ){
//                String keyword;
//                while ((keyword = reader.readLine())!=null)
//                {
//                    this.addKeyword(keyword);
//                }
//
//            }catch (IOException e)
//            {
//                logger.error("读取文件出错");
//            }
//    }

    private void addKeyword(String keyword)
    {
        TrieNode tempnode = trieNode;
        for(int i = 0;i<keyword.length();i++)
        {
            char c = keyword.charAt(i);
            TrieNode temp = tempnode.getSubnodes(c);

            if(temp ==null)
            {
                temp = new TrieNode();
                tempnode.setSubNodes(c,temp);
            }

                tempnode = temp;

                if(i == keyword.length() -1)
                {
                    tempnode.setKeywordEend();
                }

        }
    }

    /**
     * 过滤敏感词
     *
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        // 指针1
        TrieNode tempNode = trieNode;
        // 指针2
        int begin = 0;
        // 指针3
        int position = 0;
        // 结果
        StringBuilder sb = new StringBuilder();

        while (position < text.length()) {
            char c = text.charAt(position);

            // 跳过符号
            if (isSymbol(c)) {
                // 若指针1处于根节点,将此符号计入结果,让指针2向下走一步
                if (tempNode == trieNode) {
                    sb.append(c);
                    begin++;
                }
                // 无论符号在开头或中间,指针3都向下走一步
                position++;
                continue;
            }

            // 检查下级节点
            tempNode = tempNode.getSubnodes(c);
            if (tempNode == null) {
                // 以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                // 进入下一个位置
                position = ++begin;
                // 重新指向根节点
                tempNode = trieNode;
            } else if (tempNode.isKeywordEnd()) {
                // 发现敏感词,将begin~position字符串替换掉
                sb.append(REPLACEMENT);
                // 进入下一个位置
                begin = ++position;
                // 重新指向根节点
                tempNode = trieNode;
            } else {
                // 检查下一个字符
                position++;
            }
        }

        // 将最后一批字符计入结果
        sb.append(text.substring(begin));

        return sb.toString();
    }

    private boolean isSymbol(Character c)
    {
        return !CharUtils.isAsciiAlphanumeric(c) && ((c< 0x2E80) || c>0x9FFF) ;
    }

    //前缀树
    private class TrieNode
    {
        private boolean isKeywordEend = false;

        public void setKeywordEend()
        {
            this.isKeywordEend = true;
        }

        public boolean isKeywordEnd()
        {
            return this.isKeywordEend;
        }


        Map<Character,TrieNode> subNodes = new HashMap<>();

        public void setSubNodes(Character character,TrieNode node)
        {
            subNodes.put(character,node);
        }

        public TrieNode getSubnodes(Character character)
        {
            return subNodes.get(character);
        }

    }
}
