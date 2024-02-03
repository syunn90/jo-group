package com.jo.biz.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 责任链模式demo（spring 版）
 * @author xtc
 * @date 2024/2/3
 */
@Component
public abstract class DemoAbstractSpringChainHandler {

    protected abstract void handler(DemoRequest request);
    public record DemoRequest(String employee, int days){};

    @Component
    @Order(1)
    public static class  ChainHandler1 extends DemoAbstractSpringChainHandler {
        @Override
        protected void handler(DemoRequest request) {
            if (request.days() <= 3){
                System.out.println("handler 1 ");
            }
        }
    }

    @Component
    @Order(2)
    public static class ChainHandler2 extends DemoAbstractSpringChainHandler {
        @Override
        protected void handler(DemoRequest request) {
            if (request.days() > 3 && request.days() < 10){
                System.out.println("handler 2 ");
            }
        }
    }
    @Component
    @Order(3)
    public static class ChainHandler3 extends DemoAbstractSpringChainHandler {
        @Override
        protected void handler(DemoRequest request) {
            if (request.days() > 10){
                System.out.println("handler 3 ");
            }
        }
    }

    @Component
    @RequiredArgsConstructor
    public static class DemoContext{
        private final List<DemoAbstractSpringChainHandler> list;

        public void doInterceptor(DemoRequest request){
            list.forEach(l -> l.handler(request));
        }
    }
}
