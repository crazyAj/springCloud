package com.example.demo;

import com.example.demo.domain.Person;
import com.example.demo.utils.HttpUtil;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @EnableRabbit 支持rabbitmq消息队列
 * @EnableTransactionManagement 作用和<tx:annotation-driven/>作用一样  true CGLIB代理  false JDK代理
 * 如果mybatis中service实现类中加入事务注解，需要此处添加该注解
 * 开启后，spring会扫描@Transactional的类和方法
 * @EnableEurekaClient 开启Eureka服务注册
 */
@Slf4j
@RestController
@RequestMapping("/app")
//@EnableRabbit
@EnableTransactionManagement(proxyTargetClass = true)
@ComponentScan(value = "com.example.demo")
@EnableEurekaClient
@SpringBootApplication
public class BaseZeroApplication extends SpringBootServletInitializer {

    @Value("${server.port}")
    private String serverPort;
    //	@Autowired
//	private CommandService commandService;
    @Value("${first.name}")
    private String name;
    @Autowired
    private Person person;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    // 过滤高频ip
    private static final Map<String, Date> ipFilter = new ConcurrentHashMap<>();
    //自定义日志
    private final static Logger logger = LoggerFactory.getLogger("self-define");
    //RestTemplate REST请求模板
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    /**
     * 三种jar启动
     */
    public static void main(String[] args) {
//		SpringApplication.run(Application.class, args);//默认开启banner

//		SpringApplication app = new SpringApplication(Application.class);
//		app.setBannerMode(Banner.Mode.OFF);//关闭banner
//		app.run(args);

        new SpringApplicationBuilder().bannerMode(Banner.Mode.CONSOLE).sources(BaseZeroApplication.class).run(args);//关闭banner
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
        return builder.bannerMode(Banner.Mode.CONSOLE).sources(BaseZeroApplication.class);
    }

    /**
     * 设置log配置文件
     */
    private void setLogConfig() {
        InputStream in = null;
        try {
            in = BaseZeroApplication.class.getResourceAsStream("/application.properties");
            Properties props = new Properties();
            props.load(in);
//			log.info("---------- env = " + props.get("spring.profiles.active"));
//			log.info("----- log.config.location = " + props.get("log.config.location"));
            String logConfig = StringUtils.isEmpty(props.get("log.config.location")) ?
                    "logback-" + props.get("spring.profiles.active") + ".xml" :
                    props.get("log.config.location") + "/logback-" + props.get("spring.profiles.active") + ".xml";
//			log.info("------ logConfig ------- " + logConfig);
            String path = BaseZeroApplication.class.getResource("/").getPath() + logConfig;
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
     * 测试filebeat
     */
    @RequestMapping("testFilebeat")
    @ResponseBody
    public Object testFilebeat(HttpServletRequest request) {
        String ipAddr = HttpUtil.getIpAddr(request);
        if (StringUtils.isEmpty(ipAddr))
            return "客户端环境异常，请稍后重试！";

        if (ipFilter.containsKey(ipAddr)) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, -1);
            Date before = ipFilter.get(ipAddr);
            if (cal.getTimeInMillis() <= before.getTime()) {
                return "访问太过频繁，请稍后重试！";
            }
        }
        ipFilter.put(ipAddr, new Date());

        String msg = request.getParameter("msg");
        logger.info("--- test info [" + ipAddr + "] --- {}", msg);
        return msg;
    }

    /**
     * 测试ELK
     */
    @RequestMapping("testELK")
    @ResponseBody
    public Object testELK(HttpServletRequest request) {
        String ipAddr = HttpUtil.getIpAddr(request);
        if (StringUtils.isEmpty(ipAddr))
            return "客户端环境异常，请稍后重试！";

        if (ipFilter.containsKey(ipAddr)) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, -1);
            Date before = ipFilter.get(ipAddr);
            if (cal.getTimeInMillis() <= before.getTime()) {
                return "访问太过频繁，请稍后重试！";
            }
        }
        ipFilter.put(ipAddr, new Date());

        String msg = request.getParameter("msg");
        log.info("--- test info [" + ipAddr + "] --- {}", msg);
        return msg;
    }

    /**
     * 注册 RestTemplate Ribbon
     * 开启 RestTemplate 负载均衡
     */
    @Bean
    @LoadBalanced //开启负载均衡器
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * 修改默认 Ribbon(均衡server) 方式
     * 此处为 随机server 策略
     */
//    @Bean
//    public IRule irule(){
//        return new RandomRule();
//    }

    /**
     * 测试 Ribbon 和 RestTemplate
     */
    @RequestMapping("testRibbon")
    @ResponseBody
    public Object testRibbon() {
        String eurekaInstance = "springCloud-base-client";
        String eurekaUrl = "http://" + eurekaInstance + "/app/testRedis";
        log.info("--- testRibbon start --- eurekaUrl = " + eurekaUrl);
        Object result = restTemplate.getForObject(eurekaUrl, String.class);
        log.info("--- testRibbon end --- result = " + result);
        return result;
    }
    /**
     * 测试 Ribbon 和 RestTemplate
     */
    @RequestMapping("testRibbonInstance")
    public void testRibbonInstance() {
        String eurekaInstance = "springCloud-base-client";
        log.info("--- testRibbonInstance --- eurekaInstance = " + eurekaInstance);

        ServiceInstance serviceInstance = loadBalancerClient.choose(eurekaInstance);
        log.info("--- testRibbonInstance --- info = " + serviceInstance.getHost()+":"+serviceInstance.getPort());
    }

    /**
     * 测试redis
     */
    @RequestMapping("testRedis")
    @ResponseBody
    public Object testRedis() {
        stringRedisTemplate.opsForValue().set("testKey", "success");
        Object result = stringRedisTemplate.opsForValue().get("testKey");
        System.out.println("testKey = " + result);
        return result;
    }

    /**
     * 测试获取自定义属性
     */
    @RequestMapping("/testProps")
    public String testProps() {
        log.info("---------- name = " + name);
        log.info("---------- person = " + person.toString());
        return "name = " + name + ", person" + person.toString();
    }

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
