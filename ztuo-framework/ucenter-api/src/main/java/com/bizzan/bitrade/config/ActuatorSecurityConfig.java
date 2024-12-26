package com.bizzan.bitrade.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/***
 功能：该类的主要功能是保护 Spring Boot Actuator 端点，通过启用 HTTP Basic 认证，确保这些管理端点需要身份验证才能访问。
 AOP（切面编程）：通过 Spring Security 的 HttpSecurity 配置来管理对 Actuator 端点的访问权限。
 配置灵活性：通过从环境变量中获取 management.context-path，使得路径配置更加灵活，可以根据实际情况调整。
 */
@Configuration
@EnableWebSecurity
public class ActuatorSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	Environment env;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		String contextPath = env.getProperty("management.context-path");
		if (StringUtils.isEmpty(contextPath)) {
			contextPath = "";
		}
		http.csrf().disable();
		http.authorizeRequests().antMatchers("/**" + contextPath + "/**").authenticated().anyRequest().permitAll().and()
				.httpBasic();
	}
}