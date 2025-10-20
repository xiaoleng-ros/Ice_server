package liuyuyang.net.vo;

import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
public class PageVo {
    @ApiParam(defaultValue = "1", value = "默认为第1�?)
    private Integer page = 1;
    @ApiParam(defaultValue = "5", value = "默认为每页显�?个数�?)
    private Integer size = 5;
}
