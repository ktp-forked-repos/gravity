package gravity;

/**
 * A class to store and manipulate vectors. This can be used for velocity, position, force, acceleration, anything.
 *
 * @author Kiran Tomlinson
 */
class Vector {

    /**
     * Instance variables for x, y components and magnitude.
     */
    double x;
    double y;
    private double magnitude;

    /**
     * Accessor for magnitude.
     * @return the magnitude of this vector
     */
    double getMagnitude() {
        calculateMagnitude();
        return magnitude;
    }

    /**
     * Returns a new vector that equals this vector multiplied by a specified scalar.
     * @param scalar the scale factor
     * @return the new scaled vector
     */
    Vector scale(double scalar) {
        return new Vector(x * scalar, y * scalar);
    }

    /**
     * Returns a new unit vector in the direction of this vector.
     * @return the unit vector
     */
    Vector normalize() {
        if (magnitude > 0) {
            return new Vector(x / magnitude, y / magnitude);
        }

        return null;
    }

    /**
     * Returns a new vector equals to the vector addition of this vector with a specified other vector
     * @param v the other vector
     * @return the sum of the two vectors
     */
    Vector add(Vector v) {
        return new Vector(x + v.x, y + v.y);
    }

    /**
     * Returns a new vector equals to the vector addition of this vector with a specified other vector
     * @param v the other vector
     * @return the subtraction this - v
     */
    Vector subtract(Vector v) {
        return new Vector(x - v.x, y - v.y);
    }

    /**
     * A helper method to find the magnitude of this vector
     */
    private void calculateMagnitude() {
        magnitude = Math.sqrt(x * x + y * y);
    }

    /**
     * Generate a pretty string that represents the vector
     * @return a pretty string
     */
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    /**
     * Constructor
     * @param x the x component
     * @param y the y component
     */
    Vector(double x, double y) {
        this.x = x;
        this.y = y;

        calculateMagnitude();
    }

    /**
     * Default constructor
     */
    Vector() {
        x = 0;
        y = 0;

        calculateMagnitude();
    }


}