package liuyuyang.net.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Rss {
    @ApiModelProperty(value = "作�?, example = "宇阳")
    private String author;
    @ApiModelProperty(value = "网站图片", example = "http://127.0.0.1:5000/1.jpg")
    private String image;
    @ApiModelProperty(value = "网站邮箱", example = "liuyuyang1024@yeah.net")
    private String email;
    @ApiModelProperty(value = "网站类型", example = "技术类")
    private String type;
    @ApiModelProperty(value = "网站标题", example = "这是一个网�?)
    private String title;
    @ApiModelProperty(value = "网站描述", example = "这是一个网站的描述")
    private String description;
    @ApiModelProperty(value = "网站链接", example = "/")
    private String url;
    @ApiModelProperty(value = "网站创建时间", example = "1723533206613")
    private String createTime;
}
