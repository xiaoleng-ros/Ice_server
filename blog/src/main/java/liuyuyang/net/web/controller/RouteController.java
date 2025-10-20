package liuyuyang.net.web.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.common.annotation.PremName;
import liuyuyang.net.model.Route;
import liuyuyang.net.model.RouteRole;
import liuyuyang.net.web.service.RouteRoleService;
import liuyuyang.net.common.utils.Result;
import liuyuyang.net.web.service.RouteService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

@Api(tags = "路由管理")
@RestController
@RequestMapping("/route")
@Transactional
public class RouteController {
    @Resource
    private RouteService routeService;
    @Resource
    private RouteRoleService routeRoleService;

    @PremName("route:add")
    @PostMapping
    @ApiOperation("新增路由")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 1)
    public Result<String> add(@RequestBody Route route) {
        routeService.save(route);

        // 每次新增路由后，自动分配给管理员角色
        RouteRole routeRole = new RouteRole();
        routeRole.setRouteId(route.getId());
        routeRole.setRoleId(1);

        routeRoleService.save(routeRole);

        return Result.success();
    }

    @PremName("route:del")
    @DeleteMapping("/{id}")
    @ApiOperation("删除路由")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 2)
    public Result<String> del(@PathVariable Integer id) {
        Route data = routeService.getById(id);
        if (data == null) return Result.error("该数据不存在");
        routeService.removeById(id);
        return Result.success();
    }

    @PremName("route:del")
    @DeleteMapping("/batch")
    @ApiOperation("批量删除路由")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 3)
    public Result batchDel(@RequestBody List<Integer> ids) {
        routeService.removeByIds(ids);
        return Result.success();
    }

    @PremName("route:edit")
    @PatchMapping
    @ApiOperation("编辑路由")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 4)
    public Result<String> edit(@RequestBody Route route) {
        routeService.updateById(route);
        return Result.success();
    }

    @PremName("route:info")
    @GetMapping("/{id}")
    @ApiOperation("获取路由")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 5)
    public Result<Route> get(@PathVariable Integer id) {
        Route data = routeService.getById(id);
        return Result.success(data);
    }

    @PremName("route:list")
    @GetMapping
    @ApiOperation("获取路由列表")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 6)
    public Result<List<Route>> list() {
        List<Route> data = routeService.list();
        return Result.success(data);
    }
}
