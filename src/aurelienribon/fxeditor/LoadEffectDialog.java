package aurelienribon.fxeditor;

import aurelienribon.fx.ParticleEffect;
import aurelienribon.fx.ParticleEffectIo;
import aurelienribon.ui.components.PaintedPanel;
import aurelienribon.ui.css.Style;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import res.Res;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class LoadEffectDialog extends javax.swing.JDialog {
	private boolean okClicked = false;

    public LoadEffectDialog(javax.swing.JFrame parent) {
        super(parent, true);

		setContentPane(new PaintedPanel());
        initComponents();

		okBtn.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {okClicked = true; dispose();}});
		cancelBtn.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {dispose();}});
		customEffectBrowseBtn.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {browseEffect();}});
		customAtlasBrowseBtn.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {browseAtlas();}});

		okBtn.requestFocusInWindow();
		presetsList.setSelectedIndex(0);

		Style.registerCssClasses(getContentPane(), ".rootPanel", ".configPanel");
		Style.apply(getContentPane(), new Style(Res.getUrl("css/style.css")));
    }

	public ParticleEffect prompt() throws IOException {
		setVisible(true);
		if (!okClicked) return null;

		FileHandle effectFileHandle;
		TextureAtlas atlas = Assets.getDefaultParticlesAtlas();

		if (customEffectField.getText().equals("")) {
			String preset = (String) presetsList.getSelectedValue();
			effectFileHandle = Gdx.files.classpath("res/data/presets/" + preset + ".effect");

		} else {
			File effectFile = new File(customEffectField.getText());
			if (!effectFile.isFile()) throw new IOException("Selected effect file does not exist");
			effectFileHandle = Gdx.files.absolute(effectFile.getAbsolutePath());

			if (!customAtlasField.getText().contains("")) {
				File atlasFile = new File(customAtlasField.getText());
				if (!atlasFile.isFile()) throw new IOException("Selected atlas file does not exist");
				try {atlas = new TextureAtlas(Gdx.files.absolute(atlasFile.getAbsolutePath()));}
				catch (GdxRuntimeException ex) {throw new IOException(ex.getMessage());}
			}
		}

		ParticleEffect effect = ParticleEffectIo.loadFromFile(effectFileHandle, atlas);
		return effect;
	}

	private void browseEffect() {
		JFileChooser chooser = new JFileChooser(".");
		chooser.setMultiSelectionEnabled(false);
		chooser.setDialogTitle("Select an effect file");
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			customEffectField.setText(file.getAbsolutePath());
		}
	}

	private void browseAtlas() {

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
        jLabel8 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        presetsList = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        customEffectField = new javax.swing.JTextField();
        customEffectBrowseBtn = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        customAtlasField = new javax.swing.JTextField();
        customAtlasBrowseBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Load Effect");

        cancelBtn.setText("Cancel");

        okBtn.setText("Ok");

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/gfx/effect2.png"))); // NOI18N
        jLabel8.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel1.setText("Presets");

        presetsList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "empty", "fire - candle" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        presetsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(presetsList);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Custom effect");

        customEffectField.setEditable(false);

        customEffectBrowseBtn.setText("...");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Custom atlas");

        customAtlasField.setEditable(false);

        customAtlasBrowseBtn.setText("...");

        javax.swing.GroupLayout paintedPanel1Layout = new javax.swing.GroupLayout(paintedPanel1);
        paintedPanel1.setLayout(paintedPanel1Layout);
        paintedPanel1Layout.setHorizontalGroup(
            paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paintedPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addGroup(paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(paintedPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(253, 311, Short.MAX_VALUE))
                    .addGroup(paintedPanel1Layout.createSequentialGroup()
                        .addGroup(paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paintedPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(paintedPanel1Layout.createSequentialGroup()
                                .addGroup(paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(customAtlasField)
                                    .addComponent(customEffectField))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(customEffectBrowseBtn, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(customAtlasBrowseBtn, javax.swing.GroupLayout.Alignment.TRAILING)))
                            .addComponent(jScrollPane1))
                        .addContainerGap())))
        );

        paintedPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelBtn, okBtn});

        paintedPanel1Layout.setVerticalGroup(
            paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paintedPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(paintedPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addGroup(paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(customEffectField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(customEffectBrowseBtn))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(customAtlasField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(customAtlasBrowseBtn))
                        .addGap(18, 18, 18)
                        .addGroup(paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cancelBtn)
                            .addComponent(okBtn)))
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        paintedPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {customAtlasBrowseBtn, customAtlasField, customEffectBrowseBtn, customEffectField, jLabel2, jLabel3});

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
    private javax.swing.JButton customAtlasBrowseBtn;
    private javax.swing.JTextField customAtlasField;
    private javax.swing.JButton customEffectBrowseBtn;
    private javax.swing.JTextField customEffectField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton okBtn;
    private aurelienribon.ui.components.PaintedPanel paintedPanel1;
    private javax.swing.JList presetsList;
    // End of variables declaration//GEN-END:variables

}
