package liuyuyang.net.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import liuyuyang.net.model.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    // 通过文章ID查询对应的所有评�?    @Select("select c.* from comment c, article a where c.article_id = a.id && c.article_id = #{aid}")
    public List<Comment> getCommentList(Integer aid);
}
