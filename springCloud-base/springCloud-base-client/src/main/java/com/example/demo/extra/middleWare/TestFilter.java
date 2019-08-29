package com.example.demo.extra.middleWare;

import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

@Slf4j
@Component
@WebFilter(urlPatterns = "/middleWare/*")
public class TestFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("----- filter init -----");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("----- filter doFilter ----- path = " + ((HttpServletRequest)servletRequest).getRequestURL());

        /**
         * filter 将 request 重新进行包裹
         */
        log.info("----- filter request wrapper -- Start ----- " + servletRequest.getServerName());
        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper((HttpServletRequest) servletRequest){
            @Override
            public String getServerName() {
                String serverName = super.getServerName();
                if(StringUtils.isNotEmpty(serverName) && serverName.startsWith("192.168"))
                    return "localhost";
                return serverName;
            }
        };
        log.info("----- filter request wrapper -- End ----- " + requestWrapper.getServerName());

        filterChain.doFilter(requestWrapper, servletResponse);
    }

    @Override
    public void destroy() {
        log.info("----- filter destory -----");
    }
}
