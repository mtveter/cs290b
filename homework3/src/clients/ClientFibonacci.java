package clients;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ClientFibonacci extends Client<Integer> {

	/** Generated Serial ID */
	private static final long serialVersionUID = 1L;
	/** The N'th fibonacci number*/
//	public static final int N = 16;
	public static final int N = 10;
	/** Join Counter of the Closure used for FibonacciJob */
	public static final int joinCounter = 2;

	public ClientFibonacci(String domainName)
			throws RemoteException, NotBoundException, MalformedURLException {
		super("Fibonacci", domainName, new FibonacciJob(N));
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) throws Exception{
		// If no argument is passed, then connect to local host, otherwise to IPv4 specified 
    	String domain;
		if(args.length > 0){
			domain = args[0];
		}
		else{domain = "localhost";}
    	
        System.setSecurityManager( new SecurityManager() );
        final ClientFibonacci client = new ClientFibonacci(domain);
        System.out.println("Generating fibonacci number for N = " + N);
        client.begin();
        final Integer value = (Integer) client.runJob();
        System.out.println("Fibonacci Value : " + value);
        client.end();
	}
	
}