package DNSRelay;

import java.net.InetAddress;

public class IDTransition {
	
	private int oldID;
	private int port;
	private InetAddress addr;
	
	public IDTransition(int oldID, int port, InetAddress addr) {
		this.oldID = oldID;
		this.port = port;
		this.addr = addr;
	}
	
	public int getOldID() {
		return oldID;
	}

	public int getPort() {
		return port;
	}

	public InetAddress getAddr() {
		return addr;
	}
}
