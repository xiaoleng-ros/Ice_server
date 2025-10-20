package liuyuyang.net.common.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Paging {
    // 将分页数据过滤为指定格式
    public static <T> Map<String, Object> filter(Page<T> data) {
        Map<String, Object> result = new HashMap<>();
        result.put("page", data.getCurrent()); // 当前�?        result.put("size", data.getSize()); // 每页数量
        result.put("pages", data.getPages()); // 总页�?        result.put("prev", data.getCurrent() > 1); // 是否还有上一�?        result.put("next", data.getCurrent() < data.getPages()); // 是否还有下一�?        result.put("total", data.getTotal()); // 总数�?        result.put("result", data.getRecords()); // 数据

        return result;
    }
}
