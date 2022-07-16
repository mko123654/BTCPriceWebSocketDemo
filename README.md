# **[JAVA] Spring boot + WebSocket STOMP 批次介接比特幣價格API推播到前端畫面**

###### tags: `Java` `Spring Boot` `WebSocket`


## [實作目的] Server端介接Cryptowatch API，定時批次取得比特幣即時價格，並推播到前端畫面。

程式碼傳送門：[GitHub](https://github.com/mko123654/BTCPriceWebSocketDemo)
啟動程式後，瀏覽器連上`localhost:8080`即可

## 功能展示
因為Cryptowatch API沒有付費的話，會有介接的使用次數限制。因此也將該資訊於前端展示，並且將批次設定為5秒執行一次以減少quota的使用。
![](https://i.imgur.com/QgONiJu.png)
幾秒後...的畫面
![](https://i.imgur.com/NTiwFhf.png)


## 前端程式碼

* ### **pom.xml**

這邊主要使用到的依賴有：
1. Spring Boot
2. webSocket
3. webjars (用於打包前端靜態資源成jar檔)
4. httpClient
```pom=
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>com.abby.websocket</groupId>
	<artifactId>BTC-price-websocket-demo</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>BTC-price-websocket-demo</name>
	<description>Demo project for WebSocket</description>

	<!-- 新版Spring Boot會和webjars不相容，暫不處理引用舊版-->
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.1.0.BUILD-SNAPSHOT</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>

	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
		<java.version>11</java.version>
	</properties>

	<dependencies>
		<!-- websocket -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-websocket</artifactId>
		</dependency>

		<!--將web前端資源（如jQuery & Bootstrap）打成jar包文件-->
		<dependency>
			<groupId>org.webjars</groupId>
			<artifactId>webjars-locator</artifactId>
			<version>0.32-1</version>
		</dependency>
		<dependency>
			<groupId>org.webjars</groupId>
			<artifactId>sockjs-client</artifactId>
			<version>1.0.2</version>
		</dependency>
		<dependency>
			<groupId>org.webjars</groupId>
			<artifactId>stomp-websocket</artifactId>
			<version>2.3.3</version>
		</dependency>
		<dependency>
			<groupId>org.webjars</groupId>
			<artifactId>bootstrap</artifactId>
			<version>3.3.7</version>
		</dependency>
		<dependency>
			<groupId>org.webjars</groupId>
			<artifactId>jquery</artifactId>
			<version>3.1.0</version>
		</dependency>

		<!-- lombok -->
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
		</dependency>

		<!-- httpClient -->
		<dependency>
			<groupId>org.apache.httpcomponents</groupId>
			<artifactId>httpclient</artifactId>
		</dependency>
		<dependency>
			<groupId>org.apache.httpcomponents</groupId>
			<artifactId>httpcore</artifactId>
		</dependency>
		<dependency>
			<groupId>commons-lang</groupId>
			<artifactId>commons-lang</artifactId>
			<version>2.6</version>
		</dependency>

		<dependency>
			<groupId>com.squareup.okhttp3</groupId>
			<artifactId>okhttp</artifactId>
			<version>3.8.1</version>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>

```

* ### **WebSocketConfig.java**
後端WebSocket功能相關的部分。

`@EnableWebSocketMessageBroker` 用於開啟使用STOMP協議來傳輸基於代理（MessageBroker）的消息，這時候controller開始支援websocket的`@MessageMapping`，就像是使用RESTful的`@requestMapping`那樣。

至於`HttpHandShakeInterceptor`、`SocketChannelInterceptor`在這次的範例中不是重點這邊不贅述
，gitHub上的原始碼我有寫了相關註解，有需要再看看就好。
```java=

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    /**
     * 註冊client端連進Server端之路徑
     * 並使用攔截器設定可以連進來的來源位置(這邊範例使用 "*" 意思是都不擋)
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/endpoint-websocket").addInterceptors(new HttpHandShakeInterceptor()).setAllowedOrigins("*").withSockJS();
    }

    /**
     * 配置訊息代理
     * enableSimpleBroker： server端推送給client端的路徑prefix
     * setApplicationDestinationPrefixes： client端發送給server端的路徑prefix
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");

    }

    /**
     * 註冊由client端傳到server端的攔截器
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new SocketChannelInterceptor());
    }

    /**
     * 註冊由server端傳到client端的攔截器
     */
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(new SocketChannelInterceptor());
    }
}

```

* ### **CoinResult.java**

CoinResult是用於將API回傳json字串，轉換為Java物件的承接DTO

這邊介接的API為Cryotiwatcgh網站提供的幣安交易所BTC/USDT即時價格：https://api.cryptowat.ch/markets/binance/btcusdt/price

該API回傳的資料格式如下：
![](https://i.imgur.com/NomdsTc.png)

`@Data`是依賴套件`Lombok`內的annotation，使用之後等於自動寫了constroctor、getter、setter、並覆寫toString、equals、hashCode，可以快速地幫我們建立好entity。

【Lombok詳細介紹推薦這篇文章】：[Java - 五分鐘學會 Lombok 用法](https://https://kucw.github.io/blog/2020/3/java-lombok/)

```java=
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

```

* ### **OutMessage.java**

`OutMessage`是server端向client端發送的物件，這裡會將從API裡拿到的`CoinResult`資訊，在service層處理為顯示給前端畫面使用者看文字內容、發送來源、時間。

```java=
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutMessage {

    private String from;

    private String content;

    private Date time = new Date();


    public OutMessage(String content) {
        this.content = content;
    }
}

```


* ### **CoinService.java**

`CoinService`實作了打到Cryptowath的API去獲取目前BTC/USDT的價格資料，並轉為`CoinResult`物件。
```java=

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

```
* ### **CoinSchedule.java**

Spring內建的批次作業功能，使用`@Scheduled`註解並設定時間即會定時執行該方法。
記得要在啟動類別加上`@EnableScheduling`才會生效。
```java=

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

```

## 前端程式碼：

* ### **index.html**

前端畫面。

```html=
<!DOCTYPE html>
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<head>
    <title>Hello WebSocket</title>
    <link href="/webjars/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="/main.css" rel="stylesheet">
    <script src="/webjars/jquery/jquery.min.js"></script>
    <script src="/webjars/sockjs-client/sockjs.min.js"></script>
    <script src="/webjars/stomp-websocket/stomp.min.js"></script>
    <script src="/app.js"></script>
</head>
<body>
<noscript><h2 style="color: #ff0000">Seems your browser doesn't support Javascript! Websocket relies on Javascript being
    enabled. Please enable
    Javascript and reload this page!</h2></noscript>
<div id="main-content" class="container">
    <div class="row">
        <div class="col-md-6">
            <form class="form-inline">
                <div class="form-group">
                    <label for="connect">向Server端建立WebSocket連結</label>
                    <button id="connect" class="btn btn-default" type="submit">Connect</button>
                    <button id="disconnect" class="btn btn-default" type="submit" disabled="disabled">Disconnect
                    </button>
                </div>
            </form>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <table id="conversation" class="table table-striped">
                <thead>
                <tr>
                    <th>價格資訊</th>
                </tr>
                </thead>
                <tbody id="notice">
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>

```

* ### **app.js**

前端的js。
`connect()`中的`var socket = new SockJS('/endpoint-websocket');`就是對應到在server端`WebSocketConfig.java`內`registerStompEndpoints`註冊的路徑。

第21~23行就是指該client端要訂閱來自server端發送的哪些內容 (本範例的server端只有發送一個`coin_info`主題，但實際上可以有很多個)，對應到後端`WebSocketService`的`template.convertAndSend("/topic/coin_info",new OutMessage(msg));`這行。



```javascript=
var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#notice").html("");
}

function connect() {
    var socket = new SockJS('/endpoint-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/coin_info', function (result) {
        	showContent(JSON.parse(result.body));
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function showContent(body) {
    $("#notice").html("<tr><td>" + body.content + "</td> <td>"+new Date(body.time).toLocaleString()+"</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
});

```

以上就是這個Demo的解說。
程式碼傳送門：[GitHub](https://github.com/mko123654/BTCPriceWebSocketDemo)


