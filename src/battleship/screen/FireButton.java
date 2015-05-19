package battleship.screen;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.Timer;

import resources.audio.SoundHolder;

public class FireButton extends JButton {
	private static final long serialVersionUID = 1L;
	BufferedImage img = null;
	private float alpha;
	private float level;
	private int delay;

	public FireButton(String text) {
		super(text);
		setOpaque(false);
		setSize(new Dimension(100, 120));
		alpha = 1.0f;
		level = -0.02f;
		delay = 40;
		this.addActionListener(new Fader());
		SoundHolder.initSounds();
	}

	class Fader implements ActionListener {
		private Timer t;

		@Override
		public void actionPerformed(ActionEvent e) {
			SoundHolder.getAudio("explosion1").playAudio();
			if(t != null && t.isRunning()) { t.stop(); t = null; }
			t = new Timer(delay, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					alpha += level;
					if (!checkBounds()) {
						changeDirection();
					}
					repaint();
				}
			});
			t.setCoalesce(true);
			t.start();
		}

		private boolean checkBounds() {
			if (alpha < 0.0f) {
				alpha = 0.0f;
				return false;
			} else if (alpha > 1.0f) {
				alpha = 1.0f;
				t.stop();
				return false;
			} else {
				return true;
			}
		}

		private void changeDirection() {
			level = -level;
		}
	}

	@Override
	public void paint(Graphics g) {
		if (img == null || img.getWidth() != getWidth()
				|| img.getHeight() != getHeight()) {
			img = getGraphicsConfiguration().createCompatibleImage(getWidth(),
					getHeight());
		}
		Graphics g_img = img.getGraphics();
		g_img.setClip(g.getClip());
		super.paint(g_img);

		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
		g2d.drawImage(img, 0, 0, null);
		g2d.dispose();
	}
}
