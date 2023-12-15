import org.apache.commons.math4.legacy.analysis.*;
import org.apache.commons.math4.legacy.stat.descriptive.DescriptiveStatistics;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.ArrayList;

public class ApachePSS {

    public ApachePSS(){
    }

    public static ArrayList<Point> SineGenerator(int numberOfPoints, double start, double end){
        UnivariateFunction sine = x -> Math.sin(x);
        ArrayList<Point> points = new ArrayList<>();

        if(end<=start){
            return null;
        }

        double step = (end - start) / (numberOfPoints - 1);

        for (int i = 0; i < numberOfPoints; i++) {
            double x = start + i * step;
            double y = sine.value(x);
            points.add(new Point(x, y));
        }
        return points;
    }

    public static ArrayList<Point> Salter(ArrayList<Point> unsalted, double saltiness) {
        return PlotLibrary.Salt(unsalted, saltiness);
    }

    public static ArrayList<Point> Smoother(ArrayList<Point> points, int windowSize) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        stats.setWindowSize(windowSize);
        ArrayList<Point> smoothed = new ArrayList<>();

        for(int i = 0; i < points.size(); i++){
            Point point = points.get(i);
            stats.addValue(point.getY());
            smoothed.add(new Point(point.getX(),stats.getMean()));
        }
        return smoothed;
    }

    public static void createGraph(ArrayList<Point> points, String name){
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("Points");
        for(int i = 0; i < points.size(); i++){
            double x = points.get(i).getX();
            double y = points.get(i).getY();
            series.add(x, y);
        }
        dataset.addSeries(series);

        JFreeChart scatterPlot = ChartFactory.createScatterPlot((name +" Scatter Plot"), "x", "y", dataset);
        ChartPanel chartPanel = new ChartPanel(scatterPlot);
        JFrame frame = new JFrame();
        frame.add(chartPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        ArrayList<Point> points = SineGenerator(500, 0, 2 * Math.PI);
        createGraph(points, "Sine Wave");
        ArrayList<Point> salted = Salter(points,3);
        createGraph(salted,"Salted Sine Wave");
        int windowSize = 110;
        ArrayList<Point> smoothed = Smoother(salted,110);
        createGraph(smoothed,"Smoothed Sine Wave (window="+windowSize+")");
    }
}
