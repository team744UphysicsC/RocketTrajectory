/**
 * Created by anonymous on 01/04/17.
 */
public class Planet {

    //declare parameters of the planet that we are going to use.
    private String name; //planet's name.

    //some constants.
    protected double sunDistance;
    private double mass;
    protected double revolutionVelocity;

    //planet's position.
    protected double xpos;
    protected double ypos;
    protected double degree = 0;

    public Planet(String n, double distance, double planetMass, double angularVelocity, double time){
        double G = 4.98217402368E-6; //Gravitational constant in (E6 Km)³ (E22 kg)⁻¹ day⁻².
        name = n;
        sunDistance = distance; //units E6 Km
        mass = planetMass*G; //units (E6 Km)³ day⁻²
        revolutionVelocity = angularVelocity; //units radian/day
        degree = angularVelocity*time; //units radians
        xpos = distance*Math.cos(degree); //units E6 Km
        ypos = distance*Math.sin(degree); //units E6 Km
    }

    protected void setTime(double t){
        //we place the planet where it would be at time t.
        degree = t*revolutionVelocity;
        xpos = sunDistance*Math.cos(degree);
        ypos = sunDistance*Math.sin(degree);
    }

    protected double[] GravPull(double x, double y, double dt){
        //declare the array where we will store the final solution.
        double[] Pull = new double[2]; //array to store the gravitational pull.

        //define two auxiliary parameters.
        double[] relDist = new double[2]; //auxiliary array to measure relative distance.
        double d; //module of distance.

        //we measure the relative distance between the planet and the rocket.
        relDist[0] = xpos-x;
        relDist[1] = ypos-y;
        //we measure the distance moduli to the third potency.
        d = Math.pow(relDist[0]*relDist[0]+relDist[1]*relDist[1], 3./2);

        Pull[0] = mass*relDist[0]/d;
        Pull[1] = mass*relDist[1]/d;

        //we slightly move the planet
        degree += dt*revolutionVelocity;
        xpos = sunDistance*Math.cos(degree);
        ypos = sunDistance*Math.sin(degree);

        //return the measured pull.
        return (Pull);
    }
}
