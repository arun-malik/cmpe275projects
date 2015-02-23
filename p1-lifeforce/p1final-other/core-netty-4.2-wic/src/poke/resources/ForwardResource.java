/*
eR * copyright 2014, gash
 * 
 * Gash licenses this file to you under the Apache License,
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
package poke.resources;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import poke.server.conf.NodeDesc;
import poke.server.conf.ServerConf;
import poke.server.queue.ChannelHandler;
import poke.server.queue.ChannelQueue;
import poke.server.queue.PerChannelQueue;
import poke.server.resources.Resource;
import poke.server.resources.ResourceFactory.RoundRobin;
import poke.server.resources.ResourceUtil;
import eye.Comm.Header;
import eye.Comm.PokeStatus;
import eye.Comm.Request;

/**
 * The forward resource is used by the ResourceFactory to send requests to a
 * destination that is not this server.
 * 
 * Strategies used by the Forward can include TTL (max hops), durable tracking,
 * endpoint hiding.
 * 
 * @author gash
 * 
 */
public class ForwardResource implements Resource {
	protected static Logger logger = LoggerFactory.getLogger("ForwardResource");

	private final String FORWARD = "forward";
	private ServerConf cfg;
	private PerChannelQueue sq;

	/**
	 * @return the sq
	 */
	public PerChannelQueue getSq() {
		return sq;
	}

	/**
	 * @param sq
	 *            the sq to set
	 */
	public void setSq(PerChannelQueue sq) {
		this.sq = sq;
	}

	public ServerConf getCfg() {
		return cfg;
	}

	/**
	 * Set the server configuration information used to initialized the server.
	 * 
	 * @param cfg
	 */
	public void setCfg(ServerConf cfg) {
		this.cfg = cfg;
	}

	String nextNodeIp = null;
	int nextNodePort = 0;

	@Override
	public Request process(Request request) {

		if (request != null && request.getBody().hasPhotoPayload()) {

			eye.Comm.Header.Builder headerBuilder = Header
					.newBuilder(request.getHeader());
			
			
			switch (request.getHeader().getPhotoHeader().getRequestType()) {
			
			case write:
				// implementation changed to round robin
				Integer nextNode = RoundRobin.getNextNode();

				if (nextNode != null) {

					// iterate over cfg and find ip & port for selected node
					for (NodeDesc node : cfg.getAdjacent().getAdjacentNodes()
							.values()) {
						if (nextNode == node.getNodeId()) {

							nextNodeIp = node.getHost();
							nextNodePort = node.getPort();
							break;
						}
					}
				} 
				
				headerBuilder.setOriginator(cfg.getNodeId()); 
				break;
			case delete:
			case read:
				nextNodeIp = request.getHeader().getIp();
				nextNodePort = request.getHeader().getPort();
				
				// in read we set originator in mapper and not in forward - because fwd resource for read is iniitalzed in per channel queue -in mapper 
				headerBuilder.setOriginator(request.getHeader().getOriginator()); 
				break;
			default:
				break;

			}
			
			// set reply message as response in the header
			headerBuilder.setReplyMsg(FORWARD);
			
			Request.Builder requestBuilder = Request
					.newBuilder(request);
			requestBuilder.setHeader(headerBuilder.build());
			//request = requestBuilder.build();

			Request fwdRequest = requestBuilder.build();
			//Request fwdRequest = ResourceUtil.buildForwardMessage(request, cfg);

			Bootstrap bootStrap = new Bootstrap();
			NioEventLoopGroup group = new NioEventLoopGroup();
			bootStrap.group(group).channel(NioSocketChannel.class)
					.handler(new ChannelHandler(this.sq));
			bootStrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
			bootStrap.option(ChannelOption.TCP_NODELAY, true);
			bootStrap.option(ChannelOption.SO_KEEPALIVE, true);

			SocketAddress mySocketAddress = new InetSocketAddress(nextNodeIp,
					nextNodePort);
			ChannelFuture futureChannel = bootStrap.connect(mySocketAddress).syncUninterruptibly();
			Channel ch = futureChannel.channel();
			futureChannel.awaitUninterruptibly();
			ch.writeAndFlush(fwdRequest);

			return fwdRequest;
		}
		return request;

	}

	public class ChannelHandler extends ChannelInitializer<Channel> {
		private ChannelQueue sq;

		public ChannelHandler(ChannelQueue sq) {
			this.sq = sq;
		}

		@Override
		protected void initChannel(Channel channel) throws Exception {

			ChannelPipeline pipeline = channel.pipeline();
			pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(
					67108864, 0, 4, 0, 4));
			pipeline.addLast("protobufDecoder", new ProtobufDecoder(
					eye.Comm.Request.getDefaultInstance()));
			pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
			pipeline.addLast("protobufEncoder", new ProtobufEncoder());

			pipeline.addLast(new SimpleChannelInboundHandler<eye.Comm.Request>() {
				protected void channelRead0(ChannelHandlerContext ctx,
						eye.Comm.Request msg) throws Exception {
					System.out.println("---------------->inside channel handler");
					sq.enqueueResponse(msg, null);
				}
			});

		}

	}
}
