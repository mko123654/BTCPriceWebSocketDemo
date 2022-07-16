package com.abby.websocket.intecepter;

import java.util.Map;
import javax.servlet.http.HttpSession;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

/**
 * WebSocket握受請求的攔截器. 檢查握手request和response, 對WebSocketHandler傳遞屬性
 */
public class HttpHandShakeInterceptor implements HandshakeInterceptor{

	/**
	 * 在握手前執行該方法，持續握手回傳true;中斷握手回傳false 通過attributes參數設置WebSocketSession的屬性
	 * 這邊範例只記錄客戶端的sessionId (雖然也沒有要幹嘛...)
	 */
	@Override
	public boolean beforeHandshake(ServerHttpRequest request,
			ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		System.out.println("【握手之前執行的攔截器】beforeHandshake");

		if (request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest)request;
			HttpSession session =  servletRequest.getServletRequest().getSession();
			String sessionId = session.getId();
			System.out.println("【握手之前執行的攔截器】beforeHandshake sessionId="+sessionId);
			//將sessionId放入SessionAttributes中，
			attributes.put("sessionId", sessionId);
		}

		return true;
	}


	/**
	 * 在握手前執行該方法 (這個範例沒用到)
	 */
	@Override
	public void afterHandshake(ServerHttpRequest request,
			ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		System.out.println("【握手之後執行的攔截器】afterHandshake");

		if(request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest)request;
			HttpSession session =  servletRequest.getServletRequest().getSession();
			String sessionId = session.getId();
			System.out.println("【握手之後執行的攔截器】afterHandshake sessionId="+sessionId);
		}
	}
}
