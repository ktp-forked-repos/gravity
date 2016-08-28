package gravity;

import javafx.scene.paint.Paint;

/**
 * Created by kiran on 1/12/16.
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
    MyVector position;
    MyVector velocity;
    MyVector acceleration;
    public Paint color;
    public int radius;
    boolean isFixed;


    /**
     * Constructor
     * @param mass
     * @param position
     * @param color
     */
    Body(int mass, MyVector position, Paint color) {
        this.mass = mass;
        this.position = position;
        this.color = color;
        this.isFixed = false;

        // v0 and a0 are initialized as zero. v0 can be set manually after creating the body object
        velocity = new MyVector(0, 0);
        acceleration = new MyVector(0, 0);


        // The radius is proportional to the square root of the mass by default. It can also be adjusted manually.
        radius = (int)Math.sqrt(mass);
    }

    /**
     * Helper method to get a vector pointing from this body to another.
     * @param otherBody
     * @return the vector this -----> otherBody
     */
    private MyVector getVectorTo(Body otherBody) {
        return otherBody.position.subtract(position);
    }

    /**
     * The method to sum the gravitational forces on this body and calculate the net acceleration.
     * @param system the Universe
     * @return the net acceleration vector
     */
    private MyVector calculateAcceleration(GravitySystem system) {
        MyVector netForce = new MyVector(0, 0);

        //Start out by summing all the forces using Fg = GMm/r^2
        for (Body otherBody : system.getBodies()) {
            if (!this.equals(otherBody)) {

                // The distance from this to otherBody
                double r = getVectorTo(otherBody).getMagnitude();

                // Here it is! The magnitude of the force of gravity
                double forceMagnitude = system.G * mass * otherBody.mass / (r * r);

                // The force points directly towards otherBody, so we can just scale the vector that points to it.
                MyVector force = getVectorTo(otherBody).normalize().scale(forceMagnitude);

                // Add this force to the running total of net force
                netForce = netForce.add(force);
            }
        }

        // F = ma, so divide force by mass to get net acceleration
        return netForce.scale(1.0 / mass);
    }

    /**
     * Method that calculates the gravitational potential energy of this body.
     * @param system the universe
     * @return the gravitational potential
     */
    public double calculateGravitationalPotential(GravitySystem system) {
        double netPotential = 0;

        for (Body otherBody : system.getBodies()) {
            if (!this.equals(otherBody)) {

                double r = getVectorTo(otherBody).getMagnitude();

                netPotential += system.G * otherBody.mass / r;
            }
        }

        return -1 * netPotential * mass;
    }

    /**
     * Method that calculates the kinetic energy of this body
     * @return the kinetic energy
     */
    public double getKineticEnergy() {
        return 0.5 * mass * velocity.getMagnitude() * velocity.getMagnitude();
    }

    /**
     * The method to update the position of this body based on the current time and some small dt. It uses a 4th order
     * Runge-Kutta approximation to minimize error from the numerical approximation of an integral. It still gives very
     * noticeable error. ie, orbits are not consistent over time. The longer the simulation runs, the more the inaccuracies
     * pile up.
     *
     * @param dt the small timestep to the next frame
     */
    void step(double dt) {

        Derivative a, b, c, d;


        // a contains the derivatives dx/dt and dv/dt at time t
        a = new Derivative();
        a.dx.x = velocity.x;
        a.dx.y = velocity.y;
        a.dv = calculateAcceleration(Main.system);

        // b contains dx/dt and dv/dt at time t = t0 + 0.5dt based on the derivatives found for a
        b = evaluate(dt * 0.5, a);

        // c contains dx/dt and dv/dt at time t = t0 + 0.5dt based on the derivatives found for b
        c = evaluate(dt * 0.5, b);

        // d contains dx/dt and dv/d at time t = t0 + dt based on the derivatives found for c
        d = evaluate(dt, c);

        /**
         * These formulas combine the derivatives found in a, b, c, and d to get a good weighted estimate for the
         * actual derivatives dx/dt and dv/dt. Not perfect, but closer than Euler's method
         *
         * I have added a multiplicative factor of 1.37 based on empirical evidence that the error is consistenly in one
         * direction. The simulation performs much more realistically with this factor.
         */
        MyVector dxdt = new MyVector();
        dxdt.x = Main.system.CORRECTION_FACTOR / 8.0 * (a.dx.x + 3.0 * (d.dx.x + c.dx.x) + d.dx.x);
        dxdt.y = Main.system.CORRECTION_FACTOR / 8.0 * (a.dx.y + 3.0 * (d.dx.y + c.dx.y) + d.dx.y);

        MyVector dvdt = new MyVector();
        dvdt.x = Main.system.CORRECTION_FACTOR / 8.0 * (a.dv.x + 3.0 * (d.dv.x + c.dv.x) + d.dv.x);
        dvdt.y = Main.system.CORRECTION_FACTOR / 8.0 * (a.dv.y + 3.0 * (d.dv.y + c.dv.y) + d.dv.y);

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
        MyVector tempPosition = new MyVector(position.x, position.y);
        MyVector tempVelocity = new MyVector(velocity.x, velocity.y);

        // Update velocity based on dv
        velocity.x += d.dv.x * dt;
        velocity.y += d.dv.y * dt;

        // Update position based on dx
        position.x += d.dx.x * dt;
        position.y += d.dx.y * dt;



        // Make the output have dx equal to the new velocity
        Derivative output = new Derivative();
        output.dx = new MyVector(velocity.x, velocity.y);

        // And dv equal to the new acceleration due to gravity
        output.dv = calculateAcceleration(Main.system);

        //Restore the old position and velocity
        position = new MyVector(tempPosition.x, tempPosition.y);
        velocity = new MyVector(tempVelocity.x, tempVelocity.y);

        return output;
    }

    /**
     * Class to hold values of dx/dt and dv/dt in both x and y directions. This exists for convenience.
     */
    class Derivative {

        MyVector dx, dv;

        Derivative() {
            dx = new MyVector();
            dx = new MyVector();

        }
    }
}
