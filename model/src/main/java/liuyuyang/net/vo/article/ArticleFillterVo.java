package liuyuyang.net.vo.article;

import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.vo.FilterVo;
import lombok.Data;

import java.util.List;

@Data
public class ArticleFillterVo extends FilterVo {
    @ApiModelProperty(value = "根据分类进行筛�?)
    private Integer cateId;
    @ApiModelProperty(value = "根据标签进行筛�?)
    private Integer tagId;
    @ApiModelProperty(value = "是否为草�? 默认�? | 草稿�?", example = "0")
    private Integer isDraft = 0;
    @ApiModelProperty(value = "是否为严格删�? 默认�? | 严格删除�?", example = "0")
    private Integer isDel = 0;
}
