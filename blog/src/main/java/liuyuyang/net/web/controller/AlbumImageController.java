package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.common.annotation.NoTokenRequired;
import liuyuyang.net.common.annotation.PremName;
import liuyuyang.net.dto.album.AlbumImageAddFormDTO;
import liuyuyang.net.model.AlbumImage;
import liuyuyang.net.common.utils.Result;
import liuyuyang.net.web.service.AlbumImageService;
import liuyuyang.net.common.utils.Paging;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "照片管理")
@RestController
@RequestMapping("/album/image")
@Transactional
public class AlbumImageController {
    @Resource
    private AlbumImageService albumImageService;

    @PremName("album_image:add")
    @PostMapping
    @ApiOperation("新增照片")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 1)
    public Result<String> add(@RequestBody AlbumImageAddFormDTO albumImageAddFormDTO) {
        albumImageService.add(albumImageAddFormDTO);
        return Result.success();
    }

    @PremName("album_image:del")
    @DeleteMapping("/{id}")
    @ApiOperation("删除照片")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 2)
    public Result<String> del(@PathVariable Integer id) {
        albumImageService.del(id);
        return Result.success();
    }

    @PremName("album_image:del")
    @DeleteMapping("/batch")
    @ApiOperation("批量删除照片")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 3)
    public Result<String> batchDel(@RequestBody List<Integer> ids) {
        albumImageService.batchDel(ids);
        return Result.success();
    }

    @PremName("album_image:edit")
    @PatchMapping
    @ApiOperation("编辑照片")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 4)
    public Result<String> edit(@RequestBody AlbumImage albumImage) {
        albumImageService.edit(albumImage);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("获取照片")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 5)
    public Result<AlbumImage> get(@PathVariable Integer id) {
        AlbumImage albumImage = albumImageService.get(id);
        return Result.success(albumImage);
    }

    @NoTokenRequired
    @PostMapping("/list")
    @ApiOperation("获取照片列表")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 6)
    public Result<List<AlbumImage>> list() {
        List<AlbumImage> albumImages = albumImageService.list();
        return Result.success(albumImages);
    }

    @NoTokenRequired
    @PostMapping("/paging")
    @ApiOperation("分页查询照片列表")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 7)
    public Result paging(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        Page<AlbumImage> data = albumImageService.paging(page, size);
        Map<String, Object> result = Paging.filter(data);
        return Result.success(result);
    }
}
