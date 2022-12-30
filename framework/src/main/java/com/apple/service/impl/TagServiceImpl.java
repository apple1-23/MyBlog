package com.apple.service.impl;

import com.apple.domain.ResponseResult;
import com.apple.domain.dto.TagListDto;
import com.apple.domain.entity.Tag;
import com.apple.domain.vo.PageVo;
import com.apple.domain.vo.TagVo;
import com.apple.enums.AppHttpCodeEnum;
import com.apple.exception.SystemException;
import com.apple.mapper.TagMapper;
import com.apple.service.TagService;
import com.apple.utils.BeanCopyUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service("tagService")
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    /**
     * 分页标签列表
     * @param pageNum
     * @param pageSize
     * @param tagListDto
     * @return
     */
    @Override
    public ResponseResult<PageVo> pageTagList(Integer pageNum, Integer pageSize, TagListDto tagListDto) {
        //分页查询
        LambdaQueryWrapper<Tag> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StringUtils.hasText(tagListDto.getName()),Tag::getName,tagListDto.getName());
        lambdaQueryWrapper.eq(StringUtils.hasText(tagListDto.getRemark()),Tag::getRemark,tagListDto.getRemark());
        Page<Tag> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);

        page(page,lambdaQueryWrapper);
        //封装数据返回
        PageVo pageVo = new PageVo(page.getRecords(),page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 新增tag
     * @param tag
     * @return
     */
    @Override
    public ResponseResult addTag(Tag tag) {
        if(!StringUtils.hasText(tag.getName())){
            throw new SystemException(AppHttpCodeEnum.TAG_NAME_NOT_NULL);
        }
        if(!StringUtils.hasText(tag.getRemark())){
            throw new SystemException(AppHttpCodeEnum.TAG_REMARK_NOT_NULL);
        }
        save(tag);
        return ResponseResult.okResult();
    }

    /**
     * 删除标签
     * @param id
     * @return
     */
    @Override
    public ResponseResult deleteTag(Long id) {
        if(id == null){
            throw new SystemException(AppHttpCodeEnum.ID_NOT_NULL);
        }
        int i = getBaseMapper().deleteById(id);
        if(i > 0){
            return ResponseResult.okResult();
        }
        return null;
    }

    /**
     * 获取tag信息
     * @param id
     * @return
     */
    @Override
    public ResponseResult<TagVo> getTag(Long id) {
        if(id == null){
            throw new SystemException(AppHttpCodeEnum.ID_NOT_NULL);
        }
        Tag tag = getBaseMapper().selectById(id);
        TagVo tagVo = BeanCopyUtils.copyBean(tag, TagVo.class);
        return ResponseResult.okResult(tagVo);
    }

    /**
     * 修改tag
     * @param tag
     * @return
     */
    @Override
    public ResponseResult updateTag(Tag tag) {
        getBaseMapper().updateById(tag);
        return ResponseResult.okResult();
    }
}
