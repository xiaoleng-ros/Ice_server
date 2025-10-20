package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("wall")
public class Wall extends BaseModel {
    @ApiModelProperty(value = "留言人名�?, example = "神秘�?, required = true)
    private String name;

    @ApiModelProperty(value = "分类id", example = "1", required = true)
    private Integer cateId;

    @TableField(exist = false)
    @ApiModelProperty(value = "留言分类", example = "全部")
    private WallCate cate;

    @ApiModelProperty(value = "留言墙颜�?, example = "#92e6f54d")
    private String color;

    @ApiModelProperty(value = "留言内容", example = "这是一段内�?, required = true)
    private String content;

    @ApiModelProperty(value = "邮箱", example = "3311118881@qq.com")
    private String email;

    @ApiModelProperty(value = "评论是否审核通过", example = "1")
    private Integer auditStatus;

    @ApiModelProperty(value = "设置与取消精�?, example = "1")
    private Integer isChoice;
}
