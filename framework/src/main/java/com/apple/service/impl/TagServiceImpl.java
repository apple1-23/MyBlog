package com.apple.service.impl;

import com.apple.domain.entity.Tag;
import com.apple.mapper.TagMapper;
import com.apple.service.TagService;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service("tagService")
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
}
