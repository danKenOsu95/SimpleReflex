import java.lang.*;

/**
 * VisionPercept: a read-only class that stores a single visual perception
 *
 * @author Eric Fosler-Lussier
 * @version 1.0
 */
public class VisionPercept {
    // myPercept is true if the view is clear
    //   could store String here but this is more efficient
    //   can change internals if needed (that's why we have accessor functions)

    private boolean myPercept;
    
    public int column;
    
    public int row;
    
    public MovingRoverSensors.direction direction;
    
    public int seen;
    
    /**
     * Create a vision percept from a string
     * @param line A String containing the vision percept (either
     * "CLEAR" or "BOULDER")
     */
    public VisionPercept(String line) {
	myPercept= line.equalsIgnoreCase("clear");
    }

    /**
     * Returns true if the vision percept is a clear space
     * @return A boolean: true if clear, false if boulder
     */
    public boolean isClear() {
	return myPercept;
    }
}
