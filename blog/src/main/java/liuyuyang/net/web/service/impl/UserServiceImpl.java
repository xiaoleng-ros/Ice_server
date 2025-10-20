package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.common.properties.JwtProperties;
import liuyuyang.net.common.utils.JwtUtils;
import liuyuyang.net.common.utils.YuYangUtils;
import liuyuyang.net.dto.user.EditPassDTO;
import liuyuyang.net.dto.user.UserDTO;
import liuyuyang.net.dto.user.UserInfoDTO;
import liuyuyang.net.dto.user.UserLoginDTO;
import liuyuyang.net.model.Role;
import liuyuyang.net.model.User;
import liuyuyang.net.model.UserToken;
import liuyuyang.net.vo.PageVo;
import liuyuyang.net.vo.user.UserFilterVo;
import liuyuyang.net.web.mapper.RoleMapper;
import liuyuyang.net.web.mapper.UserMapper;
import liuyuyang.net.web.mapper.UserTokenMapper;
import liuyuyang.net.web.service.UserService;
import lombok.Getter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private YuYangUtils yuYangUtils;
    @Resource
    private UserMapper userMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private UserTokenMapper userTokenMapper;

    @Override
    public void add(UserDTO user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());

        User data = userMapper.selectOne(queryWrapper);

        // 判断用户是否存在
        if (data != null) throw new CustomException(400, "该用户已存在�? + user.getUsername());

        // 密码加密
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));

        User temp = new User();
        BeanUtils.copyProperties(user, temp);

        userMapper.insert(temp);
    }

    @Override
    public void del(Integer id) {
        User data = userMapper.selectById(id);
        if (data == null) throw new CustomException(400, "该用户不存在");
        userMapper.deleteById(id);
    }

    @Override
    public void delBatch(List<Integer> ids) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        userMapper.delete(queryWrapper);
    }

    @Override
    public void edit(UserInfoDTO user) {
        User data = userMapper.selectById(user.getId());
        BeanUtils.copyProperties(user, data);
        userMapper.updateById(data);
    }

    @Override
    public User get(Integer id) {
        User data = userMapper.selectById(id);
        data.setPassword("只有聪明的人才能看到密码");

        Role role = roleMapper.selectById(data.getRoleId());
        data.setRole(role);

        return data;
    }

    @Override
    public List<User> list(UserFilterVo filterVo) {
        QueryWrapper<User> queryWrapper = yuYangUtils.queryWrapperFilter(filterVo, "name");
        if (filterVo.getRoleId() != null) {
            queryWrapper.eq("role_id", filterVo.getRoleId());
        }

        List<User> list = userMapper.selectList(queryWrapper);

        for (User user : list) {
            user.setPassword("只有聪明的人才能看到密码");
            Role role = roleMapper.selectById(user.getRoleId());
            user.setRole(role);
        }

        list = list.stream().sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())).collect(Collectors.toList());

        return list;
    }

    @Override
    public Page<User> paging(UserFilterVo filterVo, PageVo pageVo) {
        List<User> list = list(filterVo);
        return yuYangUtils.getPageData(pageVo, list);
    }

    @Override
    public Map<String, Object> login(UserLoginDTO userDTO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userDTO.getUsername());
        queryWrapper.eq("password", DigestUtils.md5DigestAsHex(userDTO.getPassword().getBytes()));

        User user = userMapper.selectOne(queryWrapper);
        if (user == null) throw new CustomException(400, "用户名或密码错误");

        Role role = roleMapper.selectById(user.getRoleId());

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("role", role);
        String token = JwtUtils.createJWT(result);
        result.put("token", token);

        // 先删除用户的token
        LambdaQueryWrapper<UserToken> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(UserToken::getUid, user.getId());
        userTokenMapper.delete(userLambdaQueryWrapper);
        // 再存储用户的token
        UserToken userToken = new UserToken();
        userToken.setUid(user.getId());
        userToken.setToken(token);
        userTokenMapper.insert(userToken);

        return result;
    }

    @Override
    public void editPass(EditPassDTO data) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", data.getOldUsername());
        queryWrapper.eq("password", DigestUtils.md5DigestAsHex(data.getOldPassword().getBytes()));

        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new CustomException(400, "用户名或旧密码错�?);
        }

        user.setUsername(data.getNewUsername());
        user.setPassword(DigestUtils.md5DigestAsHex(data.getNewPassword().getBytes()));
        userMapper.updateById(user);
    }

    @Override
    public void check(String token) {
        boolean isCheck = yuYangUtils.check(token);

        if (!isCheck) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletResponse response = attributes.getResponse();
            response.setStatus(401);
            throw new CustomException(400, "身份验证失败：无效或过期的Token");
        }
    }
}
