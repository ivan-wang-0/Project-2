import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Represents the stock data for a single trading day.
 *
 * @author Ivan Wang
 */
public class StockData {
     private Date date;
     private double open;
     private double high;
     private double low;
     private double close;
     private double adjClose;
     private Long volume;
     private double rsi;
     private double ma;


     /**
      * Constructs a new StockData object with detailed stock information.
      *
      * @param dateString The date of the stock data in "yyyy-MM-dd" format.
      * @param open       The opening price of the stock.
      * @param high       The highest price of the stock during the trading day.
      * @param low        The lowest price of the stock during the trading day.
      * @param close      The closing price of the stock.
      * @param adjClose   The adjusted closing price of the stock.
      * @param volume     The trading volume of the stock.
      */
     public StockData(String dateString, double open, double high, double low, double close, double adjClose, Long volume) {
          try {
               this.date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
          } catch (ParseException e) {
               e.printStackTrace();
          }
          this.open = open;
          this.high = high;
          this.low = low;
          this.close = close;
          this.adjClose = adjClose;
          this.volume = volume;
     }

     /**
      * Constructs a new StockData object with the date and opening price.
      *
      * @param dateString The date of the stock data in "yyyy-MM-dd" format.
      * @param open       The opening price of the stock.
      */
     public StockData(String dateString, double open){
          try {
               this.date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
          } catch (ParseException e) {
               e.printStackTrace();
          }
          this.open = open;
     }


     /**
      * Constructs a new StockData object with the date and opening price.
      *
      * @param date The date of the stock data.
      * @param open The opening price of the stock.
      */
     public StockData(Date date, double open) {
          this.date = date;
          this.open = open;
     }



     // Getter and setter methods

     /**
      * Returns the date of the stock data.
      *
      * @return The date of the stock data.
      */
     public Date getDate() {
          return date;
     }

     /**
      * Returns the opening price of the stock.
      *
      * @return The opening price of the stock.
      */
     public double getOpen() {
          return open;
     }

     /**
      * Returns the closing price of the stock.
      *
      * @return The closing price of the stock.
      */
     public double getClose() {
          return close;
     }

     /**
      * Returns the adjusted closing price of the stock.
      * The adjusted closing price reflects stock splits, dividends, and other corporate actions.
      *
      * @return The adjusted closing price of the stock.
      */
     public double getAdjClose() {
          return adjClose;
     }

     /**
      * Returns the highest price of the stock during the trading day.
      *
      * @return The highest price of the stock.
      */
     public double getHigh() {
          return high;
     }

     /**
      * Returns the lowest price of the stock during the trading day.
      *
      * @return The lowest price of the stock.
      */
     public double getLow() {
          return low;
     }

     /**
      * Returns the trading volume of the stock.
      * Volume is the number of shares or contracts traded in a security or an entire market during a given period.
      *
      * @return The trading volume of the stock.
      */
     public Long getVolume() {
          return volume;
     }

     /**
      * Returns the RSI (Relative Strength Index) value.
      *
      * @return The RSI value.
      */
     public double getRsi() {
          return rsi;
     }

     /**
      * Sets the RSI (Relative Strength Index) value.
      *
      * @param rsi The RSI value to set.
      */
     public void setRsi(double rsi) {
          this.rsi = rsi;
     }

     /**
      * Returns the moving average value.
      *
      * @return The moving average value.
      */
     public double getMa() {
          return ma;
     }

     /**
      * Sets the moving average value.
      *
      * @param ma The moving average value to set.
      */
     public void setMa(double ma) {
          this.ma = ma;
     }
}
