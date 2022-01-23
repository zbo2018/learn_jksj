package com.zbo.backend;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.URI;
import java.net.URISyntaxException;

public class NettyHttpClient {
    private ChannelFuture future;
    private FullHttpRequest inboundRequest;
    private ChannelHandlerContext inboundCtx;

    public NettyHttpClient(FullHttpRequest inboundRequest, ChannelHandlerContext inboundCtx){
        this.inboundRequest = inboundRequest;
        this.inboundCtx = inboundCtx;
    }

    public void connect(String host, int port) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
                    ch.pipeline().addLast(new HttpResponseDecoder());
                    //客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码
                    ch.pipeline().addLast(new HttpRequestEncoder());
                    ch.pipeline().addLast(new NettyHttpClientOutboundHandler(inboundRequest, inboundCtx));
                }
            });

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync();




            //f.channel().write(request);
            //f.channel().flush();
            f.channel().closeFuture().sync();

            future = f;
        } finally {
            workerGroup.shutdownGracefully();
        }

    }

    public void sentMsg(){
        try {
            URI uri = new URI("/user/get");

            FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString());
            request.headers().add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            request.headers().add(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
            //发到后端服务的请求，增加自定义属性
            request.headers().add("filter_add_attr_modify", inboundRequest.headers().get("filter_add_attr"));

            future.channel().writeAndFlush(request);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        NettyHttpClient client = new NettyHttpClient(null, null);
        client.connect("127.0.0.1", 8801);
    }
}