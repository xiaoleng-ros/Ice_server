package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.dto.role.BindRouteAndPermission;
import liuyuyang.net.web.mapper.*;
import liuyuyang.net.model.*;
import liuyuyang.net.web.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private RouteMapper routeMapper;
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private RouteRoleMapper routeRoleMapper;
    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public List<Route> getRouteList(Integer id) {
        if (id != 1) return roleMapper.getRouteList(id);
        return routeMapper.selectList(null);
    }

    @Override
    public List<Permission> getPermissionList(Integer id) {
        if(id != 1) return roleMapper.getPermissionList(id);
        return permissionMapper.selectList(null);
    }

    @Override
    public void binding(Integer roleId, BindRouteAndPermission data) {
        if (roleId == 1) return;

        // 先删除当前角色绑定的所有路由和权限
        QueryWrapper<RouteRole> routeQueryWrapper = new QueryWrapper<>();
        routeQueryWrapper.eq("role_id", roleId);
        routeRoleMapper.delete(routeQueryWrapper);

        QueryWrapper<RolePermission> permissionQueryWrapper = new QueryWrapper<>();
        permissionQueryWrapper.eq("role_id", roleId);
        rolePermissionMapper.delete(permissionQueryWrapper);

        // 然后再重新给角色绑定路由和权�?        for (Integer routeId : data.getRoute_ids()) {
            RouteRole routeRole = new RouteRole();
            routeRole.setRoleId(roleId);
            routeRole.setRouteId(routeId);
            routeRoleMapper.insert(routeRole);
        }

        for (Integer permissionId : data.getPermission_ids()) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            rolePermissionMapper.insert(rolePermission);
        }
    }
}
