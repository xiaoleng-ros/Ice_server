package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.common.annotation.NoTokenRequired;
import liuyuyang.net.common.annotation.PremName;
import liuyuyang.net.model.Tag;
import liuyuyang.net.common.utils.Result;
import liuyuyang.net.web.service.TagService;
import liuyuyang.net.common.utils.Paging;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "标签管理")
@RestController
@RequestMapping("/tag")
@Transactional
public class TagController {
    @Resource
    private TagService tagService;

    @PremName("tag:add")
    @PostMapping
    @ApiOperation("新增标签")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 1)
    public Result<String> add(@RequestBody Tag tag) {
        tagService.save(tag);
        return Result.success();
    }

    @PremName("tag:del")
    @DeleteMapping("/{id}")
    @ApiOperation("删除标签")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 2)
    public Result<String> del(@PathVariable Integer id) {
        Tag data = tagService.getById(id);
        if (data == null) return Result.error("该数据不存在");
        tagService.removeById(id);
        return Result.success();
    }

    @PremName("tag:del")
    @DeleteMapping("/batch")
    @ApiOperation("批量删除标签")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 3)
    public Result batchDel(@RequestBody List<Integer> ids) {
        tagService.removeByIds(ids);
        return Result.success();
    }

    @PremName("tag:edit")
    @PatchMapping
    @ApiOperation("编辑标签")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 4)
    public Result<String> edit(@RequestBody Tag tag) {
        tagService.updateById(tag);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("获取标签")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 5)
    public Result<Tag> get(@PathVariable Integer id) {
        Tag data = tagService.getById(id);
        return Result.success(data);
    }

    @NoTokenRequired
    @PostMapping("/list")
    @ApiOperation("获取标签列表")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 6)
    public Result<List<Tag>> list() {
        List<Tag> data = tagService.list();
        return Result.success(data);
    }

    @NoTokenRequired
    @PostMapping("/paging")
    @ApiOperation("分页查询标签列表")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 7)
    public Result paging(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "5") Integer size) {
        Page<Tag> data = tagService.list(page, size);
        Map<String, Object> result = Paging.filter(data);
        return Result.success(result);
    }

    // 统计文章数量
    @GetMapping("/article/count")
    @ApiOperation("统计每个标签下的文章数量")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 8)
    public Result staticArticleCount() {
        List<Tag> list = tagService.staticArticleCount();
        return Result.success(list);
    }
}
