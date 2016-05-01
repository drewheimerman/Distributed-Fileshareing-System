import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RequestThreadManager implements Runnable {

	private UDPUtilities udpUtil;
	private ExecutorService executor;
	private ConcurrentSkipListMap<Integer, String[]> servers;
	
	public RequestThreadManager(UDPUtilities u, ConcurrentSkipListMap<Integer, String[]> c){
		udpUtil = u;
		executor = Executors.newFixedThreadPool(5);
		servers = c;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			while(true){
				DatagramPacket p = udpUtil.listen();
				//String message = new String(p.getData(), 0, p.getLength());
				System.out.println("RequestThreadManager: "+udpUtil.readPacketString(p));
				executor.execute(new RequestThread(udpUtil, servers, p));
			}
		}catch(IOException e){
			executor.shutdown();
			try {
				executor.awaitTermination(3, TimeUnit.SECONDS);
			} catch (InterruptedException e1) {
				
				e1.printStackTrace();
			}
		}finally{
			executor.shutdownNow();
		}
	}

}
