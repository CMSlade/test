package DNSRelay;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class DNSRelay {

	// DNS IP address
	public static final String DNS_IP = "202.106.0.2";
	//DNS port (default: 53)
	public static final int DNS_PORT = 53;
	// local port
	public static final int LOCAL_PORT = 53;
	// maximum size of packet
	private static final int DATA_LEN = 4096;
	byte[] inBuff = new byte[DATA_LEN];
	// receive packet
	private DatagramPacket inPacket = new DatagramPacket(inBuff, inBuff.length);
	// transmit packet
	private DatagramPacket outPacket;
	// domain name (after analysis)
	private String domainNameStr;
	// IP address and port number of resolver 
	private InetAddress resolverAddress;
	private int resolverPort;
	// flag bit of IPv6 packet
	private boolean IPv6_Flag = false;
	// point to current analyzed place
	int udpCursor;
	
	private Map<Integer, IDTransition> idMap = new HashMap<Integer, IDTransition>();
	SimpleDateFormat time=new SimpleDateFormat("HH:mm:ss");
	
	public static void main(String[] args) throws UnknownHostException {
		try {
			CheckLocalFile.readDB("C:\\Users\\DJ\\Desktop\\Ð¡Ñ§ÆÚ\\DNScode\\dnsrelay.txt");// read the local database
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Remote DNS Server: " + DNS_IP);
		System.out.println("Successfully bind UDP port " + LOCAL_PORT);
		System.out.println("Successfully load table \"dnsrelay.txt\" ");
		System.out.println(CheckLocalFile.ipTable.size() + " names," + "occupy "
				+ CheckLocalFile.fileSize + " bytes memory");
		System.out.println("========================================");
		
		// start
		DNSRelay startDNS = new DNSRelay();
		startDNS.initTrans();
	}
	
	public void initTrans() {
		DatagramSocket socket1 = null;
		
		try {
			// bind to port 53
			socket1 = new DatagramSocket(LOCAL_PORT);
			
			// keep waiting transmit
			while (true) {
				// receive UDP packet
				socket1.receive(inPacket);
				// get DNS data
				byte[] sendData = inPacket.getData();
				
				
				// if request is query
				if (((sendData[2] & 0x80) == 0x00)) { 
					// query
					System.out.println("\nQuery receive time£º " + new java.util.Date());
					// get domain name
					domainNameStr = getDomainName(sendData);
					System.out.println("domain name: " + domainNameStr);
					
					// store the source address and port number of packet
					resolverAddress = inPacket.getAddress();
					resolverPort = inPacket.getPort();
					
					IDTransition idTrans = new IDTransition((int) TypeConvert.byteToShort(sendData, 0),resolverPort, resolverAddress);
				if(!idMap.containsKey(idTrans.getOldID())){
					idMap.put(idTrans.getOldID(), idTrans);
				}
					
					// check domain name from local IP table(dnsrelay.txt) 
					if (CheckLocalFile.ipTable.containsKey(domainNameStr)) {
						
						/*local check start*/
						
						// get ID address via port number
						String LocalIPAddress = CheckLocalFile.ipTable.get(domainNameStr);
						
						// check if this domain name need to be shield
						if (LocalIPAddress.equals("0.0.0.0")) {
							// shield this IP address
							//System.out.println("function£º" + "shield"); 
							System.out.println("Function: shield");
							
							// change flag bit response (flag=0x8183):domain name doesn't exist, or domain name is out of date
							sendData[2] = (byte) (sendData[2] | 0x81);
							sendData[3] = (byte) (sendData[3] | 0x83);
							
							// packing data packet and send out
							outPacket = new DatagramPacket(sendData,sendData.length, resolverAddress,resolverPort);
							socket1.send(outPacket);
							IPv6_Flag = false;
						} else {
							// if IP is not 0.0.0.0, packing UDP packet locally 
							// and send back resolver response
							
							// new packet
							byte[] finalData = new byte[udpCursor + 16];
							int pointerCur = 0; // answer cursor
							
							// if the request is IPv6 form
							if (IPv6_Flag == true) {
								System.out.println("Function£º" + "IPV6 local response");
								// change flag bit response (flag=0x8180): no error in DNS flag
								outPacket = new DatagramPacket(sendData,
										sendData.length, InetAddress.getByName(DNS_IP),
										DNS_PORT);
								socket1.send(outPacket);	
								System.out.println("Transmit time£º " + new java.util.Date());
								IPv6_Flag = false;
								System.out.println("Function£º" + "transmit to remote DNS server");
								IPv6_Flag = false;
								
		
							} else {// the request is IPv4 form
								System.out.println("Function£º" + "IPV4 local response");
								// change flag bit response (flag=0x8180)
								sendData[2] = (byte) (sendData[2] | 0x81);
								sendData[3] = (byte) (sendData[3] | 0x80);
								// set Answer count to 1
								sendData[6] = (byte) (sendData[6] | 0x00);
								sendData[7] = (byte) (sendData[7] | 0x01);
								System.arraycopy(sendData, 0, finalData, pointerCur,udpCursor);
								
								/*build data structure*/
								// store name:compressed format
								pointerCur += udpCursor;
								short name = (short) 0xc00c;
								System.arraycopy(
										TypeConvert.shortToByte(name), 0,finalData, pointerCur, 2);
								// store typeA: use to record IP address of host,
								//if input domain name, DNS will guide this to A record mapped server
								pointerCur += 2;
								short typeA = (short) 0x0001;
								System.arraycopy(
										TypeConvert.shortToByte(typeA), 0,finalData, pointerCur, 2);
								// store classA
								pointerCur += 2;
								short classA = (short) 0x0001;
								System.arraycopy(
										TypeConvert.shortToByte(classA), 0,
										finalData, pointerCur, 2);
								// store timeLive
								pointerCur += 2;
								int timeLive = 0x00015180;
								System.arraycopy(
										TypeConvert.intToByte(timeLive), 0,
										finalData, pointerCur, 4);
								// store responseIPLen
								pointerCur += 4;
								short responseIPLen = (short) 0x0004;
								System.arraycopy(
										TypeConvert.shortToByte(responseIPLen), 0,
										finalData, pointerCur, 2);
								// store responseIP
								pointerCur += 2;
								byte[] responseIP = InetAddress.getByName(
										CheckLocalFile.ipTable.get(domainNameStr))
										.getAddress();
								System.arraycopy(responseIP, 0, finalData, pointerCur,4);
								pointerCur += 4;
								/*end of building data structure*/
							}

							// response the request£¬send UDP packet
							outPacket = new DatagramPacket(finalData,
									finalData.length, resolverAddress,
									resolverPort);
							socket1.send(outPacket);
						}
						
						/*end of local check*/
					
					} 
					else {
						
						/*remote DNS check start*/
						
						// send request to remote DNS
						outPacket = new DatagramPacket(sendData,
								sendData.length, InetAddress.getByName(DNS_IP),
								DNS_PORT);
						socket1.send(outPacket);	
						System.out.println("Transmit time£º " + new java.util.Date());
						IPv6_Flag = false;
						System.out.println("Function£º" + "transmit to remote DNS server");
						// transform port and address to the resolver address and port
						
						
						/*end remote DNS check*/
					}
				} else {
					/* response to the receive packet */
					// receive data packet
					int responseID = TypeConvert.byteToShort(sendData, 0);
					
//					outPacket = new DatagramPacket(sendData,
//							sendData.length, resolverAddress, resolverPort);
//					socket.send(outPacket);
					if (idMap.containsKey(responseID)) {
						IDTransition id = idMap.get(responseID);
						// transmit the received remote DNS response
						outPacket = new DatagramPacket(sendData,sendData.length, id.getAddr(), id.getPort());
						socket1.send(outPacket);
					}
				}
			}
		} catch (Exception e) {
			// stop socket
			socket1.close();
			e.printStackTrace();
		}
	}
	public String getDomainName(byte[] buf) {
		String domainName = "";
		//the beginning of domain name is stored in bit 13
		udpCursor = 12;
		// get the length of domain name
		int length = TypeConvert.byteToInt(buf, udpCursor);
		
		while (length != 0) {
			udpCursor++;
			domainName = domainName
					+ TypeConvert.byteToString(buf, udpCursor, length) + ".";
			udpCursor += length;
			length = TypeConvert.byteToInt(buf, udpCursor);
		}
		udpCursor++;
		// judge whether the data packet is IPv6 type. if yes, set flag bit to true
		if (buf[udpCursor] == 0x00 && buf[udpCursor + 1] == 0x1c) {
			IPv6_Flag = true;
		}
		udpCursor += 4;
		// return the domain name without end "."
		return domainName.substring(0, domainName.length() - 1);
	}

}
