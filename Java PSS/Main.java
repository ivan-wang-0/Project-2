import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        ArrayList<Point> plot = PlotLibrary.plotParabola(0.05, 2, 100, 0.7);
//        PlotLibrary.plotToCSV(plot, "Plot_to_CSV.csv");
        ArrayList<Point> salted = PlotLibrary.Salt(plot,50);
        PlotLibrary.plotToCSV(salted, "Salted.csv");
        ArrayList<Point> smoothed = PlotLibrary.Smooth(salted, 5);
        PlotLibrary.plotToCSV(smoothed, "Smoothed5.csv");
        ArrayList<Point> smoothed2 = PlotLibrary.Smooth(salted, 10);
        PlotLibrary.plotToCSV(smoothed2, "Smoothed10.csv");
        ArrayList<Point> smoothed3 = PlotLibrary.Smooth(salted, 1);
        PlotLibrary.plotToCSV(smoothed3, "Smoothed1.csv");
        ArrayList<Point> smoothed4 = PlotLibrary.Smooth(salted, 20);
        PlotLibrary.plotToCSV(smoothed4, "Smoothed20.csv");
    }
}