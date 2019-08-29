package com.example.demo.extra.middleWare;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@Slf4j
@Component
@WebListener
public class TestListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        log.info("----- listener init -----");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        log.info("----- listener destroy -----");
    }
}
