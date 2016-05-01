import java.io.*;
import java.net.*;
import java.util.*;

public class TCPUtilities {
	private ServerSocket serverSocket;
	private Socket clientSocket;
	
	private FileOutputStream fos;
	private FileInputStream fis;
	
	private DataOutputStream dos;
	private DataInputStream dis;
	
	public TCPUtilities(int serverPort) throws IOException{
		serverSocket = new ServerSocket(serverPort);
		//clientSocket = serverSocket.accept();
	}
	public void listen() throws IOException{
		clientSocket = serverSocket.accept();
		dos = new DataOutputStream(clientSocket.getOutputStream());
		dis = new DataInputStream(clientSocket.getInputStream());
	}
	public void sendBytes(byte[] b) throws IOException{
		if(b.length<1024){
			dos.write(b);
		}else{
			System.err.println("Byte[] too large to send.");
		}
	}
	public byte[] receiveBytes() throws IOException{
		byte[] b = new byte[1024];
		dis.read(b);
		return b;
	}
	
	private void receiveFile(){
		//lock.readLock().lock();
		try{
			byte[] b = new byte[128];
			int size = 0;
			try{
				size = dis.read(b);
				while(size != -1){
					//bos.write(b, 0, size);
					size = dis.read(b);
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}finally{
			//lock.readLock().unlock();
		}
	}
}
