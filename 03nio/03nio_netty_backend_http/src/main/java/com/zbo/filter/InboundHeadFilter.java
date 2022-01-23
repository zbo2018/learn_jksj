package com.zbo.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class InboundHeadFilter {

    public void filter(FullHttpRequest request, ChannelHandlerContext ctx){
        request.headers().add("filter_add_attr","filter中给head增加的属性值");
    }
}
