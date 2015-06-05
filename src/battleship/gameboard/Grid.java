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
	private boolean miss = false;

	/**
	 * @param integer row and integer column
	 * */
	public Grid(int row, int col) {
		super();
		setPreferredSize(new Dimension(32, 32));
		this.row = row;
		this.col = col;
	}

	/**
	 * setOccupied
	 * @name setOccupied
	 * @brief Set occupied state.
	 * */
	public void setOccupied() {
		empty = false;
	}

	/**
	 * isEmpty
	 * @name isEmpty
	 * @return return empty state.
	 * */
	public boolean isEmpty() {
		return empty;
	}

	/**
	 * setHit
	 * @name setHit
	 * */
	public void setHit() {
		hit = true;
	}
	
	public boolean isHit() {
		return hit;
	}
	
	public void setMiss() {
		miss = true;
	}
	
	public boolean isMiss() {
		return miss;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public void fadeOut() {
		new Fader().fadeOut();
	}

	public void fadeIn() {
		new Fader().fadeIn();
	}

	public void fastFade() {
		new Fader().fastFade();
	}

	/**
	 * @class Fader
	 * @brief Describes a fading effect.
	 * */
	class Fader {
		private Timer t;

		public Fader() {
			if (t != null && t.isRunning()) {
				t.stop();
				t = null;
			}
		}

		/**
		 * @name fadeOut
		 * @brief fadeout effect.
		 * */
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

		/**
		 * fadeIn
		 * @name fadeIn
		 * */
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

		/**
		 * fastFade
		 * @name fastFade
		 * @brief Fast fade out effect.
		 * */
		public void fastFade() {
			dir = -0.20f;
			alpha = 1.0f;

			t = new Timer(40, new ActionListener() {
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

		private void stop() {
			t.stop();
			t = null;
		}
		
		/**
		 * @name checkLowerBounds
		 * @return If alpha is within lower bounds return true. 
		 * */
		private boolean checkLowerBounds() {
			if (alpha <= 0.0f) {
				alpha = 0.0f;
				return false;
			}
			return true;
		}

		/**
		 * @name checkUpperBounds
		 * @return If alpha is within upper bounds return true. 
		 * */
		private boolean checkUpperBounds() {
			if (alpha >= 1.0f) {
				alpha = 1.0f;
				return false;
			}
			return true;
		}
	}

	/**
	 * paint
	 * @name paint
	 * @brief Paints 
	 * */
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

	@Override
	public boolean equals(Object otherGrid) {
		if (otherGrid == null)
			return false;
		if (otherGrid == this)
			return true;
		if (!(otherGrid instanceof Grid))
			return false;
		Grid grid = (Grid) otherGrid;
		if (this.getRow() == grid.getRow() && this.getCol() == grid.getCol())
			return true;

		return false;
	}

	/**
	 * hashcode
	 * @name hashcode
	 * @return produce and return hashcode as Integer.
	 * */
	@Override
	public int hashCode() {
		int hash = 13;
		hash = 37 * hash + this.row;
		hash = 37 * hash + this.col;
		return hash;
	}
}
