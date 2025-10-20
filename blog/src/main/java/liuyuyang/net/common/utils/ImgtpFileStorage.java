package liuyuyang.net.common.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.InputStreamPlus;
import org.dromara.x.file.storage.core.UploadPretreatment;
import org.dromara.x.file.storage.core.exception.FileStorageRuntimeException;
import org.dromara.x.file.storage.core.platform.FileStorage;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

/**
 * 路过图床存储实现
 * @author laifeng
 * @version 1.0
 * @date 2024/12/10 21:21
 */
public class ImgtpFileStorage implements FileStorage {
    
    private final ImgtpConfig config;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    public ImgtpFileStorage(ImgtpConfig config) {
        this.config = config;
        this.webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public boolean save(FileInfo fileInfo, UploadPretreatment uploadPretreatment) {
        try {
            InputStreamPlus inputStream = uploadPretreatment.getFileWrapper().getInputStream();
            
            // 构建 multipart 请求体
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("token", config.getSecretKey());
            builder.part("file", inputStream.readAllBytes())
                    .filename(fileInfo.getOriginalFilename())
                    .contentType(MediaType.parseMediaType(fileInfo.getContentType()));
            
            // 发送请求
            String responseBody = webClient.post()
                    .uri("https://www.imgtp.com/api/upload")
                    .header(HttpHeaders.USER_AGENT, "ThriveX-Blog/1.0")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(60))
                    .block();
            
            if (responseBody != null) {
                JsonNode jsonResponse = objectMapper.readTree(responseBody);
                
                if (jsonResponse.has("code") && jsonResponse.get("code").asInt() == 200) {
                    JsonNode data = jsonResponse.get("data");
                    String imageUrl = data.get("url").asText();
                    String deleteHash = data.has("delete") ? data.get("delete").asText() : null;
                    
                    // 设置文件信息
                    fileInfo.setUrl(imageUrl);
                    fileInfo.setBasePath("");
                    fileInfo.setPath(imageUrl);
                    fileInfo.setFilename(imageUrl.substring(imageUrl.lastIndexOf("/") + 1));
                    
                    // 存储删除hash用于后续删除
                    if (deleteHash != null) {
                        fileInfo.getAttr().put("deleteHash", deleteHash);
                    }
                    
                    return true;
                } else {
                    String message = jsonResponse.has("msg") ? jsonResponse.get("msg").asText() : "上传失败";
                    throw new FileStorageRuntimeException("路过图床上传失败: " + message);
                }
            } else {
                throw new FileStorageRuntimeException("路过图床上传失败：响应为空");
            }
            
        } catch (IOException | InterruptedException e) {
            throw new FileStorageRuntimeException("路过图床上传异常", e);
        }
    }
    
    @Override
    public boolean delete(FileInfo fileInfo) {
        try {
            String deleteHash = (String) fileInfo.getAttr().get("deleteHash");
            if (deleteHash == null || deleteHash.isEmpty()) {
                // 如果没有删除hash，无法删�?                return false;
            }
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.imgtp.com/api/delete"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("User-Agent", "ThriveX-Blog/1.0")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "token=" + config.getSecretKey() + "&delete=" + deleteHash))
                    .timeout(Duration.ofSeconds(30))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonNode jsonResponse = objectMapper.readTree(response.body());
                return jsonResponse.has("code") && jsonResponse.get("code").asInt() == 200;
            }
            
            return false;
        } catch (IOException | InterruptedException e) {
            throw new FileStorageRuntimeException("路过图床删除异常", e);
        }
    }
    
    @Override
    public boolean exists(FileInfo fileInfo) {
        // 路过图床不提供文件存在性检查API，默认返回true
        return true;
    }
    
    @Override
    public void download(FileInfo fileInfo, InputStream inputStream) {
        throw new FileStorageRuntimeException("路过图床不支持下载功�?);
    }
    
    @Override
    public String getPlatform() {
        return config.getPlatform();
    }
    
    @Override
    public void close() {
        // 清理资源
    }
}
