package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.common.annotation.CheckRole;
import liuyuyang.net.common.annotation.NoTokenRequired;
import liuyuyang.net.common.utils.Paging;
import liuyuyang.net.model.Rss;
import liuyuyang.net.common.utils.Result;
import liuyuyang.net.vo.FilterVo;
import liuyuyang.net.vo.PageVo;
import liuyuyang.net.web.service.RssService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.*;

@Api(tags = "订阅管理")
@RestController
@RequestMapping("/rss")
public class RssController {
    @Resource
    private RssService rssService;

    @NoTokenRequired
    @GetMapping("/list")
    @ApiOperation("获取订阅内容")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 1)
    public Result<List<Rss>> list() {
        List<Rss> list = rssService.list();
        return Result.success(list);
    }

    @NoTokenRequired
    @PostMapping("/paging")
    @ApiOperation("分页查询订阅内容")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 2)
    public Result paging(PageVo pageVo) {
        Page<Rss> data = rssService.paging(pageVo);
        Map<String, Object> result = Paging.filter(data);
        return Result.success(result);
    }
}
