package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.common.annotation.NoTokenRequired;
import liuyuyang.net.common.annotation.PremName;
import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.model.Swiper;
import liuyuyang.net.common.utils.Result;
import liuyuyang.net.web.service.SwiperService;
import liuyuyang.net.common.utils.Paging;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "轮播图管理")
@RestController
@RequestMapping("/swiper")
@Transactional
public class SwiperController {
    @Resource
    private SwiperService swiperService;

    @PremName("swiper:add")
    @PostMapping
    @ApiOperation("新增轮播图")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    public Result<String> add(@RequestBody Swiper swiper) {
        try {
            boolean res = swiperService.save(swiper);

            return res ? Result.success() : Result.error();
        } catch (Exception e) {
            throw new CustomException(400, e.getMessage());
        }
    }

    @PremName("swiper:del")
    @DeleteMapping("/{id}")
    @ApiOperation("删除轮播图")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<String> del(@PathVariable Integer id) {
        Swiper data = swiperService.getById(id);
        if (data == null) return Result.error("该数据不存在");

        Boolean res = swiperService.removeById(id);

        return res ? Result.success() : Result.error();
    }

    @PremName("swiper:del")
    @DeleteMapping("/batch")
    @ApiOperation("批量删除轮播图")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    public Result batchDel(@RequestBody List<Integer> ids) {
        Boolean res = swiperService.removeByIds(ids);

        return res ? Result.success() : Result.error();
    }

    @PremName("swiper:edit")
    @PatchMapping
    @ApiOperation("编辑轮播图")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<String> edit(@RequestBody Swiper swiper) {
        try {
            boolean res = swiperService.updateById(swiper);

            return res ? Result.success() : Result.error();
        } catch (Exception e) {
            throw new CustomException(400, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @ApiOperation("获取轮播图")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    public Result<Swiper> get(@PathVariable Integer id) {
        Swiper data = swiperService.getById(id);
        return Result.success(data);
    }

    @NoTokenRequired
    @PostMapping("/list")
    @ApiOperation("获取轮播图列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 6)
    public Result<List<Swiper>> list() {
        List<Swiper> data = swiperService.list();
        return Result.success(data);
    }

    @PostMapping("/paging")
    @ApiOperation("分页查询轮播图列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 7)
    public Result paging(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "5") Integer size) {
        Page<Swiper> data = swiperService.list(page, size);
        Map<String, Object> result = Paging.filter(data);
        return Result.success(result);
    }
}
