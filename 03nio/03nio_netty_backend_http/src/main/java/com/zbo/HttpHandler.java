package com.zbo;

import com.zbo.backend.NettyHttpClient;
import com.zbo.filter.InboundHeadFilter;
import com.zbo.filter.ResponseHeadFilter;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.ReferenceCountUtil;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * 继承ChannelInboundHandlerAdapter(入站适配器)
 * 问：ChannelInboundHandlerAdapter是什么？
 * 答：是我们整个NettyServer启动以后，客户端的请求进来。读取客户端请求的这个handler
 */
public class HttpHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    /**
     * 重写channelRead方法，就可以通过客户端连接Netty的这个通道，直接读取到我们的数据
     * @param ctx
     * @param msg 这次请求的所有数据。它的http协议的报文及相关信息都在这个msg里
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            /**
             * 步骤3：
             *      pipeline调用我们自定义的HttpHandler。就开始读请求中的数据
             *      读到以后就会进入我们自己写的这段业务逻辑的处理代码
             */
            //logger.info("channelRead流量接口请求开始，时间为{}", startTime);
            FullHttpRequest fullRequest = (FullHttpRequest) msg;//将msg转型成HttpRequest对象就可以拿到它内部的结构
            String uri = fullRequest.uri();//拿到这次请求的http协议的URL是什么
            //logger.info("接收到的请求url为{}", uri);
            /*if (uri.contains("/test")) {//根据url做不同的处理。相当于路由、相当于spring mvc里的mapping路径
                handlerTest(fullRequest, ctx, "hello,kimmking");
            } else {
                handlerTest(fullRequest, ctx, "hello,others");
            }*/

            //filter中增加head属性
            InboundHeadFilter filter = new InboundHeadFilter();
            filter.filter(fullRequest, ctx);

            //路由
            if(uri.contains("/test")){
                //用netty做客户端，请求后面的服务
                NettyHttpClient client = new NettyHttpClient(fullRequest, ctx);
                client.connect("127.0.0.1", 8801);
                System.out.println("netty 主动发消息=================");
                client.sentMsg();
            }else if(uri.startsWith("/user/get")){
                //用netty做客户端，请求后面的服务
                NettyHttpClient client = new NettyHttpClient(fullRequest, ctx);
                client.connect("127.0.0.1", 8802);
                System.out.println("netty 主动发消息=================");
                client.sentMsg();
            }else{
                //用netty做客户端，请求后面的服务
                NettyHttpClient client = new NettyHttpClient(fullRequest, ctx);
                client.connect("127.0.0.1", 8803);
                System.out.println("netty 主动发消息=================");
                client.sentMsg();
            }


        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * 给客户端发送http相关的报文
     * @param fullRequest
     * @param ctx
     * @param body
     */
    public void handlerTest(FullHttpRequest fullRequest, ChannelHandlerContext ctx, String body) {
        FullHttpResponse response = null;//最终要组装的对象
        try {

            String value = body; // 对接上次作业的httpclient或者okhttp请求另一个url的响应数据

//            httpGet ...  http://localhost:8801
//            返回的响应，"hello,nio";
//            value = reponse....

            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(value.getBytes("UTF-8")));
            response.headers().set("Content-Type", "application/json");
            response.headers().setInt("Content-Length", response.content().readableBytes());

            ResponseHeadFilter filter = new ResponseHeadFilter();
            filter.filter(response);

        } catch (Exception e) {
            System.out.println("处理出错:"+e.getMessage());
            response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
        } finally {
            if (fullRequest != null) {
                if (!HttpUtil.isKeepAlive(fullRequest)) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    response.headers().set(CONNECTION, KEEP_ALIVE);
                    ctx.write(response);
                }
                ctx.flush();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}