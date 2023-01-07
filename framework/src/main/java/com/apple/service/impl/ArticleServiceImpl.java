package com.apple.service.impl;

import com.apple.constants.SystemConstants;
import com.apple.domain.ResponseResult;
import com.apple.domain.dto.AddArticleDto;
import com.apple.domain.entity.Article;
import com.apple.domain.entity.ArticleTag;
import com.apple.domain.entity.Category;
import com.apple.domain.vo.ArticleDetailVo;
import com.apple.domain.vo.ArticleListVo;
import com.apple.domain.vo.HotArticleVo;
import com.apple.domain.vo.PageVo;
import com.apple.enums.AppHttpCodeEnum;
import com.apple.exception.SystemException;
import com.apple.mapper.ArticleMapper;
import com.apple.service.ArticleService;
import com.apple.service.ArticleTagService;
import com.apple.service.CategoryService;
import com.apple.utils.BeanCopyUtils;
import com.apple.utils.RedisCache;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ArticleTagService articleTagService;

    @Override
    public ResponseResult hotArticleList() {
        //查询热门文章，封装成ResponseResult返回
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        //必须是正式文章
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        //按照浏览量进行排序
        queryWrapper.orderByDesc(Article::getViewCount);
        //最多只能查10条
        Page<Article> page = new Page(1,10);
        page(page,queryWrapper);

        List<Article> articles = page.getRecords();
        //bean拷贝
//        List<HotArticleVo> articleVos = new ArrayList<>();
//        for (Article article : articles) {
//            HotArticleVo vo = new HotArticleVo();
//            BeanUtils.copyProperties(article,vo);
//            articleVos.add(vo);
//        }
        List<HotArticleVo> vos = BeanCopyUtils.copyBeanList(articles, HotArticleVo.class);
        return ResponseResult.okResult(vos);
    }

    @Override
    public ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId) {
        //查询条件
        LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //如果有 categoryId 就要 查询时要和传入的相同
        lambdaQueryWrapper.eq(Objects.nonNull(categoryId)&&categoryId>0,Article::getCategoryId,categoryId);
        //状态是正式发布的
        lambdaQueryWrapper.eq(Article::getStatus,SystemConstants.ARTICLE_STATUS_NORMAL);
        //对isTop进行降序
        lambdaQueryWrapper.orderByDesc(Article::getIsTop);
        //分页查询
        Page<Article> page = new Page<>(pageNum,pageSize);
        page(page,lambdaQueryWrapper);

        //查询categoryName
        List<Article> articles = page.getRecords();
        articles.stream()
                .map(new Function<Article, Article>() {
                    @Override
                    public Article apply(Article article) {
                        //获取分类id，查询分类信息，获取分类名称
                        Category category = categoryService.getById(article.getCategoryId());
                        String name = category.getName();
                        //把分类名称设置给article
                        article.setCategoryName(name);
                        return article;
                    }
                })
                .collect(Collectors.toList());
        //categoryId去查询CategoryName进行设置
//        for (Article article : articles) {
//            Category category = categoryService.getById(article.getCategoryId());
//            article.setCategoryName(category.getName());
//        }

        //封装查询结果
        List<ArticleListVo> articleListVos = BeanCopyUtils.copyBeanList(page.getRecords(), ArticleListVo.class);

        PageVo pageVo = new PageVo(articleListVos,page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult getArticleDetail(Long id) {
        //根据id查询文章
        Article article = getById(id);
        //从redis中获取viewCount
        Integer viewCount = redisCache.getCacheMapValue("article:viewCount", id.toString());
        article.setViewCount(viewCount.longValue());
        //转换成VO
        ArticleDetailVo articleDetailVo = BeanCopyUtils.copyBean(article, ArticleDetailVo.class);
        //根据分类id查询分类名
        Long categoryId = articleDetailVo.getCategoryId();
        Category category = categoryService.getById(categoryId);
        if(category != null){
            articleDetailVo.setCategoryName(category.getName());
        }
        //封装响应返回
        return ResponseResult.okResult(articleDetailVo);
    }

    /**
     * 更新浏览量
     * @param id
     * @return
     */
    @Override
    public ResponseResult updateViewCount(Long id) {
        //更新redis中对应 id的浏览量
        redisCache.incrementCacheMapValue("article:viewCount",id.toString(),1);
        return ResponseResult.okResult();
    }

    /**
     * 新增文章
     * @param article
     * @return
     */
    @Override
    @Transactional
    public ResponseResult addArticle(AddArticleDto article) {
        //添加 博客
        Article article1 = BeanCopyUtils.copyBean(article, Article.class);
        save(article1);

        //添加 博客和标签的关联
        for (Long tag : article.getTags()) {
            //注意是添加完文章后，才有id
            articleTagService.save(new ArticleTag(article1.getId(), tag));
        }

        return ResponseResult.okResult();
    }

    /**
     * 分页查询文章列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param title 文章标题
     * @param summary 文章摘要
     * @return
     */
    @Override
    public ResponseResult<PageVo> list(Integer pageNum, Integer pageSize, String title, String summary) {
        LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //对文章标题模糊查询
        lambdaQueryWrapper.like(StringUtils.hasText(title),Article::getTitle,title);
        //对文章摘要模糊查询
        lambdaQueryWrapper.like(StringUtils.hasText(summary),Article::getSummary,summary);
        Page<Article> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page(page,lambdaQueryWrapper);
        PageVo pageVo = new PageVo(page.getRecords(),page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 根据id查文章详情（包括tags）
     * @param id
     * @return
     */
    @Override
    public ResponseResult<AddArticleDto> getArticleById(Long id) {
        if(id == null){
            throw new SystemException(AppHttpCodeEnum.ID_NOT_NULL);
        }
        //查询tags
        LambdaQueryWrapper<ArticleTag> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ArticleTag::getArticleId,id);
        List<ArticleTag> articleTags = articleTagService.getBaseMapper().selectList(lambdaQueryWrapper);
        List<Long> tags = articleTags.stream()
                .map(ArticleTag::getTagId)
                .collect(Collectors.toList());
        //查询article
        Article article = getById(id);
        //封装返回
        if(article != null){
            AddArticleDto dto = BeanCopyUtils.copyBean(article, AddArticleDto.class);
            dto.setTags(tags);
            return ResponseResult.okResult(dto);
        }
        return ResponseResult.okResult();
    }

    /**
     * 修改文章
     * @param addArticleDto
     * @return
     */
    @Override
    @Transactional
    public ResponseResult updateArticle(AddArticleDto addArticleDto) {
        //根据id更新文章
        Article article = BeanCopyUtils.copyBean(addArticleDto, Article.class);
        update(article,new LambdaQueryWrapper<Article>().eq(Article::getId,addArticleDto.getId()));
        //删除原关联
        LambdaQueryWrapper<ArticleTag> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ArticleTag::getArticleId,addArticleDto.getId());
        //添加新关联
        for (Long tag : addArticleDto.getTags()) {
            articleTagService.save(new ArticleTag(addArticleDto.getId(),tag));
        }
        return ResponseResult.okResult();
    }

    /**
     * 根据id删除文章
     * @param id
     * @return
     */
    @Override
    @Transactional
    public ResponseResult deleteArticle(Long id) {
        if(id == null){
            throw new SystemException(AppHttpCodeEnum.ID_NOT_NULL);
        }
        getBaseMapper().deleteById(id);
        //删除原关联
        LambdaQueryWrapper<ArticleTag> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ArticleTag::getArticleId,id);
        articleTagService.getBaseMapper().delete(lambdaQueryWrapper);

        return ResponseResult.okResult();
    }
}
