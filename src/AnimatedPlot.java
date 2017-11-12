import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by anonymous on 01/04/17.
 */
public class AnimatedPlot extends JPanel {
    // Container box's width and height
    private static final int BOX_WIDTH = 360;
    private static final int BOX_HEIGHT = 360;

    private int rocketRadius = 5; // radius of the oval representing the rocket.
    private int mercuryRadius = 5;
    private int venusRadius = 7;
    private int earthRadius = 12;
    private int marsRadius = 9;
    private int jupiterRadius = 17;
    private int sunRadius = 20;

    private Color BROWN = new Color(156, 93, 82);


    private double dt; //time between consecutive frames.

    private int[] pos; //position in euclidean space.
    private int T = 0; //keeps track of the time.
    private double T0;
    private double max;

    /** Constructor to create the UI components and init game objects. */
    protected AnimatedPlot(ArrayList<Double> vect, double[] Tif, double m) {
        setPreferredSize(new Dimension(2*BOX_WIDTH, 2*BOX_HEIGHT)); //set dimensions
        int N;
        N=vect.size();

        //we load the obtained results in a new vector easier to handle.
        pos = new int[N];

        //we declare how much time has passed between steps and where they start.
        dt = 2*(Tif[2]-Tif[0])/N;
        T0 = Tif[0];

        //we define a dimensional parameter.
        max = m;

        //get the adequate elements in order for the program to plot the simulation at a given speed.
        for (T=0; 2*T<N; T++){
            //points in euclidean space x and y resized to fill the panel
            pos[2*T] = (int) (BOX_WIDTH*(1+vect.get(2*T)/m));
            pos[2*T+1] = (int) (BOX_HEIGHT*(1-vect.get(2*T+1)/m));
        }

        // Start the ball bouncing (in its own thread)
        Thread gameThread = new Thread() {
                public void run() {
                    while (true) { // Execute a hole loop
                        for (T=0; T<pos.length/2; T++){
                            // Refresh the display
                            revalidate();
                            repaint(); // Callback paintComponent()

                            // Delay for timing control and give other threads a chance
                            try {
                                Thread.sleep(1);  // milliseconds
                            } catch (InterruptedException ex) { }
                        }
                        T0 = Tif[0];
                    }
                }
            };
            gameThread.start();  // Callback run()
        }

        /** Custom rendering codes for drawing the JPanel */
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);    // Paint background

            //we are going to scale the found trajectories in order for them to fit the panel.
            // Draw the box
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, 2*BOX_WIDTH+2, 2*BOX_HEIGHT+2);


            //draw the rocket.
            g.setColor(Color.WHITE);
            g.fillOval(pos[2*T], pos[2*T+1], rocketRadius, rocketRadius);

            //draw the planets and Sun.
            g.setColor(Color.YELLOW); //Sun
            g.fillOval(BOX_WIDTH, BOX_HEIGHT, sunRadius, sunRadius);

            g.setColor(Color.GRAY); //Mercury
            g.fillOval((int) (BOX_WIDTH*(1+57.9*Math.cos(T0*2*Math.PI/88)/max)), (int) (BOX_HEIGHT*(1-57.9*Math.sin(T0*2*Math.PI/88)/max)), mercuryRadius, mercuryRadius);

            g.setColor(Color.GREEN); //Venus
            g.fillOval((int) (BOX_WIDTH*(1+108.2*Math.cos(T0*2*Math.PI/224.7)/max)), (int) (BOX_HEIGHT*(1-108.2*Math.sin(T0*2*Math.PI/224.7)/max)), venusRadius, venusRadius);

            g.setColor(Color.BLUE); //Earth
            g.fillOval((int) (BOX_WIDTH*(1+149.6*Math.cos(T0*2*Math.PI/365.2)/max)), (int) (BOX_HEIGHT*(1-149.6*Math.sin(T0*2*Math.PI/365.2)/max)), earthRadius, earthRadius);

            g.setColor(Color.RED); //Mars
            g.fillOval((int) (BOX_WIDTH*(1+227.9*Math.cos(T0*2*Math.PI/687)/max)), (int) (BOX_HEIGHT*(1-227.9*Math.sin(T0*2*Math.PI/687)/max)), marsRadius, marsRadius);

            g.setColor(BROWN); //Jupiter
            g.fillOval((int) (BOX_WIDTH*(1+778.6*Math.cos(T0*2*Math.PI/4331)/max)), (int) (BOX_HEIGHT*(1-778.6*Math.sin(T0*2*Math.PI/4331)/max)), jupiterRadius, jupiterRadius);


            //draw the time passed
            g.setColor(Color.GREEN);
            g.drawString(String.format("Day %.2f", T0), 1, 12);

            T0 += dt; //actualize the time.
        }
}
