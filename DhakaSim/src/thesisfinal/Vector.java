package thesisfinal;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Vector {
    private double x;
    private double y;

    // Constructor to initialize the vector
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Getter for x
    public double getX() {
        return x;
    }

    // Getter for y
    public double getY() {
        return y;
    }

    // Setters
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    // Vector addition
    public Vector add(Vector other) {
        return new Vector(this.x + other.x, this.y + other.y);
    }

    // Vector subtraction
    public Vector subtract(Vector other) {
        return new Vector(this.x - other.x, this.y - other.y);
    }

    // Scalar multiplication
    public Vector multiply(double scalar) {
        return new Vector(this.x * scalar, this.y * scalar);
    }

    // Scalar divide
    public Vector divide(double scalar) {
        return new Vector(this.x / scalar, this.y / scalar);
    }

    // Dot product
    public double dot(Vector other) {
        return this.x * other.x + this.y * other.y;
    }

    // Magnitude (length) of the vector
    public double magnitude() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    // Normalize the vector (convert to a unit vector)
    public Vector normalize() {
        double mag = magnitude();
        if (mag == 0) {
            return new Vector(0, 0);
        }
        return new Vector(this.x / mag, this.y / mag);
    }

    public Vector tangent() {
        return new Vector(-this.y, this.x); // Rotate by 90 degrees
    }

    // String representation of the vector
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    // // Test the Vector class
    // public static void main(String[] args) {
    // // Create two vectors
    // Vector v1 = new Vector(3, 4);
    // Vector v2 = new Vector(1, 2);

    // // Test vector operations
    // System.out.println("v1: " + v1);
    // System.out.println("v2: " + v2);
    // System.out.println("v1 + v2: " + v1.add(v2));
    // System.out.println("v1 - v2: " + v1.subtract(v2));
    // System.out.println("v1 * 2: " + v1.multiply(2));
    // System.out.println("v1 . v2 (dot product): " + v1.dot(v2));
    // System.out.println("Magnitude of v1: " + v1.magnitude());
    // System.out.println("Normalized v1: " + v1.normalize());
    // }
}
