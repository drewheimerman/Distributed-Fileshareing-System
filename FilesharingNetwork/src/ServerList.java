import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ServerList {
	ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private HashMap serverMap;
	//ConcurrentHashMap hashmap = new ConcurrentHashMap();
	
	public ServerList(){
		serverMap = new HashMap();
	}
	
	public void add(int id, InetAddress ip, int port){
		try{
			rwl.writeLock().lock();
			//serverMap.put(id, {ip, port})
		}finally{
			rwl.writeLock().unlock();
		}
	}
	public void remove(Server s){
		try{
			
			rwl.writeLock().lock();
		}finally{
			rwl.writeLock().unlock();
		}
	}
	public void get(){
		try{
			rwl.readLock().lockInterruptibly();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}finally{
			rwl.readLock().unlock();
		}
	}
}
