package gravity2d;

/**
 * Created by kiran on 1/12/16.
 * Thanks to Glenn Fiedler from gafferongames.com for the informative guide on RK4 integration.
 * http://gafferongames.com/game-physics/integration-basics/
 */
public class Body {

    int mass;
    MyVector position;
    MyVector velocity;
    MyVector acceleration;

    final static double G = 100000;


    Body(int mass, MyVector position) {
        this.mass = mass;
        this.position = position;
        velocity = new MyVector(0, 0);
        acceleration = new MyVector(0, 0);
    }

    private MyVector getVectorTo(Body otherBody) {
        return otherBody.position.subtract(position);
    }

    private MyVector calculateAcceleration(GravitySystem system) {
        MyVector netForce = new MyVector(0, 0);

        for (Body otherBody : system.getBodies()) {
            if (!this.equals(otherBody)) {
                MyVector separation = getVectorTo(otherBody);
                double forceMagnitude = G * mass * otherBody.mass / (separation.getMagnitude() * separation.getMagnitude());
                MyVector force = separation.normalize().scale(forceMagnitude);
                netForce = netForce.add(force);
            }
        }

        return netForce.scale(1.0 / mass);
    }

    void step(double t, double dt) {
        Derivative a, b, c, d;

        a = new Derivative();
        a.dx.x = velocity.x;
        a.dx.y = velocity.y;
        a.dv = calculateAcceleration(Main.system);

        b = evaluate(t, dt * 0.5, a);
        c = evaluate(t, dt * 0.5, b);
        d = evaluate(t, dt, c);

        MyVector dxdt = new MyVector();
        dxdt.x = 1.0 / 6.0 * (a.dx.x + 2.0 * (d.dx.x + c.dx.x) + d.dx.x);
        dxdt.y = 1.0 / 6.0 * (a.dx.y + 2.0 * (d.dx.y + c.dx.y) + d.dx.y);

        MyVector dvdt = new MyVector();
        dvdt.x = 1.0 / 6.0 * (a.dv.x + 2.0 * (d.dv.x + c.dv.x) + d.dv.x);
        dvdt.y = 1.0 / 6.0 * (a.dv.y + 2.0 * (d.dv.y + c.dv.y) + d.dv.y);

        position = position.add(dxdt.scale(dt));
        velocity = velocity.add(dvdt.scale(dt));
    }

    Derivative evaluate(double t, double dt, Derivative d) {
        position.x += d.dx.x * dt;
        position.y += d.dx.y * dt;


        velocity.x += d.dv.x * dt;
        velocity.y += d.dv.y * dt;

        Derivative output = new Derivative();
        output.dx = new MyVector(velocity.x, velocity.y);
        output.dv = calculateAcceleration(Main.system);

        return output;
    }

    class Derivative {

        MyVector dx, dv;

        Derivative() {
            dx = new MyVector();
            dx = new MyVector();

        }
    }
}
