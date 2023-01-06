package com.apple.domain.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleChangeVo {

    @TableId
    private Long roleId;

    //角色状态（0正常 1停用）
    private String status;
}
