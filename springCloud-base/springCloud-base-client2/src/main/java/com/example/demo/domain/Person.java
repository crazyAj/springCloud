package com.example.demo.domain;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "person")
@PropertySource(value = {"classpath:props/my.properties", "file:${person.home}/my.properties"}, ignoreResourceNotFound = true)
public class Person {
    private String name;
    private String age;

    @Value("${test.props.inner}")
    private String inner;
    @Value("${test.props.outer}")
    private String outer;
}
