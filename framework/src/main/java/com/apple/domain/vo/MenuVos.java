package com.apple.domain.vo;

import com.apple.annotation.CopyField;
import com.apple.domain.entity.Menu;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MenuVos {
    //菜单id
    @TableId
    private Long id;

    //菜单名称
    @CopyField(source = "menuName")
    private String label;

    //父菜单id
    private Long parentId;

    @TableField(exist = false)
    private List<MenuVos> children;
}
