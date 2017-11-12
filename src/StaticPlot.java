import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

/**
 * Created by anonymous on 01/04/17.
 */
public class StaticPlot {
    private static XYSeriesCollection dataset = new XYSeriesCollection();
    private static JFrame frame = new JFrame("Rocket trajectory");
    private static JPanel jPanel1 = new JPanel();

    //define the array that is actually going to simulate the heating barr.
    private static JFreeChart chart;

    public StaticPlot(XYSeries serie){
        //we give a name to the new plot.
        //XYSeries series = new XYSeries("Horizontal projection");

        //add the simulation to the existing dataset.
        dataset.addSeries(serie);

        //plot and show it.
        chart = ChartFactory.createXYLineChart("Rocket trajectory",
                "X (Gm)",
                "Y (Gm)",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                false,
                false);

        ChartPanel chartpanel = new ChartPanel(chart);
        chartpanel.setDomainZoomable(true);

        jPanel1.setLayout(new BorderLayout());
        jPanel1.add(chartpanel, BorderLayout.NORTH);

        frame.add(jPanel1);
        frame.pack();
        frame.setVisible(true);
    }
}
