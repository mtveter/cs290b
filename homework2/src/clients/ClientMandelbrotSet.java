package clients;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author Peter Cappello
 */
public class ClientMandelbrotSet extends Client<Integer[][]>
{
    /** Generated serial ID */
	private static final long serialVersionUID = 6880792391195873610L;
	
	/* Variables for homework1 */
//	private static final double LOWER_LEFT_X_HW1 = -2.0;
//    private static final double LOWER_LEFT_Y_HW1 = -2.0;
//    private static final double EDGE_LENGTH_HW1 = 4.0;
//    private static final int N_PIXELS_HW1 = 256;
//    private static final int ITERATION_LIMIT_HW1 = 64;
    
    /* Variables for homework2 */
 	private static final double LOWER_LEFT_X_HW2 = -0.7510975859375;
    private static final double LOWER_LEFT_Y_HW2 = 0.1315680625;
    private static final double EDGE_LENGTH_HW2 = 0.01611;
    private static final int N_PIXELS_HW2 = 1024;
    private static final int ITERATION_LIMIT_HW2 = 512;
    
    
    
    public ClientMandelbrotSet(String args) throws RemoteException, NotBoundException, MalformedURLException 
    { 	
    	super( "Mandelbrot Set Visualizer", args,
                new MandelbrotJob( LOWER_LEFT_X_HW2, LOWER_LEFT_Y_HW2, EDGE_LENGTH_HW2, N_PIXELS_HW2, 
                                                        ITERATION_LIMIT_HW2) ); 
//        super( "Mandelbrot Set Visualizer", args,
//               new TaskMandelbrotSet( LOWER_LEFT_X_HW1, LOWER_LEFT_Y_HW1, EDGE_LENGTH_HW1, N_PIXELS_HW1, 
//                                                       ITERATION_LIMIT_HW1) ); 
    }
    
    
    
    /**
     * Run the MandelbrotSet visualizer client.
     * @param args unused 
     * @throws java.rmi.RemoteException 
     */
    public static void main( String[] args ) throws Exception
    {  
    	// If no argument is passed, then connect to local host, otherwise to IPv4 specified 
    	String domain;
		if(args.length > 0){
			domain = args[0];
		}
		else{domain = "localhost";}
    	
        System.setSecurityManager( new SecurityManager() );
        final ClientMandelbrotSet client = new ClientMandelbrotSet(domain);
        client.begin();
        Integer[][] value = (Integer[][]) client.runJob();
        System.out.println("This is the value return as result to MandelbrotJob: " + value);
        client.add( client.getLabel( value ) );
        client.end();
    }
    
    public JLabel getLabel( Integer[][] counts )
    {
        final Image image = new BufferedImage( N_PIXELS_HW2, N_PIXELS_HW2, BufferedImage.TYPE_INT_ARGB );
        final Graphics graphics = image.getGraphics();
        for ( int i = 0; i < counts.length; i++ )
            for ( int j = 0; j < counts.length; j++ )
            {
                graphics.setColor( getColor( counts[i][j] ) );
                graphics.fillRect( i, N_PIXELS_HW2 - j, 1, 1 );
            }
        final ImageIcon imageIcon = new ImageIcon( image );
        return new JLabel( imageIcon );
    }
    
    private Color getColor( int iterationCount )
    {
        return iterationCount == ITERATION_LIMIT_HW2 ? Color.BLACK : Color.WHITE;
    }
}