/**
 * @file Grid.java
 * @authors rickard, lars
 * @date 2015-05-18
 * */
package battleship.gameboard;

import java.awt.AlphaComposite;
import static battleship.game.Constants.empty;
import static battleship.game.Constants.hit;
import static battleship.game.Constants.miss;
import static battleship.game.Constants.occupied;
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
 * @brief Class describes a battleship Grid
 * @extends JLabel
 * */
public class Grid extends JLabel {
	private static final long serialVersionUID = 1L;
	private BufferedImage img = null;
	private int col, row;
	private int delay = 40;
	private float dir = -0.02f, alpha = 1.0f;
	private char flag = empty;

	/**
	 * Grid 
	 * @constructor Two arguments constructor
	 * @param row the Grid row coordinate
	 * @param row the Grid col coordinate
	 * */
	public Grid(int row, int col) {
		super();
		setPreferredSize(new Dimension(32, 32));
		this.row = row;
		this.col = col;
		flag = empty;
	}

	/**
	 * setOccupied
	 * 
	 * @name setOccupied
	 * @brief Set occupied state.
	 * */
	public void setOccupied() {
		flag = occupied;
	}
	
	public boolean getOccupied() {
		return flag == occupied;
	}

	/**
	 * isEmpty
	 * 
	 * @name isEmpty
	 * @return return empty state.
	 * */
	public boolean isEmpty() {
		return flag == empty;
	}

	/**
	 * setHit
	 * 
	 * @name setHit
	 * @brief sets the Grid value hit to true
	 * */
	public void setHit() {
		flag = hit;
	}
	
	/**
	 * isHit
	 * 
	 * @name isHit
	 * @brief checks whether grid is hit
	 * @return true if hit false otherwise
	 * */
	public boolean isHit() {
		return flag == hit;
	}
	
	/**
	 * setHit
	 * @name setMiss
	 * @brief sets the Grid value miss to true
	 * */
	public void setMiss() {
		flag = miss;
	}
	
	/**
	 * isMiss
	 * 
	 * @name isMiss
	 * @brief checks whether grid is miss
	 * @return true if miss false otherwise
	 * */
	public boolean isMiss() {
		return flag == miss;
	}

	/**
	 * getRow
	 * 
	 * @name getRow
	 * @brief returns the Grid row
	 * @return row
	 * */
	public int getRow() {
		return row;
	}

	/**
	 * getCol
	 * 
	 * @name getCol
	 * @brief returns the Grid col
	 * @return col
	 * */
	public int getCol() {
		return col;
	}

	/**
	 * fadeOut
	 * 
	 * @name fadeOut
	 * @brief calls inner class Fader to perform fade out effect on Grid
	 * */
	public void fadeOut() {
		new Fader().fadeOut();
	}

	/**
	 * fadeIn
	 * 
	 * @name fadeIn
	 * @brief calls inner class Fader to perform fade in effect on Grid
	 * */
	public void fadeIn() {
		new Fader().fadeIn();
	}

	/**
	 * fastFade
	 * 
	 * @name fastFade
	 * @brief calls inner class Fader to perform fast fade out in effect on Grid
	 * */
	public void fastFade() {
		new Fader().fastFade();
	}

	/**
	 * @class Fader
	 * @brief Timer that changes alpha value to display an fading effect.
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
		 * @brief fade out effect.
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
		 * @brief fade in effect
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
		 * 
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
		 * checkLowerBounds
		 * 
		 * @name checkLowerBounds
		 * @brief checks whether alpha value is within range
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
		 * checkUpperBounds
		 * 
		 * @name checkUpperBounds
		 * @brief checks whether alpha value is within range
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

	/**
	 * equals
	 * 
	 * @name equals
	 * @brief checks whether to instances is equal
	 * @return return true if equal false otherwise
	 * */
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
	 * hashCode
	 * 
	 * @name hashCode
	 * @brief calculates a hash for this grid instance
	 * @return return a number used as hash
	 * */
	@Override
	public int hashCode() {
		int hash = 13;
		hash = 37 * hash + this.row;
		hash = 37 * hash + this.col;
		return hash;
	}
}
