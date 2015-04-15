package clients;
import api.Space;
import api.Task;
import system.ComputerImpl;
import system.SpaceImpl;

import java.awt.BorderLayout;
import java.awt.Container;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

/**
 *
 * @author Peter Cappello
 * @param <T> return type the Task that this Client executes.
 */
public class Client<T> extends JFrame
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 4825731753313244149L;
	final protected Job<?> job;
          final private Space space;
                protected T taskReturnValue;
                private long clientStartTime;
    
    public Client( final String title, final String domainName, final Job<?> job ) 
            throws RemoteException, NotBoundException, MalformedURLException
    {     
        this.job = job;
        setTitle( title );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        
        String url = "rmi://" + domainName + ":" + Space.PORT + "/" + Space.SERVICE_NAME;
       space = ( domainName == null ) ? new SpaceImpl() : (Space) Naming.lookup( url );
    }
    
    public void begin() { clientStartTime = System.nanoTime(); }
    
    public void end() 
    { 
        Logger.getLogger( Client.class.getCanonicalName() )
            .log(Level.INFO, "Client time: {0} ms.", ( System.nanoTime() - clientStartTime) / 1000000 );
    }
    
    public void add( final JLabel jLabel )
    {
        final Container container = getContentPane();
        container.setLayout( new BorderLayout() );
        container.add( new JScrollPane( jLabel ), BorderLayout.CENTER );
        pack();
        setVisible( true );
    }
    
    public Object runJob() throws RemoteException
    {
        final long taskStartTime = System.nanoTime();
        
        // Generate Tasks and send to Space
        job.generateTasks(space);
        // Collect results from Space after Computer(s) have completed calculation
        final Object value = job.collectResults(space);
        
        final long taskRunTime = ( System.nanoTime() - taskStartTime ) / 1000000;
        Logger.getLogger( Client.class.getCanonicalName() )
            .log(Level.INFO, "Task {0}Task time: {1} ms.", new Object[]{ job, taskRunTime } );
        return value;
    }
}
