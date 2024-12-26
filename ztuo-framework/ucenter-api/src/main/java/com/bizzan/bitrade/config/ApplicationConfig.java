package com.bizzan.bitrade.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.bizzan.bitrade.ext.OrdinalToEnumConverterFactory;
import com.bizzan.bitrade.interceptor.MemberInterceptor;

/**
 国际化支持：通过 ResourceBundleMessageSource 提供了国际化消息源，支持不同语言的消息。
 验证器配置：配置了 LocalValidatorFactoryBean，确保 Bean Validation 的错误消息能够使用国际化配置。
 静态资源处理：配置了静态资源处理，将请求路径 /asset/** 映射到 classpath:/asset/。
 格式化：注册了自定义的格式化转换器，用于将数字转换为枚举值。
 拦截器：注册了 MemberInterceptor，用于拦截特定的请求并排除一些路径，保证系统的安全性或业务逻辑。
 跨域配置：配置了 CORS 以允许所有源访问，同时暴露了 x-auth-token 响应头。
 */
@Configuration
public class ApplicationConfig  extends WebMvcConfigurerAdapter {


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

    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(getMessageSource());
        return validator;
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/asset/**").addResourceLocations("classpath:/asset/");
        super.addResourceHandlers(registry);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new OrdinalToEnumConverterFactory());
        super.addFormatters(registry);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MemberInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/register/**", "/mobile/code", "/login","/check/login","/start/captcha","/support/country",
                        "/ancillary/**","/announcement/**","/mobile/reset/code","/reset/email/code","/reset/login/password","/vote/info","/coin/supported","/financial/items/**","/coin/guess/index","/coin/guess/record"
                        ,"/coin/guess/detail"
                        ,"/coin/guess/type"
                        ,"/activity/page-query"
                        ,"/activity/detail"
                        ,"/promotion/toprank"
                        ,"/promotioncard/detail"
                        ,"/redenvelope/query"
                        ,"/redenvelope/query-detail"
                        ,"/redenvelope/receive"
                        ,"/redenvelope/code"
                        ,"/reg/email/code");
        super.addInterceptors(registry);
    }

    @Bean
    public FilterRegistrationBean corsFilterForBusi() {
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

}
