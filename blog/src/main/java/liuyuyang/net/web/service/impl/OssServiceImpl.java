package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.web.mapper.OssMapper;
import liuyuyang.net.model.Oss;
import liuyuyang.net.web.service.OssService;
import liuyuyang.net.common.utils.OssUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OssServiceImpl extends ServiceImpl<OssMapper, Oss> implements OssService {
    @Resource
    private OssMapper ossMapper;

    @Override
    public void saveOss(Oss oss) {
        // 判断是否有重�?        Integer count = this.lambdaQuery().eq(Oss::getPlatform, oss.getPlatform()).count();
        if (count > 0) throw new CustomException("该平台已存在，请勿重复添�?);

        if ("local".equals(oss.getPlatform())) {
            // 获取当前项目的路�?            String projectPath = System.getProperty("user.dir");
            oss.setEndPoint(projectPath + "/");
        }

        this.save(oss);
    }

    @Override
    public void delOss(Integer id) {
        Oss oss = this.getById(id);
        if (oss == null) throw new CustomException("删除失败");
        // 如果是默认的平台，提示不可删�?        if (oss.getPlatform().equals("local")) throw new CustomException("默认平台不可删除");
        boolean result = this.removeById(id);
        if (result) OssUtils.removeStorage(OssUtils.getStorageList(), oss.getPlatform());
    }

    @Override
    public List<Oss> list() {
        QueryWrapper<Oss> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(Oss::getId);
        List<Oss> list = ossMapper.selectList(queryWrapper);

        for (Oss data : list) {
            data.setAccessKey(maskMiddleTen(data.getAccessKey()));
            data.setSecretKey(maskMiddleTen(data.getSecretKey()));
            data.setPlatformName(platformName(data.getPlatform()));
        }

        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enable(Integer id) {
        // 先禁用所有的配置
        boolean temp1 = this.update(Wrappers.<Oss>update().lambda().set(Oss::getIsEnable, 0));
        if (!temp1) throw new CustomException("操作失败");

        // 再启用制定的配置
        boolean temp2 = this.update(Wrappers.<Oss>update().lambda().set(Oss::getIsEnable, 1).eq(Oss::getId, id));
        if (!temp2) throw new CustomException("启用失败");

        Oss oss = this.getById(id);
        OssUtils.registerPlatform(oss);
    }

    public Oss getEnableOss() {
        QueryWrapper<Oss> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Oss::getIsEnable, 1);
        return ossMapper.selectOne(queryWrapper);
    }

    @Override
    public List<Map> getPlatform() {
        List<Map> result = new ArrayList<>();
        String[] list = {"huawei", "aliyun", "qiniu", "tencent", "minio", "smms", "imgtp"};

        for (String item : list) {
            Map<String, String> data = new HashMap<>();
            data.put("name", platformName(item));
            data.put("value", item);
            result.add(data);
        }

        return result;
    }

    @Override
    public void updateOss(Oss oss) {
        String platform = oss.getPlatform();

        if ("local".equals(platform)) {
            // 获取当前项目的路�?            String projectPath = System.getProperty("user.dir");
            oss.setEndPoint(projectPath + "/");

            // 每次修改时候，如果路径不包含static则追加上
            if(!oss.getDomain().contains("static")){
                oss.setDomain(oss.getDomain() + "static/");
            }
        }

        // 不允许更改平�?        oss.setPlatform(null);
        boolean result = this.updateById(oss);
        if (result) {
            oss.setPlatform(platform);
            OssUtils.registerPlatform(oss);
        }
    }

    // 对数据中�?0位数进行脱敏
    public String maskMiddleTen(String input) {
        if (input == null || input.length() <= 10) return input;

        int start = (input.length() - 10) / 2;
        int end = start + 10;

        StringBuilder masked = new StringBuilder(input);
        for (int i = start; i < end; i++) {
            masked.setCharAt(i, '*');
        }

        return masked.toString();
    }

    // 处理平台名称
    public String platformName(String data) {
        switch (data) {
            case "local":
                return "本地存储";
            case "huawei":
                return "华为�?;
            case "aliyun":
                return "阿里�?;
            case "qiniu":
                return "七牛�?;
            case "tencent":
                return "腾讯�?;
            case "minio":
                return "Minio";
            case "smms":
                return "SM.MS图床";
            case "imgtp":
                return "路过图床";
        }

        return data;
    }
}
