package api;

import java.rmi.RemoteException;

import clients.ClientEuclideanTsp;
import system.ComputerImpl;
import system.SpaceImpl;

public class runner implements Runnable{
	
	public runner() {
		// TODO Auto-generated constructor stub
	}
	
	
public static void main(String[] args) throws Exception {
	
	//SpaceImpl.main(args);
	//ComputerImpl.main(args);
	//ClientEuclideanTsp.main(args);
	runner r = new runner();
	r.run();
	r.run();
	
}


@Override
public void run() {
	// TODO Auto-generated method stub
	
	System.out.println("hello");
	
}
}
