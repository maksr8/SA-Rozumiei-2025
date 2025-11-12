package interpreter;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, Point> points = new HashMap<>();

    public void createPoint(String name, double x, double y) throws Exception {
        if (points.containsKey(name)) {
            throw new Exception("Semantic Error: interpreter.Point '" + name + "' is already defined.");
        }
        Point point = new Point(name, x, y);
        points.put(name, point);
        System.out.println("Semantic: Created " + point);
    }

    public Point getPoint(String name) throws Exception {
        Point point = points.get(name);
        if (point == null) {
            throw new Exception("Semantic Error: interpreter.Point '" + name + "' is not defined.");
        }
        return point;
    }
}
