import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.net.*;

public class Updater implements Runnable {

	
	private UDPUtilities udpUtils;
	private ReentrantReadWriteLock lock;
	
	private String filename;
	
	public Updater(ReentrantReadWriteLock l, UDPUtilities u, String f){
		udpUtils = u;
		lock = l;
		filename = f;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		InetAddress rmIP = udpUtils.getDestination();
		InetAddress serverIP = null;
		int serverUDPPort = 0;
		int serverTCPPort = 0;
		
		try{
			//utils.setDestination(rmIP);
			//utils.setDestPort(rmPort);
			//udpUtils.setDestPort(7778);
			udpUtils.sendString(filename);
			
			System.out.println("filename sent");
			
			DatagramPacket p = udpUtils.listen();
			String reply = udpUtils.readPacketString(p);
			System.out.println("Server: "+reply);
			
			Pattern pattern = Pattern.compile("^\\s*(.*?):(\\d+)\\s*$");
			Matcher matcher = pattern.matcher(reply);
			
			if(matcher.matches()){
				serverIP = InetAddress.getByName(matcher.group(1));
				serverUDPPort = Integer.parseInt(matcher.group(2));
			}else{
				System.err.println("Received a bad host. Aborting");
				System.exit(0);
				
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		try{
			udpUtils.setDestination(serverIP);
			udpUtils.setDestPort(serverUDPPort);
			udpUtils.sendString(filename);
			
			System.out.println("filename sent to server (General UDP)");
			
			DatagramPacket p = udpUtils.listen();
			String reply = udpUtils.readPacketString(p);
			System.out.println(reply);
			
			//Pattern pattern = Pattern.compile("^\\s*(.*?):(\\d+)\\s*$");
			//Matcher matcher = pattern.matcher(reply);
		
			serverTCPPort = Integer.parseInt(reply);
			
		}catch(IOException e){
			e.printStackTrace();
		}
		//^^^Clean^^^//
		
		udpUtils.setDestination(rmIP);
		udpUtils.setDestPort(7778);
		
		
		/*-----Initialize TCP Socket-----*/
		
		try{
			Socket clientSocket = new Socket(serverIP, serverTCPPort);
			DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
			DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
			//send the action, filename to the server
			
			
			/*-----Download from the Server-----*/
			
			updater(filename);
			
			
			
			dos.write(("update\n").getBytes());
			dos.write((filename+"\n").getBytes());
			/*FileOutputStream fos = new FileOutputStream("./"+filename);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			System.err.println("Updater: "+filename);*/
			receiveFile(dis, filename);
			
			/*bos.flush();
			bos.close();*/
			
			
		/*-----Flush and close-----*/
			
			
			dos.flush();
			
			clientSocket.close();
			//udpUtils.close();
			
			
			
			
		
		/*try{
			
			udpUtils.sendString(filename);
			
			System.out.println("filename sent 1");
			
			DatagramPacket p = udpUtils.listen();
			String reply = udpUtils.readPacketString(p);
			System.out.println(reply);
			
			Pattern pattern = Pattern.compile("^\\s*(.*?):(\\d+)\\s*$");
			Matcher matcher = pattern.matcher(reply);
			
			if(matcher.matches()){
				serverIP = InetAddress.getByName(matcher.group(1));
				serverUDPPort = Integer.parseInt(matcher.group(2));
			}else{
				System.err.println("Received a bad host. Aborting");
				System.exit(0);
				
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		
		try{
			udpUtils.setDestination(serverIP);
			udpUtils.setDestPort(serverUDPPort);
			udpUtils.sendString(filename);
			
			System.out.println("filename sent 2");
			
			DatagramPacket p = udpUtils.listen();
			String reply = udpUtils.readPacketString(p);
			System.out.println(reply);
			
			//Pattern pattern = Pattern.compile("^\\s*(.*?):(\\d+)\\s*$");
			//Matcher matcher = pattern.matcher(reply);
		
			serverTCPPort = Integer.parseInt(reply);
			
		}catch(IOException e){
			e.printStackTrace();
		}
		
		
		try{
			
			
			Socket clientSocket = new Socket(serverIP, serverTCPPort);
			DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
			DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
			//send the action, filename to the server
			
			//dos.write(("update"+"\n").getBytes());
			//dos.write((redfilename+"\n").getBytes());
			
			String redFilename = filename.substring(filename.indexOf('_')+1);
			File file = null;
			File dir = new File("./");
			File[] directory = dir.listFiles();
			for(File f: directory){
				//System.out.println(f.getName());
				
				int version = f.getName().indexOf('_');
				String name;
				
				if(version!=-1){
					name = f.getName().substring(version+1);
					if(f.isFile() && name.equals(redFilename)){
						System.out.println(f.getName());
						//count = Integer.parseInt(f.getName().substring(0, version));
						//count++;
						
						f.delete();
						//file = f;
						break;
						
					}
				}else{
					name = f.getName();
					if(f.isFile() && name.equals(filename)){
						System.out.println(f.getName());
						file = f;
						break;
						
					}
				}
				
			}
			
			dos.write(("update"+"\n").getBytes());
			dos.write((redFilename+"\n").getBytes());
			
			FileOutputStream fos = new FileOutputStream("./"+filename);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			System.out.println(filename);
			receiveFile(dis, fos, bos);
			
			bos.flush();
			bos.close();
			
			dos.close();*/
		}catch(IOException e){
			
		}
	}
	
	private synchronized void updater(String filename){
		File file;
		File dir = new File("./");
		for(File f: dir.listFiles()){
			//System.out.println(f.getName());
			
			int version = f.getName().indexOf('_');
			String name;
			String reducedFilename;
			
			int v = filename.indexOf('_');
			if(v!=-1){
				reducedFilename = filename.substring(v+1);
			}else{
				reducedFilename = filename;
			}
			if(version!=-1){
				name = f.getName().substring(version+1);
			}else{
				name = f.getName();
			}
				if(f.isFile() && name.equals(reducedFilename)){
					System.err.println(f.getName()+" "+reducedFilename);
					//count = Integer.parseInt(f.getName().substring(0, version));
					//count++;
					
					f.delete();
					//file = f;
					break;
					
				}
			
			
		}
	}
	
	private synchronized void receiveFile(DataInputStream dis, String filename){
		//lock.writeLock().lock();
		try{
			
			FileOutputStream fos = new FileOutputStream("./"+filename);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			System.err.println("Updater: "+filename);
			//receiveFile(dis, fos, bos);
			
			System.out.println("Reading");
			byte[] b = new byte[128];
			int size = 0;
			try{
				size = dis.read(b);
				while(size != -1){
					bos.write(b, 0, size);
					System.out.println(b.length);
					for(byte a: b){
						System.out.print(a);
					}
					size = dis.read(b);
				}
			}catch(IOException e){
				e.printStackTrace();
			}
			
			bos.flush();
			bos.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			//transactions.add(filename);
			
			//lock.writeLock().unlock();
		}
	}

}
xception e){
			e.printStackTrac