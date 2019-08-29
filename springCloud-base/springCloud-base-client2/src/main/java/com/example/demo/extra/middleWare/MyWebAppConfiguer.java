package com.example.demo.extra.middleWare;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * 执行顺序：
 *    Listener > Filter > Servlet(核心控制器) > Interceptor(Aspect) > Controller
 *    注意：Intercerpot是作用于Controller上面的，而不是Servlet。
 */
@Slf4j
@SpringBootApplication
@ServletComponentScan
public class MyWebAppConfiguer implements WebMvcConfigurer {

//    @Autowired
//    private TestListener testListener;
//    @Autowired
//    private TestFilter testFilter;
    @Autowired
    private TestInterceptor testInterceptor;
//    @Autowired
//    private TestServlet testServlet;

    /**
     * 测试注册自定义Listener
     */
//	@Bean
//	public ServletListenerRegistrationBean servletListenerRegistrationBean(){
//		return new ServletListenerRegistrationBean(testListener);
//	}

    /**
     * 测试注册自定义Filter
     */
//	@Bean
//	public FilterRegistrationBean filterRegistrationBean(){
//		log.info("---- filterRegistrationBean ---");
//		List<String> urls = new ArrayList<>();
//		urls.add("/servlet/*");
//
//		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
//		filterRegistrationBean.setFilter(testFilter);
//		filterRegistrationBean.setUrlPatterns(urls);
//		return filterRegistrationBean;
//	}

    /**
     * 测试注册自定义Servlet
     */
//	@Bean
//	public ServletRegistrationBean servletRegistrationBean(){
//		log.info("---- servletRegistrationBean ---");
//		return new ServletRegistrationBean(testServlet, "/servlet/*");
//	}

    /**
     * 测试自定义Interceptor
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(testInterceptor).addPathPatterns("/middleWare/*");
    }

}
