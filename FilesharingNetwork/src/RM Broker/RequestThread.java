import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.*;

//import java.io.*;
import java.net.*;

public class RequestThread implements Runnable {
	
	private UDPUtilities udpUtil;
	private ConcurrentSkipListMap<Integer, String[]> availableServers;
	private DatagramPacket clientPacket;
	
	public RequestThread(UDPUtilities u, ConcurrentSkipListMap<Integer, String[]> c, DatagramPacket p){
		udpUtil = u;
		availableServers = c;
		clientPacket = p;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("RequestThread");
		udpUtil.setDestination(clientPacket.getAddress());
		udpUtil.setDestPort(clientPacket.getPort());
		String message = new String(clientPacket.getData(), 0, clientPacket.getLength());
		int numAvailable = availableServers.size();
		
		/*Pattern pattern = Pattern.compile("^\\s*(.*?):(\\d+)\\s*$");
		Matcher matcher = pattern.matcher(message);*/
		
		String action = "";
		String filename = "";
		filename = message;
		/*if(matcher.matches()){
			action = matcher.group(1);
			filename = matcher.group(2);
		}else{
			System.err.println("Received a bad host. Aborting");
			System.exit(0);
			
		}*/
		System.out.println("number availble: " + numAvailable);
		if(numAvailable!=0){
			int hash = hashFilename(filename, numAvailable);
			System.out.println(hash+" "+filename);
			//int i = 0;
			Iterator it = availableServers.keySet().iterator();
			Integer i = (Integer) it.next();
			
			System.out.println("size: " + hash);
		//>>>>>>>ISSUES HERE>>>>>>>>>///
			//for(int loc = 0; loc<hash-1;loc++){
			
		//<<<<<<<<<<<<<<<<<<<<<<<<<<<///
			
			
			String[] s = availableServers.get(i);
			System.out.println(i);
			System.out.println(s[0]+" "+s[1]);
			System.out.println(availableServers.size());
			String m = s[0]+":"+s[1];
			udpUtil.sendString(m);
			//java.util.Iterator<Integer> i = availableServers.keySet().iterator();
			//Integer key = null;	
			/*Iterator<Integer> it = (Iterator) availableServers.keySet().iterator();
			
			for(int i = 0; i<hash; i++){ 
				key = it.next();
				System.out.println(key);
				//System.out.printf("Key : %s and Value: %s %n", server.getKey(), server.getValue());
			}*/
			//String[] s = availableServers.get(key);
			//String[] s = availableServers.get(Integer.valueOf(0));
			//System.out.println(key+" "+s[0]+" "+s[1]);
			//System.out.println(key);
		}else{
			udpUtil.sendString("none");
		}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		for(char c: subs.toCharArray()){
			id = id+(c-96);
		}
		
		return id%n;
	}

}
