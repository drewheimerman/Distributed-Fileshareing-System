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

public class ServerRequestThread implements Runnable {

	private Socket client;
	
	public ServerRequestThread(Socket c){
		client = c;
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
			File file = null;
			
			if(action.equals("download")){
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
					
					sendFile(dos, fis, bis, file);
				//dos.writeLong(file.length());
				}else{
					System.out.println("There are no files named "+filename+" in this directory.");
					dos.write(("There are no files named "+filename+" in this directory.").getBytes());
				}
				
			}
			
		/*-----Download to the Server-----*/
			if(action.equals("upload")){
				FileOutputStream fos = new FileOutputStream("./"+filename);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				System.out.println(filename);
				receiveFile(dis, fos, bos);
				bos.flush();
			}
			
		/*-----Flush and close-----*/
			
			dos.flush();
			
			client.close();
		}catch(IOException e){
			
		}
		
	}
	
	private File readFileStream(){
		
		
		return null;
	}
	
	private void sendFile(DataOutputStream dos, FileInputStream fis, BufferedInputStream bis, File file){
		//lock.writeLock().lock();
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
					dos.write(b);
				}catch(Exception e){
					e.printStackTrace();
				}
				System.out.println((read/file.length())*100+"%");
			}
		}finally{
			//lock.writeLock().unlock();
		}
		
	}
	private void receiveFile(DataInputStream dis, FileOutputStream fos, BufferedOutputStream bos){
		//lock.readLock().lock();
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
			//lock.readLock().unlock();
		}
	}

}
