package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.model.Cate;
import liuyuyang.net.result.cate.CateArticleCount;

import java.util.List;

public interface CateService extends IService<Cate> {
    // 判断是否存在二级分类
    Boolean isExistTwoCate(Integer cid);

    // 判断该分类中是否有文�?    Boolean isCateArticleCount(Integer cid);

    void del(Integer cid);

    void batchDel(List<Integer> ids);

    Cate get(Integer cid);

    List<Cate> list(String pattern);

    Page<Cate> paging(Integer page, Integer size);

    List<CateArticleCount> cateArticleCount();

    List<Cate> buildCateTree(List<Cate> list, Integer lever);
}
