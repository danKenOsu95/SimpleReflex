/**
 * SamplePercept: a read-only class that stores a single sample perception
 *
 * @author Eric Fosler-Lussier
 * @version 1.0
 */
public class SamplePercept {
    private int myPercept;

    /**
     * Create a sample percept from a string
     * @param line A String containing the integer percept
     */
    public SamplePercept(String line) {
	myPercept=Integer.parseInt(line);
    }

    /**
     * Get the value of the percept
     * @return The integer value of the percept
     */
    public int value() {
	return myPercept;
    }
}
