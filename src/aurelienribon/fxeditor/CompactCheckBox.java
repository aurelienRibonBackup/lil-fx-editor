package aurelienribon.fxeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class CompactCheckBox extends JPanel {
	private boolean isSelected = false;
	private final JLabel label = new JLabel();
	private Paint stroke = Color.GRAY;
	private Paint fill = Color.WHITE;

	public CompactCheckBox() {
		setOpaque(false);
		setLayout(new BorderLayout());
		add(label, BorderLayout.CENTER);

		label.setText("Compact checkbox");
		add(Box.createHorizontalStrut(label.getPreferredSize().height+5), BorderLayout.WEST);

		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
	}

	@Override
	protected void paintComponent(Graphics g) {
		label.setFont(getFont());
		label.setForeground(getForeground());

		Graphics2D gg = (Graphics2D) g.create();
		int h = getHeight();

		gg.setPaint(fill);
		gg.fillRect(0, 0, h-1, h-1);
		gg.setPaint(stroke);
		gg.drawRect(0, 0, h-1, h-1);

		if (isSelected) {
			gg.setPaint(stroke);
			gg.fillRect(2, 2, h-4, h-4);
		}

		gg.dispose();
	}

	private final MouseAdapter mouseAdapter = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			isSelected = !isSelected;
			repaint();
		}
	};

	public void setSelected(boolean isSelected) {this.isSelected = isSelected;repaint();}
	public boolean isSelected() {return isSelected;}

	public void setText(String text) {label.setText(text);}
	public void setStroke(Paint stroke) {this.stroke = stroke;}
	public void setFill(Paint fill) {this.fill = fill;}
	public String getText() {return label.getText();}
	public Paint getStroke() {return stroke;}
	public Paint getFill() {return fill;}
}
