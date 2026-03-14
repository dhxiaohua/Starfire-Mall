package com.starfire.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CaptchaStore {
    
    @Value("${captcha.expiration:300000}")
    private Long expiration;
    
    // 验证码存储结构: { username: { code: string, timestamp: long } }
    private final Map<String, CaptchaData> store = new ConcurrentHashMap<>();
    
    public void put(String username, String code) {
        store.put(username, new CaptchaData(code, System.currentTimeMillis()));
    }
    
    public CaptchaData get(String username) {
        return store.get(username);
    }
    
    public void remove(String username) {
        store.remove(username);
    }
    
    public boolean verify(String username, String code) {
        CaptchaData data = store.get(username);
        if (data == null) {
            return false;
        }
        
        // 检查是否过期
        if (System.currentTimeMillis() - data.getTimestamp() > expiration) {
            store.remove(username);
            return false;
        }
        
        // 验证成功后删除，防止重复使用
        store.remove(username);
        
        return data.getCode().equalsIgnoreCase(code);
    }
    
    public static class CaptchaData {
        private String code;
        private Long timestamp;
        
        public CaptchaData(String code, Long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }
        
        public String getCode() {
            return code;
        }
        
        public Long getTimestamp() {
            return timestamp;
        }
    }
}
