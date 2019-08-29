package com.example.demo.webSocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootApplication
@EnableWebSocketMessageBroker
public class WebSocketSTOMPConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 配置消息代理：/topic 广播式
     *      发布信息路径 = setApplicationDestinationPrefixes() + @MessageMapping()
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //配置到 @MessageMapping Controller
        registry.setApplicationDestinationPrefixes("/webSocket");
        registry.enableSimpleBroker("/topic","/user");
    }

    /**
     * 注册STOMP的endpoint，并映射指定的URL，指定使用SockJS协议
     *      setAllowedOrigins("*") 解决跨域问题
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/endpointDemo")
                .addInterceptors(new HttpSessionHandshakeInterceptor(){
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                        if(request instanceof ServletServerHttpRequest) {
                            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                            attributes.put(WebSocketConstants.SESSION_ID, servletRequest.getServletRequest().getSession().getAttribute("sessionId"));
                            attributes.put(WebSocketConstants.ACCOUNT_ID, servletRequest.getServletRequest().getSession().getAttribute("accountId"));
                            attributes.put(WebSocketConstants.IP, servletRequest.getServletRequest().getSession().getAttribute("ip"));
                        }
                        return super.beforeHandshake(request, response, wsHandler, attributes);
                    }
                })
                .setAllowedOrigins("*")
                .withSockJS();
    }

    /**
     * 调用方法前    添加拦截器，进行业务判断
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor(){
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
//                log.info("-------------------- receive：" + message);
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if(accessor.getCommand() == null){
                    return message;
                }

                String accountId = (String) accessor.getSessionAttributes().get(WebSocketConstants.ACCOUNT_ID);
                String sessionId = (String)accessor.getSessionAttributes().get(WebSocketConstants.SESSION_ID);
                switch(accessor.getCommand()){
                    case CONNECT:
//                        log.info("---------- StompCommand.CONNECT -----------");
                        connect(accountId, sessionId);
                        break;
                    case CONNECTED:
//                        log.info("---------- StompCommand.CONNECTED -----------");
                        break;
                    case DISCONNECT:
                        disConnect(accessor, accountId);
//                        log.info("---------- StompCommand.DISCONNECT -----------");
                        break;
                    default:
                        break;
                }
                return message;
            }
        });
    }

    /**
     * 连接后，将用户信息放置到缓存中
     */
    private void connect(String accountId, String sessionId){
        redisTemplate.opsForSet().add(WebSocketConstants.ACCOUNT_ID + "_" + accountId, sessionId);
    }

    /**
     * 断开后，将用户信息从缓存中移除
     */
    private void disConnect(StompHeaderAccessor accessor, String accountId){
        Map<String, Object> map = accessor.getSessionAttributes();
        List<String> sendType = accessor.getNativeHeader("sendType");
        if(sendType != null && sendType.size()>0) {
            String type = sendType.get(0);
//            log.info("---------- type = " + type);
            if("topic".equals(type)){//注销用户
            log.info("-------------- accountId = " + accountId);
                map.remove(WebSocketConstants.SESSION_ID);
                map.remove(WebSocketConstants.ACCOUNT_ID);
                map.remove(WebSocketConstants.IP);

                if(redisTemplate.hasKey(WebSocketConstants.ACCOUNT_ID + "_" + accountId))
                    redisTemplate.opsForSet().pop(WebSocketConstants.ACCOUNT_ID + "_" + accountId);

            } else if("user".equals(type)) {//注销chatId
                List<String> chatIds = accessor.getNativeHeader("chatId");
                if(chatIds != null && chatIds.size()>0) {
                    String chatId = chatIds.get(0);
//                    log.info("-------------- chatId = " + chatId);
                    String t = (String) map.get(WebSocketConstants.CHAT_ID + "_" + chatId);
//                    log.info("-------------- t = " + t);

                    //删除redis聊天chatId
                    if (redisTemplate.hasKey(t))
                        redisTemplate.opsForSet().pop(t);
                    //删除session聊天chatId
                    map.remove(WebSocketConstants.CHAT_ID + "_" + chatId);
                }
            }

        }
    }

}

/**
 * 常量类
 */
class WebSocketConstants {
    private WebSocketConstants(){}
    public static final String SESSION_ID = "sessionId";
    public static final String ACCOUNT_ID = "accountId";
    public static final String CHAT_ID = "chatId";
    public static final String IP = "ip";
}
