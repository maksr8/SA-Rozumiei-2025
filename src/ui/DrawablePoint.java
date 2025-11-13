package ui;

import model.Point;
import java.awt.Graphics;
import java.awt.Color;

public class DrawablePoint implements Drawable {
    private final Point p;

    public DrawablePoint(Point p) {
        this.p = p;
    }

    @Override
    public void draw(Graphics g) {
        int x = (int)p.getX();
        int y = (int)p.getY();

        g.setColor(Color.RED);
        g.fillOval(x - 3, y - 3, 6, 6);

        g.setColor(Color.BLACK);
        g.drawString(p.getName(), x + 5, y + 5);
    }
}