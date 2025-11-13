package ui;

import model.Point;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.Graphics;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DrawingPanel extends JPanel {

    private final List<Drawable> drawables = Collections.synchronizedList(new ArrayList<>());
    private static final int PANEL_WIDTH = 1200;
    private static final int PANEL_HEIGHT = 2000;

    public DrawingPanel() {
        JFrame frame = new JFrame("Triangle Interpreter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(100, 100);
        frame.setSize(1300, 700);

        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        JScrollPane scrollPane = new JScrollPane(this);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        frame.add(scrollPane);
        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        synchronized (drawables) {
            for (Drawable d : drawables) {
                d.draw(g);
            }
        }
    }

    public void addSegment(Point p1, Point p2) {
        drawables.add(new DrawableSegment(p1, p2));
        this.repaint();
    }

    public void addPoint(Point p) {
        drawables.add(new DrawablePoint(p));
        this.repaint();
    }
}