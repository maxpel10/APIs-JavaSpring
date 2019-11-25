package unsl.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import unsl.entities.User;
import unsl.services.UserService;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;


@Configuration
public class CacheConfig {

    public static final String USERS_CACHE = "users";
    @Value("${cache.users.maximum.size:10}")
    private int usersMaxSize;

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        simpleCacheManager.setCaches(Arrays.asList(
                buildUsersCache()
        ));
        return simpleCacheManager;
    }


    private GuavaCache buildUsersCache() {
        return new GuavaCache(USERS_CACHE, CacheBuilder
                .newBuilder()
                .maximumSize(usersMaxSize)
                .expireAfterAccess(1, TimeUnit.DAYS)
                .build(),
                true
        );
    }



}
