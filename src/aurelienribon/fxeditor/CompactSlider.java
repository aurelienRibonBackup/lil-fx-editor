package aurelienribon.fxeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class CompactSlider extends JPanel {
	private float min = 0, max = 1, value = 0;
	private final JLabel label = new JLabel();
	private boolean isFloat = true;
	private int labelWidth = 35;
	private Paint stroke = Color.GRAY;
	private Paint fill = Color.WHITE;

	public CompactSlider() {
		setOpaque(false);
		setLayout(new BorderLayout());
		add(label, BorderLayout.EAST);

		label.setText("0.0");
		label.setPreferredSize(new Dimension(labelWidth, label.getPreferredSize().height));
		setPreferredSize(new Dimension(150, label.getPreferredSize().height));

		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
	}

	@Override
	protected void paintComponent(Graphics g) {
		label.setFont(getFont());
		label.setForeground(getForeground());
		label.setText(String.format(Locale.US, isFloat ? "%.2f" : "%.0f", value));

		Graphics2D gg = (Graphics2D) g.create();
		int w = getWidth();
		int h = getHeight();

		gg.setPaint(fill);
		gg.fillRect(0, 0, w-labelWidth-5-1, h-1);
		gg.setPaint(stroke);
		gg.drawRect(0, 0, w-labelWidth-5-1, h-1);

		int x = (int) ((value-min)/(max-min) * (w-labelWidth-5-10)) + 5;
		gg.setPaint(stroke);
		gg.fillRect(x-5, 0, 10, h-1);

		gg.dispose();
	}

	private final MouseAdapter mouseAdapter = new MouseAdapter() {
		private boolean isDragEnabled = false;

		@Override
		public void mousePressed(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				int w = getWidth();
				int x = e.getX();
				isDragEnabled = x < w-labelWidth-5;
				if (isDragEnabled) setValue((x-5.0f)/(w-labelWidth-5-10) * (max-min) + min);
			} else if (SwingUtilities.isRightMouseButton(e)) {
				Window wnd = SwingUtilities.getWindowAncestor(CompactSlider.this);
				String input = JOptionPane.showInputDialog(wnd, "New value?", value);
				if (input != null) {
					try {setValue(Float.parseFloat(input));}
					catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(wnd, "Invalid number format.");
					}
				}
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (!isDragEnabled) return;
			int w = getWidth();
			int x = e.getX();
			setValue((x-5.0f)/(w-labelWidth-5-10) * (max-min) + min);
		}
	};

	public void setMin(float min) {
		this.min = min;
		if (value < min) {
			value = min;
			repaint();
		}
	}

	public void setMax(float max) {
		this.max = max;
		if (value > max) {
			value = max;
			repaint();
		}
	}

	public void setValue(float value) {
		if (value < min) value = min;
		if (value > max) value = max;
		this.value = value;
		repaint();
	}

	public float getMin() {return min;}
	public float getMax() {return max;}
	public float getValue() {return isFloat ? value : Math.round(value);}

	public void setFloat(boolean isFloat) {this.isFloat = isFloat;}
	public void setLabelWidth(int labelWidth) {this.labelWidth = labelWidth;}
	public void setStroke(Paint stroke) {this.stroke = stroke;}
	public void setFill(Paint fill) {this.fill = fill;}
	public boolean isFloat() {return isFloat;}
	public int getLabelWidth() {return labelWidth;}
	public Paint getStroke() {return stroke;}
	public Paint getFill() {return fill;}
}
