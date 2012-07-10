package aurelienribon.fxeditor;

import aurelienribon.fx.ParticleEffect;
import aurelienribon.fx.ParticleEffectIo;
import aurelienribon.fx.ParticleEmitter;
import aurelienribon.ui.components.PaintedPanel;
import aurelienribon.ui.css.Style;
import com.badlogic.gdx.Gdx;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import res.Res;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class EditEmitterDialog extends javax.swing.JDialog {
	private boolean okClicked = false;

    public EditEmitterDialog(javax.swing.JFrame parent) {
        super(parent, true);

		setContentPane(new PaintedPanel());
        initComponents();

		okBtn.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {okClicked = true; dispose();}});
		cancelBtn.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {dispose();}});

		nameField.requestFocusInWindow();

		Style.registerCssClasses(getContentPane(), ".rootPanel", ".configPanel");
		Style.apply(getContentPane(), new Style(Res.getUrl("css/style.css")));
    }

	public ParticleEmitter prompt(ParticleEmitter emitter) {
		if (emitter != null) {
			nameField.setText(emitter.name);
			if (emitter.imageName.equals("point")) imgPointRadio.setSelected(true);
			else if (emitter.imageName.equals("disc")) imgDiscRadio.setSelected(true);
			else if (emitter.imageName.equals("halo")) imgHaloRadio.setSelected(true);
			else if (emitter.imageName.equals("square")) imgSquareRadio.setSelected(true);
			else if (emitter.imageName.equals("fire")) imgFireRadio.setSelected(true);
			else {imgCustomRadio.setSelected(true); imgCustomField.setText(emitter.imageName);}
		}

		setVisible(true);
		if (!okClicked) return emitter;

		String regionName;
		if (imgPointRadio.isSelected()) regionName = "point";
		else if (imgDiscRadio.isSelected()) regionName = "disc";
		else if (imgHaloRadio.isSelected()) regionName = "halo";
		else if (imgSquareRadio.isSelected()) regionName = "square";
		else if (imgFireRadio.isSelected()) regionName = "fire";
		else regionName = imgCustomField.getText();

		if (emitter == null) {
			ParticleEffect effect = ParticleEffectIo.loadFromFile(Gdx.files.classpath("res/data/default.effect"), null);
			emitter = effect.getEmitters().get(0);
		}

		emitter.name = nameField.getText().trim().equals("") ? "unamed" : nameField.getText().trim();
		emitter.imageName = regionName;
		emitter.initialize(Assets.getDefaultParticlesAtlas());
		return emitter;
	}

	// -------------------------------------------------------------------------
	// Generated stuff
	// -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        paintedPanel1 = new aurelienribon.ui.components.PaintedPanel();
        cancelBtn = new javax.swing.JButton();
        okBtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        imgPointRadio = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        imgDiscRadio = new javax.swing.JRadioButton();
        imgHaloRadio = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        imgSquareRadio = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        imgFireRadio = new javax.swing.JRadioButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        imgCustomRadio = new javax.swing.JRadioButton();
        imgCustomField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("New Emitter");

        cancelBtn.setText("Cancel");

        okBtn.setText("Ok");

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Particle image");

        buttonGroup1.add(imgPointRadio);
        imgPointRadio.setSelected(true);
        imgPointRadio.setText("Point");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/particles/point.png"))); // NOI18N

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Emitter name");

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/particles/disc.png"))); // NOI18N

        buttonGroup1.add(imgDiscRadio);
        imgDiscRadio.setText("Disc");

        buttonGroup1.add(imgHaloRadio);
        imgHaloRadio.setText("Halo");

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/particles/halo.png"))); // NOI18N

        buttonGroup1.add(imgSquareRadio);
        imgSquareRadio.setText("Square");

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/particles/square.png"))); // NOI18N

        buttonGroup1.add(imgFireRadio);
        imgFireRadio.setText("Fire");

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/particles/fire.png"))); // NOI18N

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/gfx/effect1.png"))); // NOI18N
        jLabel8.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        buttonGroup1.add(imgCustomRadio);
        imgCustomRadio.setText("Custom");

        imgCustomField.setText("<atlas region name>");

        javax.swing.GroupLayout paintedPanel1Layout = new javax.swing.GroupLayout(paintedPanel1);
        paintedPanel1.setLayout(paintedPanel1Layout);
        paintedPanel1Layout.setHorizontalGroup(
            paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paintedPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addGroup(paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paintedPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(paintedPanel1Layout.createSequentialGroup()
                        .addGroup(paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nameField)
                            .addGroup(paintedPanel1Layout.createSequentialGroup()
                                .addGroup(paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(paintedPanel1Layout.createSequentialGroup()
                                        .addComponent(imgCustomRadio)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(imgCustomField))
                                    .addGroup(paintedPanel1Layout.createSequentialGroup()
                                        .addComponent(imgPointRadio)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(imgDiscRadio)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(imgHaloRadio)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel5))
                                    .addGroup(paintedPanel1Layout.createSequentialGroup()
                                        .addComponent(imgSquareRadio)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(imgFireRadio)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel7)))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );

        paintedPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelBtn, okBtn});

        paintedPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel3});

        paintedPanel1Layout.setVerticalGroup(
            paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paintedPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(paintedPanel1Layout.createSequentialGroup()
                        .addGroup(paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel1)
                            .addComponent(imgPointRadio)
                            .addComponent(jLabel2)
                            .addComponent(jLabel5)
                            .addComponent(imgHaloRadio)
                            .addComponent(jLabel4)
                            .addComponent(imgDiscRadio))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel6)
                            .addComponent(imgSquareRadio)
                            .addComponent(jLabel7)
                            .addComponent(imgFireRadio))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(imgCustomField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(imgCustomRadio))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cancelBtn)
                            .addComponent(okBtn))))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(paintedPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(paintedPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton cancelBtn;
    private javax.swing.JTextField imgCustomField;
    private javax.swing.JRadioButton imgCustomRadio;
    private javax.swing.JRadioButton imgDiscRadio;
    private javax.swing.JRadioButton imgFireRadio;
    private javax.swing.JRadioButton imgHaloRadio;
    private javax.swing.JRadioButton imgPointRadio;
    private javax.swing.JRadioButton imgSquareRadio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JTextField nameField;
    private javax.swing.JButton okBtn;
    private aurelienribon.ui.components.PaintedPanel paintedPanel1;
    // End of variables declaration//GEN-END:variables

}
