package com.apple.service;

import com.apple.domain.ResponseResult;
import com.apple.domain.dto.AddArticleDto;
import com.apple.domain.entity.Article;
import com.apple.domain.vo.PageVo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ArticleService extends IService<Article> {
    ResponseResult hotArticleList();

    ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId);

    ResponseResult getArticleDetail(Long id);

    ResponseResult updateViewCount(Long id);

    ResponseResult addArticle(AddArticleDto article);

    ResponseResult<PageVo> list(Integer pageNum, Integer pageSize, String title, String summary);

    ResponseResult<AddArticleDto> getArticleById(Long id);

    ResponseResult updateArticle(AddArticleDto addArticleDto);

    ResponseResult deleteArticle(Long id);
}
