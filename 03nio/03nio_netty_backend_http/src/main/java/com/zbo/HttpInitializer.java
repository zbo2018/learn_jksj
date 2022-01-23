package com.zbo;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;

public class HttpInitializer extends ChannelInitializer<SocketChannel> {//集成channel的初始化

    /**
     *
     * @param ch
     */
    @Override
    public void initChannel(SocketChannel ch) {
        /**
         * 拿到channelPipeline
         * 也就是我们需要在这次网络处理里面，中间这一段需要我们自己控制它的流水线、它的流程的这部分
         *
         * 第二步：
         *      当有网络请求进来，就会进入这个自定义的initChannel里面的pipeline。
         *      这个pipeline里面就会调用我们自定义的HttpHandler
         */
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpServerCodec());//添加httpServer的编码器
        p.addLast(new HttpServerExpectContinueHandler());
        p.addLast(new HttpObjectAggregator(1024 * 1024));//添加一个报文聚合器，HttpObjectAggregator这样一个聚合器
        p.addLast(new HttpHandler());//最后添加自己定义的HttpHandler
    }
}