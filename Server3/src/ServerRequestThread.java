import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
//import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ServerRequestThread implements Runnable {
	private ReentrantReadWriteLock lock;
	//private ServerSocket serverSocket;
	private Socket client;
	private ServerMulticast mUtils;
	//private List transactions;
	public ServerRequestThread(ReentrantReadWriteLock l, Socket c, ServerMulticast s){
		lock = l;
		client = c;
		mUtils = s;
		//transactions = list;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		/*-----Initialize TCP Socket-----*/
	
		try{
			DataOutputStream dos = new DataOutputStream(client.getOutputStream());
			DataInputStream dis = new DataInputStream(client.getInputStream());
			//send the action, filename to the server
			
			BufferedReader br = new BufferedReader(new InputStreamReader(dis));
			String action = br.readLine();
			String filename = br.readLine();
			System.out.println(action);
			System.out.println(filename);
			
			
		/*-----Upload to the server------*/
			
			File dir = new File("./");
			File[] directory = dir.listFiles();
			File file = null;
			
			if(action != null && (action.equals("download")||action.equals("update"))){
				
				// /Users/Andrew/Development/EclipseWorkspace/DistributedFileTransfer/src
				
					
				sendFile(dos, filename);
				//dos.writeLong(file.length());
					
				}else{
					System.out.println("There are no files named "+filename+" in this directory.");
					dos.write(("There are no files named "+filename+" in this directory.").getBytes());
				}
				
			
			
		/*-----Download to the Server-----*/
			if(action != null && action.equals("upload")){
				//dos.write((action+"\n").getBytes());
				//dos.write((filename+"\n").getBytes());
				
				receiveFile(dis, directory, filename);
				
				
			}
			/*if(action.equals("update")){
				File[] files = new File("./").listFiles();
				
				//System.out.println(System.getProperty("user.dir"));
				for(File f: files){
					if(f.isFile()){
						System.out.println(f.getName());
					}
					if(f.isFile() && f.equals(filename)){
						//System.out.println(f.getName());
						file = f;
						break;
						
					}
				}
				
				if(file != null){
					//dos.write((action+"\n").getBytes());
					//dos.write((filename+"\n").getBytes());
					FileInputStream fis = new FileInputStream(file);
					BufferedInputStream bis = new BufferedInputStream(fis);
					
					sendFile(dos, fis, bis, file);
				//dos.writeLong(file.length());
					
				}else{
					System.out.println("There are no files named "+filename+" in this directory.");
					dos.write(("There are no files named "+filename+" in this directory.").getBytes());
				}
				
			}*/
			
			
			
		/*-----Flush and close-----*/
			
			dos.flush();
			directory = dir.listFiles();
			if(directory == null){
				System.err.println("NULL DIRECTORY LISTING");
			}
			
			if(!action.equals("update")){
				mUtils.sendObject(directory);
			}
			
			client.close();
		}catch(IOException e){
			
		}
		
	}
	
	
	
	private synchronized void sendFile(DataOutputStream dos, String filename){
		File file = null;
		File[] files = new File("./").listFiles();
		
		//System.out.println(System.getProperty("user.dir"));
		for(File f: files){
			//System.out.println(f.getName());
			String reducedFilename;
			int v = filename.indexOf('_');
			int version = f.getName().indexOf('_');
			String name;
			if(version!=-1){
				name = f.getName().substring(version+1);
				//System.err.println(name+" ");
			}else{
				name = f.getName();
			}
			
			if(v!=-1){
				reducedFilename = filename.substring(v+1);
			}else{
				reducedFilename = filename;
			}
			if(f.isFile() && name.equals(reducedFilename)){
				System.out.println(f.getName());
				file = f;
				break;
				
			}
		}
		
		
		
		
		//lock.readLock().lock();
		try{
			if(file != null){
				//dos.write((action+"\n").getBytes());
				//dos.write((filename+"\n").getBytes());
				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);
			
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
						//mUtils.sendBytes(b);
						dos.write(b);
					}catch(Exception e){
						e.printStackTrace();
					}
					System.out.println((read/file.length())*100+"%");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			
		}finally{
			//lock.readLock().unlock();
		}
		
	}
	private synchronized void receiveFile(DataInputStream dis, File[] directory, String filename){
		//lock.writeLock().lock();
		File file = null;
		int count = 0;
		for(File f: directory){
			//System.out.println(f.getName());
			
			int version = f.getName().indexOf('_');
			String name;
			
			if(version!=-1){
				name = f.getName().substring(version+1);
				if(f.isFile() && name.equals(filename)){
					System.out.println(f.getName());
					count = Integer.parseInt(f.getName().substring(0, version));
					count++;
					
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
		filename = count+"_"+filename;
		
		/*if(new File("./"+filename).exists()){
			int version = f.getName().indexOf('_');
			if(version!=-1){
				name = f.getName().substring(version+1);
			}else{
				name = f.getName();
			}
		}*/
		
		System.out.println(filename);
		
		
		try{
			FileOutputStream fos = new FileOutputStream("./"+filename);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			
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
