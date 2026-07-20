package com.booktrade.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    public InterceptorConfig(LoginInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/book", "/book/publish", "/book/edit/**", "/book/off/**",
                        "/book/my", "/order/**", "/admin/**",
                        "/profile", "/notifications", "/notifications/**")
                .excludePathPatterns("/", "/login", "/register", "/book/detail/**",
                        "/seller/**", "/css/**", "/js/**", "/uploads/**");
    }
}
