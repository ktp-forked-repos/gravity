package gravity;

import java.util.ArrayList;

/**
 * Created by Kiran Tomlinson on 1/12/16.
 * A class to hold bodies. The System represents the simulated universe.
 */
public class System {

    //The bodies in this system.
    private ArrayList<Body> bodies;

    //The value of the gravitational constant in the simulated universe.
    final double G = 100;

    /**
     * Constructor
     */
    System() {
        bodies = new ArrayList<>();
    }

    /**
     * Method to add a body to this system.
     * @param body
     */
    public void addBody(Body body) {
        bodies.add(body);
    }

    /**
     * Accessor for the bodies array
     * @return
     */
    public ArrayList<Body> getBodies() {
        return bodies;
    }
}
