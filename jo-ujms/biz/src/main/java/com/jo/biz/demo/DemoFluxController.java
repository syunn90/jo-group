package com.jo.biz.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 异步响应接口
 * @author xtc
 * @date 2024/2/5
 */
@RestController
@RequestMapping("/sse/c")
@Slf4j
public class DemoFluxController {
//
//    private String s;
//
//    @Autowired
//    SubscribableChannel channel;
//
//    @GetMapping("/flux/async/resp")
//    public Flux<ServerSentEvent<Integer>> getMenuTree() {
//        Flux<ServerSentEvent<Integer>> random = Flux.interval(Duration.ofSeconds(5))
//                .map(seq -> Tuples.of(seq, ThreadLocalRandom.current().nextInt()))
//                .map(data -> ServerSentEvent.<Integer>builder()
//                        .event("random")
//                        .id(Long.toString(data.getT1()))
//                        .data(data.getT2())
//                        .build()).doOnError( throwable -> {});
//        return random;
//    }
//
//    @Bean
//    SubscribableChannel channel(){
//        return MessageChannels.publishSubscribe().get();
//    }
//
//    @Bean
//    IntegrationFlow integrationFLow(){
//        return IntegrationFlow.from("channel")
//                .transform(String.class)
//                .channel(channel)
//                .get();
//    }
//
//    @GetMapping("/flux/demo")
//    public Flux<String> demo() {
//        return Flux.create(sink -> {
//            FluxSink<String> serialize = sink;
//            MessageHandler handler = msg -> sink.next((String) msg.getPayload());
//            serialize.onCancel(() -> {
//                channel.unsubscribe(handler);
//            });
//            channel.subscribe(handler);
//        });
//
//    }
private static Map<String, SseEmitter> sseCache = new ConcurrentHashMap<>();
    @GetMapping(path = "subscribe", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter push(String id) throws IOException {
        // 超时时间设置为3s，用于演示客户端自动重连
        SseEmitter sseEmitter = new SseEmitter();
        // 设置前端的重试时间为1s
        sseEmitter.send(SseEmitter.event().reconnectTime(1000).data("连接成功"));
        sseCache.put(id, sseEmitter);
        System.out.println("add " + id);
        sseEmitter.onTimeout(() -> {
            System.out.println(id + "超时");
            sseCache.remove(id);
        });
        sseEmitter.onCompletion(() -> System.out.println("完成！！！"));
        return sseEmitter;
    }

    @GetMapping(path = "push")
    public String push(String id, String content) throws IOException {
        SseEmitter sseEmitter = sseCache.get(id);
        if (sseEmitter != null) {
            sseEmitter.send(SseEmitter.event().name("msg").data("message from service：" + content));
        }
        return "over";
    }

    @GetMapping(path = "over")
    public String over(String id) {
        SseEmitter sseEmitter = sseCache.get(id);
        if (sseEmitter != null) {
            sseEmitter.complete();
            sseCache.remove(id);
        }
        return "over";
    }
}
