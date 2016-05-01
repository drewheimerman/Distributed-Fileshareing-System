import java.io.*;
import java.net.*;
import java.util.*;

public class Broker {

	public static void main(String[] args) throws IOException{
		
		Properties properties = new Properties();
		Properties appProps = new Properties(properties);
		System.out.println(System.getProperty("user.dir"));
		InputStream in = new FileInputStream("./src/resources/config.properties");
		appProps.load(in);
		in.close();
		
		
		UDPUtilities udpUtil = new UDPUtilities(7660);

		//InetAddress rm0 = InetAddress.getByName(args[0]);
		//InetAddress rm1 = InetAddress.getByName(args[1]);
		//InetAddress rm2 = InetAddress.getByName(args[2]);
		
		//int rmPort0 = Integer.parseInt(appProps.getProperty("rmPort0"));
		//int rmPort1 = Integer.parseInt(appProps.getProperty("rmPort1"));
		//int rmPort2 = Integer.parseInt(appProps.getProperty("rmPort2"));
		
		while(true){
			DatagramPacket received = udpUtil.listen();
			udpUtil.setDestination(received.getAddress());
			udpUtil.setDestPort(received.getPort());
			int hash = hashFilename(udpUtil.readPacketString(received), 2);
			System.out.println(hash);
			String rm = args[hash];
			int port = 7778;
			String message = rm+":"+port;
			udpUtil.sendString(message);
			
		}
	}
	public static int hashFilename(String s, int n){
		int id = 0;
		String subs;
		if(s.contains(".")){
			subs = s.subSequence(0, s.lastIndexOf(".")).toString();
		}else{
			subs = s;
		}
		System.out.println(subs);
		subs = subs.toLowerCase();
		//System.out.println(subs.toCharArray());
		for(char c: subs.toCharArray()){
			
			id = id+(c-97);
			System.out.println(c+" "+id);
				
		}
		System.out.println(id+" "+id%n);
		return id%n;
	}


}
