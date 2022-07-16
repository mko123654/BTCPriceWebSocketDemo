package com.abby.websocket.intecepter;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;


/**
 * channel的攔截器
 */
public class SocketChannelInterceptor extends ChannelInterceptorAdapter {

    /**
     * 在訊息被實際發送到channel前執行
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        System.out.println("SocketChannelInterceptor -> preSend");

        return super.preSend(message, channel);
    }

    /**
     * 在訊息被送到channel後立即執行
     */
    @Override
    public void postSend(Message<?> message, MessageChannel channel,
                         boolean sent) {
        System.out.println("SocketChannelInterceptor -> postSend");

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message); //消息的Header

        if (headerAccessor.getCommand() == null) return;// 避免非STOMP消息類型，例如心跳檢測

        String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();
        System.out.println("SocketChannelInterceptor -> sessionId = " + sessionId);

        switch (headerAccessor.getCommand()) {
            case CONNECT:
                connect(sessionId);
                break;
            case DISCONNECT:
                disconnect(sessionId);
                break;
            case SUBSCRIBE:
                break;

            case UNSUBSCRIBE:
                break;
            default:
                break;
        }
    }

    /**
     * 訊息被實際發送到channel後執行 (常用於清除資源)
     */
    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel,
                                    boolean sent, Exception ex) {
        System.out.println("SocketChannelInterceptor -> afterSendCompletion");
        super.afterSendCompletion(message, channel, sent, ex);
    }




    /**
     * 新建連接
     */
    private void connect(String sessionId) {
        System.out.println("connect sessionId=" + sessionId);
    }

    /**
     * 中斷連接
     */
    private void disconnect(String sessionId) {
        System.out.println("disconnect sessionId=" + sessionId);
    }

}
