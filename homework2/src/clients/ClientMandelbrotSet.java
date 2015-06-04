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
//	private static final double LOWER_LEFT_X_HW2 = -2.0;
//    private static final double LOWER_LEFT_Y_HW2 = -2.0;
//    private static final double EDGE_LENGTH_HW2 = 4.0;
//    private static final int N_PIXELS_HW2 = 256;
//    private static final int ITERATION_LIMIT_HW2 = 64;
    
    /* Variables for homework2 */
	/** X-coordinate of lower left corner of a square in the complex plane */
 	private static final double LOWER_LEFT_X_HW2 = -0.7510975859375;
 	/** Y-coordinate of lower left corner of a square in the complex plane */
    private static final double LOWER_LEFT_Y_HW2 = 0.1315680625;
    /** The edge length of a square in the complex plane, whose sides are parallel to the axes */
    private static final double EDGE_LENGTH_HW2 = 0.01611;
    /** An integer such that the square region of the complex plane is subdivided into n X n squares,
	 *  each of which is visualized by 1 pixel*/
    private static final int N_PIXELS_HW2 = 1024;
    /** The representative point of a region that is considered to be in the Mandelbrot set */
    private static final int ITERATION_LIMIT_HW2 = 512;
    
    
    
    public ClientMandelbrotSet(String args) throws RemoteException, NotBoundException, MalformedURLException 
    { 	
    	super( "Mandelbrot Set Visualizer", args,
                new MandelbrotJob( LOWER_LEFT_X_HW2, LOWER_LEFT_Y_HW2, EDGE_LENGTH_HW2, N_PIXELS_HW2, 
                                                        ITERATION_LIMIT_HW2) );  
    }
    /**
     * Run a Mandebrot Set visualizer Client
     * @param args 				IP domain of Space, default is 'localhost'
     * @throws RemoteException If there is a connection error  
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
//        System.out.println("This is the value return as result to MandelbrotJob: " + value);
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