package com.bizzan.bitrade.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieHttpSessionStrategy;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;

import com.bizzan.bitrade.ext.SmartHttpSessionStrategy;

/**
 * 5个小时过期
 * 启用 Redis 存储 HTTP 会话：通过 @EnableRedisHttpSession 注解配置了会话存储到 Redis。
 * 会话过期时间配置：设置了会话的过期时间为 5 小时（18000 秒）。
 * 自定义会话管理策略：通过 httpSessionStrategy Bean 配置了一个自定义的会话管理策略，支持通过请求头和 Cookie 来传递会话 ID。
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 18000)
public class HttpSessionConfig {
	 
	 @Bean
	 public HttpSessionStrategy httpSessionStrategy(){
		 HeaderHttpSessionStrategy headerSession = new HeaderHttpSessionStrategy();
		 CookieHttpSessionStrategy cookieSession = new CookieHttpSessionStrategy();
		 headerSession.setHeaderName("x-auth-token");
		 return new SmartHttpSessionStrategy(cookieSession,headerSession);
	 }
}
