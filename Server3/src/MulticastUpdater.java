import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MulticastUpdater implements Runnable {

	private ReentrantReadWriteLock lock;
	private ServerMulticast mUtils;
	private UDPUtilities udpUtils;
	private InetAddress serverIP;
	private int serverUDPPort;
	
	private InetAddress rmIP;
	private int rmPort;
	
	public MulticastUpdater(ReentrantReadWriteLock l, ServerMulticast s, InetAddress i, int p){
		lock = l;
		mUtils = s;
		rmIP = i;
		rmPort = p;
	}
	
	@Override
	public void run() {
		List<String> list = new LinkedList<String>();
		while(true){
			try {
				
				//DatagramPacket p = mUtils.listen();
				//String message = new String(p.getData(), 0, p.getLength());
				//System.err.println(message);
				
				
				
				//byte[] b = new byte[1024];
			
				DatagramPacket  p = mUtils.listen();
				ByteArrayInputStream bais = new ByteArrayInputStream(p.getData());
				ObjectInputStream ois = new ObjectInputStream(bais);
				
				//DatagramPacket p = mUtils.listen();
				
				//System.out.println(p.getData());
				
				File[] primaryFiles = (File[])ois.readObject();
				ois.close();
				File dir = new File("./");
				File[] files = dir.listFiles();
				for(File f : primaryFiles){
					//System.out.print(f.getName()+" ");
					
					//File ff = new File("./"+f.getName());
					if(!f.exists()){
						//System.out.print("NEEDS:"+f.getName()+" ");
						list.add(f.getName());
					}
				}
				
				//InetAddress primaryIP = InetAddress.getByName(p.getAddress().getHostAddress());
				//int primaryPort = 7778;
				//udpUtils.setDestination(primaryIP);
				
				for(String s : list){
					System.out.println("Need: "+s);
					UDPUtilities utils = new UDPUtilities();
					utils.setDestination(rmIP);
					utils.setDestPort(rmPort);
					Updater u = new Updater(lock, utils, s);
					Thread update = new Thread(u);
					update.start();
					System.out.println("Started an update thread for: "+s);
					try{
						update.join();
					}catch(Exception e){
						
					}
				}
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		
		
		/*try{
			
			udpUtils.sendString("updating");
			
			System.out.println("filename sent");
			
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
			//utils.sendString(filename);
			
			System.out.println("filename sent");
			
			DatagramPacket p = utils.listen();
			String reply = utils.readPacketString(p);
			System.out.println(reply);
			
			//Pattern pattern = Pattern.compile("^\\s*(.*?):(\\d+)\\s*$");
			//Matcher matcher = pattern.matcher(reply);
		
			serverTCPPort = Integer.parseInt(reply);
			
		}catch(IOException e){
			e.printStackTrace();
		}*/
		//^^^Clean^^^//
		
		/*-----Initialize TCP Socket-----*/
		
		/*try{
			Socket clientSocket = new Socket(serverIP, serverTCPPort);
			DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
			DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
			//send the action, filename to the server
			
			dos.write((action+"\n").getBytes());
			dos.write((filename+"\n").getBytes());*/
		/*-----Upload to the server------*/
			
			/*File dir = new File("./");
			File file = null;
			
			if(action.equals("upload")){
				// /Users/Andrew/Development/EclipseWorkspace/DistributedFileTransfer/src
				File[] files = new File("./").listFiles();
				
				//System.out.println(System.getProperty("user.dir"));
				for(File f: files){
					//System.out.println(f.getName());
					if(f.isFile() && f.getName().equals(filename)){
						System.out.println(f.getName());
						file = f;
						break;
						
					}
				}
				if(file != null){
					FileInputStream fis = new FileInputStream(file);
					BufferedInputStream bis = new BufferedInputStream(fis);
					System.out.println("Going to write the file to the stream.");
					sendFile(dos, fis, bis, file);
				//dos.writeLong(file.length());
					fis.close();
					bis.close();
				}else{
					System.out.println("There are no files named "+filename+" in this directory.");
				}
				
			}*/
		
		/*-----Download from the Server-----*/
			/*if(action.equals("download")){
				FileOutputStream fos = new FileOutputStream("./"+filename);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				System.out.println(filename);
				receiveFile(dis, fos, bos);
				fos.flush();
				bos.flush();
				fos.close();
				bos.close();
			}*/
			
		/*-----Flush and close-----*/
			
			/*dos.flush();
			
			clientSocket.close();
			utils.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}*/
	
	/*private void sendFile(DataOutputStream dos, FileInputStream fis, BufferedInputStream bis, File file){
		lock.readLock().lock();
		try{
			System.out.println("Starting to write the file to the stream.");
			int bytes = 128;
			long read = 0;
			byte[] b = new byte[bytes];
			while(read < file.length()){
				if(file.length()-read >= bytes){
					read = read + bytes;
				}else{
					bytes = (int)(file.length()-read);
					read = read + bytes;
				}
				try{
					bis.read(b, 0 , bytes);
					System.out.println(b);
					dos.write(b);
				}catch(Exception e){
					e.printStackTrace();
				}
				System.out.println((read/file.length())*100+"%");
			}
		}finally{
			lock.readLock().unlock();
		}
		
	}*/
		
	}
	private void sendFile(DataOutputStream dos, FileInputStream fis, BufferedInputStream bis, File file){
		lock.readLock().lock();
		try{
			int bytes = 128;
			long read = 0;
			byte[] b = new byte[bytes];
			while(read < file.length()){
				if(file.length()-read >= bytes){
					read = read + bytes;
				}else{
					bytes = (int)(file.length()-read);
					read = read + bytes;
				}
				try{
					bis.read(b, 0 , bytes);
					System.out.println(b.length);
					for(byte a: b){
						System.out.print(a);
					}
					dos.write(b);
				}catch(Exception e){
					e.printStackTrace();
				}
				System.out.println((read/file.length())*100+"%");
			}
		}finally{
			lock.readLock().unlock();
		}
		
	}
	private void receiveFile(DataInputStream dis, FileOutputStream fos, BufferedOutputStream bos){
		lock.writeLock().lock();
		try{
			byte[] b = new byte[128];
			int size = 0;
			try{
				size = dis.read(b);
				while(size != -1){
					bos.write(b, 0, size);
					size = dis.read(b);
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}finally{
			lock.writeLock().unlock();
		}
	}

}