package com.abby.websocket.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CoinResult {

    /**
     * 用於轉換API回傳json字串為Java物件的實體
     */
    @JsonProperty("result")
    private Result result;

    @JsonProperty("allowance")
    private Allowance allowance;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        /**
         * 價格
         */
        @JsonProperty("price")
        private String price;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Allowance {
        /**
         * 本次查詢扣點
         */
        @JsonProperty("cost")
        private String cost;
        /**
         * 剩餘扣點餘額
         */
        @JsonProperty("remaining")
        private String remaining;
        /**
         * API提供網站的無上限查詢限制升級提示訊息
         */
        @JsonProperty("upgrade")
        private String upgrade;
    }
}
