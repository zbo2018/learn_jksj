package com.zbo.filter;

import io.netty.handler.codec.http.FullHttpResponse;

public class ResponseHeadFilter {
    public void filter(FullHttpResponse response) {
        response.headers().set("kk", "java-1-nio");
    }
}
