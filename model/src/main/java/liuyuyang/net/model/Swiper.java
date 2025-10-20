package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("swiper")
public class Swiper {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "轮播图ID")
    private Integer id;
    @ApiModelProperty(value = "轮播图标�?, example = "这是一个轮播图", required = true)
    private String title;
    @ApiModelProperty(value = "轮播�?, example = "http://127.0.0.1:5000/1.jpg", required = true)
    private String image;
    @ApiModelProperty(value = "轮播图描�?, example = "这是一个轮播图的描�?)
    private String description;
    @ApiModelProperty(value = "轮播图链�?, example = "/")
    private String url;
}
