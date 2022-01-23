package com.zbo.backend;

import com.zbo.HttpHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

public class NettyHttpClientOutboundHandler  extends ChannelInboundHandlerAdapter {

    private FullHttpRequest inboundRequest;
    private ChannelHandlerContext inboundCtx;

    public NettyHttpClientOutboundHandler(FullHttpRequest inboundRequest, ChannelHandlerContext inboundCtx){
        this.inboundRequest = inboundRequest;
        this.inboundCtx = inboundCtx;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx)
            throws Exception {
        System.out.println("已连接....");
        
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {

        System.out.println("返回了数据....");

        if(msg instanceof DefaultHttpResponse){
            DefaultHttpResponse response = (DefaultHttpResponse) msg;
            String result = response.decoderResult().toString();
            System.out.println("DefaultHttpResponse response -> "+result);
        }else if(msg instanceof FullHttpResponse){
            FullHttpResponse response = (FullHttpResponse)msg;
            ByteBuf buf = response.content();
            String result = buf.toString(CharsetUtil.UTF_8);
            System.out.println("FullHttpResponse response -> "+result);
        }else {
            DefaultLastHttpContent response = (DefaultLastHttpContent) msg;
            ByteBuf buf = response.content();
            String result = buf.toString(CharsetUtil.UTF_8);
            System.out.println("DefaultLastHttpContent response -> "+result);

            //返回给浏览器
            HttpHandler httpHandlerResponseClient = new HttpHandler();
            httpHandlerResponseClient.handlerTest(inboundRequest, inboundCtx, result);
        }


    }
}