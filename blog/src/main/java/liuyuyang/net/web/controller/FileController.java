package liuyuyang.net.web.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.qiniu.common.QiniuException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.common.annotation.PremName;
import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.common.utils.Result;
import liuyuyang.net.common.utils.OssUtils;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.get.ListFilesResult;
import org.dromara.x.file.storage.core.get.RemoteDirInfo;
import org.dromara.x.file.storage.core.get.RemoteFileInfo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * 统一文件上传
 *
 * @author laifeng
 * @date 2024/12/14
 */
@Api(tags = "文件管理")
@RestController
@RequestMapping("/file")
@Transactional
public class FileController {
    @Resource
    private FileStorageService fileStorageService;

    @PremName("file:add")
    @PostMapping
    @ApiOperation("文件上传")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 1)
    public Result<Object> add(@RequestParam(defaultValue = "") String dir, @RequestParam MultipartFile[] files) throws IOException {
        if (dir == null || dir.trim().isEmpty()) throw new CustomException(400, "请指定一个目�?);

        List<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {
            FileInfo result = fileStorageService.of(file)
                    .setPlatform(OssUtils.getPlatform())
                    .setPath(dir + '/')
                    .upload();

            if (result == null) throw new CustomException("上传文件失败");

            String url = result.getUrl();
            urls.add(url.startsWith("https://") ? url : "https://" + url);
        }

        return Result.success("文件上传成功�?, urls);
    }

    @PremName("file:del")
    @DeleteMapping
    @ApiOperation("删除文件")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 2)
    public Result<String> del(@RequestParam String filePath) {
        String url = filePath.startsWith("https://") ? filePath : "https://" + filePath;
        boolean delete = fileStorageService.delete(url);
        return Result.status(delete);
    }

    @PremName("file:del")
    @DeleteMapping("/batch")
    @ApiOperation("批量删除文件")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 3)
    public Result batchDel(@RequestBody String[] pathList) throws QiniuException {
        for (String url : pathList) {
            boolean delete = fileStorageService.delete(url.startsWith("https://") ? url : "https://" + url);
            if (!delete) throw new CustomException("删除文件失败");
        }
        return Result.success();
    }

    @PremName("file:info")
    @GetMapping("/info")
    @ApiOperation("获取文件信息")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 4)
    public Result<FileInfo> get(@RequestParam String filePath) throws QiniuException {
        FileInfo fileInfo = fileStorageService.getFileInfoByUrl(filePath);
        return Result.success(fileInfo);
    }

    @PremName("file:dir")
    @GetMapping("/dir")
    @ApiOperation("获取目录列表")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 5)
    public Result<List<Map>> getDirList() {
        ListFilesResult result = fileStorageService.listFiles()
                .setPlatform(OssUtils.getPlatform())
                .listFiles();

        // 获取文件列表
        List<Map> list = new ArrayList<>();
        List<RemoteDirInfo> fileList = result.getDirList();

        for (RemoteDirInfo item : fileList) {
            Map<String, Object> data = new HashMap<>();
            data.put("name", item.getName());
            data.put("path", item.getOriginal());
            list.add(data);
        }

        return Result.success(list);
    }

    @PremName("file:list")
    @GetMapping("/list")
    @ApiOperation("获取指定目录中的文件")
    @ApiOperationSupport(author = "刘宇�?| liuyuyang1024@yeah.net", order = 5)
    public Result<Map<String, Object>> getFileList(
            @RequestParam String dir,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        if (dir == null || dir.trim().isEmpty()) throw new CustomException(400, "请指定一个目�?);

        ListFilesResult result = fileStorageService.listFiles()
                .setPlatform(OssUtils.getPlatform())
                .setPath(dir + '/')
                .listFiles();

        // 获取文件列表
        List<Map<String, Object>> fileList = new ArrayList<>();
        List<RemoteFileInfo> remoteFileList = result.getFileList();

        // 按lastModified时间降序排序（最新的在前�?        remoteFileList.sort((a, b) -> b.getLastModified().compareTo(a.getLastModified()));

        // 计算分页参数
        int total = remoteFileList.size();
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, total);
        
        // 分页处理
        List<RemoteFileInfo> pageList = remoteFileList.subList(startIndex, endIndex);

        for (RemoteFileInfo item : pageList) {
            // 如果是目录就略过
            if (Objects.equals(item.getExt(), "")) continue;

            Map<String, Object> data = new HashMap<>();
            data.put("basePath", item.getBasePath());
            data.put("dir", dir);
            data.put("path", item.getBasePath() + item.getPath() + item.getFilename());
            data.put("name", item.getFilename());
            data.put("size", item.getSize());
            data.put("type", item.getExt());
            data.put("date", item.getLastModified());

            String url = item.getUrl();
            if (!url.startsWith("https://")) url = "https://" + url;
            data.put("url", url);

            fileList.add(data);
        }

        // 构建分页结果
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", fileList);
        resultMap.put("size", size);
        resultMap.put("page", page);
        resultMap.put("pages", (total + size - 1) / size);
        resultMap.put("total", total);

        return Result.success(resultMap);
    }
}
