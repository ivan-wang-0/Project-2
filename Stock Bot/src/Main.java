/**
 * The Main class serves as the tester for the StockBot application.
 * It initializes StockBot instances and runs different trading strategies for demonstration.
 *
 * @author Ivan Wang
 */
public class Main {

    /**
     * The main method to run the StockBot application.
     *
     * @param args Command line arguments (not used in this application).
     */
    public static void main(String[] args) {
        // Initialize the bot with $10,000 and stock data from "INTC 5Y Weekly.csv"
        StockBot intelBot = new StockBot(10000, "INTC 5Y Weekly.csv");
        System.out.println("Intel 1 Year Data, Daily:");

        // Test Long Hold Strategy
        System.out.println("\nTesting Long Hold Strategy:");
        intelBot.run(intelBot::longHoldStrategy); // Runs the simulation with a "long hold" strategy
        intelBot.reset();

        // Test RSI and Moving Average Strategy
        System.out.println("\nTesting RSI and Moving Average Strategy:");
        intelBot.run(intelBot::rsiAndMaStrategy); // Runs the simulation with a RSI + MA strategy
        intelBot.reset();

        // Test Momentum-Based Trading Strategy
        System.out.println("\nTesting Momentum-Based Trading Strategy:");
        intelBot.run(intelBot::momentumAndVolumeStrategy); // Runs the simulation with a MA + Volume strategy
        intelBot.reset();

        // Initialize another bot with $10,000 and stock data from "TSLA 5Y Weekly.csv"
        StockBot elonBot = new StockBot(10000, "TSLA 5Y Weekly.csv");
        System.out.println("\nTesla 5 Year Data, Daily:");

        // Testing various strategies on the second bot
        System.out.println("\nTesting Long Hold Strategy:");
        elonBot.run(elonBot::longHoldStrategy);
        elonBot.reset();

        System.out.println("\nTesting RSI and Moving Average Strategy:");
        elonBot.run(elonBot::rsiAndMaStrategy);
        elonBot.reset();

        System.out.println("\nTesting Momentum-Based Trading Strategy:");
        elonBot.run(elonBot::momentumAndVolumeStrategy);
        elonBot.reset();
    }
}