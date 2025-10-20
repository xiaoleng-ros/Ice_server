package liuyuyang.net.web.service;

import liuyuyang.net.model.EnvConfig;
import java.util.List;
import java.util.Map;

public interface EnvConfigService {
    /**
     * 根据 ID 获取环境配置
     */
    EnvConfig getById(Integer id);
    
    /**
     * 根据名称获取环境配置
     */
    EnvConfig getByName(String name);
    
    /**
     * 获取全部环境配置列表
     */
    List<EnvConfig> list();
    
    /**
     * 更新 JSON 配置�?     */
    boolean updateJsonValue(Integer id, Map<String, Object> jsonValue);
    
    /**
     * 获取 JSON 配置中的特定字段�?     */
    Object getJsonFieldValue(Integer id, String fieldName);
    
    /**
     * 更新 JSON 配置中的特定字段�?     */
    boolean updateJsonFieldValue(Integer id, String fieldName, Object value);
} 
