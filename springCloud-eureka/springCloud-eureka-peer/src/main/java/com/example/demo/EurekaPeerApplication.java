package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

@Slf4j
@EnableEurekaServer
@SpringBootApplication
public class EurekaPeerApplication extends SpringBootServletInitializer {

    /**
     * 三种jar启动
     */
    public static void main(String[] args) {
//		SpringApplication.run(Application.class, args);//默认开启banner

//		SpringApplication app = new SpringApplication(Application.class);
//		app.setBannerMode(Banner.Mode.OFF);//关闭banner
//		app.run(args);

        new SpringApplicationBuilder().bannerMode(Banner.Mode.CONSOLE).sources(EurekaPeerApplication.class).run(args);//关闭banner
    }

    /**
     * war包启动
     * 这里主要是指向原先用main方法执行的Application启动类
     * Banner.Mode.OFF 关
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        //手动切换日志目录(由于外部Tomcat配置会覆盖，application.properties里面的配置，所有得代码设置环境)
        //自定义属性log.config.location设置文件路径
        //logback-{profile}.xml设置日志配置
        setLogConfig();
        return builder.bannerMode(Banner.Mode.CONSOLE).sources(EurekaPeerApplication.class);
    }

    /**
     * 设置log配置文件
     */
    private void setLogConfig() {
        InputStream in = null;
        try {
            in = EurekaPeerApplication.class.getResourceAsStream("/application.properties");
            Properties props = new Properties();
            props.load(in);
//			log.info("---------- env = " + props.get("spring.profiles.active"));
//			log.info("----- log.config.location = " + props.get("log.config.location"));
            String logConfig = StringUtils.isEmpty(props.get("log.config.location")) ?
                    "logback-" + props.get("spring.profiles.active") + ".xml" :
                    props.get("log.config.location") + "/logback-" + props.get("spring.profiles.active") + ".xml";
//			log.info("------ logConfig ------- " + logConfig);
            String path = EurekaPeerApplication.class.getResource("/").getPath() + logConfig;
//			log.info("---------- path = " + path);
            if (new File(path).exists()) {
                System.setProperty("logging.config", "classpath:" + logConfig);
                log.info("------ Loading log config ------- " + logConfig);
            } else {
                log.info("------ log config " + logConfig + " no exists, loading default config -------");
            }
        } catch (Exception e) {
            log.error("Loading log config Exception:{}", e);
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                log.error("Loading log config Exception:{}", e);
            }
        }
    }

    /**
     * SpringBoot初始化，执行自定义方法
     *   1. 需要implements ApplicationContextInitializer
     *   2. 实现initialize方法
     *   3. 在resource下面添加META-INF/spring.factories
     *   	  内容：org.springframework.context.ApplicationContextInitializer=com.example.thread.Application
     */
//	@Override
//	public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
//		String profile = configurableApplicationContext.getEnvironment().getActiveProfiles()[0];
//		log.info("---------- evn --------- " + profile);
//	}

    /**
     * 类方式配置各个配置项，如果有配置文件以配置文件优先
     *   配了两个参数：ssl、server-port
     */
//	@Bean
//	public ConfigurableServletWebServerFactory configurableServletWebServerFactory(){
//		TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory(){
//			@Override
//			protected void postProcessContext(Context context) {
//				SecurityConstraint securityConstraint = new SecurityConstraint();
//				securityConstraint.setUserConstraint("CONFIDENTIAL");
//
//				SecurityCollection securityCollection = new SecurityCollection();
//				securityCollection.addPattern("/*");
//
//				securityConstraint.addCollection(securityCollection);
//				context.addConstraint(securityConstraint);
//			}
//		};
//
//		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//		connector.setScheme("http");
//		connector.setPort(8090);
//		connector.setSecure(false);
//        connector.setRedirectPort(Integer.parseInt(serverPort));
//
//		factory.addAdditionalTomcatConnectors(connector);
////		factory.setPort(8888);// Tomcat配置端口
//
//		return factory;
//	}

    /**
     * 测试SpringBoot运作原理
     */
//	@RequestMapping("/testBootCore")
//	public String testBootCore(){
//		String command = commandService.getCommand();
//		log.info("---------- print test info ---------- {}", command);
//		return command;
//	}


    /**
     * 测试读取banner
     */
    @RequestMapping("/protal")
    public String testProtal() {
        StringBuffer buf = new StringBuffer();
        InputStream in = null;
        InputStreamReader reader = null;
        try {
            String fileName = "/banner.txt";
            in = getClass().getResourceAsStream(fileName);
            reader = new InputStreamReader(in);
            int t;
            while ((t = reader.read()) != -1) {
                buf.append((char) t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return buf.toString();
    }

}
