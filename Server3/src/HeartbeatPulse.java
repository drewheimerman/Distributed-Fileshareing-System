import java.io.IOException;
import java.net.DatagramPacket;

public class HeartbeatPulse implements Runnable {
	private UDPUtilities udpUtils;
	
	public HeartbeatPulse(UDPUtilities u){
		udpUtils = u;
	}
	
	@Override
	public void run() {
		
		//udpUtils.sendString("init");
		try {
			DatagramPacket p = udpUtils.listen();
			udpUtils.setDestination(p.getAddress());
			udpUtils.setDestPort(p.getPort());
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		while(true){
			udpUtils.sendString("ping");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
