package com.healnexus.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
@Service
public class LoginAttemptService {
    private static final int MAX_ATTEMPTS=10;
    private static final long BLOCK_DURATION_MINUTES=1;

    private final Map<String,Attempt> attempts=new ConcurrentHashMap<>();
    public void loginFailed(String ip){
        Attempt attempt=attempts.getOrDefault(ip,new Attempt());
        if(attempt.isBlocked()) return;
        attempt.increment();
        if(attempt.getCount()>=MAX_ATTEMPTS){
            log.warn("IP {} current attempt count BEFORE increment: {}", ip, attempt.getCount());
            attempt.block();
        }
        attempts.put(ip,attempt);
    }
    public void loginSuccess(String ip){
        attempts.remove(ip);
    }
    public boolean isBlocked(String ip){
        Attempt attempt=attempts.get(ip);
        if(attempt==null) return false;
        if(attempt.isBlocked() && LocalDateTime.now().isAfter(attempt.getBlockedAt().plusMinutes(BLOCK_DURATION_MINUTES))){
            attempts.remove(ip);
            return false;
        }
        return attempt.isBlocked();
    }

    private static class Attempt{
        private int count=0;
        private boolean blocked=false;
        private LocalDateTime blockedAt;
        void increment(){
            count++;
        }
        int getCount(){
            return count;
        }
        boolean isBlocked(){
            return blocked;
        }
        void block(){
            blocked=true;
            blockedAt = LocalDateTime.now();

        }
        LocalDateTime getBlockedAt(){
            return blockedAt;
        }


    }
}

