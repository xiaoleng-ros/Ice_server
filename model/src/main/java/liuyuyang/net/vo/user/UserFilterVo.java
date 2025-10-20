package liuyuyang.net.vo.user;

import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.vo.FilterVo;
import lombok.Data;

@Data
public class UserFilterVo extends FilterVo {
    @ApiModelProperty(value = "根据角色进行筛�?)
    private Integer roleId;
}
