package liuyuyang.net.common.aspect;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.jsonwebtoken.Claims;
import liuyuyang.net.common.annotation.PremName;
import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.common.utils.JwtUtils;
import liuyuyang.net.model.Permission;
import liuyuyang.net.model.RolePermission;
import liuyuyang.net.web.mapper.PermissionMapper;
import liuyuyang.net.web.mapper.RoleMapper;
import liuyuyang.net.web.mapper.RolePermissionMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Aspect
@Component
public class PremNameAspect {
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private RoleMapper roleMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    // 定义切点，支持类和方法上的注�?    @Pointcut("@within(liuyuyang.net.common.annotation.PremName) || @annotation(liuyuyang.net.common.annotation.PremName)")
    private void cut() {
    }

    @Before("cut()")
    public void before(JoinPoint joinPoint) {
        // 获取方法上的 @PremName 注解
        Optional<PremName> nameOpt = Optional.of(getMethodAnnotation(joinPoint).get());

        // 如果注解存在，进行权限验�?        nameOpt.ifPresent(name -> {
            // 当前接口的权限名�?            String prem = name.value();
            log.info("权限名称：{}", prem);

            // 获取当前请求的上下文
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                HttpServletResponse response = attributes.getResponse();

                Map<String, Object> role;

                // 解析 token 并获取角色信�?                try {
                    // 获取请求头中�?token
                    String token = request.getHeader("Authorization");
                    log.debug("Authorization Header: {}", token);

                    // 如果 token �?null，跳过权限校�?                    if (token == null) {
                        log.info("Token为空，跳过权限校�?);
                        throw new CustomException("Token 不能为空");
                    }

                    // 去掉 Bearer 前缀
                    if (token.startsWith("Bearer ")) {
                        token = token.substring(7);
                    }

                    Claims claims = JwtUtils.parseJWT(token);
                    role = (Map<String, Object>) claims.get("role");
                } catch (Exception e) {
                    response.setStatus(401);
                    throw new CustomException(401, e.getMessage());
                }

                String mark = (String) role.get("mark");

                // 如果是管理员，则不需要权限校�?                if (Objects.equals(mark, "admin")) return;

                // 查询当前角色的权�?                LambdaQueryWrapper<RolePermission> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
                roleLambdaQueryWrapper.eq(RolePermission::getRoleId, role.get("id"));
                // 当前角色能访问的所有权�?                List<Permission> role_permissions = roleMapper.getPermissionList((int) role.get("id"));

                // 判断当前�?prem 权限是否存在�?role_permissions，通过 name 判断
                boolean hasPermission = role_permissions.stream()
                        .anyMatch(permission -> permission.getName().equals(prem));

                if (!hasPermission) {
                    throw new CustomException(400, "当前角色没有权限�? + prem);
                }

                log.info("角色ID：{}", role.get("id"));
            }
        });
    }

    // 获取当前方法上的 @PremName 注解
    private Optional<PremName> getMethodAnnotation(JoinPoint joinPoint) {
        return Optional.ofNullable(getCurrentMethod(joinPoint))
                .map(method -> method.getAnnotation(PremName.class));
    }

    // 获取当前执行的方法对�?    private Method getCurrentMethod(JoinPoint joinPoint) {
        try {
            String methodName = joinPoint.getSignature().getName();
            Class<?> targetClass = joinPoint.getTarget().getClass();
            Method[] methods = targetClass.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    return method;
                }
            }
        } catch (Exception e) {
            log.error("获取方法时出�?, e);
        }
        return null;
    }
}
