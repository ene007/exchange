package com.bizzan.bitrade.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.bizzan.bitrade.ext.OrdinalToEnumConverterFactory;
import com.bizzan.bitrade.interceptor.MemberInterceptor;

/**
 该 ApplicationConfig 类配置了应用中的几个关键功能：

 跨域支持： 配置了一个 WebSocket 服务和跨域请求支持，允许不同来源的请求访问应用。
 格式化和转换： 通过自定义转换器处理枚举类型的转换。
 拦截器： 配置了 MemberInterceptor 拦截器来对特定路径进行处理，如身份验证、权限控制等。
 国际化支持： 配置了资源文件以支持不同语言的消息，提升了应用的可国际化性
 */
@Configuration
public class ApplicationConfig extends WebMvcConfigurerAdapter {
    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addExposedHeader("x-auth-token");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }


    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new OrdinalToEnumConverterFactory());
        super.addFormatters(registry);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MemberInterceptor())
                .addPathPatterns("/order/**", "/wallet/**", "/favor/**")
                .excludePathPatterns("/register/**",
                					 "/order/time_limit",
                					 "/order/mockaddydhdnskd",
                					 "/order/mockcurrentydhdnskd",
                					 "/order/mockcancelydhdnskd",
                                     "/contract-coin/add-coin/");
        super.addInterceptors(registry);
    }

    /**
     * 国际化
     *
     * @return
     */
    @Bean(name = "messageSource")
    public ResourceBundleMessageSource getMessageSource() {
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setDefaultEncoding("UTF-8");
        resourceBundleMessageSource.setBasenames("i18n/messages", "i18n/ValidationMessages");
        resourceBundleMessageSource.setCacheSeconds(3600);
        return resourceBundleMessageSource;
    }

}
