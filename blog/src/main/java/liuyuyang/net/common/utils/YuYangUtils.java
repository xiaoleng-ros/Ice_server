package liuyuyang.net.common.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.jsonwebtoken.Claims;
import liuyuyang.net.model.UserToken;
import liuyuyang.net.vo.FilterVo;
import liuyuyang.net.vo.PageVo;
import liuyuyang.net.web.mapper.UserTokenMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class YuYangUtils {
    @Resource
    private UserTokenMapper userTokenMapper;

    /**
     * 获取 HttpServletRequest
     *
     * @return {HttpServletRequest}
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return (requestAttributes == null) ? null : ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    /**
     * 获取Header的�?     *
     * @param name 请求头名�?     * @return 请求�?     */
    public static String getHeader(String name) {
        HttpServletRequest request = getRequest();
        return Objects.requireNonNull(request).getHeader(name);
    }

    /**
     * 判断是否是管理员
     */
    public static boolean isAdmin() {
        String token = getHeader("Authorization");
        return isAdmin(token);
    }

    // 鉴权：判断是否为超级管理�?    public static boolean isAdmin(String token) {
        if (token != null) {
            if (token.startsWith("Bearer ")) token = token.substring(7);
            Claims claims = JwtUtils.parseJWT(token);
            Map<String, Object> role = (Map<String, Object>) claims.get("role");

            // 是超级管理员
            return "admin".equals(role.get("mark"));
        }

        return false;
    }

    // 分页查询逻辑
    public <T> Page<T> getPageData(PageVo pageVo, List<T> list) {
        int start = (pageVo.getPage() - 1) * pageVo.getSize();
        int end = Math.min(start + pageVo.getSize(), list.size());
        List<T> pagedRecords = list.subList(start, end);

        Page<T> result = new Page<>(pageVo.getPage(), pageVo.getSize());
        result.setRecords(pagedRecords);
        result.setTotal(list.size());

        return result;
    }

    // 过滤数据
    public <T> QueryWrapper<T> queryWrapperFilter(FilterVo filterVo) {
        return queryWrapperFilter(filterVo, "title");
    }

    public <T> QueryWrapper<T> queryWrapperFilter(FilterVo filterVo, String key) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");

        // 根据关键字通过标题过滤出对应数�?        if (filterVo.getKey() != null && !filterVo.getKey().isEmpty()) {
            queryWrapper.like(key, "%" + filterVo.getKey() + "%");
        }

        // 根据开始与结束时间过滤
        if (filterVo.getStartDate() != null && filterVo.getEndDate() != null) {
            queryWrapper.between("create_time", filterVo.getStartDate(), filterVo.getEndDate());
        } else if (filterVo.getStartDate() != null) {
            queryWrapper.ge("create_time", filterVo.getStartDate());
        } else if (filterVo.getEndDate() != null) {
            queryWrapper.le("create_time", filterVo.getEndDate());
        }

        return queryWrapper;
    }

    // 校验当前JWT是否有效
    public boolean check(String token) {
        try {
            if (token != null) {
                if (token.startsWith("Bearer ")) token = token.substring(7);

                LambdaQueryWrapper<UserToken> userTokenLambdaQueryWrapper = new LambdaQueryWrapper<>();
                userTokenLambdaQueryWrapper.eq(UserToken::getToken, token);
                List<UserToken> userTokens = userTokenMapper.selectList(userTokenLambdaQueryWrapper);

                // 如果跟之前的token相匹配则进一步判断token是否有效
                if (userTokens != null && !userTokens.isEmpty()) {
                    Claims claims = JwtUtils.parseJWT(token);
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }
}
