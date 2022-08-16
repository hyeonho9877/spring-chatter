package com.hyunho9877.chatter.domain;

public enum Exchange {
    EXCHANGE("amq.topic");

    private final String exchange;

    Exchange(String exchange) {
        this.exchange = exchange;
    }

    public String getExchange() {
        return exchange;
    }
}
