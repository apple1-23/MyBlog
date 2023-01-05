package com.apple.domain.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddMenuDto {

    //菜单id
    @TableId
    private Long id;

    //菜单名称
    private String menuName;

    //父菜单id
    private Long parentId;

    //显示顺序
    private Integer orderNum;

    //路由地址
    private String path;

    //组件路径
    private String component;

    //是否为外链（0是 1否）
    private Integer isFrame;

    //菜单类型（M目录 C菜单 F按钮）
    private String menuType;

    //菜单状态（0显示 1隐藏）
    private String visible;

    //菜单状态（0正常 1停用）
    private String status;

    //权限标识
    private String perms;

    //菜单图标
    private String icon;

    private Long createBy;
    private Date createTime;
    private Long updateBy;
    private Date updateTime;
    //删除标志（0代表未删除，1代表已删除）
    private Integer delFlag;
    private String remark;
}
