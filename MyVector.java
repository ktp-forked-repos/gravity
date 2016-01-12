package gravity2d;

/**
 * A class to store and manipulate vectors. This can be used for velocity, position, force, acceleration, anything.
 * @author kiran
 */
class MyVector {

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
        return magnitude;
    }

    /**
     * Calculates the dot product of this vector with another specified vector.
     * @param v the other vector
     * @return the dot product
     */
    double dot(MyVector v) {
        return (x * v.x + y * v.y);
    }

    /**
     * Return a new vector perpendicular to this one, counterclockwise.
     * @return the perpendicular vector
     */
    MyVector perp() {
        return new MyVector(- 1 * y, x);
    }

    /**
     * Returns a new vector that equals this vector multiplied by a specified scalar.
     * @param scalar the scale factor
     * @return the new scaled vector
     */
    MyVector scale(double scalar) {
        return new MyVector(x * scalar, y * scalar);
    }

    /**
     * Returns a new unit vector in the direction of this vector.
     * @return the unit vector
     */
    MyVector normalize() {
        if (magnitude > 0) {
            return new MyVector(x / magnitude, y / magnitude);
        }

        return null;

    }

    /**
     * Returns a new vector equals to the vector addition of this vector with a specified other vector
     * @param v the other vector
     * @return the sum of the two vectors
     */
    MyVector add(MyVector v) {
        return new MyVector(x + v.x, y + v.y);
    }

    /**
     * Returns a new vector equals to the vector addition of this vector with a specified other vector
     * @param v the other vector
     * @return the subtraction this - v
     */
    MyVector subtract(MyVector v) {
        return new MyVector(x - v.x, y - v.y);
    }

    /**
     * A helper method to find the magnitude of this vector
     */
    private void calculateMagnitude() {
        magnitude = Math.sqrt(x * x + y * y);
    }

    /**
     * Constructor
     * @param x the x component
     * @param y the y component
     */
    MyVector(double x, double y) {
        this.x = x;
        this.y = y;

        calculateMagnitude();
    }


}