package battleship.screen;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

public class MainPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private RadialGradientPaint rgp;
	private Point2D center;
	private float radius;
	private float[] gradientStops = { 0.2f, 0.4f, 0.6f, 0.8f, 1.0f };
	private Color[] colors = { new Color(255, 0, 0), new Color(200, 0, 0),
		new Color(150, 0, 0), new Color(100, 0, 0), new Color(50, 0, 0) };
	
	public MainPanel() {
		super();
		setSize(800, 600);
		center = new Point2D.Double(getWidth() / 2, getHeight() / 2);
		radius = (float) (getWidth() / 2.0);
		rgp = new RadialGradientPaint(center, radius, gradientStops, colors);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics tmp = g.create();
		Graphics2D g2 = (Graphics2D) tmp;
		g2.setPaint(rgp);
		g2.fillRect(0, 0, getWidth(), getHeight());
        tmp.dispose();
	}
	
}
