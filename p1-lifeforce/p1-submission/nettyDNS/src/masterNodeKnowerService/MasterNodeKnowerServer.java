package masterNodeKnowerService;

import node.Node;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public final class MasterNodeKnowerServer implements Runnable{

    private static final int PORT = 5353;
    
    private final Node node;
    
    public MasterNodeKnowerServer(Node node){
    	this.node = node;
    }

    public void run() {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new MasterNodeKnowerServerInitializer(node));

            b.bind(PORT).syncUninterruptibly().channel().closeFuture().syncUninterruptibly();
         
        } finally {
        	System.out.println("Shutting down");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    
    public static void main(String args[]){
    	Node n = new Node("localhost", 5353);
    	Thread t1 = new Thread(new MasterNodeKnowerServer(n));
    	t1.start();
    	
    	
    }
}
