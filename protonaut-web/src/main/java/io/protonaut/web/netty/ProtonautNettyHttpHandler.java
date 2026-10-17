package io.protonaut.web.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.protonaut.web.dispatch.ProtonautRestDispatcher;

import java.util.HashMap;
import java.util.Map;

public class ProtonautNettyHttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final ProtonautRestDispatcher dispatcher;

    public ProtonautNettyHttpHandler(ProtonautRestDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
        // Handle CORS preflight
        if (req.method() == HttpMethod.OPTIONS) {
            FullHttpResponse corsResp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
            addCorsHeaders(corsResp);
            ctx.writeAndFlush(corsResp).addListener(ChannelFutureListener.CLOSE);
            return;
        }

        Map<String, String> headerMap = new HashMap<>();
        for (Map.Entry<String, String> entry : req.headers()) {
            headerMap.put(entry.getKey().toLowerCase(), entry.getValue());
        }

        byte[] bodyBytes = new byte[req.content().readableBytes()];
        req.content().readBytes(bodyBytes);

        ProtonautRestDispatcher.HttpResponse response = dispatcher.dispatch(
                req.method().name(),
                req.uri(),
                headerMap,
                bodyBytes
        );

        HttpResponseStatus status = HttpResponseStatus.valueOf(response.statusCode());
        FullHttpResponse httpResp = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                status,
                Unpooled.wrappedBuffer(response.body())
        );

        httpResp.headers().set(HttpHeaderNames.CONTENT_TYPE, response.contentType());
        httpResp.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.body().length);
        if (response.headers() != null) {
            for (Map.Entry<String, String> entry : response.headers().entrySet()) {
                httpResp.headers().set(entry.getKey(), entry.getValue());
            }
        }
        addCorsHeaders(httpResp);

        boolean keepAlive = HttpUtil.isKeepAlive(req);
        if (keepAlive) {
            httpResp.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            ctx.writeAndFlush(httpResp);
        } else {
            ctx.writeAndFlush(httpResp).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void addCorsHeaders(FullHttpResponse response) {
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS");
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type, Authorization, X-Requested-With, Accept, Origin");
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE, "3600");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
