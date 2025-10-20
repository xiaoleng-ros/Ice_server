package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.model.Oss;

import java.util.List;
import java.util.Map;

public interface OssService extends IService<Oss> {
    /**
     * 分页
     */
    List<Oss> list();

    void enable(Integer id);

    /**
     * 获取当前启用的oss
     */
    Oss getEnableOss();

    /**
     * 删除
     */
    void delOss(Integer id);

    /**
     * 新增
     *
     * @param oss
     */
    void saveOss(Oss oss);

    // 获取支持的平�?    List<Map> getPlatform();

    /**
     * 更新
     * @param oss
     */
    void updateOss(Oss oss);
}
