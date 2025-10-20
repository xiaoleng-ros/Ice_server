package liuyuyang.net.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FilterVo {
    @ApiModelProperty(value = "根据关键词进行筛�?)
    private String key;
    @ApiModelProperty(value = "根据开始时间进行筛�?)
    private String startDate;
    @ApiModelProperty(value = "根据结束时间进行筛�?)
    private String endDate;
}
