package com.apple.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sg_comment")
@ApiModel(description = "添加评论实体类")
public class Comment {
    @TableId
    private Long id;

    //评论类型（0代表文章评论，1代表友链评论）
    private String type;

    private Long articleId;

    private Long rootId;

    private String content;

    //所回复的目标评论的userid
    private Long toCommentUserId;

    //回复目标评论id
    private Long toCommentId;

    //创建人的用户id
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    //更新人
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    //删除标志（0代表未删除，1代表已删除）
    private Integer delFlag;
}
