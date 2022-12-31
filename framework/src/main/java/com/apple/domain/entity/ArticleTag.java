package com.apple.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sg_article_tag")
public class ArticleTag implements Serializable {

    public static final long serialVersionUID = 625337492348897098L;

    /**
     * 文章 id
     */
    private Long articleId;

    /**
     * 标签 id
     */
    private Long tagId;
}
