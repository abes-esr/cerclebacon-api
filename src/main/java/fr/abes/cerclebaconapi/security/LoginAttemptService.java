package fr.abes.cerclebaconapi.security;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LoginAttemptService {

    private final LoadingCache<String, Integer> attemptsCache;

    public LoginAttemptService() {
        super();
        int nbMinutes = 15;
        attemptsCache = CacheBuilder.newBuilder().expireAfterWrite(nbMinutes, TimeUnit.MINUTES).build(new CacheLoader<>() {
            @Override
            public Integer load(final @NonNull String key) {
                return 0;
            }
        });
    }

    //

    public void loginSucceeded(final String key) {
        log.debug("ENTER_LOGIN_SUCCEED {}", key);
        attemptsCache.invalidate(key);
    }

    public void loginFailed(final String key) {
        log.debug("ENTER_LOGIN_FAILED");
        int attempts;
        try {
            attempts = attemptsCache.get(key);
            log.info("NUMBER_IP_TENTATIVES {} est : {}", key, attempts);
        } catch (final ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);
    }

    public boolean isBlocked(final String key) {
        try {
            int maxAttempt = 10;
            return attemptsCache.get(key) >= maxAttempt;
        } catch (final ExecutionException e) {
            return false;
        }
    }
}
