package gravity2d;

import java.util.ArrayList;

/**
 * Created by kiran on 1/12/16.
 */
public class GravitySystem {

    private ArrayList<Body> bodies;

    GravitySystem() {
        bodies = new ArrayList<Body>();
    }

    public void addBody(Body body) {
        bodies.add(body);
    }

    public ArrayList<Body> getBodies() {
        return bodies;
    }


}
