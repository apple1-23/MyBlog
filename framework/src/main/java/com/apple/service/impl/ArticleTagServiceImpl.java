package com.apple.service.impl;

import com.apple.domain.entity.ArticleTag;
import com.apple.mapper.ArticleTagMapper;
import com.apple.service.ArticleTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service("articleTagService")
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTag> implements ArticleTagService {
}
