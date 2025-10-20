package liuyuyang.net.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.web.mapper.FileDetailMapper;
import liuyuyang.net.model.FileDetail;
import lombok.SneakyThrows;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.hash.HashInfo;
import org.dromara.x.file.storage.core.recorder.FileRecorder;
import org.dromara.x.file.storage.core.upload.FilePartInfo;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 用来将文件上传记录保存到数据库，这里使用�?MyBatis-Plus �?Hutool 工具�? */
@Service
public class FileDetailService extends ServiceImpl<FileDetailMapper, FileDetail> implements FileRecorder {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 保存文件信息到数据库
     */
    @SneakyThrows
    @Override
    public boolean save(FileInfo info) {
        FileDetail detail = toFileDetail(info);
        boolean b = save(detail);
        if (b) info.setId(detail.getId());
        return b;
    }

    /**
     * 更新文件记录，可以根据文�?ID �?URL 来更新文件记录，
     * 主要用在手动分片上传文件-完成上传，作用是更新文件信息
     */
    @SneakyThrows
    @Override
    public void update(FileInfo info) {
        FileDetail detail = toFileDetail(info);
        LambdaQueryWrapper<FileDetail> qw = new QueryWrapper<FileDetail>()
                .lambda()
                .eq(detail.getUrl() != null, FileDetail::getUrl, detail.getUrl())
                .eq(detail.getId() != null, FileDetail::getId, detail.getId());
        update(detail, qw);
    }

    /**
     * 根据 url 查询文件信息
     */
    @SneakyThrows
    @Override
    public FileInfo getByUrl(String url) {
        FileDetail detail = getOne(new QueryWrapper<FileDetail>().lambda().eq(FileDetail::getUrl, url));
        if (detail == null) throw new CustomException("文件不存�?);
        return toFileInfo(detail);
    }

    /**
     * 根据 url 删除文件信息
     */
    // @Override
    public boolean delete(String url) {
        remove(new QueryWrapper<FileDetail>()
                .lambda()
                .eq(FileDetail::getUrl, url));
        return true;
    }

    /**
     * 保存文件分片信息
     *
     * @param filePartInfo 文件分片信息
     */
    @Override
    public void saveFilePart(FilePartInfo filePartInfo) {
    }

    /**
     * 删除文件分片信息
     */
    @Override
    public void deleteFilePartByUploadId(String uploadId) {
    }

    /**
     * �?FileInfo 转为 FileDetail
     */
    public FileDetail toFileDetail(FileInfo info) throws JsonProcessingException {
        String url = info.getUrl();

        // 如果前缀不带 https:// 则自动补�?        if (!url.startsWith("https://")) url = "https://" + url;

        FileDetail detail = BeanUtil.copyProperties(
                info, FileDetail.class, "metadata", "userMetadata", "thMetadata", "thUserMetadata", "attr", "hashInfo");

        detail.setUrl(url);
        // 这里手动�?元数�?并转�?json 字符串，方便存储在数据库�?        detail.setMetadata(valueToJson(info.getMetadata()));
        detail.setUserMetadata(valueToJson(info.getUserMetadata()));
        detail.setThMetadata(valueToJson(info.getThMetadata()));
        detail.setThUserMetadata(valueToJson(info.getThUserMetadata()));
        // 这里手动�?取附加属性字�?并转�?json 字符串，方便存储在数据库�?        detail.setAttr(valueToJson(info.getAttr()));
        // 这里手动�?哈希信息 并转�?json 字符串，方便存储在数据库�?        detail.setHashInfo(valueToJson(info.getHashInfo()));
        return detail;
    }

    /**
     * �?FileDetail 转为 FileInfo
     */
    public FileInfo toFileInfo(FileDetail detail) throws JsonProcessingException {
        FileInfo info = BeanUtil.copyProperties(
                detail, FileInfo.class, "metadata", "userMetadata", "thMetadata", "thUserMetadata", "attr", "hashInfo");

        // 这里手动获取数据库中�?json 字符�?并转�?元数据，方便使用
        info.setMetadata(jsonToMetadata(detail.getMetadata()));
        info.setUserMetadata(jsonToMetadata(detail.getUserMetadata()));
        info.setThMetadata(jsonToMetadata(detail.getThMetadata()));
        info.setThUserMetadata(jsonToMetadata(detail.getThUserMetadata()));
        // 这里手动获取数据库中�?json 字符�?并转�?附加属性字典，方便使用
        info.setAttr(jsonToDict(detail.getAttr()));
        // 这里手动获取数据库中�?json 字符�?并转�?哈希信息，方便使�?        info.setHashInfo(jsonToHashInfo(detail.getHashInfo()));
        return info;
    }

    /**
     * 将指定值转换成 json 字符�?     */
    public String valueToJson(Object value) throws JsonProcessingException {
        if (value == null) return null;
        return objectMapper.writeValueAsString(value);
    }

    /**
     * �?json 字符串转换成元数据对�?     */
    public Map<String, String> jsonToMetadata(String json) throws JsonProcessingException {
        if (StrUtil.isBlank(json)) return null;
        return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {
        });
    }

    /**
     * �?json 字符串转换成字典对象
     */
    public Dict jsonToDict(String json) throws JsonProcessingException {
        if (StrUtil.isBlank(json)) return null;
        return objectMapper.readValue(json, Dict.class);
    }

    /**
     * �?json 字符串转换成哈希信息对象
     */
    public HashInfo jsonToHashInfo(String json) throws JsonProcessingException {
        if (StrUtil.isBlank(json)) return null;
        return objectMapper.readValue(json, HashInfo.class);
    }
}
