package liuyuyang.net.common.utils;

import lombok.Data;

/**
 * SM.MS 配置类
 * 用于存储和管理 SM.MS 存储平台的配置信息
 * 
 * @author laifeng
 * @date 2024/12/14
 */
@Data
public class SmmsConfig {
    
    /**
     * 存储平台标识
     */
    private String platform = "smms";
    
    /**
     * 启用存储
     */
    private Boolean enableStorage = true;
    
    /**
     * 域名
     */
    private String domain = "https://sm.ms";
    
    /**
     * 基础路径
     */
    private String basePath = "";
    
    /**
     * SM.MS API Token (Secret Key)
     */
    private String secretKey;
    
    /**
     * 是否启用
     */
    private Boolean enable = true;
}
