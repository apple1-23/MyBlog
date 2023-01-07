package com.apple.domain.vo;

import com.apple.domain.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVo {

    //用户所关联的角色id列表
    private List<Long> roleIds;

    //所有角色的列表
    private List<Role> roles;

    //用户信息
    private UserInfoVo user;
}
