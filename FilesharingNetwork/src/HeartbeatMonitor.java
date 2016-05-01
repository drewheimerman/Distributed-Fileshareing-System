import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class HeartbeatMonitor implements Runnable {

	private ServerMulticast mUtil;
	private UDPUtilities udpUtil;
	private int sid;
	private int serverPort;
	private boolean isMiddleware = false;
	private ConcurrentSkipListMap<Integer, String[]> availableServers;
	
	public HeartbeatMonitor(UDPUtilities u, ConcurrentSkipListMap<Integer, String[]> c, int id){
		//mUtil = m;
		//id = i;
		udpUtil = u;
		availableServers = c;
		sid = id;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		/*try{
			
		}catch(IOException e){
			
		}*/
		
		try {
			//mUtil.sendString("joined");
			udpUtil.sendString("setup");
			System.out.println("Started heartbeat monitor.");
			udpUtil.getIncomingSocket().setSoTimeout(15000);
			while(true){
				try{
					DatagramPacket received = udpUtil.listen();
					String message = new String(received.getData(), 0, received.getLength());
					//System.out.println(message);
					/*if(!isMiddleware){
						udpUtil.setDestination(received.getAddress());
						udpUtil.setDestPort(received.getPort());
						//udpUtil.sendString("ack");
						byte[] b;
						ByteBuffer bb = ByteBuffer.allocate(8);
						bb.putInt(id);
						bb.putInt(serverPort);
						b = bb.array();
						
						udpUtil.sendBytes(b);
					}else{*/
						/*byte[] b = received.getData();
						ByteBuffer bb = ByteBuffer.wrap(b);
						sid = bb.getInt();
						int sPort = bb.getInt();
						System.out.println(sid+" "+sPort);*/
						
						
					//}
				}catch(SocketTimeoutException e){
					System.out.println("lost connection.");
					availableServers.remove(sid);
					for(int k : availableServers.keySet()){
						System.out.print(k+" ");
					}
					break;
				}
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(IOException e){
			
		}
		
	}
	public void setMiddleware(boolean b){
		isMiddleware = b;
	}

	public int getId() {
		return sid;
	}

	public void setId(int id) {
		this.sid = id;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	
}
