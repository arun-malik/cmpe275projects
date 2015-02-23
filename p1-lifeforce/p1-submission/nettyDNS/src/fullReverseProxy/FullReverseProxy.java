package fullReverseProxy;

import node.Node;
import masterNodeKnowerService.MasterNodeKnowerServer;
import proxyService.Proxy;

public class FullReverseProxy {
	public static void main(String args[]){
		Node masterNode = new Node("localhost", 8080);
		Thread t1 = new Thread(new MasterNodeKnowerServer(masterNode));
		Thread t2 = new Thread(new Proxy(masterNode));
		t1.start();
		t2.start();	
	}
}
