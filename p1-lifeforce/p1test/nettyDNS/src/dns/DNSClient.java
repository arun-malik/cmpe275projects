package dns;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public final class DNSClient {

    static final String HOST = "127.0.0.1";
    static final int PORT = 5353;

    public static void main(String[] args) throws Exception {

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new DNSClientInitializer());

            // Make a new connection.
            Channel ch = b.connect(HOST, PORT).sync().channel();

            // Get the handler instance to initiate the request.
            DNSClientHandler handler = ch.pipeline().get(DNSClientHandler.class);

            // Request and get the response.
            handler.putServerInfo("test", "10.0.0.1", 9000);
       

            // Close the connection.
            ch.close();
            
            // Make a new connection.
            ch = b.connect(HOST, PORT).sync().channel();

            // Get the handler instance to initiate the request.
            handler = ch.pipeline().get(DNSClientHandler.class);

            // Request and get the response.
            handler.putServerInfo("test", "10.0.0.2", 9000);
       

            // Close the connection.
            ch.close();
            
            // Make a new connection.
            ch = b.connect(HOST, PORT).sync().channel();

            // Get the handler instance to initiate the request.
            handler = ch.pipeline().get(DNSClientHandler.class);

            // Request and get the response.
            handler.putServerInfo("test2", "10.0.0.1", 9000);
       

            // Close the connection.
            ch.close();
            
            // Make a new connection.
            ch = b.connect(HOST, PORT).sync().channel();

            // Get the handler instance to initiate the request.
            handler = ch.pipeline().get(DNSClientHandler.class);

            // Request and get the response.
            handler.getServers();

            // Close the connection.
            ch.close();
            
            // Make a new connection.
            ch = b.connect(HOST, PORT).sync().channel();

            // Get the handler instance to initiate the request.
            handler = ch.pipeline().get(DNSClientHandler.class);

            // Request and get the response.
            handler.getServer("test");

            // Close the connection.
            ch.close();
            
        } finally {
        	System.out.println("Client shutting down.");
            group.shutdownGracefully();
        }
    }
}
