package masterNodeKnowerService;

import java.util.HashMap;

import node.Node;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import masterNodeKnowerService.MasterNodeKnowerProtocol.MasterNode;
import masterNodeKnowerService.MasterNodeKnowerProtocol.DNSRequest;
import masterNodeKnowerService.MasterNodeKnowerProtocol.DNSResponse;

public class MasterNodeKnowerServerHandler extends SimpleChannelInboundHandler<DNSRequest> {
	
    static final HashMap<String, MasterNode> MASTERNODES = new HashMap<>();

    private final Node node;
    
    public MasterNodeKnowerServerHandler(Node node){
    	this.node = node;
    }
    
    @Override
    public void channelRead0(final ChannelHandlerContext ctx, DNSRequest request) {
    	
    	DNSResponse response;
    	
    	// Handle request.
    	System.out.println("Received:\n" + request.toString());
    	
    	if (request.getAction() == DNSRequest.Action.GET){
    		response = handleGET(request);
    	}
    	else if (request.getAction() == DNSRequest.Action.PUT) {
    		response = handlePUT(request);
    	}
    	else { // Handle error.
    		DNSResponse.Builder rb = DNSResponse.newBuilder();
    		rb.setStatus(DNSResponse.Status.ERROR);
    		rb.setErrorMessage("Not a valid Action");
    		response = rb.build();
    	}
    	
    	// Respond.
    	System.out.println("Sending:\n" + response.toString());
    	final ChannelFuture f = ctx.writeAndFlush(response);
    	f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                assert f == future;
                System.out.println("Closing channel.");
                ctx.close();
            }
        });
    }
    
    private DNSResponse handleGET(DNSRequest request){
    	
    	DNSResponse.Builder rb = DNSResponse.newBuilder();
        rb.setStatus(DNSResponse.Status.OK);
        MasterNode.Builder mb = MasterNode.newBuilder();
        mb.setClusterName("Reverse proxy is SPoF. Need a DNS")
        	.setMasterIp(node.getIp())
        	.setMasterPort(node.getPort());
        rb.addMasterNode(mb.build());
        return rb.build();
    }
    
    private DNSResponse handlePUT(DNSRequest request){
        node.setIp(request.getMasterNode().getMasterIp());
        node.setPort(request.getMasterNode().getMasterPort());
        
    	DNSResponse.Builder rb = DNSResponse.newBuilder();
        rb.setStatus(DNSResponse.Status.OK);
        rb.addMasterNode(request.getMasterNode());
        return rb.build();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
