package liuyuyang.net.common.utils;

import lombok.Data;

/**
 * 路过图床配置类
 * @author laifeng
 * @version 1.0
 * @date 2024/12/10 21:21
 */
@Data
public class ImgtpConfig {
    /**
     * 平台名称
     */
    private String platform;
    
    /**
     * 是否启用存储
     */
    private Boolean enableStorage;
    
    /**
     * 域名
     */
    private String domain;
    
    /**
     * 基础路径
     */
    private String basePath;
    
    /**
     * API Token (路过图床的API密钥)
     */
    private String secretKey;
    
    /**
     * 是否启用
     */
    private Boolean enable;
}