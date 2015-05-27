/**
 * @file Grid.java
 * @authors rickard, lars
 * @date 2015-05-18
 * */
package battleship.gameboard;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.Timer;

/**
 * @class Grid
 * @package battleship.player
 * @brief Class describes a battleship 10 * 10 grid
 * @extends JLabel
 * */
public class Grid extends JLabel {
	private static final long serialVersionUID = 1L;
	private BufferedImage img = null;
	private int col, row;
	private int delay = 40;
	private float dir = -0.02f, alpha = 1.0f;
	private boolean empty = true;
	private boolean hit = false;

	public Grid(int row, int col) {
		super();
		setPreferredSize(new Dimension(32, 32));
		this.row = row;
		this.col = col;
		
	}

	public void setOccupied() {
		empty = false;
	}

	public boolean isEmpty() {
		return empty;
	}

	public void setHit(boolean hit) {
		this.hit = hit;
	}

	public boolean isHit() {
		return hit;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
	
	public void fadeOut() {
		new Fader().fadeOut();;
	}
	
	public void fadeIn() {
		new Fader().fadeIn();
	}
	
	class Fader {
		private Timer t;

		public Fader() {
			if (t != null && t.isRunning()) {
				t.stop();
				t = null;
			}
		}
		
		public void fadeOut() {
			dir = -0.02f; 
			alpha = 1.0f;
			
			t = new Timer(delay, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					alpha += dir;
					if (checkLowerBounds()) {
						repaint();
					} else {
						stop();
					}
				}
			});
			t.setCoalesce(true);
			t.start();
		}
		
		public void fadeIn() {
			dir = 0.02f; 
			alpha = 0.0f;
			t = new Timer(delay, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					alpha += dir;
					if (checkUpperBounds()) {
						repaint();
					} else {
						stop();
					}
				}
			});
			t.setCoalesce(true);
			t.start();
		}
			
		private void stop() {
			t.stop(); t = null;
		}

		private boolean checkLowerBounds() {
			return alpha >= 0.0f;
		}
		
		private boolean checkUpperBounds() {
			return alpha <= 1.0f;
		}
	}
	
	@Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
        super.paint(g2d);
        g2d.dispose();
    }
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (img != null) {
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
			g2d.drawImage(img, 0, 0, null);
			g2d.dispose();
		}
	}
}
