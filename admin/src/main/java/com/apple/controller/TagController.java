package com.apple.controller;

import com.apple.domain.ResponseResult;
import com.apple.domain.dto.TagListDto;
import com.apple.domain.entity.Tag;
import com.apple.domain.vo.PageVo;
import com.apple.domain.vo.TagVo;
import com.apple.service.TagService;
import com.apple.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 标签分页列表
     * @param pageNum
     * @param pageSize
     * @param tagListDto
     * @return
     */
    @GetMapping("/list")
    public ResponseResult<PageVo> list(Integer pageNum, Integer pageSize, TagListDto tagListDto){
        return tagService.pageTagList(pageNum,pageSize,tagListDto);
    }

    /**
     * 新增tag
     * @param tagListDto
     * @return
     */
    @PostMapping
    public ResponseResult addTag(@RequestBody TagListDto tagListDto){
        Tag tag = BeanCopyUtils.copyBean(tagListDto,Tag.class);
        return tagService.addTag(tag);
    }

    /**
     * 删除tag
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseResult deleteTag(@PathVariable Long id){
        return tagService.deleteTag(id);
    }

    /**
     * 获取tag信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult<TagVo> getTagInfo(@PathVariable Long id){
        return tagService.getTag(id);
    }

    /**
     * 修改tag
     * @param tagVo
     * @return
     */
    @PutMapping
    public ResponseResult updateTag(@RequestBody TagVo tagVo){
        Tag tag = BeanCopyUtils.copyBean(tagVo, Tag.class);
        return tagService.updateTag(tag);
    }
 }
