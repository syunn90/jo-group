package com.jo.biz.demo;

import java.util.Objects;

/**
 * 责任链模式DEMO
 * @author xtc
 * @date 2024/2/3
 */
public abstract class DemoAbstractChainHandler {
    protected DemoAbstractChainHandler next;

    private void setNext(DemoAbstractChainHandler next){
        this.next = next;
    }

    public void chain(DemoRequest request){
        handler(request);
        if (Objects.nonNull(next)){
            next.chain(request);
        }
    }

    protected abstract void handler(DemoRequest request);

    public static class Builder{
        private DemoAbstractChainHandler head;
        private DemoAbstractChainHandler tail;

        public Builder addHandler(DemoAbstractChainHandler handler){
            if (Objects.isNull(head)){
                this.head = this.tail = handler;
                return this;
            }
            this.tail.setNext(handler);
            this.tail = handler;
            return this;
        }
        public DemoAbstractChainHandler build(){
            return this.head;
        }
    }

    private record DemoRequest(String employee, int days){};


    public static class ChainHandler1 extends DemoAbstractChainHandler {
        @Override
        protected void handler(DemoRequest request) {
            if (request.days() <= 3){
                System.out.println("handler 1 ");
            }
        }
    }
    public static class ChainHandler2 extends DemoAbstractChainHandler {
        @Override
        protected void handler(DemoRequest request) {
            if (request.days() > 3 && request.days() < 10){
                System.out.println("handler 2 ");
            }
        }
    }
    public static class ChainHandler3 extends DemoAbstractChainHandler {
        @Override
        protected void handler(DemoRequest request) {
            if (request.days() > 10){
                System.out.println("handler 3 ");
            }
        }
    }
    public static class ChainDemo{
        private static DemoAbstractChainHandler getChainOfHandler() {
            return new DemoAbstractChainHandler.Builder()
                    .addHandler(new ChainHandler1())
                    .addHandler(new ChainHandler2())
                    .addHandler(new ChainHandler3())
                    .build();
        }
    }

    public static void main(String[] args) {
        var chain = ChainDemo.getChainOfHandler();
        var request = new DemoRequest("name1", 2);
        chain.chain(request);

        var request1 = new DemoRequest("name2", 5);
        chain.chain(request1);
    }
}



