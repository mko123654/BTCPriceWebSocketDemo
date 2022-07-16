package com.abby.websocket.service;

import com.abby.websocket.controller.coin.CoinService;
import com.abby.websocket.model.CoinResult;
import com.abby.websocket.model.OutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate template;

    /**
     * server端推送比特幣價格資訊到client端
     */
    public void sendCoinInfo() {

        CoinResult coinResult = CoinService.getStockInfo();

        String msgTpl = "btc/usdt 目前價格： %s ; 本次API查詢扣點: %s; 剩餘API查詢額度: %s";
        CoinResult.Result  result = coinResult.getResult();
        CoinResult.Allowance allowance = coinResult.getAllowance();
        if (null != result) {
            String msg = String.format(msgTpl, result.getPrice(), allowance.getCost(), allowance.getRemaining());

            // 前端client端subscribe地址：/topic/coin_info
            template.convertAndSend("/topic/coin_info",new OutMessage(msg));
        }
    }
}