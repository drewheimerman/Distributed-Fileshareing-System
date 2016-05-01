import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Properties;

public class Server {
	
	public static void main(String[] args) throws IOException{
		
		Properties properties = new Properties();
		Properties appProps = new Properties(properties);
		System.out.println(System.getProperty("user.dir"));
		InputStream in = new FileInputStream("./src/resources/config.properties");
		appProps.load(in);
		in.close();
		
		
		//Get port data from configs.properties//
		int id = Integer.parseInt(args[0]);
		InetAddress ip = InetAddress.getByName(appProps.getProperty("multicast"+(id%3)));
		int mport = Integer.parseInt(appProps.getProperty("mport"+(id%3)));
		int serverPort = Integer.parseInt(appProps.getProperty("port"+id));
		int rmPort = Integer.parseInt(appProps.getProperty("rmPort"+(id%3)));
		
		ServerMulticast mUtil = new ServerMulticast(ip, mport, 0);
		UDPUtilities udpUtil = new UDPUtilities();
		//UDPUtilities udpUtilGen = new UDPUtilities(serverPort);
		
		udpUtil.setDestination(InetAddress.getByName(args[1]));
		udpUtil.setDestPort(rmPort);

		byte[] b;
		ByteBuffer bb = ByteBuffer.allocate(8);
		//bb.putInt(id);
		bb.putInt(serverPort);
		b = bb.array();
		
		udpUtil.sendBytes(b);

		//HeartbeatMonitor monitor = new HeartbeatMonitor(mUtil);
		//monitor.setId(id);
		//monitor.setServerPort(serverPort);
		
		HeartbeatPulse pulse = new HeartbeatPulse(udpUtil);
		ServerRequestManager manager = new ServerRequestManager(serverPort);
		
		System.out.println("id: "+id);
		System.out.println("serverPort: "+serverPort);
		System.out.println("localhost: "+InetAddress.getByName(args[1]));
		System.out.println("rmPort: "+rmPort);
		Thread ps = new Thread(pulse);
		Thread m = new Thread(manager);
		ps.start();
		m.start();
		try{
			ps.join();
			m.join();
		}catch(Exception e){
			
		}
		mUtil.close();
		udpUtil.close();
	}
	
	/*private ConcurrentHashMap getAvailableServers(){
		
	}*/
	
	
}
