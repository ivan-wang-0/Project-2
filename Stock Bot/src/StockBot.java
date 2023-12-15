import org.apache.commons.math4.legacy.stat.descriptive.DescriptiveStatistics;
import java.io.*;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import javax.swing.JFrame;

/**
 * This class represents a stock trading bot capable of executing various trading strategies.
 * It can load stock data, calculate indicators like RSI and moving averages, and simulate trading actions.
 *
 * @author Ivan Wang
 */
public class StockBot {
    private String fileName;
    private List<StockData> originalList;
    private List<StockData> rollingList;
    private List<Double> rollingAvg;
    private List<Double> rollingRSI;
    final private double initBalance;
    private double balance;
    private int sharesOwned;
    private int day;
    private int sum;

    /**
     * Constructor for the StockBot class.
     *
     * @param balance  Initial balance for trading.
     * @param fileName The name of the CSV file containing stock data.
     */
    public StockBot(double balance, String fileName){
        this.fileName = fileName;
        this.balance = balance;
        this.initBalance = balance;
        rollingAvg = new ArrayList<>();
        rollingList = new ArrayList<>();
        rollingRSI = new ArrayList<>();
        sharesOwned = 0;
        sum = 0;
        day = 1;

        try{
            List<StockData> data = new ArrayList<>();
            FileReader fr = new FileReader(this.fileName);
            BufferedReader br = new BufferedReader(fr);
            br.readLine();
            for(String line; ((line = br.readLine()) != null);){
                String[] lineData = line.split(",");
                data.add(new StockData(lineData[0],
                                    Double.parseDouble(lineData[1]),
                                    Double.parseDouble(lineData[2]),
                                    Double.parseDouble(lineData[3]),
                                    Double.parseDouble(lineData[4]),
                                    Double.parseDouble(lineData[5]),
                                    Long.parseLong(lineData[6])));
            }
            br.close();
            this.originalList = data;
            addRsiToList();
            addMaToList(50);
            writeStockDataToCSV((ArrayList<StockData>) originalList, "adjusted " + fileName);
            rollingList.add(originalList.get(0));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    public interface TradingStrategy {
        double execute(double openPrice);
    }


    /**
     * Runs the trading simulation using the provided trading strategy.
     *
     * @param strategy The trading strategy to be used in the simulation.
     */
    public void run(TradingStrategy strategy){
        for(int i = 0; i < originalList.size(); i++) {
            double open = rollingList.get(i).getOpen();
            double sharesToBuy = strategy.execute(open);

            sharesOwned += sharesToBuy;
            balance = balance - (sharesToBuy * open);
            newDay();
        }
        printSummary();
    }

    /**
     * Prints a summary of the current trading status, including balance,
     * shares owned, value of shares, and total net worth.
     */
    public void printSummary(){
        System.out.println("Money in your wallet = " + balance);
        System.out.println("Shares owned right now = " + sharesOwned);
        double valueOfShares = sharesOwned * rollingList.get(day-1).getOpen();
        System.out.println("Value of your shares = " + valueOfShares);
        System.out.println("Total net worth = " + (balance + valueOfShares));
    }

    /**
     * Strategy for long term hold. (Strategy one)
     *
     * @param open The opening price of the stock.
     * @return The number of shares to buy or sell based on the strategy.
     */
    public double longHoldStrategy (double open){
        double shares = 0;
        if (day == 1){
            shares = balance/open;
        }
        if (day == originalList.size()){
            shares = -sharesOwned;
        }
        return shares;
    }

    /**
     * Trading strategy based on Relative Strength Index (RSI) and Moving Average (MA). (Strategy two)
     * "Trade Evaluator"
     *
     * @param open The opening price of the stock.
     * @return The number of shares to buy or sell based on the RSI and MA indicators.
     */
    public double rsiAndMaStrategy(double open){
        double shares = 0;
        if (day < 15)
            return 0;
        double todaysRSI = calculateRSI(rollingList);
        double maxInvestment = balance * 0.1; // Only use 10% of balance for each trade
        if (todaysRSI < 30) {
            double investment = Math.min(maxInvestment, balance * 0.60);
            shares = investment / open;
        }
        if (open < movingAvg(30)) {
            double investment = Math.min(maxInvestment, balance);
            shares += investment / open;
        }
        if (todaysRSI > 70)
            shares = -(sharesOwned * 0.40);
        if (open > movingAvg(30))
            shares += -(sharesOwned * 0.33);

        if(sharesOwned + shares < 0)
            shares = 0;
        return shares;
    }

    /**
     * Momentum and Volume based trading strategy. (Strategy three)
     *
     * @param open The opening price of the stock.
     * @return The number of shares to buy or sell based on momentum and volume indicators.
     */
    public double momentumAndVolumeStrategy(double open) {
        double shares = 0;
        double avgVolume = calculateAvgVolume();
        StockData currentDay = rollingList.get(day - 1);

        if (currentDay.getOpen() > movingAvg(30) && currentDay.getVolume() > avgVolume) {
            // Buy when price is above MA and volume is high
            shares = (balance * 0.05) / open; // Using 5% of balance to buy
        } else if (currentDay.getOpen() < movingAvg(30) && currentDay.getVolume() > avgVolume) {
            // Sell when price is below MA and volume is high
            shares = -Math.min(sharesOwned, (balance * 0.05) / open); // Selling up to 5% of balance worth of shares
        }

        return shares;
    }

    /**
     * Calculates the average volume of the rolling list.
     *
     * @return The average volume.
     */
    public double calculateAvgVolume() {
        double totalVolume = 0;
        for (StockData data : rollingList) {
            totalVolume += data.getVolume();
        }
        return totalVolume / rollingList.size();
    }


    /**
     * Calculates the Relative Strength Index (RSI) for a specific day.
     *
     * @param day The day for which to calculate the RSI.
     * @return The RSI value for the specified day.
     */
    public double dayRSI(int day){
        List<StockData> sublist = originalList.subList(day - 14, day + 1);
        return calculateRSI(sublist);
    }


    /**
     * Calculates the rolling RSI values for the stock data.
     *
     * @return A list of rolling RSI values.
     */
    public ArrayList<Double> rollingRSI(){
        ArrayList<Double> rsiList = new ArrayList<>();

        for (int i = 14; i < originalList.size(); i++) {
            double rsi = dayRSI(i);
            rsiList.add(rsi);
        }
        return rsiList;
    }


    /**
     * Calculates the Relative Strength Index (RSI) for a given list of stock data.
     *
     * @param stockDataList The list of stock data to calculate RSI.
     * @return The calculated RSI value.
     */
    public static double calculateRSI(List<StockData> stockDataList){
        double gain = 0;
        double loss = 0;

        for (int i = 1; i < stockDataList.size(); i++){
            double change = stockDataList.get(i).getClose() - stockDataList.get(i - 1).getClose();
            if (change > 0){
                gain += change;
            }
            else {
                loss += Math.abs(change);
            }
        }
        double avgGain = gain/14;
        double avgLoss = loss/14;

        if(avgLoss == 0){
            return 100;
        }

        double RS = avgGain/avgLoss;
        return 100 - (100 / (1 + RS));
    }


    /**
     * Adds RSI values to the stock data list.
     */
    public void addRsiToList(){
        for(int i = 14; i < originalList.size(); i++){
            originalList.get(i).setRsi(dayRSI(i));
        }
    }


    /**
     * Adds Moving Average (MA) values to the stock data list.
     *
     * @param windowSize The window size for the moving average calculation.
     */
    public void addMaToList(int windowSize){
        for(int i = 0; i < originalList.size(); i++){
            originalList.get(i).setMa(dailyMovingAvg(windowSize, i));
        }
    }


    /**
     * Advances the simulation to the next day, updating the rolling list and averages.
     */
    public void newDay(){
        if (originalList.size() == rollingList.size())
            return;
        day++;
        sum += originalList.get(day-1).getOpen();
        rollingAvg.add(calculateAvg());
        rollingList.add(originalList.get(day-1));
        if (day > 15)
            rollingRSI.add(calculateRSI(rollingList));
    }


    /**
     * Applies a smoothing algorithm to the stock data points over a specified window size.
     *
     * @param points     The stock data points to be smoothed.
     * @param windowSize The window size for the smoothing algorithm.
     * @return A list of smoothed stock data points.
     */
    public static ArrayList<StockData> Smoother(ArrayList<StockData> points, int windowSize) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        stats.setWindowSize(windowSize);
        ArrayList<StockData> smoothed = new ArrayList<>();

        for(int i = 0; i < points.size(); i++){
            StockData point = points.get(i);
            stats.addValue(point.getOpen());
            smoothed.add(new StockData(point.getDate(),stats.getMean()));
        }
        return smoothed;
    }


    /**
     * Writes stock data to a CSV file.
     *
     * @param stockTable The stock data to write.
     * @param filename   The name of the output CSV file.
     */
    public void writeStockDataToCSV(ArrayList<StockData> stockTable, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Write the headers
            writer.write("date,open,high,low,close,adj close,volume,rsi,ma\n");

            for(int i=0; i < stockTable.size(); i++) {
                StockData current = stockTable.get(i);
                Format formatter = new SimpleDateFormat("yyyy-MM-dd"); // https://stackoverflow.com/questions/5683728/convert-java-util-date-to-string
                String date = formatter.format(current.getDate());


                writer.write(date+ "," +
                        current.getOpen() + "," +
                        current.getHigh() + "," +
                        current.getLow() + "," +
                        current.getClose() + "," +
                        current.getAdjClose() + "," +
                        current.getVolume() + "," +
                        current.getRsi() + "," +
                        current.getMa() + "," +
                        "\n");
            }
        } catch(IOException e) {
            System.out.println("An error occurred while writing to the CSV file");
            e.printStackTrace();
        }
    }


    /**
     * Writes a list of Double values to a CSV file.
     *
     * @param table    The list of Double values.
     * @param filename The name of the output CSV file.
     */
    public void writeDoubleToCSV(ArrayList<Double> table, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Write the headers
            writer.write("Double Value\n");

            for(int i=0; i < table.size(); i++) {
                Double current = table.get(i);
                writer.write(current + "\n");
            }
        } catch(IOException e) {
            System.out.println("An error occurred while writing to the CSV file");
            e.printStackTrace();
        }
    }


    /**
     * Creates a JFreeCHart graph from the provided stock data.
     *
     * @param points The stock data points to plot.
     * @param name   The name of the graph.
     */
    public static void createGraph(List<StockData> points, String name) {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        TimeSeries series = new TimeSeries("Stock Data");

        for (StockData data : points) {
            series.add(new Day(data.getDate()), data.getOpen());
        }
        dataset.addSeries(series);

        JFreeChart timeSeriesChart = ChartFactory.createTimeSeriesChart(
                name + " Time Series Chart",
                "Date",
                "Open Price",
                dataset,
                true, // include legend
                true, // tooltips
                false // urls
        );
        ChartPanel chartPanel = new ChartPanel(timeSeriesChart);
        JFrame frame = new JFrame();
        frame.add(chartPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }


    /**
     * Calculates the average opening price of stocks over the current number of days.
     *
     * @return The calculated average opening price.
     */
    public double calculateAvg(){
        return (double)sum / day;
    }


    /**
     * Calculates the moving average of stock's opening prices over a specified window of days.
     *
     * @param windowOfDays The number of days to consider for the moving average calculation.
     * @return The calculated moving average.
     */
    public double movingAvg(int windowOfDays){
        int today = rollingList.size();
        if (windowOfDays <= 0) return 0;
        double movingSum = 0;
        int startDate = Math.max(today-windowOfDays, 0);
        for (int i = startDate; i < today; i++){
            movingSum += rollingList.get(i).getOpen();
        }
        return movingSum/(day-startDate);
    }


    /**
     * Calculates the daily moving average of the stock's opening prices up to a specified day.
     *
     * @param windowOfDays The window size for the moving average calculation.
     * @param day          The day up to which the moving average is calculated.
     * @return The daily moving average.
     */
    public double dailyMovingAvg(int windowOfDays, int day){
        if (windowOfDays <= 0) return 0;
        double movingSum = 0;
        int startDate = Math.max(day-windowOfDays,0);
        for (int i = startDate; i < day; i++){
            movingSum += originalList.get(i).getOpen();
        }
        return movingSum/(day-startDate);
    }


    /**
     * Resets the StockBot to its initial state, clearing all accumulated data and resetting the balance.
     */
    public void reset() {
        this.balance = this.initBalance; // Reset to initial balance
        this.sharesOwned = 0;
        this.day = 1;
        this.sum = 0;
        this.rollingList.clear();
        this.rollingList.add(originalList.get(0)); // Reset to the first day
    }
}