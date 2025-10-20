package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class Config {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "环境配置ID")
    private Integer id;

    @ApiModelProperty(value = "配置名称", example = "database_config", required = true)
    private String name;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "配置�?JSON格式)", example = "{\"name\":\"宇阳\"}", required = true)
    private Map<String, Object> value;

    @ApiModelProperty(value = "配置备注")
    private String notes;
}
