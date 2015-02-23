package node;

public class Node {
	
	private String ip;
	private Integer port;
	
	public Node(){
		this.ip = "google.com";
		this.port = 443;
	}
	
	public Node(String ip, int port){
		this.ip = ip;
		this.port = port;
	}
	
	public void setIp(String ip) {
		synchronized (this.ip){
			this.ip = ip;
		}
	}

	public void setPort(int port) {
		synchronized (this.port){
			this.port = port;
		}
	}
	
	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

}
