package io.protonaut.web.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.protonaut.web.dispatch.ProtonautRestDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.BindException;
import java.net.InetSocketAddress;

public class ProtonautServer {
    private static final Logger log = LoggerFactory.getLogger(ProtonautServer.class);

    private final String host;
    private final int grpcPort;
    private final int restPort;
    private final boolean restEnabled;
    private final boolean failFastOnPortConflict;
    private final ProtonautRestDispatcher dispatcher;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel grpcChannel;
    private Channel restChannel;
    private boolean isRunning = false;

    public ProtonautServer(
            String host,
            int grpcPort,
            int restPort,
            boolean restEnabled,
            boolean failFastOnPortConflict,
            ProtonautRestDispatcher dispatcher
    ) {
        this.host = (host != null && !host.isBlank()) ? host : "0.0.0.0";
        this.grpcPort = grpcPort > 0 ? grpcPort : 6570;
        this.restPort = restPort > 0 ? restPort : 8590;
        this.restEnabled = restEnabled;
        this.failFastOnPortConflict = failFastOnPortConflict;
        this.dispatcher = dispatcher;
    }

    public synchronized void start() {
        if (isRunning) return;

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        log.info("[Protonaut] Starting Protonaut embedded web listeners on host {}...", host);

        // 1. Bind gRPC-compatible browser entry point (port 6570)
        grpcChannel = bindPort(grpcPort, "gRPC-compatible browser entry point");

        // 2. Bind dedicated REST entry point (port 8590) if enabled
        if (restEnabled) {
            restChannel = bindPort(restPort, "Dedicated REST entry point");
        } else {
            log.info("[Protonaut] Dedicated REST listener on port {} is disabled (protonaut.rest.enabled = false)", restPort);
        }

        isRunning = true;
    }

    private Channel bindPort(int port, String description) {
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new HttpServerCodec());
                            p.addLast(new HttpObjectAggregator(10 * 1024 * 1024)); // 10MB max request
                            p.addLast(new ChunkedWriteHandler());
                            p.addLast(new ProtonautNettyHttpHandler(dispatcher));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(new InetSocketAddress(host, port)).syncUninterruptibly();
            log.info("[Protonaut] {} successfully bound to http://{}:{}/protonaut", description, host.equals("0.0.0.0") ? "localhost" : host, port);
            return f.channel();
        } catch (Exception e) {
            boolean isBindException = isBindException(e);
            if (isBindException) {
                log.warn("[Protonaut] WARN - Port {} is already in use. Protonaut listener was not started. Host application startup will continue.", port);
                if (failFastOnPortConflict) {
                    throw new IllegalStateException("Port " + port + " is already in use and fail-fast is enabled", e);
                }
                return null;
            } else {
                log.error("[Protonaut] ERROR - Failed to bind port {}: {}", port, e.getMessage(), e);
                if (failFastOnPortConflict) {
                    throw new RuntimeException("Failed to bind port " + port, e);
                }
                return null;
            }
        }
    }

    private boolean isBindException(Throwable t) {
        Throwable curr = t;
        while (curr != null) {
            if (curr instanceof BindException) {
                return true;
            }
            if (curr.getMessage() != null && (curr.getMessage().contains("Address already in use") || curr.getMessage().contains("bind"))) {
                return true;
            }
            curr = curr.getCause();
        }
        return false;
    }

    public synchronized void stop() {
        if (!isRunning) return;
        log.info("[Protonaut] Shutting down Protonaut embedded web listeners...");

        if (grpcChannel != null) {
            try {
                grpcChannel.close().syncUninterruptibly();
            } catch (Exception ignored) {}
            grpcChannel = null;
        }

        if (restChannel != null) {
            try {
                restChannel.close().syncUninterruptibly();
            } catch (Exception ignored) {}
            restChannel = null;
        }

        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
            bossGroup = null;
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
            workerGroup = null;
        }

        isRunning = false;
        log.info("[Protonaut] Protonaut listeners stopped cleanly.");
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int getGrpcPort() {
        return grpcPort;
    }

    public int getRestPort() {
        return restPort;
    }

    public boolean isRestEnabled() {
        return restEnabled;
    }

    public Channel getGrpcChannel() {
        return grpcChannel;
    }

    public Channel getRestChannel() {
        return restChannel;
    }
}
