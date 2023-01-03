package com.apple.controller;

import com.apple.domain.ResponseResult;
import com.apple.domain.dto.AddArticleDto;
import com.apple.domain.vo.PageVo;
import com.apple.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 写博客
     * @param article
     * @return
     */
    @PostMapping
    public ResponseResult addArticle(@RequestBody AddArticleDto article){
        return articleService.addArticle(article);
    }

    /**
     * 分页查询文章列表
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @param title 文章标题
     * @param summary 文章摘要
     * @return
     */
    @GetMapping("/list")
    public ResponseResult<PageVo> list(Integer pageNum, Integer pageSize, String title, String summary){
        return articleService.list(pageNum,pageSize,title,summary);
    }

    /**
     * 根据id查询文章详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult<AddArticleDto> getArticleById(@PathVariable Long id){
        return articleService.getArticleById(id);
    }

    /**
     * 修改文章
     * @param addArticleDto
     * @return
     */
    @PutMapping
    public ResponseResult updateArticle(@RequestBody AddArticleDto addArticleDto){
        return articleService.updateArticle(addArticleDto);
    }

    /**
     * 根据id删除文章
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseResult deleteArticle(@PathVariable Long id){
        return articleService.deleteArticle(id);
    }
}
