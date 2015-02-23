/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package proxyService;

import node.Node;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public final class Proxy implements Runnable{

    static final int LOCAL_PORT = 3535;

    private final Node node;
    
    public Proxy(Node node){
    	this.node = node;
    }
    
    public void run() {
        System.err.println("Proxying *:" + LOCAL_PORT + " to " + node.getIp() + ':' + node.getPort() + " ...");

        // Configure the bootstrap.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new ProxyInitializer(node))
             .childOption(ChannelOption.AUTO_READ, false)
             .bind(LOCAL_PORT).syncUninterruptibly().channel().closeFuture().syncUninterruptibly();
            
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    
    public static void main(String args[]){
    	Node n = new Node("localhost", 5353);
    	Thread t1 = new Thread(new Proxy(n));
    	t1.start(); // don't do run!
    }
}
