import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
//import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RemoteManager {
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	public static void main(String[] args) throws IOException{
		
		ConcurrentSkipListMap<Integer, String[]> availableServers = new ConcurrentSkipListMap<Integer, String[]>();
		int serverCount = 0;
		
		Properties properties = new Properties();
		Properties appProps = new Properties(properties);
		System.out.println(System.getProperty("user.dir"));
		InputStream in = new FileInputStream("./src/resources/config.properties");
		appProps.load(in);
		in.close();
		//Properties appProps = new Properties(properties);
		//Enumeration keys = properties.keys();
		int id = Integer.parseInt(args[0]);
		
		//InetAddress groupIP = InetAddress.getByName(appProps.getProperty("multicast"+id));
		//int port = Integer.parseInt(appProps.getProperty("mport"+id));
		
		System.out.println("localhost: "+InetAddress.getByName("localhost"));
		System.out.println("id: "+id);
		
		//ServerMulticast mUtil = new ServerMulticast(groupIP, port, 0);
		
		UDPUtilities udpUtilPing = new UDPUtilities(7777); //NOTICE HERE NOTICE HERE NOTICE HERE NOTICE HERE NOTICE HERE
		UDPUtilities udpUtilGen = new UDPUtilities(7778);
		
		/*DatagramPacket p = udpUtil.listen();
		byte[] b = p.getData();
		ByteBuffer bb = ByteBuffer.wrap(b);
		int sid = bb.getInt();
		int sPort = bb.getInt();*/
		
		//System.out.println("Packet received: "+sid+" "+sPort);
		//mUtil.joinGroup();
		RequestThreadManager requests = new RequestThreadManager(udpUtilGen, availableServers);
		Thread reqs = new Thread(requests);
		reqs.start();
		while(true){
			System.out.println("Top");
			DatagramPacket p = udpUtilPing.listen();
			
			UDPUtilities u = new UDPUtilities();
			u.setDestination(p.getAddress());
			u.setDestPort(p.getPort());
			//udpUtilPing.sendString("setting up");
			byte[] b = p.getData();
			ByteBuffer bb = ByteBuffer.wrap(b);
			//int  sid = bb.getInt();
			int sPort = bb.getInt();
			
			if(!availableServers.containsKey(serverCount)){
				System.out.println(serverCount+" "+sPort);

				String[] s = {p.getAddress().getHostAddress(), Integer.toString(sPort)};
				availableServers.put(serverCount, s);
				
				HeartbeatMonitor monitor = new HeartbeatMonitor(u, availableServers, serverCount);
				monitor.setMiddleware(true);
				Thread hm = new Thread(monitor);
				hm.start();
				
				System.out.println("Started HeartbeatMonitor Thread");
				for(int k : availableServers.keySet()){
					System.out.print("Server: "+k+" ");
				}
				System.out.println("\n");
			}
			
			serverCount++;
			/*try{
				hm.join();
				//reqs.join();
			}catch(Exception e){
				
			}*/
		}
		
		
		//HeartbeatMonitor monitor = new HeartbeatMonitor(mUtil);
		//monitor.setMiddleware(true);
		//Thread hm = new Thread(monitor);
		//hm.start();
		//mUtil.sendString("ping");
		
		
		//mUtil.close();
		//udpUtilPing.close();
	}
	
}
