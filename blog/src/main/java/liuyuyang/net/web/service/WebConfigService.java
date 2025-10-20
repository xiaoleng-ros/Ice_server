package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.model.PageConfig;
import liuyuyang.net.model.WebConfig;

import java.util.List;
import java.util.Map;

public interface WebConfigService extends IService<WebConfig> {
    /**
     * 根据ID获取网站配置
     */
    WebConfig getById(Integer id);

    /**
     * 获取全部网站配置列表
     */
    List<WebConfig> list();

    /**
     * 更新 JSON 配置�?     */
    boolean updateJsonValue(Integer id, Map<String, Object> jsonValue);

    /**
     * 根据名称获取网站配置
     */
    WebConfig getByName(String name);
}
