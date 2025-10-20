package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import liuyuyang.net.common.utils.YuYangUtils;
import liuyuyang.net.web.mapper.LinkMapper;
import liuyuyang.net.web.mapper.LinkTypeMapper;
import liuyuyang.net.model.Link;
import liuyuyang.net.model.Rss;
import liuyuyang.net.vo.PageVo;
import liuyuyang.net.web.service.RssService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Transactional
public class RssServiceImpl implements RssService {
    @Resource
    private LinkMapper linkMapper;
    @Resource
    private LinkTypeMapper linkTypeMapper;
    @Resource
    private YuYangUtils yuYangUtils;

    // 线程池，用于并发获取RSS内容
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    // 类型缓存，避免重复查询数据库
    private final Map<Integer, String> typeCache = new ConcurrentHashMap<>();

    /**
     * 初始化方法，在Bean创建后自动执�?     * 预加载所有链接类型数据到内存缓存�?     */
    @PostConstruct
    public void init() {
        // 从数据库加载所有链接类型，并存入缓�?        linkTypeMapper.selectList(null).forEach(lt ->
                typeCache.put(lt.getId(), lt.getName()));
    }

    @Cacheable(value = "rssCache", key = "'allFeeds'")
    @Override
    public List<Rss> list() {
        // 线程安全的列表，用于收集所有RSS条目
        List<Rss> rssList = Collections.synchronizedList(new ArrayList<>());

        // 从数据库获取所有链�?        List<Link> linkList = linkMapper.selectList(null);

        // 为每个有RSS地址的链接创建异步任�?        List<CompletableFuture<Void>> futures = linkList.stream()
                .filter(link -> link.getRss() != null)  // 过滤掉没有RSS地址的链�?                .map(link -> CompletableFuture.runAsync(() ->
                        processFeedWithTimeout(link, rssList), executorService))  // 异步处理每个RSS�?                .collect(Collectors.toList());

        // 等待所有异步任务完�?        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 按发布时间降序排序后返回
        return rssList.stream()
                .sorted(Comparator.comparingLong(r -> -Long.parseLong(r.getCreateTime())))
                .collect(Collectors.toList());
    }

    // 定时任务更新缓存
    @Scheduled(fixedRate = 3600000) // 每小时更新一�?    @CacheEvict(value = "rssCache", key = "'allFeeds'")
    public void evictCache() {
    }

    /**
     * 处理单个RSS源，带有超时控制
     *
     * @param link    包含RSS地址的链接对�?     * @param rssList 用于收集结果的列�?     */
    private void processFeedWithTimeout(Link link, List<Rss> rssList) {
        try {
            HttpURLConnection connection = (HttpURLConnection)
                    new URL(link.getRss()).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);

            try (InputStream input = connection.getInputStream()) {
                SyndFeed feed = new SyndFeedInput().build(new XmlReader(input));

                // 使用Stream处理并限制数�?                List<Rss> limitedItems = feed.getEntries().stream()
                        .sorted(Comparator.comparing(SyndEntry::getPublishedDate).reversed())
                        .limit(5)
                        .map(data -> {
                            Rss rss = new Rss();
                            rss.setImage(link.getImage());
                            rss.setEmail(link.getEmail());
                            rss.setType(typeCache.get(link.getTypeId()));
                            rss.setAuthor(data.getAuthor());
                            rss.setTitle(data.getTitle());
                            rss.setDescription(data.getDescription() != null ?
                                    data.getDescription().getValue() : "");
                            rss.setUrl(data.getLink());
                            rss.setCreateTime(String.valueOf(data.getPublishedDate().getTime()));
                            return rss;
                        })
                        .collect(Collectors.toList());

                rssList.addAll(limitedItems);
            }
        } catch (Exception e) {
            System.err.println("解析失败: " + link.getRss());
        }
    }

    @Override
    public Page<Rss> paging(PageVo pageVo) {
        // 使用工具类进行分�?        return yuYangUtils.getPageData(pageVo, list());
    }

    /**
     * Bean销毁前的清理方�?     * 关闭线程池，释放资源
     */
    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }
}
