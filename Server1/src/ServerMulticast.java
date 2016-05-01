import java.net.*;
import java.io.*;

public class ServerMulticast {

	private MulticastSocket mSocket;
	private InetAddress mIP;
	private int port;
	private int ttl;
	
	public ServerMulticast(InetAddress ip, int port, int ttl){
		try {
			mIP = ip;
			this.port = port;
			this.ttl = ttl;
			
			mSocket = new MulticastSocket(this.port);
			setTTL(ttl);
			mSocket.setInterface(InetAddress.getLocalHost());
			System.out.println(mIP);
			System.out.println("Port: "+port);
			joinGroup();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*----Sending and Receiving Methods----*/
	
	public void sendString(String s) throws IOException{
		byte[] b = s.getBytes();
		DatagramPacket packet = new DatagramPacket(b, b.length, mIP, port);
		mSocket.send(packet);
	}
	
	public void sendBytes(byte[] b) throws IOException{
		DatagramPacket packet = new DatagramPacket(b, b.length, mIP, port);
		mSocket.send(packet);
	}
	
	public void sendObject(Object ob) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		//ObjectOutputStream oos = new ObjectOutputStream(bas);
		
		oos.writeObject(ob);
		sendBytes(baos.toByteArray());
		oos.flush();
		oos.close();
	}
	
	public Object receiveObject() throws Exception{
		byte[] b = new byte[4096];
		DatagramPacket p = new DatagramPacket(b, b.length);
		mSocket.receive(p);
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		ObjectInputStream ois = new ObjectInputStream(bais);
		
		Object o = ois.readObject();
		ois.close();
		return o;
	}
	
	public DatagramPacket listen() throws IOException{
		byte[] b = new byte[4096];
		DatagramPacket packet = new DatagramPacket(b, b.length);
		mSocket.receive(packet);
		return packet;
	}
	
	/*----Getters, Setters, and Misc. Setup----*/
	
	public void joinGroup() throws IOException{
		mSocket.joinGroup(mIP);
	}
	
	public void setMulticastIP(InetAddress ip){
		mIP = ip;
	}
	
	public void setTTL(int t) throws IOException{
		ttl = t;
		mSocket.setTimeToLive(ttl);
	}
	
	public void setPort(int p){
		port = p;
	}
	
	public InetAddress getInetAddress(){
		return mIP;
	}
	public int getPort(){
		return port;
	}
	public void close(){
		mSocket.close();
	}
}
