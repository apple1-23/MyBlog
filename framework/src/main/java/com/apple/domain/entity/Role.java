package com.apple.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_role")
public class Role {

    @TableId
    private Long id;

    //角色名称
    private String roleName;

    //角色权限字符串
    private String roleKey;

    //显示顺序
    private Integer roleSort;

    //角色状态（0正常 1停用）
    private String status;

    private Long createBy;
    private Date createTime;
    private Long updateBy;
    private Date updateTime;
    //删除标志（0代表未删除，1代表已删除）
    private Integer delFlag;
    private String remark;
}
