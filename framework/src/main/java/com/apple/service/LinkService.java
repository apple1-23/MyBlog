package com.apple.service;

import com.apple.domain.ResponseResult;
import com.apple.domain.entity.Link;
import com.apple.domain.vo.LinkVo;
import com.apple.domain.vo.PageVo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface LinkService extends IService<Link> {

    ResponseResult getAllLink();

    ResponseResult<PageVo> list(Integer pageNum, Integer pageSize, String name, String status);

    ResponseResult addLink(LinkVo linkVo);

    ResponseResult<LinkVo> getLink(Long id);

    ResponseResult updateLink(LinkVo linkVo);

    ResponseResult deleteLink(Long id);
}
