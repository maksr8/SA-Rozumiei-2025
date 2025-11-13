package model;

public class Triangle {
    private final String name;
    private final Point p1;
    private final Point p2;
    private final Point p3;

    public Triangle(String name, Point p1, Point p2, Point p3) {
        this.name = name;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    public String getName() {
        return name;
    }
    public Point getP1() {
        return p1;
    }
    public Point getP2() {
        return p2;
    }
    public Point getP3() {
        return p3;
    }

    public Point[] getOtherPoints(String vertexName) {
        if (p1.getName().equals(vertexName)) {
            return new Point[]{p2, p3};
        } else if (p2.getName().equals(vertexName)) {
            return new Point[]{p1, p3};
        } else if (p3.getName().equals(vertexName)) {
            return new Point[]{p1, p2};
        }
        return null;
    }
}