package com.jq.config;

import com.jq.interceptor.LoginInterceptor;
import com.jq.interceptor.RefreshTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.Resource;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登录拦截器：放行无需登录访问的接口和静态资源
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(
                        // 登录相关接口
                        "/user/login",
                        "/user/code",
                        // 业务上对外开放的接口
                        "/upload/**",
                        "/voucher/**",
                        "/shop/**",
                        "/shop-type/**",
                        "/blog/hot",
                        "/blog/of/user",
                        "/blog/likes/*",
                        "/blog/*",
                        // 静态资源与首页
                        "/",
                        "/index.html",
                        "/static/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/webjars/**"
                )
                .order(1);
        //token刷新的拦截器
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate))
                .addPathPatterns("/**")
                .excludePathPatterns("/user/code") // 👇 加上这一行
                .order(0);
    }
}
