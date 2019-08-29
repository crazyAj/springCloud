package com.example.demo.api;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.domain.Example;
import com.example.demo.domain.Person;
import com.example.demo.extra.rabbitmq.RabbitmqProducer;
import com.example.demo.service.ExampleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/rest")
public class RestDemo {

    @Value("${first.name}")
    private String name;
    @Autowired
    private ExampleService exampleService;
    @Autowired
    private RabbitmqProducer rabbitmqProducer;
    @Autowired
    private Person person;

    /**
     * test 配置放jar同目录
     */
    @RequestMapping("/testOuterProps")
    @ResponseBody
    public String testOuterProps() {
        log.info("------ testOuterProps ------");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>> inner = " + person.getInner());
        System.out.println(">>>>>>>>>>>>>>>>>>>>>> outer = " + person.getOuter());
        System.out.println(">>>>>>>>>>>>>>>>>>>>>> name = " + person.getName());
        System.out.println(">>>>>>>>>>>>>>>>>>>>>> age = " + person.getAge());
        return person.getName();
    }

    /**
     * test Rabbitmq
     */
    @RequestMapping("/testRabbitmq")
    @ResponseBody
    public String testRabbitmq(HttpServletRequest request,
                               @Value("${rabbitmq.exchange.uniteExchange}") String exchage,
                               @Value("${rabbitmq.queue.uniteKey}") String routingKey) {
        String msg = request.getParameter("msg");
        log.info("--- RestDemo --- testRabbitmq ------- exchage = " + exchage + " --- routingKey = " + routingKey + " --- msg = " + msg);
        rabbitmqProducer.sendRabbitmqMessage(exchage, routingKey, msg);
        return "SUCCESS";
    }

    /**
     * test Hikari Transaction
     */
    @RequestMapping("/testHikariTransaction")
    @ResponseBody
    public String testHikariTransaction() {
        exampleService.testTransaction();
        return "SUCCESS";
    }

    /**
     * test Druid Transaction
     */
    @RequestMapping("/testDruidTransaction")
    @ResponseBody
    public String testDruidTransaction() {
        exampleService.testTransactionDruid();
        return "SUCCESS";
    }

    /**
     * test Druid2 Transaction
     */
    @RequestMapping("/testDruid2Transaction")
    @ResponseBody
    public String testDruid2Transaction() {
        exampleService.testTransactionDruid2();
        return "SUCCESS";
    }

    /**
     * test Mix Transaction
     * jta：Java Transaction API，即是java中对事务处理的api 即 api即是接口的意思
     * atomikos：Atomikos TransactionsEssentials 是一个为Java平台提供增值服务的并且开源类事务管理器
     * HikariDS不支持JTA，Druid支持JTA
     * <p>
     * 1. (未添加jta-atomikos分布式事务管理)全局性事务，只能回滚事务配置的sql，未在配置下的并不进行回滚
     * 第一个更新正常，第二个更新正常，异常后，第一个不会滚，第二个回滚
     * <p>
     * 2. 将事务交给atomikos分布式事务管理器管理，可以正常管理事务
     * 第一个更新正常，第二个更新正常，异常，第一个回滚，第二个回滚
     */
    @RequestMapping("/testMixTransaction")
    @ResponseBody
    public String testMixTransaction() {
        exampleService.testTransactionMix();
        return "SUCCESS";
    }

    @RequestMapping("/testAtomikosTransaction")
    @ResponseBody
    public String testAtomikosTransaction() {
        exampleService.testTransactionAtomikos();
        return "SUCCESS";
    }


    /**
     * test HikariCP
     */
    @RequestMapping("/testHikari")
    @ResponseBody
    public String testHikari() {
        List<Example> examples = exampleService.findAll();
        log.info("------- findAll() ------- " + JSONObject.toJSONString(examples));
        log.info("------- examples.get(0) ------- " + examples.get(0));

        List<Example> examps = exampleService.find(examples.get(0));
        log.info("------- find() ------- " + JSONObject.toJSONString(examps));

        Example example = exampleService.getByPrimaryKey(examples.get(0).getId());
        log.info("------- getByPrimaryKey() ------- " + example);

        Integer count = exampleService.getCount(examples.get(0));
        log.info("------- getCount() ------- " + count);

        Example ex = new Example();
        ex.setId("123");
        ex.setEmpKey("key123");
        ex.setEmpValue("val123");
        exampleService.insertSelective(ex);

        ex.setEmpKey("update123");
        exampleService.updateById(ex);

        exampleService.deleteById("2");

        Example e = new Example();
        e.setId("4");
        exampleService.delete(e);

        return "SUCCESS";
    }

    /**
     * test druid
     */
    @RequestMapping("/testDruid")
    @ResponseBody
    public String testDruid() {
        List<Example> examples = exampleService.findAllDruid();
        log.info("------- findAll() ------- " + JSONObject.toJSONString(examples));
        log.info("------- examples.get(0) ------- " + examples.get(0));

        List<Example> examps = exampleService.findDruid(examples.get(0));
        log.info("------- find() ------- " + JSONObject.toJSONString(examps));

        Example example = exampleService.getByPrimaryKeyDruid(examples.get(0).getId());
        log.info("------- getByPrimaryKey() ------- " + example);

        Integer count = exampleService.getCountDruid(examples.get(0));
        log.info("------- getCount() ------- " + count);

        Example ex = new Example();
        ex.setId("123");
        ex.setEmpKey("key123");
        ex.setEmpValue("val123");
        exampleService.insertSelectiveDruid(ex);

        ex.setEmpKey("update123");
        exampleService.updateByIdDruid(ex);

        exampleService.deleteByIdDruid("2");

        Example e = new Example();
        e.setId("4");
        exampleService.deleteDruid(e);

        return "SUCCESS";
    }

    /**
     * test druid2
     */
    @RequestMapping("/testDruid2")
    @ResponseBody
    public String testDruid2() {
        List<Example> examples = exampleService.findAllDruid2();
        log.info("------- findAll() ------- " + JSONObject.toJSONString(examples));
        log.info("------- examples.get(0) ------- " + examples.get(0));

        List<Example> examps = exampleService.findDruid2(examples.get(0));
        log.info("------- find() ------- " + JSONObject.toJSONString(examps));

        Example example = exampleService.getByPrimaryKeyDruid2(examples.get(0).getId());
        log.info("------- getByPrimaryKey() ------- " + example);

        Integer count = exampleService.getCountDruid2(examples.get(0));
        log.info("------- getCount() ------- " + count);

        Example ex = new Example();
        ex.setId("123");
        ex.setEmpKey("key123");
        ex.setEmpValue("val123");
        exampleService.insertSelectiveDruid2(ex);

        ex.setEmpKey("update123");
        exampleService.updateByIdDruid2(ex);

        exampleService.deleteByIdDruid2("2");

        Example e = new Example();
        e.setId("4");
        exampleService.deleteDruid2(e);

        return "SUCCESS";
    }

    /**
     * test redirect
     */
    @RequestMapping("/testRedirect")
    public String testRedirect(HttpServletRequest request, HttpServletResponse response) {
        try {
            String s = null;
            s.length();
            String path = "/page/test.html";
            log.info("----- path ----- " + path);
            return "redirect:" + path;
        } catch (Exception e) {
            String path = "/page/error-page.html";
            log.info("----- path ----- " + path);
            return "redirect:" + path;
        }
    }

    /**
     * test redirect
     */
    @RequestMapping("/testDispatcher")
    public String testDispatcher(HttpServletRequest request, HttpServletResponse response) {
        String path = request.getContextPath() + "/WEB-INF/jsp/test.jsp";
        log.info("----- path ----- " + path);
        request.setAttribute("name", name);
        return "test";
    }

    /**
     * test json
     */
    @RequestMapping(value = "/testJson", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public String testJson() {
        return "{\"json\":\"test\"}";
    }

    @RequestMapping("/testJsp")
    public String testJsp() {
        log.info("----- testJsp -----");
        return "index";
    }


    /**
     * test cookie
     */
    @RequestMapping("/addCookie")
    public String addCookie(HttpServletResponse response){
        Cookie cookie = new Cookie("spring-boot","test1234");
        cookie.setMaxAge(Integer.MAX_VALUE);
        response.addCookie(cookie);
        return "Add cookie success  &  k_v = " + "spring-boot : test1234";
    }
    @RequestMapping("/getCookie")
    public String getCookie(HttpServletRequest request){
        for (Cookie cookie : request.getCookies()) {
            System.out.println(cookie.getPath()+" - "+cookie.getName()+" - "+cookie.getValue());
        }
        return "Get cookie success !";
    }

}
