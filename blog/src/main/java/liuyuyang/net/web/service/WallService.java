package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.model.Wall;
import liuyuyang.net.model.WallCate;
import liuyuyang.net.vo.PageVo;
import liuyuyang.net.vo.wall.WallFilterVo;

import java.util.List;

public interface WallService extends IService<Wall> {
    void add(Wall wall) throws Exception;

    Wall get(Integer id);

    Page<Wall> getCateWallList(Integer cateId, PageVo pageVo);

    List<WallCate> getCateList();

    List<Wall> list(WallFilterVo filterVo);

    Page<Wall> paging(WallFilterVo filterVo, PageVo pageVo);

    void updateChoice(Integer id);
}
