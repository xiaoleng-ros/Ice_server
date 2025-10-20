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

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        // 非控制器方法，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        // 方法上标注了@NoTokenRequired，直接放行
        if (method.isAnnotationPresent(NoTokenRequired.class)) {
            return true;
        }

        // 从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getTokenName());

        try {
            log.info("jwt校验: {}", token);

            // GET 请求：未传 token 放行；传了必须校验
            if ("GET".equalsIgnoreCase(request.getMethod())) {
                if (token != null && !token.isEmpty()) {
                    if (token.startsWith("Bearer ")) token = token.substring(7);
                    JwtUtils.parseJWT(token);
                }
                return true;
            }

            // 非 GET 请求：必须携带并校验 token
            if (token == null || token.isEmpty()) {
                throw new CustomException(401, "缺少认证令牌");
            }
            if (token.startsWith("Bearer ")) token = token.substring(7);

            LambdaQueryWrapper<UserToken> userTokenLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userTokenLambdaQueryWrapper.eq(UserToken::getToken, token);
            List<UserToken> userTokens = userTokenMapper.selectList(userTokenLambdaQueryWrapper);

            // 如果跟之前的 token 相匹配则进一步判断 token 是否有效
            if (userTokens != null && !userTokens.isEmpty()) {
                Claims claims = JwtUtils.parseJWT(token);
                return true;
            } else {
                throw new CustomException(401, "该账号已在另一台设备登录");
            }
        } catch (Exception ex) {
            log.warn("JWT校验失败: {}", ex.getMessage(), ex);
            // 校验失败，响应 401 状态码
            response.setStatus(401);
            String message = ex.getMessage() != null ? ex.getMessage() : "无效或过期的token";
            throw new CustomException(401, message);
        }
    }
}

