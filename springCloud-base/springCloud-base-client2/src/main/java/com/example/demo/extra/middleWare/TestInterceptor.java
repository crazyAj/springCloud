package com.example.demo.extra.middleWare;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 拦截器，不能修改请求和响应
 */
@Slf4j
@RestController
public class TestInterceptor implements HandlerInterceptor {

    /**
     * test json
     */
    @RequestMapping(value="/middleWare/test", produces={MediaType.TEXT_PLAIN_VALUE})
    public String testInterceptor(){
        log.info("------ This is a interceptor. ------");
        return "This is a interceptor.";
    }

    /**
     * 调用controller之前执行
     * 如果返回false或发生异常，则请求中断
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("------ preHandle ------");
        JSONObject json = new JSONObject();
        /*
         *  {
         *      "request": {
         *          "params": {
         *              "key": "111"
         *          },
         *          "uri": "/thread/interceptor/test"
         *      },
         *      "class": {
         *          "className": "org.springframework.web.method.HandlerMethod",
         *          "methodName": "testInterceptor",
         *          "parameterTypes": [],
         *          "returnType": "java.lang.String"
         *      }
         *  }
         */
        Map<String, Object> req = new HashMap<>();
        //请求地址
        String uri = request.getRequestURI();
        req.put("uri", uri);

        //请求参数
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> reqMap = new HashMap<>();
        for (String key : parameterMap.keySet()) {
            reqMap.put(key, parameterMap.get(key)[0]);
        }
        req.put("params", reqMap);
        json.put("request", req);

        Map<String, Object> map = new TreeMap<>();
        //调用类名
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        String className = handlerMethod.getClass().getName();
        map.put("className", className);
        //调用方法
        Method method = handlerMethod.getMethod();
        Class returnType = method.getReturnType();
        map.put("returnType", returnType);
        String methodName = method.getName();
        map.put("methodName", methodName);
        Class[] parameterTypes = method.getParameterTypes();
        map.put("parameterTypes", parameterTypes);
        json.put("class", map);
        log.info("------ preHandle ------ " + json.toJSONString());
        return true;
    }

    /**
     * 调用contoller之后视图渲染完成之前
     * 如果controller层中的方法抛出了异常，就会不执行方法了
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        log.info("------ postHandle ------");
    }

    /**
     * 视图渲染完成之后
     * 只有preHandle返回false或preHandle抛出异常才不会执行该方法
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        log.info("----- afterCompletion -----");
    }

}
