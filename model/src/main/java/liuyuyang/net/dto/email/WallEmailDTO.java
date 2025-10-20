package liuyuyang.net.dto.email;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WallEmailDTO extends EmailDTO {
    @ApiModelProperty(value = "邮件标题", example = "驳回通知", required = true)
    private String subject;
    @ApiModelProperty(value = "发送方", example = "张三", required = true)
    String recipient;
    @ApiModelProperty(value = "评论时间", example = "2024�?0�?5�?14:44", required = true)
    String time;
    @ApiModelProperty(value = "你的内容", example = "太赞�?, required = true)
    String your_content;
    @ApiModelProperty(value = "回复内容", example = "必须�?, required = true)
    String reply_content;
    @ApiModelProperty(value = "文章地址", example = "https://liuyuyang.net", required = true)
    String url;
}
