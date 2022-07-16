package com.abby.websocket.controller.coin;

import java.util.HashMap;
import java.util.Map;

import com.abby.websocket.model.CoinResult;
import com.abby.websocket.utils.HttpUtils;
import com.abby.websocket.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;


/**
 * 打到cryptowat的API去獲取目前btc/usdt的價格資料
 */
@Slf4j
public class CoinService {

    public static CoinResult getStockInfo() {
        String host = "https://api.cryptowat.ch";
        String path = "/markets/binance/btcusdt/price";
        String method = "GET";
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> querys = new HashMap<String, String>();

        try {
            //GET方式打過去
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            //將response的body轉為字串
            String responseText = EntityUtils.toString(response.getEntity());
            //將json格式的字串轉成Java的物件
            CoinResult coinResult = JsonUtils.objectFromJson(responseText, CoinResult.class);
            log.info("console印出API回傳資訊=======================================");
            System.out.println(coinResult.toString());
            return coinResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
