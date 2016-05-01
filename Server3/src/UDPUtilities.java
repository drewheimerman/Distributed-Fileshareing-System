import java.util.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;


public class UDPUtilities {
	
	private DatagramSocket incomingSocket;
	private int selfPort;
	
	private InetAddress destination;
	private int destPort;
	
	public UDPUtilities(int selfPort, InetAddress dIP, int destPort){
		destination = dIP;
		this.destPort = destPort;
		this.selfPort = selfPort;
		setup();
	}
	public UDPUtilities(int selfPort){
		this.selfPort = selfPort;
		setup();
	}
	public UDPUtilities(){
		try {
			incomingSocket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
/*-----Getters and Setters------*/
	
	public InetAddress getDestination() {
		return destination;
	}

	public void setDestination(InetAddress destination) {
		this.destination = destination;
	}

	public int getDestPort() {
		return destPort;
	}

	public void setDestPort(int destPort) {
		this.destPort = destPort;
	}
	
	public DatagramSocket getIncomingSocket() {
		return incomingSocket;
	}

	public void setIncomingSocket(DatagramSocket incomingSocket) {
		this.incomingSocket = incomingSocket;
	}

	public int getSelfPort() {
		return selfPort;
	}

	public void setSelfPort(int selfPort) {
		this.selfPort = selfPort;
	}
	
/*--------UDP Actions-----------*/
	
	public void close(){
		incomingSocket.close();
	}
	
	public void sendString(String s){
		byte[] buffer = new byte[1024];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		try {
			dos.writeUTF(s);
			buffer = baos.toByteArray();
			DatagramPacket p = new DatagramPacket(buffer, buffer.length, destination, destPort);
			incomingSocket.send(p);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	public void sendBytes(byte[] b) throws IOException{
		if(b.length<1024){
			DatagramPacket packet = new DatagramPacket(b, b.length, destination, destPort);
			incomingSocket.send(packet);
			System.out.println("made it here");
		}else{
			System.err.println("The byte array is too big to send.");
		}
	}
	
	private void setup(){	
		try{
			if(incomingSocket == null)
				incomingSocket = new DatagramSocket(selfPort);
			
		}catch(IOException e){
			
		}
	}
	public DatagramPacket listen() throws IOException{
			byte[] buff = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buff, buff.length);
			incomingSocket.receive(packet);
			
			//System.out.println("listener");
			return packet;
		
	}
	public String readPacketString(DatagramPacket packet){
		try{
			ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
			DataInputStream din = new DataInputStream(bais);
			String s = din.readUTF();
			//System.out.println(s);
			return s;
		}catch(IOException e){
			System.out.println("fucked");
			e.printStackTrace();
			return null;
		}
		
	}
	public int readPacketInt(DatagramPacket packet){
		try{
			ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
			DataInputStream din = new DataInputStream(bais);
			int s = din.readInt();
			//System.out.println(s);
			return s;
		}catch(IOException e){
			System.out.println("fucked");
			e.printStackTrace();
			return -1;
		}
		
	}
	
	
	
}
