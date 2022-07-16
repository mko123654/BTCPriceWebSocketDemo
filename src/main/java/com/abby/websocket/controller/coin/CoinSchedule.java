package com.abby.websocket.controller.coin;

import com.abby.websocket.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * 批次作業
 *
 */
@Component
public class CoinSchedule {

    @Autowired
    private WebSocketService ws;

    /**
     * 每5秒一次，推送給client端
     */
    @Scheduled(cron = "*/5 * * * * ?")
    public void coinInfo() {
        ws.sendCoinInfo();
    }
}
