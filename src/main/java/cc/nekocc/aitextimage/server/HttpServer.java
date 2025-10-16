package cc.nekocc.aitextimage.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class HttpServer
{
    private final int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel channel;

    public HttpServer(int port)
    {
        this.port = port;
    }

    public void start() throws InterruptedException
    {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new HttpServerInitializer());

        channel = b.bind(port).sync().channel();
        System.out.println("HTTP Server is start on port: " + port);

        channel.closeFuture().sync();
    }

    public void stop()
    {
        System.out.println("HTTP Server is shutting down...");
        if (channel != null)
        {
            channel.close();
        }
        if (bossGroup != null)
        {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null)
        {
            workerGroup.shutdownGracefully();
        }
        System.out.println("HTTP Server stopped.");
    }
}