package com.company.eam.infrastructure.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SaTokenConfig {

    @Bean
    public OncePerRequestFilter SaTokenFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                           HttpServletResponse response,
                                           FilterChain chain) throws ServletException, IOException {
                String uri = request.getRequestURI();
                // 放行路径
                if (uri.startsWith("/api/auth") ||
                        uri.startsWith("/doc.html") ||
                        uri.startsWith("/webjars") ||
                        uri.startsWith("/swagger-resources") ||
                        uri.startsWith("/v3/api-docs") ||
                        uri.startsWith("/ws/notifications")) {
                    chain.doFilter(request, response);
                    return;
                }
                // 验证登录
                StpUtil.checkLogin();
                // 将token注入到 SaHolder，便于后续获取
                String token = request.getHeader("Authorization");
                if (token != null && token.startsWith("Bearer ")) {
                    SaHolder.getStorage().set("token", token.replace("Bearer ", ""));
                }
                chain.doFilter(request, response);
            }
        };
    }
}
