package com.apple.service;

import com.apple.domain.ResponseResult;
import com.apple.domain.dto.TagListDto;
import com.apple.domain.entity.Tag;
import com.apple.domain.vo.PageVo;
import com.apple.domain.vo.TagVo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface TagService extends IService<Tag> {
    ResponseResult<PageVo> pageTagList(Integer pageNum, Integer pageSize, TagListDto tagListDto);

    ResponseResult addTag(Tag tag);

    ResponseResult deleteTag(Long id);

    ResponseResult<TagVo> getTag(Long id);

    ResponseResult updateTag(Tag tag);

    ResponseResult<TagVo> listAllTag();
}
