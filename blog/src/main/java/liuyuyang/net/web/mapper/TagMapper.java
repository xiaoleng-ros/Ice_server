package liuyuyang.net.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import liuyuyang.net.model.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {
    @Select("select t.*, count(at.article_id) as count from article a, tag t, article_tag at where a.id = at.article_id and t.id = at.tag_id group by t.id, t.name order by count desc")
    List<Tag> staticArticleCount();
}
