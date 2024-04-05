//package com.jo.biz.demo;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.http.codec.ServerSentEvent;
//import org.springframework.integration.dsl.IntegrationFlow;
//import org.springframework.integration.dsl.MessageChannels;
//import org.springframework.messaging.MessageHandler;
//import org.springframework.messaging.SubscribableChannel;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.FluxSink;
//import reactor.util.function.Tuples;
//
//import java.time.Duration;
//import java.util.concurrent.ThreadLocalRandom;
//
///**
// * 异步响应接口
// * @author xtc
// * @date 2024/2/5
// */
//@RestController
//@RequestMapping("/demo/sse")
//@Slf4j
//public class DemoFluxController {
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
//}
