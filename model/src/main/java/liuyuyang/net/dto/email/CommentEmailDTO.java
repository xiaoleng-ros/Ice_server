package liuyuyang.net.dto.email;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CommentEmailDTO extends EmailDTO {
    @ApiModelProperty(value = "文章标题", example = "这是一段标题", required = true)
    String title;
    @ApiModelProperty(value = "发送方", example = "神秘用户", required = true)
    String recipient;
    @ApiModelProperty(value = "评论时间", example = "2024-05-14 14:44", required = true)
    String time;
    @ApiModelProperty(value = "评论内容", example = "这是一段内容", required = true)
    String content;
    @ApiModelProperty(value = "文章地址", example = "https://liuyuyang.net", required = true)
    String url;
}
