package liuyuyang.net.common.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.InputStreamPlus;
import org.dromara.x.file.storage.core.UploadPretreatment;
import org.dromara.x.file.storage.core.exception.FileStorageRuntimeException;
import org.dromara.x.file.storage.core.platform.FileStorage;
import org.dromara.x.file.storage.core.upload.FilePartInfo;
import org.dromara.x.file.storage.core.upload.UploadPartInfo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

/**
 * SM.MS 图床存储实现
 * 
 * @author laifeng
 * @date 2024/12/14
 */
@Getter
@Setter
@NoArgsConstructor
public class SmmsFileStorage implements FileStorage {
    
    private String platform;
    private String domain;
    private String basePath;
    private String secretKey; // SM.MS API Token
    
    private static final String UPLOAD_URL = "https://sm.ms/api/v2/upload";
    private static final String DELETE_URL = "https://sm.ms/api/v2/delete/";
    
    public SmmsFileStorage(SmmsConfig config) {
        this.platform = config.getPlatform();
        this.domain = config.getDomain();
        this.basePath = config.getBasePath();
        this.secretKey = config.getSecretKey();
    }

    @Override
    public boolean save(FileInfo fileInfo, UploadPretreatment pre) {
        try {
            // 读取文件内容
            byte[] bytes;
            try (InputStream inputStream = pre.getFileWrapper().getInputStream()) {
                bytes = inputStream.readAllBytes();
            }
            
            // 构建上传请求
            HttpRequest request = HttpRequest.post(UPLOAD_URL)
                    .header("Authorization", secretKey)
                    .form("smfile", bytes, fileInfo.getOriginalFilename());
            
            // 发送请求
            HttpResponse response = request.execute();
            
            if (!response.isOk()) {
                throw new FileStorageRuntimeException("SM.MS上传失败，HTTP状态码：" + response.getStatus());
            }
            
            // 解析响应
            JSONObject result = JSONUtil.parseObj(response.body());
            
            if (!result.getBool("success", false)) {
                String message = result.getStr("message", "未知错误");
                throw new FileStorageRuntimeException("SM.MS上传失败：" + message);
            }
            
            JSONObject data = result.getJSONObject("data");
            String url = data.getStr("url");
            String hash = data.getStr("hash");
            String deleteUrl = data.getStr("delete");
            
            // 设置文件信息
            fileInfo.setBasePath(basePath);
            fileInfo.setPath("");
            fileInfo.setFilename(fileInfo.getOriginalFilename());
            fileInfo.setUrl(url);
            
            // 将删除URL存储在用户元数据中，以便后续删除
            if (StrUtil.isNotBlank(deleteUrl)) {
                fileInfo.putUserMetadata("deleteUrl", deleteUrl);
            }
            if (StrUtil.isNotBlank(hash)) {
                fileInfo.putUserMetadata("hash", hash);
            }
            
            return true;
            
        } catch (IOException e) {
            throw new FileStorageRuntimeException("读取文件失败", e);
        } catch (Exception e) {
            throw new FileStorageRuntimeException("SM.MS上传失败", e);
        }
    }

    @Override
    public boolean delete(FileInfo fileInfo) {
        try {
            String deleteUrl = fileInfo.getUserMetadata("deleteUrl");
            if (StrUtil.isBlank(deleteUrl)) {
                // 如果没有删除URL，尝试使用hash构建删除URL
                String hash = fileInfo.getUserMetadata("hash");
                if (StrUtil.isNotBlank(hash)) {
                    deleteUrl = DELETE_URL + hash;
                } else {
                    throw new FileStorageRuntimeException("无法删除文件：缺少删除URL或hash");
                }
            }
            
            HttpRequest request = HttpRequest.get(deleteUrl)
                    .header("Authorization", secretKey);
            
            HttpResponse response = request.execute();
            
            if (!response.isOk()) {
                throw new FileStorageRuntimeException("SM.MS删除失败，HTTP状态码：" + response.getStatus());
            }
            
            JSONObject result = JSONUtil.parseObj(response.body());
            return result.getBool("success", false);
            
        } catch (Exception e) {
            throw new FileStorageRuntimeException("SM.MS删除失败", e);
        }
    }

    @Override
    public boolean exists(FileInfo fileInfo) {
        // SM.MS 不提供文件存在性检查API，默认返回true
        return true;
    }

    @Override
    public void download(FileInfo fileInfo, Consumer<InputStreamPlus> consumer) {
        try {
            HttpRequest request = HttpRequest.get(fileInfo.getUrl());
            HttpResponse response = request.execute();
            
            if (!response.isOk()) {
                throw new FileStorageRuntimeException("下载文件失败，HTTP状态码：" + response.getStatus());
            }
            
            byte[] bytes = response.bodyBytes();
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
                InputStreamPlus inputStreamPlus = new InputStreamPlus(inputStream, bytes.length, true);
                consumer.accept(inputStreamPlus);
            }
            
        } catch (Exception e) {
            throw new FileStorageRuntimeException("下载文件失败", e);
        }
    }

    @Override
    public boolean supportPresignedUrl() {
        return false;
    }

    @Override
    public String generatePresignedUrl(FileInfo fileInfo, long expiration) {
        throw new FileStorageRuntimeException("SM.MS不支持预签名URL");
    }

    @Override
    public boolean supportAcl() {
        return false;
    }

    @Override
    public boolean setFileAcl(FileInfo fileInfo, Object acl) {
        return false;
    }

    @Override
    public boolean supportMetadata() {
        return true;
    }

    @Override
    public boolean setFileMetadata(FileInfo fileInfo) {
        return false;
    }

    @Override
    public boolean isSupportListFiles() {
        return false;
    }



    @Override
    public void close() {
        // SM.MS 不需要特殊的关闭操作
    }
}