package gravity;

import javafx.scene.paint.Color;

/**
 * Created by Kiran Tomlinson on 1/12/16.
 * Thanks to Glenn Fiedler from gafferongames.com for the informative guide on RK4 integration.
 * http://gafferongames.com/game-physics/integration-basics/
 *
 * This class holds all the information about a body, as well as methods to calculate the forces acting on it,
 * and the position after a small dt
 */
public class Body {

    /**
     * Instance variables: all the information about this body
     */
    int mass;
    Vector position;
    Vector velocity;
    Vector acceleration;
    public Color color;
    public int radius;


    /**
     * Constructor
     * @param mass
     * @param position
     * @param color
     */
    Body(int mass, Vector position, Color color) {
        this.mass = mass;
        this.position = position;
        this.color = color;

        // v0 and a0 are initialized as zero. v0 can be set manually after creating the body object
        velocity = new Vector(0, 0);
        acceleration = new Vector(0, 0);

        updateRadius();
    }

    /**
     * Detect if this body is touching another body
     * @param body the other one
     * @return true if touching, else false
     */
    public boolean isTouching(Body body) {
        return getVectorTo(body).getMagnitude() - radius - body.radius < 0;
    }


    /**
     * Resolve a collision with another body using conservation of momentum
     * @param body
     */
    public void collide(Body body) {
        Vector momentum1 = velocity.scale(mass);
        Vector momentum2 = body.velocity.scale(body.mass);
        Vector totalMomentum = momentum1.add(momentum2);

        mass += body.mass;
        velocity = totalMomentum.scale(1.0 / mass);
        updateRadius();
    }

    /**
     * The radius is proportional to the square root of the mass by default. It can also be adjusted manually.
     */
    private void updateRadius() {
        radius = (int)Math.sqrt(mass);
    }



    /**
     * Helper method to get a vector pointing from this body to another.
     * @param otherBody
     * @return the vector this -----> otherBody
     */
    private Vector getVectorTo(Body otherBody) {
        return otherBody.position.subtract(position);
    }

    /**
     * The method to sum the gravitational forces on this body and calculate the net acceleration.
     * @param system the Universe
     * @return the net acceleration vector
     */
    private Vector calculateAcceleration(GravitySystem system) {
        Vector netAcceleration = new Vector(0, 0);

        // Start out by summing all the accelerations using Fg = Gm/r^2
        for (Body otherBody : system.getBodies()) {
            if (this.equals(otherBody)) continue;

            double r = getVectorTo(otherBody).getMagnitude();
            double magnitude = system.G * otherBody.mass / (r * r);

            Vector acceleration = getVectorTo(otherBody).normalize().scale(magnitude);
            netAcceleration = netAcceleration.add(acceleration);
        }

        return netAcceleration;
    }

    /**
     * The method to update the position of this body based on the current time and some small dt. It uses a 4th order
     * Runge-Kutta approximation to minimize error from the numerical approximation of an integral.
     *
     * @param dt the small timestep to the next frame
     */
    void step(double dt) {

        Derivative a, b, c, d;

        a = new Derivative();
        a.dx.x = velocity.x;
        a.dx.y = velocity.y;
        a.dv = calculateAcceleration(Gravity.system);
        b = evaluate(dt * 0.5, a);
        c = evaluate(dt * 0.5, b);
        d = evaluate(dt, c);

        /**
         * These formulas combine the derivatives found in a, b, c, and d to get a good weighted estimate for the
         * actual derivatives dx/dt and dv/dt.
         */
        Vector dxdt = new Vector();
        dxdt.x = (a.dx.x + 2.0 * (b.dx.x + c.dx.x) + d.dx.x) / 6.0;
        dxdt.y = (a.dx.y + 2.0 * (b.dx.y + c.dx.y) + d.dx.y) / 6.0;

        Vector dvdt = new Vector();
        dvdt.x = (a.dv.x + 2.0 * (b.dv.x + c.dv.x) + d.dv.x) / 6.0;
        dvdt.y = (a.dv.y + 2.0 * (b.dv.y + c.dv.y) + d.dv.y) / 6.0;

        // Finally, update the current position and velocity
        position = position.add(dxdt.scale(dt));
        velocity = velocity.add(dvdt.scale(dt));


    }


    /**
     * A method to evaluate dx/dt and dv/dt after a timestep dt based on current values for dx/dt and dv/dt
     * @param dt small timestep
     * @param d current values of dx/dt and dv/dt
     * @return the new derivatives dx/dt and dv/dt
     */
    Derivative evaluate(double dt, Derivative d) {

        // Temp storage for position and velocity
        Vector tempPosition = new Vector(position.x, position.y);
        Vector tempVelocity = new Vector(velocity.x, velocity.y);

        // Update velocity based on dv
        velocity.x += d.dv.x * dt;
        velocity.y += d.dv.y * dt;

        // Update position based on dx
        position.x += d.dx.x * dt;
        position.y += d.dx.y * dt;

        // Make the output have dx equal to the new velocity
        Derivative output = new Derivative();
        output.dx = new Vector(velocity.x, velocity.y);

        // And dv equal to the new acceleration due to gravity
        output.dv = calculateAcceleration(Gravity.system);

        //Restore the old position and velocity
        position = new Vector(tempPosition.x, tempPosition.y);
        velocity = new Vector(tempVelocity.x, tempVelocity.y);

        return output;
    }


    /**
     * Class to hold values of dx/dt and dv/dt in both x and y directions.
     */
    class Derivative {

        Vector dx, dv;

        Derivative() {
            dx = new Vector();
            dx = new Vector();

        }
    }
}
