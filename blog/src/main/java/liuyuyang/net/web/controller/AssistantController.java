package liuyuyang.net.web.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.common.annotation.NoTokenRequired;
import liuyuyang.net.common.annotation.PremName;
import liuyuyang.net.common.utils.Result;
import liuyuyang.net.model.Assistant;
import liuyuyang.net.web.service.AssistantService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

@Api(tags = "助手管理")
@RestController
@RequestMapping("/assistant")
@Transactional
public class AssistantController {
    @Resource
    private AssistantService assistantService;

    @PremName("assistant:add")
    @PostMapping
    @ApiOperation("新增助手")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 1)
    public Result<String> add(@RequestBody Assistant assistant) {
        // 将之前的都设置为 0 表示未选中
        assistantService.lambdaUpdate()
                .set(Assistant::getIsDefault, 0)
                .update();

        // 将当前的设置为选中状�?        assistant.setIsDefault(1);
        assistantService.save(assistant);
        return Result.success();
    }

    @PremName("assistant:del")
    @DeleteMapping("/{id}")
    @ApiOperation("删除助手")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 2)
    public Result<String> del(@PathVariable Integer id) {
        Assistant data = assistantService.getById(id);
        if (data == null) return Result.error("该助手不存在");
        if (data.getIsDefault() == 1) return Result.error("无法删除默认助手，请更换后重�?);

        assistantService.removeById(id);
        return Result.success();
    }

    @PremName("assistant:del")
    @DeleteMapping("/batch")
    @ApiOperation("批量删除助手")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 3)
    public Result batchDel(@RequestBody List<Integer> ids) {
        assistantService.removeByIds(ids);
        return Result.success();
    }

    @PremName("assistant:edit")
    @PatchMapping
    @ApiOperation("编辑助手")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 4)
    public Result<String> edit(@RequestBody Assistant assistant) {
        assistantService.updateById(assistant);
        return Result.success();
    }

    @PremName("assistant:list")
    @GetMapping("/{id}")
    @ApiOperation("获取助手")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 5)
    public Result<Assistant> get(@PathVariable Integer id) {
        Assistant data = assistantService.getById(id);
        return Result.success(data);
    }

    @PremName("assistant:list")
    @PostMapping("/list")
    @ApiOperation("获取助手列表")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 6)
    public Result<List<Assistant>> list() {
        List<Assistant> data = assistantService.list();
        return Result.success(data);
    }

    @PremName("assistant:default")
    @PatchMapping("/default/{id}")
    @ApiOperation("设置默认助手")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 7)
    public Result<String> selectDefault(@PathVariable Integer id) {
        Assistant assistant = assistantService.getById(id);
        if (assistant == null) return Result.error("暂无该助�?);

        // 将之前的都设置为 0 表示未选中
        assistantService.lambdaUpdate()
                .set(Assistant::getIsDefault, 0)
                .update();

        // 将当前的设置�?1 选中状�?        assistant.setIsDefault(1);
        assistantService.updateById(assistant);
        return Result.success();
    }
}
