import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class PlotLibrary {

    public PlotLibrary(){

    }

    public static ArrayList<Point> PointsFromCsv(String filename) {
        ArrayList<Point> data = null;
        try {
            data = new ArrayList<>();
            String file = filename;
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            for (String line; ((line = br.readLine()) != null); ) {
                String[] lineData = line.split(",");
                double x = Double.parseDouble(lineData[0]);
                double y = Double.parseDouble(lineData[1]);
                data.add(new Point(x, y));
            }
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void plotToCSV(ArrayList<Point> table, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Write the headers
            writer.write("X,Y\n");

            for(int i=0; i < table.size(); i++) {
                Point current = table.get(i);
                writer.write(current.getX()+ "," + current.getY() + "\n");
            }
        } catch(IOException e) {
            System.out.println("An error occurred while writing to the CSV file");
            e.printStackTrace();
        }
    }


    public static ArrayList<Point> plotParabola(double c, int n, int size, double density){
        int number_of_points = (int)(size * density);
        double left_bound = -size/2;
        double right_bound = size/2;
        double interval = (double)size/number_of_points;
        ArrayList<Point> table = new ArrayList<>();
        for(int i = 0; i <= number_of_points; i++){
            double x = left_bound + (interval * i);
            double y = c * Math.pow(x ,n);
            table.add(new Point(x, y));
        }
        return table;
}
    public static ArrayList<Point> Salt(ArrayList<Point> points, double salinity){
        ArrayList<Point> salted_points = points;
        Random gen = new Random();
        for(int i = 0; i < points.size(); i++){
            Point current = salted_points.get(i);
            double salted_y = current.getY() + (salinity * gen.nextDouble(-1,1));
            current.setY(salted_y);
        }
        return salted_points;
    }

    public static ArrayList<Point> Smooth(ArrayList<Point> points, int window) {
        ArrayList<Point> smoothed_points = new ArrayList<>();

        for (int i = 0; i < points.size(); i++) {
            double sum = 0;
            int count = 0;

            // Calculate the start and end indices for the window
            int start_index = Math.max(0, i - window);
            int end_index = Math.min(points.size() - 1, i + window);

            // Sum the y values within the window
            for (int j = start_index; j <= end_index; j++) {
                sum += points.get(j).getY();
                count++;
            }

            // Calculate the average y value
            double avg_y = (count > 0) ? sum / count : 0;

            // Create a new point with the original x value and the averaged y value
            Point smoothed_point = new Point(points.get(i).getX(), avg_y);
            smoothed_points.add(smoothed_point);
        }

        return smoothed_points;
    }

}