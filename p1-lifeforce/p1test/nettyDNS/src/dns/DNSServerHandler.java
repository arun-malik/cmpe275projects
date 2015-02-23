package dns;

import java.util.HashMap;
import java.util.Set;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import dns.DNSProtocol.MasterNode;
import dns.DNSProtocol.DNSRequest;
import dns.DNSProtocol.DNSResponse;

public class DNSServerHandler extends SimpleChannelInboundHandler<DNSRequest> {
	
    static final HashMap<String, MasterNode> MASTERNODES = new HashMap<>();

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
    	
    	String clusterName = request.getClusterName();
    	
    	if (clusterName.equals("")){ // asking for all clusters
    		DNSResponse.Builder rb = DNSResponse.newBuilder();
            rb.setStatus(DNSResponse.Status.OK);
            Set<String> clusterNames = MASTERNODES.keySet();
            for (String name : clusterNames){
            	rb.addMasterNode(MASTERNODES.get(name));
            }
            return rb.build();
    	} 
    	else { // asking for specific cluster
    		MasterNode m = MASTERNODES.get(clusterName);
	    	if (m != null) { // found
	    		DNSResponse.Builder rb = DNSResponse.newBuilder();
	            rb.setStatus(DNSResponse.Status.OK);
	            rb.addMasterNode(m);
	            return rb.build();
	    	}
	    	else { // not found
	    		DNSResponse.Builder rb = DNSResponse.newBuilder();
	    		rb.setStatus(DNSResponse.Status.ERROR);
	    		rb.setErrorMessage("Cluster not found");
	    		return rb.build();
	    	}
    	}
    }
    
    private DNSResponse handlePUT(DNSRequest request){
    	// Insert first.
    	MasterNode m = request.getMasterNode();
    	MASTERNODES.put(m.getClusterName(), m);
    	
    	// Respond.
    	DNSResponse.Builder rb = DNSResponse.newBuilder();
        rb.setStatus(DNSResponse.Status.OK);
        rb.addMasterNode(m);
        return rb.build();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
