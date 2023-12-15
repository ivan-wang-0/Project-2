/**
 * Project 2 specific Stats Library
 *
 * @author Ivan Wang
 */
public class StatsLibrary {

    /**
     * Class for uniform distribution functions
     */
    public class UniformDistribution {

        /**
         * Computes the expected mean value for a uniform distribution.
         *
         * @param min The minimum boundary of the distribution
         * @param max The maximum boundary of the distribution
         * @return The mean value
         */
        public double expectedValue(double min, double max) {
            return (min + max) / 2;
        }

        /**
         * Determines the variance of a uniform distribution.
         *
         * @param minValue The minimum boundary of the distribution
         * @param maxValue The maximum boundary of the distribution
         * @return The calculated variance
         */
        public double variance(double minValue, double maxValue) {
            return Math.pow(maxValue - minValue, 2) / 12;
        }


    }
 }
