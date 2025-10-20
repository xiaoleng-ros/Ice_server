package liuyuyang.net.dto.comment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CommentFormDTO {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "评论者名�?, example = "宇阳", required = true)
    private String name;

    @ApiModelProperty(value = "评论者头�?, example = "yuyang.jpg")
    private String avatar;

    @ApiModelProperty(value = "评论者邮�?, example = "liuyuyang1024@yeah.net")
    private String email;

    @ApiModelProperty(value = "评论者网�?, example = "https://blog.liuyuyang.net")
    private String url;

    @ApiModelProperty(value = "评论内容", example = "这是一段评论内�?, required = true)
    private String content;

    @ApiModelProperty(value = "该评论所绑定的文章ID", example = "1", required = true)
    private Integer articleId;

    @ApiModelProperty(value = "二级评论", example = "2", required = true)
    private Integer commentId;

    @ApiModelProperty(value = "评论是否审核通过", example = "1")
    private Integer auditStatus;

    @ApiModelProperty(value = "创建时间", example = "1723533206613", required = true)
    private String createTime;
}
