package liuyuyang.net.common.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.jsonwebtoken.Claims;
import liuyuyang.net.common.annotation.NoTokenRequired;
import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.common.properties.JwtProperties;
import liuyuyang.net.common.utils.JwtUtils;
import liuyuyang.net.model.UserToken;
import liuyuyang.net.web.mapper.UserTokenMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {
    @Resource
    private JwtProperties jwtProperties;
    @Resource
    private UserTokenMapper userTokenMapper;

    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        // 从请求头中获取令�?        String token = request.getHeader(jwtProperties.getTokenName());

        // 如果是预检请求，直接放�?        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        // 检查方法上是否有@NoTokenRequired注解，如果有就直接放�?        if (method.isAnnotationPresent(NoTokenRequired.class)) {
            return true;
        }

        // 校验令牌
        try {
            log.info("jwt校验：{}", token);

            // 如果是GET请求没有传token就直接放行，传了token就必须经过验�?            if ("GET".equalsIgnoreCase(request.getMethod())) {
                if (token != null) {
                    if (token.startsWith("Bearer ")) token = token.substring(7);
                    JwtUtils.parseJWT(token);
                }
                return true;
            }

            // 处理Authorization的Bearer
            if (token.startsWith("Bearer ")) token = token.substring(7);

            LambdaQueryWrapper<UserToken> userTokenLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userTokenLambdaQueryWrapper.eq(UserToken::getToken, token);
            List<UserToken> userTokens = userTokenMapper.selectList(userTokenLambdaQueryWrapper);

            // 如果跟之前的token相匹配则进一步判断token是否有效
            if (userTokens != null && !userTokens.isEmpty()) {
                Claims claims = JwtUtils.parseJWT(token);
                return true;
            } else {
                throw new CustomException(401, "该账号已在另一台设备登�?);
            }
        } catch (Exception ex) {
            System.out.println("校验失败�? + ex);
            // 校验失败，响�?01状态码
            response.setStatus(401);
            String message = ex.getMessage() != null ? ex.getMessage() : "无效或过期的token";
            throw new CustomException(401, message);
        }
    }
}
