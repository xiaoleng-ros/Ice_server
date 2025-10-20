package liuyuyang.net.common.aspect;

import io.jsonwebtoken.Claims;
import liuyuyang.net.common.annotation.CheckRole;
import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.common.utils.JwtUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Aspect
@Component
public class CheckRoleAspect {


    // 定义切点，支持类和方法上的注�?    @Pointcut("@within(liuyuyang.net.common.annotation.CheckRole) || @annotation(liuyuyang.net.common.annotation.CheckRole)")
    private void cut() {
    }

    @Before("cut()")
    public void before(JoinPoint joinPoint) {
        CheckRole checkRole = getCheckRoleAnnotation(joinPoint);

        if (checkRole != null) {
            String[] roles = checkRole.value();
            List<String> rolesList = new ArrayList<>(Arrays.asList(roles));
            rolesList.add("admin");

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                HttpServletResponse response = attributes.getResponse();

                String token = request.getHeader("Authorization");
                System.out.println("Authorization Header: " + token);

                if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }

                Map<String, Object> role;

                try {
                    Claims claims = JwtUtils.parseJWT(token);
                    role = (Map<String, Object>) claims.get("role");
                } catch (Exception e) {
                    response.setStatus(401);
                    throw new CustomException(401, "身份验证失败：无效或过期的token");
                }

                boolean isPerm = rolesList.contains(role.get("mark"));

                if (!isPerm) {
                    throw new CustomException(401, "该权限仅限于�?" + String.join(", ", rolesList) + " 角色");
                }
            }
        }
    }

    private CheckRole getCheckRoleAnnotation(JoinPoint joinPoint) {
        Method method = getCurrentMethod(joinPoint);
        if (method != null) {
            CheckRole checkRole = method.getAnnotation(CheckRole.class);
            if (checkRole != null) {
                return checkRole;
            }
        }
        return joinPoint.getTarget().getClass().getAnnotation(CheckRole.class);
    }

    private Method getCurrentMethod(JoinPoint joinPoint) {
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
            e.printStackTrace();
        }
        return null;
    }
}
