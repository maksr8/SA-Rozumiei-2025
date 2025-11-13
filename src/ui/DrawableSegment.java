package ui;

import model.Point;
import java.awt.Graphics;

public class DrawableSegment implements Drawable {
    private final Point p1;
    private final Point p2;

    public DrawableSegment(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public void draw(Graphics g) {
        g.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
    }
}