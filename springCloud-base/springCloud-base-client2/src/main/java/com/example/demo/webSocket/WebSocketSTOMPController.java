package com.example.demo.webSocket;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;
import sun.misc.BASE64Decoder;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Controller
public class WebSocketSTOMPController {

    @Autowired
    private WebSocketMessageBrokerStats webSocketMessageBrokerStats;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:MM:ss");

    /**
     * 初始化WebSocket日志，设置不显示日志
     */
    @PostConstruct
    public void init(){
        webSocketMessageBrokerStats.setLoggingPeriod(0);//毫秒
    }

    /**
     * 跳转topic页面
     */
    @RequestMapping("/webSocket/topic")
    public String getTopicPage(HttpServletRequest request){
        request.getSession().setAttribute("sessionId", request.getSession().getId());
        request.getSession().setAttribute("accountId", UUID.randomUUID().toString().replace("-", "").substring(16).toUpperCase());
        request.getSession().setAttribute("ip", HttpUtil.getIpAddr(request));
        return "webSocket-topic";
    }

    /**
     * topic controller
     */
    @MessageMapping("/topic/sendMsg")
    public void topicMsg(String json){
//        log.info("-------- msg ------- " + json);
        WebSocketTransportBean transportBean = JSONObject.parseObject(json, WebSocketTransportBean.class);
        Map<String, String> map = new HashMap<>();
        map.put("accountId", transportBean.getFromAccountId());
        map.put("ip", transportBean.getFromIp());
        map.put("nickName", transportBean.getFromNickName());
        map.put("time", sdf.format(new Date()));
        map.put("msg", transportBean.getMsg());
//        log.info("-------- map ------- " + JSONObject.toJSONString(map));
        simpMessagingTemplate.convertAndSend("/topic/getMsg", JSONObject.toJSONString(map));
    }

    /**
     * 验证窗口，chatId
     */
    @RequestMapping(value = "/webSocket/user/checkUser", method = RequestMethod.POST)
    @ResponseBody
    public String checkUser(@RequestBody String t, HttpServletRequest request){
//        log.info("-------- t = " + t);
        Map<String, String> map = new HashMap<>();
        try {
            map.put("code", "fail");

            //如果打开同一窗口，返回
            if(!StringUtils.isEmpty(t) && stringRedisTemplate.hasKey(t)) {
                map.put("msg", "已打开私聊窗口，请勿重复打开!");
                return JSONObject.toJSONString(map);
            }else{//缓存chatId
                String chatId = generateChatId(t);
//                log.info("------------------ chatId = " + chatId);
                if(StringUtils.isEmpty(chatId)){
                    map.put("msg", "验证信息失败!");
                    return JSONObject.toJSONString(map);
                }
                request.getSession().setAttribute(WebSocketConstants.CHAT_ID + "_" + chatId, t);
                stringRedisTemplate.opsForSet().add(t, sdf.format(new Date()));
                map.put("code", "success");
                map.put("msg", chatId);
                return JSONObject.toJSONString(map);
            }
        } catch (Exception e) {
            log.error("--- WebSocketSTOMPController checkUser error --- {}", e);
            map.put("msg", "验证信息失败!");
            return JSONObject.toJSONString(map);
        }
    }

    /**
     * 生成chatId
     */
    private String generateChatId(String t) throws Exception{
        String chatId = "";
        try {
            String json = new String(new BASE64Decoder().decodeBuffer(t));
            WebSocketTransportBean transportBean = JSONObject.parseObject(json, WebSocketTransportBean.class);

            SortedMap<String, String> map = new TreeMap<>();
            map.put(transportBean.getFromAccountId(), "fromAccountId");
            map.put(transportBean.getToAccountId(), "toAccountId");

            Set set = map.entrySet();
            Iterator it = set.iterator();
            while (it.hasNext()) {
                chatId += (String) ((Map.Entry) it.next()).getKey();
            }
            return chatId;

        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 跳转user聊天页面
     */
    @RequestMapping("/webSocket/user")
    public String getUserPage(HttpServletRequest request){
        try {
            String t = request.getParameter("t");
            String json = new String(new BASE64Decoder().decodeBuffer(t));
            WebSocketTransportBean transportBean = JSONObject.parseObject(json,WebSocketTransportBean.class);
            request.setAttribute("chatId",transportBean.getChatId());
            request.setAttribute("fromAccountId",transportBean.getFromAccountId());
            request.setAttribute("fromIp",transportBean.getFromIp());
            request.setAttribute("fromNickName",transportBean.getFromNickName());
            request.setAttribute("toAccountId",transportBean.getToAccountId());
            request.setAttribute("toIp",transportBean.getToIp());
            request.setAttribute("toNickName",transportBean.getToNickName());
        } catch (Exception e) {
            log.error("--- WebSocketSTOMPController getUserPage error --- {}", e);
            return "redirect:/page/error-page.html";
        }
        return "webSocket-user";
    }

    /**
     * user controller
     */
    @MessageMapping("/queue/sendMsg")
    public void userMsg(String json){
//        log.info("-------- msg ------- " + json);
        WebSocketTransportBean transportBean = JSONObject.parseObject(json, WebSocketTransportBean.class);
        if(!stringRedisTemplate.hasKey(WebSocketConstants.ACCOUNT_ID + "_" + transportBean.getToAccountId())){
            log.info("-------- testUserMsg 用户 [" + transportBean.getToAccountId() + "] 已断线，消息不推送 -------");
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("accountId", transportBean.getFromAccountId());
        map.put("ip", transportBean.getFromIp());
        map.put("nickName", transportBean.getFromNickName());
        map.put("time", sdf.format(new Date()));
        map.put("msg", transportBean.getMsg());
//        log.info("-------- map ------- " + JSONObject.toJSONString(map));
        simpMessagingTemplate.convertAndSendToUser(transportBean.getToAccountId(),"/queue/getMsg",JSONObject.toJSONString(map));
        if(!transportBean.getFromAccountId().equals(transportBean.getToAccountId())) //自己发给自己，只推送一次
            simpMessagingTemplate.convertAndSendToUser(transportBean.getFromAccountId(),"/queue/getMsg",JSONObject.toJSONString(map));
    }

}
