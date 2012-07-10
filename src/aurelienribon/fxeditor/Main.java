package aurelienribon.fxeditor;

import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class Main {
	public static void main (String[] args) {
		SwingUtilities.invokeLater(new Runnable() {@Override public void run() {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException ex) {
			} catch (InstantiationException ex) {
			} catch (IllegalAccessException ex) {
			} catch (UnsupportedLookAndFeelException ex) {
			}

			Canvas canvas = new Canvas();
			LwjglCanvas glCanvas = new LwjglCanvas(canvas, true);

			MainWindow mw = new MainWindow(canvas, glCanvas.getCanvas());
			mw.pack();
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			mw.setSize(
				Math.min(1200, screenSize.width - 100),
				Math.min(mw.getHeight(), screenSize.height - 50)
			);
			mw.setLocationRelativeTo(null);
			mw.setVisible(true);
		}});
	}
}
