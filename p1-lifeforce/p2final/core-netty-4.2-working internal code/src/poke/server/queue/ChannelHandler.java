/**
 * 
 */
package poke.server.queue;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

/**
 * @author arun_malik
 *
 */
public class ChannelHandler extends ChannelInitializer<Channel> {
	private ChannelQueue sq;

	public ChannelHandler(ChannelQueue sq) {
		this.sq = sq;
	}

	@Override
	protected void initChannel(Channel channel) throws Exception {
	
		ChannelPipeline pipeline = channel.pipeline();
		pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(67108864, 0, 4, 0, 4));
		pipeline.addLast("protobufDecoder", new ProtobufDecoder(eye.Comm.Request.getDefaultInstance()));
		pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
		pipeline.addLast("protobufEncoder", new ProtobufEncoder());
		
		pipeline.addLast(new SimpleChannelInboundHandler<eye.Comm.Request>() {
			protected void channelRead0(ChannelHandlerContext ctx, eye.Comm.Request msg) throws Exception {
				sq.enqueueResponse(msg, null);
			}
		});

	}

}