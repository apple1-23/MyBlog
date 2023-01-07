package com.apple.controller;

import com.apple.domain.ResponseResult;
import com.apple.domain.vo.LinkVo;
import com.apple.domain.vo.PageVo;
import com.apple.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("content/link")
public class LinkController {

    @Autowired
    private LinkService linkService;

    @GetMapping("/list")
    public ResponseResult<PageVo> list(Integer pageNum,Integer pageSize,String name,String status){
        return linkService.list(pageNum,pageSize,name,status);
    }

    @PostMapping
    public ResponseResult addLink(@RequestBody LinkVo linkVo){
        return linkService.addLink(linkVo);
    }

    @GetMapping("/{id}")
    public ResponseResult<LinkVo> getLink(@PathVariable Long id){
        return linkService.getLink(id);
    }

    @PutMapping
    public ResponseResult updateLink(@RequestBody LinkVo linkVo){
        return linkService.updateLink(linkVo);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteLink(@PathVariable Long id){
        return linkService.deleteLink(id);
    }
}
