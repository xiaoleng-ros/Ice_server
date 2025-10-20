package liuyuyang.net.web.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import liuyuyang.net.common.annotation.PremName;
import liuyuyang.net.common.utils.Result;
import liuyuyang.net.model.PageConfig;
import liuyuyang.net.web.service.PageConfigService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "页面配置管理")
@RestController
@RequestMapping("/page_config")
public class PageConfigController {
    @Resource
    private PageConfigService pageConfigService;

    @ApiOperation("获取页面配置列表")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 1)
    @GetMapping("/list")
    public Result<List<PageConfig>> list() {
        List<PageConfig> data = pageConfigService.list();
        return Result.success("获取成功", data);
    }

    @ApiOperation("根据名称获取页面配置")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 2)
    @GetMapping("/name/{name}")
    public Result<PageConfig> getByName(@ApiParam(value = "配置名称", required = true, example = "home_page") @PathVariable String name) {
        PageConfig pageConfig = pageConfigService.getByName(name);
        return pageConfig != null ? Result.success("获取成功", pageConfig) : Result.error("配置不存�?);
    }

    @ApiOperation("根据ID获取页面配置")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 3)
    @GetMapping("/{id}")
    public Result<PageConfig> getById(@ApiParam(value = "页面配置ID", required = true, example = "1") @PathVariable Integer id) {
        PageConfig pageConfig = pageConfigService.getById(id);
        return pageConfig != null ? Result.success("获取成功", pageConfig) : Result.error("配置不存�?);
    }

    @ApiOperation("根据ID更新页面配置")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 4)
    @PatchMapping("/json/{id}")
    public Result<String> updateJsonValue(
            @ApiParam(value = "页面配置ID", required = true, example = "1") @PathVariable Integer id,
            @ApiParam(value = "JSON配置�?, required = true) @RequestBody Map<String, Object> jsonValue) {
        boolean success = pageConfigService.updateJsonValue(id, jsonValue);
        return success ? Result.success() : Result.error();
    }
} 
