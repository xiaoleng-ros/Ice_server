package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("link_type")
public class LinkType {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "网站类型ID")
    private Integer id;
    @ApiModelProperty(value = "网站类型名称", example = "技术类", required = true)
    private String name;
    @ApiModelProperty(value = "用户是否可选，0表示不可�?1表示可�?, example = "0")
    private Integer isAdmin;
    @TableField("`order`")
    @ApiModelProperty(value = "网站类型顺序", example = "1")
    private Integer order;
}
