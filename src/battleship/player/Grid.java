/**
 * @file Grid.java
 * @authors rickard, lars
 * @date 2015-05-18
 * */
package battleship.player;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

import resources.audio.SoundHolder;

/**
 * @class Grid
 * @package battleship.player
 * @brief Class describes a battleship 10 * 10 grid
 * @extends JLabel
 * */
public class Grid extends JLabel {
	private static final long serialVersionUID = 1L;
	private int col, row;
	private boolean empty = true;
	private boolean hit = false;
	private BufferedImage img = null;
	private float level = -0.02f;
	private int delay = 40;
	private float alpha = 1.0f;

	public Grid(int row, int col) {
		super();
		this.row = row;
		this.col = col;
		this.setPreferredSize(new Dimension(32, 32));
	}

	public void setImage(BufferedImage img) {
		this.img = new BufferedImage(img.getWidth(null), img.getHeight(null),
				BufferedImage.TYPE_INT_ARGB);
		repaint();
		// setIcon(new ImageIcon(this.img));
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

	class Fader {
		private Timer t;

		public Fader() {
			if (t != null && t.isRunning()) {
				t.stop();
				t = null;
			}
			t = new Timer(delay, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					alpha += level;
					if (checkBounds()) {
						repaint();
					}
				}
			});
			t.setCoalesce(true);
			t.start();
		}

		private boolean checkBounds() {
			if (alpha < 0.0f) {
				alpha = 0.0f;
				t.stop();
				t = null;
				return false;
			}
			return true;
		}

		private void changeDirection() {
			level = -level;
		}
	}

	public void fadeDown() {
		new Fader();
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
