package liuyuyang.net.common.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import liuyuyang.net.model.EnvConfig;
import liuyuyang.net.web.mapper.EnvConfigMapper;
import liuyuyang.net.web.service.EnvConfigService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import jakarta.annotation.Resource;
import java.util.Map;
import java.util.Properties;

@Configuration
public class MailConfig {
    @Resource
    private EnvConfigService envConfigService;

    private Map<String, Object> getEmailConfig() {
        EnvConfig envConfig = envConfigService.getByName("email");
        return envConfig.getValue();
    }

    /**
     * 验证并获取邮箱服务商类型
     */
    private String getEmailProvider(String host, String username) {
        if (host == null || username == null) {
            throw new RuntimeException("邮箱主机或用户名不能为空");
        }
        
        // QQ邮箱
        if (host.contains("qq.com") || username.contains("@qq.com")) {
            return "QQ";
        }
        // 163邮箱
        else if (host.contains("163.com") || username.contains("@163.com")) {
            return "163";
        }
        // yeah邮箱(网易)
        else if (host.contains("yeah.net") || username.contains("@yeah.net")) {
            return "YEAH";
        }
        else {
            throw new RuntimeException("不支持的邮箱服务商！当前仅支持QQ邮箱(@qq.com)�?63邮箱(@163.com)和yeah邮箱(@yeah.net)");
        }
    }

    /**
     * 根据邮箱服务商配置SMTP属�?     */
    private void configureSmtpProperties(Properties props, String provider, Integer port) {
        // 基础配置
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.timeout", 30000);
        props.put("mail.smtp.connectiontimeout", 15000);
        props.put("mail.smtp.writetimeout", 15000);
        
        // 根据端口配置SSL/STARTTLS
        if (port == 465) {
            System.out.println("使用465端口，启用SSL连接");
            props.put("mail.smtp.ssl.enable", true);
            props.put("mail.smtp.ssl.required", true);
            props.put("mail.smtp.starttls.enable", false);
        } else if (port == 587) {
            System.out.println("使用587端口，启用STARTTLS连接");
            props.put("mail.smtp.ssl.enable", false);
            props.put("mail.smtp.starttls.enable", true);
            props.put("mail.smtp.starttls.required", true);
        } else {
            throw new RuntimeException("不支持的端口！建议使�?65(SSL)�?87(STARTTLS)端口");
        }
        
        // 根据邮箱服务商配置特定属�?        switch (provider) {
            case "QQ":
                System.out.println("配置QQ邮箱专用设置");
                props.put("mail.smtp.ssl.protocols", "TLSv1.2");
                props.put("mail.smtp.ssl.trust", "smtp.qq.com");
                props.put("mail.smtp.ssl.checkserveridentity", false);
                // QQ邮箱特有配置
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.socketFactory.fallback", false);
                if (port == 465) {
                    props.put("mail.smtp.socketFactory.port", 465);
                }
                break;
                
            case "163":
                System.out.println("配置163邮箱专用设置");
                props.put("mail.smtp.ssl.protocols", "TLSv1.2");
                props.put("mail.smtp.ssl.trust", "smtp.163.com");
                props.put("mail.smtp.ssl.checkserveridentity", false);
                // 163邮箱建议配置
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.socketFactory.fallback", false);
                if (port == 465) {
                    props.put("mail.smtp.socketFactory.port", 465);
                }
                break;
                
            case "YEAH":
                System.out.println("配置yeah邮箱专用设置");
                props.put("mail.smtp.ssl.protocols", "TLSv1.2");
                props.put("mail.smtp.ssl.trust", "smtp.yeah.net");
                props.put("mail.smtp.ssl.checkserveridentity", false);
                // yeah邮箱(网易)配置
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.socketFactory.fallback", false);
                if (port == 465) {
                    props.put("mail.smtp.socketFactory.port", 465);
                }
                break;
                
            default:
                throw new RuntimeException("未知的邮箱服务商类型: " + provider);
        }
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE) // 每次使用时重新创�?    public JavaMailSender javaMailSender() {
        try {
            Map<String, Object> config = getEmailConfig();
            
            String host = (String) config.get("host");
            Integer port = (Integer) config.get("port");
            String username = (String) config.get("username");
            String password = (String) config.get("password");
            
            // 验证邮箱服务�?            String provider = getEmailProvider(host, username);
            System.out.println("检测到邮箱服务�? " + provider);
            
            // 创建邮件发送器
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost(host);
            mailSender.setPort(port);
            mailSender.setUsername(username);
            mailSender.setPassword(password);

            // 配置SMTP属�?            Properties props = mailSender.getJavaMailProperties();
            configureSmtpProperties(props, provider, port);

            System.out.println("=== 邮件发送器配置完成 ===");
            System.out.println("邮箱服务�? " + provider);
            System.out.println("SMTP主机: " + host + ":" + port);
            System.out.println("发件邮箱: " + username);
            System.out.println("SSL启用: " + props.getProperty("mail.smtp.ssl.enable"));
            System.out.println("STARTTLS启用: " + props.getProperty("mail.smtp.starttls.enable"));
            System.out.println("========================");
            
            return mailSender;
        } catch (Exception e) {
            System.err.println("创建邮件发送器失败: " + e.getMessage());
            throw new RuntimeException("邮件发送器初始化失�?, e);
        }
    }
}
