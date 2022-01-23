package com.zbo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyHttpServer {
    public static void main(String[] args) throws InterruptedException {

        int port = 8808;

        EventLoopGroup bossGroup = new NioEventLoopGroup(2);//作为bossGroup
        EventLoopGroup workerGroup = new NioEventLoopGroup(16);//作为workerGroup

        try {
            ServerBootstrap b = new ServerBootstrap();//netty启动的入口点
            b.option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.SO_RCVBUF, 32 * 1024)
                    .childOption(ChannelOption.SO_SNDBUF, 32 * 1024)
                    .childOption(EpollChannelOption.SO_REUSEPORT, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            b.group(bossGroup, workerGroup)//绑定eventLoop
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HttpInitializer());//添加childHandler，在这里面做具体http相关逻辑的处理。这里是添加自定义的HttpHandler

            /**
             * 第一步：启动
             */
            Channel ch = b.bind(port).sync().channel();//将启动器绑定端口并开启channel。这样服务器就启动了
            System.out.println("开启netty http服务器，监听地址和端口为 http://127.0.0.1:" + port + '/');
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


        /**
         * 测试方法：
         *      浏览器访问 http://127.0.0.1:8808/
         *      浏览器访问 http://127.0.0.1:8808/test
         */

    }
}