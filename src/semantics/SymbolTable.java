package semantics;

import lexer.TokenType;
import model.Point;
import model.Triangle;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SymbolTable {

    private final Map<String, Point> points = new HashMap<>();
    private final Map<String, Triangle> pointToTriangleMap = new HashMap<>();
    private int triangleCounter = 0;
    private final Random rand = new Random();

    public Point[] defineTriangle(String p1Name, String p2Name, String p3Name, TokenType type) throws Exception {
        if (points.containsKey(p1Name)) {
            throw new Exception("Semantic Error: Point " + p1Name + " already exists.");
        }
        if (points.containsKey(p2Name)) {
            throw new Exception("Semantic Error: Point " + p2Name + " already exists.");
        }
        if (points.containsKey(p3Name)) {
            throw new Exception("Semantic Error: Point " + p3Name + " already exists.");
        }

        double offsetX = (triangleCounter % 2) * 550.0;
        double offsetY = (triangleCounter / 2) * 500.0;
        triangleCounter++;

        Point p1, p2, p3;
        if (type == TokenType.KEYWORD_ISOSCELES) {
            p1 = new Point(p1Name, 100.0 + offsetX, 400.0 + offsetY);
            p2 = new Point(p2Name, 250.0 + offsetX, 100.0 + offsetY);
            p3 = new Point(p3Name, 400.0 + offsetX, 400.0 + offsetY);
        } else if (type == TokenType.KEYWORD_RIGHT) {
            p1 = new Point(p1Name, 100.0 + offsetX, 400.0 + offsetY);
            p2 = new Point(p2Name, 100.0 + offsetX, 100.0 + offsetY);
            p3 = new Point(p3Name, 500.0 + offsetX, 400.0 + offsetY);
        } else {
            double baseX1 = 100.0;
            double baseY1 = 400.0;
            double baseX2 = 250.0;
            double baseY2 = 100.0;
            double baseX3 = 450.0;
            double baseY3 = 350.0;

            Point t1 = new Point(p1Name, baseX1 + jitter(), baseY1 + jitter());
            Point t2 = new Point(p2Name, baseX2 + jitter(), baseY2 + jitter());
            Point t3 = new Point(p3Name, baseX3 + jitter(), baseY3 + jitter());

            double cX = (t1.getX() + t2.getX() + t3.getX()) / 3.0;
            double cY = (t1.getY() + t2.getY() + t3.getY()) / 3.0;
            Point center = new Point("center", cX, cY);

            // -45 to +45 deg random
            double angleRad = (rand.nextDouble() * (Math.PI / 2.0)) - (Math.PI / 4.0);

            p1 = rotatePoint(t1, center, angleRad, offsetX, offsetY);
            p2 = rotatePoint(t2, center, angleRad, offsetX, offsetY);
            p3 = rotatePoint(t3, center, angleRad, offsetX, offsetY);
        }

        points.put(p1Name, p1);
        points.put(p2Name, p2);
        points.put(p3Name, p3);

        String triangleName = p1Name + p2Name + p3Name;
        Triangle tri = new Triangle(triangleName, p1, p2, p3);

        pointToTriangleMap.put(p1Name, tri);
        pointToTriangleMap.put(p2Name, tri);
        pointToTriangleMap.put(p3Name, tri);

        System.out.println("Defined Triangle " + triangleName);
        return new Point[]{p1, p2, p3};
    }

    public Point[] defineSegment(String p1Name, String p2Name, TokenType segmentType) throws Exception {
        if (points.containsKey(p2Name)) {
            throw new Exception("Semantic Error: Point " + p2Name + " already exists.");
        }
        Triangle t = pointToTriangleMap.get(p1Name);
        if (t == null) {
            throw new Exception("Semantic Error: Point " + p1Name + " does not belong to any triangle.");
        }

        Point p1 = getPoint(p1Name);

        Point[] otherPoints = t.getOtherPoints(p1Name);
        if (otherPoints == null) {
            throw new Exception("Internal Error: Point " + p1Name + " not found in its triangle " + t.getName());
        }
        Point pB = otherPoints[0];
        Point pC = otherPoints[1];

        Point p2;

        if (segmentType == TokenType.KEYWORD_BUILD_MEDIAN) {
            p2 = calculateMedian(p2Name, pB, pC);
        } else if (segmentType == TokenType.KEYWORD_BUILD_HEIGHT) {
            p2 = calculateHeight(p2Name, p1, pB, pC);
        } else {
            p2 = calculateBisector(p2Name, p1, pB, pC);
        }

        Point coincidentPoint = null;
        if (areCoincident(p2, pB)) {
            coincidentPoint = pB;
        } else if (areCoincident(p2, pC)) {
            coincidentPoint = pC;
        }

        if (coincidentPoint != null) {
            System.out.println("Segment " + p1Name + p2Name + " coincides with existing segment " +
                    p1Name + coincidentPoint.getName() + ". No new segment will be drawn.");
            return null;
        }

        points.put(p2Name, p2);
        System.out.println("Defined segment " + p1.getName() + p2.getName());

        return new Point[]{p1, p2};
    }

    private boolean areCoincident(Point pA, Point pB) {
        double distance = Math.sqrt(Math.pow(pA.getX() - pB.getX(), 2) + Math.pow(pA.getY() - pB.getY(), 2));
        return distance < 1.0;
    }

    private double jitter() {
        // [-25.0, 25.0)
        return (rand.nextDouble() - 0.5) * 50.0;
    }

    private Point rotatePoint(Point p, Point center, double angleRad, double offsetX, double offsetY) {
        double x = p.getX();
        double y = p.getY();
        double cx = center.getX();
        double cy = center.getY();

        double translatedX = x - cx;
        double translatedY = y - cy;

        double rotatedX = translatedX * Math.cos(angleRad) - translatedY * Math.sin(angleRad);
        double rotatedY = translatedX * Math.sin(angleRad) + translatedY * Math.cos(angleRad);

        double finalX = rotatedX + cx + offsetX;
        double finalY = rotatedY + cy + offsetY;

        return new Point(p.getName(), finalX, finalY);
    }

    private Point getPoint(String name) throws Exception {
        Point point = points.get(name);
        if (point == null) {
            throw new Exception("Semantic Error: Point '" + name + "' is not defined.");
        }
        return point;
    }

    private Point calculateMedian(String name, Point pB, Point pC) {
        double mx = (pB.getX() + pC.getX()) / 2.0;
        double my = (pB.getY() + pC.getY()) / 2.0;
        return new Point(name, mx, my);
    }

    private Point calculateHeight(String name, Point pA, Point pB, Point pC) {
        double Bx = pB.getX(), By = pB.getY();
        double Cx = pC.getX(), Cy = pC.getY();
        double Ax = pA.getX(), Ay = pA.getY();

        double L2 = (Cx - Bx) * (Cx - Bx) + (Cy - By) * (Cy - By);
        if (L2 == 0) return calculateMedian(name, pB, pC);

        double t = ((Ax - Bx) * (Cx - Bx) + (Ay - By) * (Cy - By)) / L2;

        double Hx = Bx + t * (Cx - Bx);
        double Hy = By + t * (Cy - By);
        return new Point(name, Hx, Hy);
    }

    private Point calculateBisector(String name, Point pA, Point pB, Point pC) {
        double Bx = pB.getX();
        double By = pB.getY();
        double Cx = pC.getX();
        double Cy = pC.getY();
        double Ax = pA.getX();
        double Ay = pA.getY();

        double c = Math.sqrt((Ax - Bx)*(Ax - Bx) + (Ay - By)*(Ay - By));
        double b = Math.sqrt((Ax - Cx)*(Ax - Cx) + (Ay - Cy)*(Ay - Cy));

        if (c + b == 0)
            return calculateMedian(name, pB, pC);

        double Lx = (b * Bx + c * Cx) / (b + c);
        double Ly = (b * By + c * Cy) / (b + c);

        return new Point(name, Lx, Ly);
    }
}