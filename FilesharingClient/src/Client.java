import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {

	private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	
	public static void main(String[] args) throws IOException{
		
		/*---Set up networking. UDP---*/
		
		UDPUtilities utils = new UDPUtilities();
		utils.setDestination(InetAddress.getByName(args[0]));
		utils.setDestPort(7660);
		
		String action;
		String filename;
		
		/*----Scan input stream for action and filename----*/
		
		do{
			System.out.println("Upload or Download file?");
			action = readKeyboardInput();
			action = action.replaceAll("[^A-Za-z0-9]", "");
			action = action.toLowerCase();
			//System.out.println(action);
		}while(!action.equals("upload") && !action.equals("download"));
		do{
			System.out.print("Filename (with extention):");
			filename = readKeyboardInput();
			filename = filename.replaceAll("[^A-Za-z0-9.]", "");
			filename = filename.toLowerCase();
			//System.out.println(action);
		}while(filename.isEmpty());
		
		/*----Start client thread which then makes connection to Broker/RM----*/
		
		ClientThread clientThread = new ClientThread(lock, utils, action, filename);
		Thread t = new Thread(clientThread);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static String readKeyboardInput() throws IOException {
		BufferedReader buffreader = new BufferedReader(new InputStreamReader(System.in));
		return buffreader.readLine();
	}
	public static String getExtension(String filename){
		
		String ext ="";
		
		return ext;
	}
}
