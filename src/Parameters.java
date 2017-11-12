import org.jfree.data.xy.XYSeries;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;

/**TRIVIA
 * Sun Mass = 198850000 E22 Kg
 * Mercury mass = 33 E22 Kg
 * Mercury distance = 57.9 Gm
 * Venus Mass = 487 E22 Kg
 * Venus distance = 108.2 Gm
 * Earth Mass = 597 E22 Kg
 * Earth distance = 149.6 Gm
 * Mars Mass = 64.2 E22 Kg
 * Mars distance = 227.9 Gm
 * Jupiter Mass = 189800 E22 Kg
 * Jupiter distance = 778.6 Gm
 * G = 6.67408*24*36*24*36 × 10⁻¹² Gm³ (E22 kg)-1 day-2
  **/

/**
 * Created by anonymous on 01/04/17.
 */
public class Parameters extends JFrame{
    //define a set of constants used to define the dimension of the panels
    private static int FrameWidth=600;
    private static int FrameHeight=500;
    private static int ButtonWidth=150;
    private static int ButtonHeight=20;
    private static int FrameCenter=(FrameWidth-ButtonWidth)/2;

    //declare the panel
    private JPanel panel = new JPanel();

    //declare the objects that are going to be shown in the menu page.

    //time settings
    private JLabel T0Label = new JLabel("Rocket launch time (days)=");
    private NumberFormat T0Format;
    private JFormattedTextField T0Field = new JFormattedTextField(T0Format);

    private JLabel T1Label = new JLabel("second burst time (days)=");
    private NumberFormat T1Format;
    private JFormattedTextField T1Field = new JFormattedTextField(T0Format);

    private JLabel T2Label = new JLabel("End simulation time (days)=");
    private NumberFormat T2Format;
    private JFormattedTextField T2Field = new JFormattedTextField(T0Format);

    //first burst info
    private JLabel V0Label = new JLabel("First burst data");

    private JLabel V0mLabel = new JLabel("Intensity of velocity change (km/s)=");//intensity
    private NumberFormat V0mFormat;
    private JFormattedTextField V0mField = new JFormattedTextField(V0mFormat);

    private JLabel V0dLabel = new JLabel("Angle of added velocity respect current (deg)=");//angle
    private NumberFormat V0dFormat;
    private JFormattedTextField V0dField = new JFormattedTextField(V0dFormat);

    //second burst settings
    private JLabel V1Label = new JLabel("First burst data");

    private JLabel V1mLabel = new JLabel("Intensity of velocity change (km/s)=");//intensity
    private NumberFormat V1mFormat;
    private JFormattedTextField V1mField = new JFormattedTextField(V1mFormat);

    private JLabel V1dLabel = new JLabel("Angle of added velocity respect current (deg)=");//angle
    private NumberFormat V1dFormat;
    private JFormattedTextField V1dField = new JFormattedTextField(V1dFormat);

    private JLabel SLabel = new JLabel("Simulation speed =");//simulation speed.
    private NumberFormat SFormat;
    private JFormattedTextField SField = new JFormattedTextField(SFormat);

    private JButton RunButton = new JButton("Run");

    //declare the planets
    private Planet[] Planets = new Planet[5];

    //units in Km³ day⁻²*10¹².
    private double SunMass = 990.7053046087681;

    //define a small time variation in units days
    private double dt = 1./1000;

    private Parameters() {
        //set frame parameters
        setTitle("Simulation parameters");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //set panel parameters
        panel.setLayout(null);
        getContentPane().add(panel);
        setSize(FrameWidth, FrameHeight);
        setResizable(false);
        setVisible(true);

        //place objects in panel
        PlaceAllObjects();

        //define the parameters of the 5 planets.
        //distance units in Km*10⁶ mass units in Kg*10²², angular velocity in rad/days, time in days.
        Planets[0] = new Planet("Mercury", 57.9, 33, 2*Math.PI/88, 0);
        Planets[1] = new Planet("Venus", 108.2, 487, 2*Math.PI/224.7, 0);
        Planets[2] = new Planet("Earth", 149.6, 597, 2*Math.PI/365.2, 0);
        Planets[3] = new Planet("Mars", 227.9, 64.2, 2*Math.PI/687, 0);
        Planets[4] = new Planet("Jupiter", 778.6, 189800, 2*Math.PI/4331, 0);

        RunButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //we copy all the inserted parameters.
                double[] T = new double[3];
                T[0] = Double.parseDouble(T0Field.getText());
                T[1] = Double.parseDouble(T1Field.getText());
                T[2] = Double.parseDouble(T2Field.getText());

                double[] V0 = new double[2];
                V0[0] = 0.0864*Double.parseDouble(V0mField.getText()); //the value multiplying converts from Km/s to Km/day*10⁶
                V0[1] = Math.toRadians(Double.parseDouble(V0dField.getText()));

                double[] V1 = new double[2];
                V1[0] = 0.0864 *Double.parseDouble(V1mField.getText());//the value multiplying converts from Km/s to Km/day*10⁶
                V1[1] = Math.toRadians(Double.parseDouble(V1dField.getText()));

                //Solve the differential equation.
                ArrayList<Double> vect = new ArrayList<Double>();
                SolveOds(T, V0, V1, vect);

                //declare the maximum in the obtained solution
                double m =0;

                //show the solution.
                //store the data in an XYSeries object.
                int i;
                XYSeries X = new XYSeries("", false);
                for (i=0; i<vect.size()/2; i++){
                    X.add(vect.get(2*i), vect.get(2*i+1));
                }

                new StaticPlot(X); //plot the rocket trajectory.

                ArrayList<Double> v = new ArrayList<Double>();
                int n = (int) (Double.parseDouble(SField.getText()));
                for (i=0; 2*n*i+1<vect.size(); i++){
                    v.add(vect.get(2*n*i));
                    v.add(vect.get(2*n*i+1));
                    //actualize the maximum.
                    if (m< Math.abs(vect.get(2*n*i))){ m = Math.abs(vect.get(2*n*i)); }
                    if (m< Math.abs(vect.get(2*n*i+1))){ m = Math.abs(vect.get(2*n*i+1)); }
                }

                //draw an animation representing the rocket trajectory.
                JFrame frame = new JFrame("Rocket movement");
                frame.setContentPane(new AnimatedPlot(v, T, m));
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    //this method solves numerically the necessary differential equation
    private void SolveOds(double[] T, double[] V0, double[] V1, ArrayList<Double> vect){
        int i;
        double t = T[0];

        //we initialize the planets position according to the selected time.
        for (i=0; i<5; i++){Planets[i].setTime(T[0]);}

        //set the initial conditions of position and velocity.
        double[] V = new double[4];
        //length measured in Km*10⁶ speed in Km*10⁶/day.
        V[0] = Planets[2].xpos+0.006052; //it is important to separate the rocket from the planets core and place it on ots surface.
        V[1] = Planets[2].ypos;
        V[2] = -Planets[2].revolutionVelocity*Planets[2].sunDistance*Math.sin(Planets[2].degree)+V0[0]*Math.cos(V0[1]+Planets[2].degree+Math.PI/2);
        V[3] = Planets[2].revolutionVelocity*Planets[2].sunDistance*Math.cos(Planets[2].degree)+V0[0]*Math.sin(V0[1]+Planets[2].degree+Math.PI/2);
        /*V[0] = 149.6;
        V[1] = 0.006052; //it is important to separate the rocket from the planets core and place it on ots surface.
        V[2] = 24*36*4./1000;
        V[3] = 24*36*(29.8+4)/10000;*/

        //we solve numerically the differential equation until the second burst.
        while (t<T[1]){
            vect.add(V[0]); vect.add(V[1]); //add measured parameters to array

            V = RK4(V); //solve using RK4

            t += dt; //make a slight increase in time.
        }

        //we activate the second burst.
        V[2] += V1[0]*Math.cos(V1[1]+Math.atan2(V[3], V[2]));
        V[3] += V1[0]*Math.sin(V1[1]+Math.atan2(V[3], V[2]));

        //we solve the differential equation until the asked time.
        while (t<T[2]){
            vect.add(V[0]); vect.add(V[1]); //add measured parameters to array

            V=RK4(V); //solve using RK4

            t += dt; //make a slight increase in time.
        }
    }

    private double[] RK4(double[] V){
        int i;
        double[] sol = new double[4];
        double[] aux1;

        aux1 = VectorField(V);//first RK4 term.
        for (i=0; i<4; i++){
            sol[i] = aux1[i];
            aux1[i] = V[i]+dt*aux1[i]/2;
        }

        aux1 = VectorField(aux1);//second RK4 term
        for (i=0; i<4; i++){
            sol[i] += 2*aux1[i];
            aux1[i] = V[i]+dt*aux1[i]/2;
        }

        aux1 = VectorField(aux1);//third RK4 term
        for (i=0; i<4; i++){
            sol[i] += 2*aux1[i];
            aux1[i] = V[i]+dt*aux1[i];
        }

        aux1 = VectorField(aux1);//fourth RK4 term
        for (i=0; i<4; i++){
            sol[i] += aux1[i];
            sol[i] *= dt/6; //compute de final difference calculated.
            sol[i] += V[i];
        }

        return (sol);
    }

    private double[] VectorField(double[] V){
        //we declare the solution vector.
        double[] sol = new double [4];

        double[] aux = new double[2]; //auxiliary vector.
        int i;

        //we write the differential equation
        sol[0] = V[2]; sol[1] = V[3]; //the derivative of the first two terms is the velocity.
        sol[2] = 0; sol[3] = 0; //we initialize the derivative of the speed to 0
        for (i=0; i<5; i++){
            aux = Planets[i].GravPull(V[0], V[1], dt);
            sol[2] += aux[0];
            sol[3] += aux[1];
        }

        //we measure the distance moduli to the third potency.
        aux[0] = Math.pow(V[0]*V[0]+V[1]*V[1], 3./2);

        //we compute the sun's gravity pull.
        sol[2] -= SunMass*V[0]/aux[0];
        sol[3] -= SunMass*V[1]/aux[0];

        return (sol);
    }

    //This method's purpose is to place all the objects needed in the panel
    private void PlaceAllObjects(){
        //time parameters
        PlaceLabelTextInPosition(T0Label, T0Field, 1, 1, 1, 11, 0);
        PlaceLabelTextInPosition(T1Label, T1Field, 1, 1, 2, 11, 312);
        PlaceLabelTextInPosition(T2Label, T2Field, 1, 1, 3, 11, 514);

        PlaceLabelInPosition(V0Label, 11, 4);//first burst parameters
        PlaceLabelTextInPosition(V0mLabel, V0mField, 1, 1, 5, 11, 4);
        PlaceLabelTextInPosition(V0dLabel, V0dField, 1, 1, 6, 11, 0);

        PlaceLabelInPosition(V1Label, 11, 7);//second burst parameters
        PlaceLabelTextInPosition(V1mLabel, V1mField, 1, 1, 8, 11, 3);
        PlaceLabelTextInPosition(V1dLabel, V1dField, 1, 1, 9, 11, 0);

        //image speed.
        PlaceLabelTextInPosition(SLabel, SField, 1, 1, 10, 11, 20);

        PlaceButtonInPosition(RunButton, 1, 1, 11, 11); //place the button

        //We refresh the page.
        panel.revalidate();
        panel.repaint();
    }

    //places a button at a specified position of the panel.
    private void PlaceButtonInPosition(JButton button, int Xpos, int Xtot, int Ypos, int Ytot) {
        button.setBounds((2*Xpos-1)*FrameWidth/(2*Xtot)-ButtonWidth/2, FrameHeight*Ypos/(Ytot+2), ButtonWidth, ButtonHeight);
        //add the material button to the existing panel and show it
        panel.add(button);
        button.setVisible(true);
    }

    //places a label in a centered position of the panel.
    private void PlaceLabelInPosition(JLabel label, int Ytot, int Ypos){
        label.setBounds(FrameCenter, FrameHeight*Ypos/(Ytot+2), 2*ButtonWidth, ButtonHeight);
        panel.add(label);
        label.setVisible(true);
    }

    //places a Label and a formatted text at a given position
    private void PlaceLabelTextInPosition(JLabel label, JFormattedTextField field, int Xpos, int Xtot, int Ypos, int Ytot, double value) {
        label.setBounds((2*Xpos-1)*FrameWidth/(2*Xtot)-3*ButtonWidth/2, FrameHeight*Ypos/(Ytot+2), 4*ButtonWidth, ButtonHeight);
        field.setBounds((2*Xpos-1)*FrameWidth/(2*Xtot)+ButtonWidth, FrameHeight*Ypos/(Ytot+2), ButtonWidth / 2, ButtonHeight);
        panel.add(label);
        panel.add(field);
        field.setValue(value);
        label.setVisible(true);
        field.setVisible(true);
    }

    public static void main(String[] args) {
        //create the Settings object.
        new Parameters();
    }
}