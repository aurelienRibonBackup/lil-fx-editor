package aurelienribon.fxeditor;

import aurelienribon.fx.ParticleAttrs;
import aurelienribon.fx.ParticleEffect;
import aurelienribon.fx.ParticleEffectIo;
import aurelienribon.fx.ParticleEmitter;
import aurelienribon.ui.components.ArStyle;
import aurelienribon.ui.components.PaintedPanel;
import aurelienribon.ui.css.Style;
import aurelienribon.ui.css.swing.SwingStyle;
import aurelienribon.utils.notifications.AutoListModel;
import com.badlogic.gdx.Gdx;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import res.Res;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class MainWindow extends javax.swing.JFrame {
	private final Canvas canvas;
	private List<ParticleEmitter> emitters;

	public MainWindow(final Canvas canvas, Component canvasCmp) {
		this.canvas = canvas;

		setContentPane(new PaintedPanel());
		getContentPane().setLayout(new BorderLayout());
		initComponents();
		renderPanel.add(canvasCmp, BorderLayout.CENTER);

		newEmitterBtn.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {newEmitter();}});
		editEmitterBtn.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {editEmitter();}});
		deleteEmitterBtn.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {deleteEmitter();}});
		moveUpEmitterBtn.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {moveUpEmitter();}});
		moveDownEmitterBtn.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {moveDownEmitter();}});
		saveEffectBtn.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {saveEffect();}});
		loadEffectBtn.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {loadEffect();}});
		bgSetBtn.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {setBackground();}});
		bgClearBtn.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {clearBackground();}});

		emittersList.setModel(new AutoListModel<ParticleEmitter>(new ArrayList<ParticleEmitter>()));
		emittersListSelectionListener.valueChanged(null);

		SwingStyle.init();
		ArStyle.init();
		Style.registerCssClasses(getContentPane(), ".rootPanel");
		Style.registerCssClasses(renderOptionPanel, ".titledPanel", "#renderOptionsPanel");
		Style.registerCssClasses(effectPanel, ".titledPanel", "#effectPanel");
		Style.registerCssClasses(emitterConfigPanel, ".titledPanel", "#emitterConfigPanel");
		Style.registerCssClasses(particleConfigPanel, ".titledPanel", "#particleConfigPanel");
		Style.registerCssClasses(startColorPanel, ".titledPanel", "#startColorPanel");
		Style.registerCssClasses(endColorPanel, ".titledPanel", "#endColorPanel");
		Style.registerCssClasses(startColorVariancePanel, ".titledPanel", "#startColorVariancePanel");
		Style.registerCssClasses(endColorVariancePanel, ".titledPanel", "#endColorVariancePanel");
		Style.registerCssClasses(headerPanel, ".headerPanel");
		Style.registerCssClasses(comment, ".comment");
		Style.apply(getContentPane(), new Style(Res.getUrl("css/style.css")));

		final Timer timer = new Timer(100, new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {updateCanvas();}
		});

		timer.setRepeats(true);
		timer.start();

		addWindowListener(new WindowAdapter() {
			@Override public void windowOpened(WindowEvent e) {
				emittersList.setCellRenderer(emittersListCellRenderer);
				emittersList.addListSelectionListener(emittersListSelectionListener);
				setEffect(ParticleEffectIo.loadFromFile(Gdx.files.classpath("res/data/presets/fire - candle.effect"), Assets.getDefaultParticlesAtlas()));
			}

			@Override public void windowClosing(WindowEvent e) {
				timer.stop();
			}
		});
	}

	private final ListCellRenderer emittersListCellRenderer = new DefaultListCellRenderer() {
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			ParticleEmitter emitter = (ParticleEmitter) value;
			label.setText(emitter.name);
			label.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
			return label;
		}
	};

	private final ListSelectionListener emittersListSelectionListener = new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			ParticleEmitter emitter = (ParticleEmitter) emittersList.getSelectedValue();
			updateWidgets(emitter);
			canvas.selectedEmitter = emitter;
			editEmitterBtn.setEnabled(emitter != null);
			deleteEmitterBtn.setEnabled(emitter != null);
			moveUpEmitterBtn.setEnabled(emitter != null);
			moveDownEmitterBtn.setEnabled(emitter != null);
			saveEffectBtn.setEnabled(emitter != null);
		}
	};

	private void newEmitter() {
		EditEmitterDialog dialog = new EditEmitterDialog(this);
		dialog.setLocationRelativeTo(this);
		dialog.setTitle("New Emitter");
		ParticleEmitter emitter = dialog.prompt(null);

		if (emitter != null) {
			emitters.add(emitter);
			emittersList.setSelectedValue(emitter, true);
			canvas.effect.setPosition(canvas.effect.getX(), canvas.effect.getY());
			updateWidgets(emitter);
			((AutoListModel<ParticleEmitter>)emittersList.getModel()).forceRefresh();
		}
	}

	private void editEmitter() {
		ParticleEmitter emitter = (ParticleEmitter) emittersList.getSelectedValue();

		EditEmitterDialog dialog = new EditEmitterDialog(this);
		dialog.setLocationRelativeTo(this);
		dialog.setTitle("Edit Emitter");
		dialog.prompt(emitter);
		((AutoListModel<ParticleEmitter>)emittersList.getModel()).forceRefresh();
	}

	private void deleteEmitter() {
		ParticleEmitter emitter = (ParticleEmitter) emittersList.getSelectedValue();
		emitters.remove(emitter);
		emittersList.clearSelection();
		((AutoListModel<ParticleEmitter>)emittersList.getModel()).forceRefresh();
	}

	private void moveUpEmitter() {
		ParticleEmitter emitter = (ParticleEmitter) emittersList.getSelectedValue();
		int idx = emitters.indexOf(emitter);
		if (idx > 0) {
			ParticleEmitter emitter2 = emitters.get(idx-1);
			emitters.set(idx-1, emitter);
			emitters.set(idx, emitter2);
			emittersList.setSelectedValue(emitter, true);
			((AutoListModel<ParticleEmitter>)emittersList.getModel()).forceRefresh();
		}
	}

	private void moveDownEmitter() {
		ParticleEmitter emitter = (ParticleEmitter) emittersList.getSelectedValue();
		int idx = emitters.indexOf(emitter);
		if (idx < emitters.size()-1) {
			ParticleEmitter emitter2 = emitters.get(idx+1);
			emitters.set(idx+1, emitter);
			emitters.set(idx, emitter2);
			emittersList.setSelectedValue(emitter, true);
			((AutoListModel<ParticleEmitter>)emittersList.getModel()).forceRefresh();
		}
	}

	private void loadEffect() {
		LoadEffectDialog dialog = new LoadEffectDialog(this);
		dialog.setLocationRelativeTo(this);
		ParticleEffect effect;
		try {
			effect = dialog.prompt();
			if (effect != null) setEffect(effect);
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage());
		}
	}

	private void saveEffect() {
		JFileChooser chooser = new JFileChooser(".");
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			String filePath = chooser.getSelectedFile().getPath();
			ParticleEffectIo.saveToFile(canvas.effect, Gdx.files.absolute(filePath));
		}
	}

	private void setBackground() {
		JFileChooser chooser = new JFileChooser(".");
		chooser.setDialogTitle("Select your background image");
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileFilter(new FileNameExtensionFilter("Images", "png", "jpg", "jpeg"));
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			canvas.setBackground(file.getAbsolutePath());
		}
	}

	private void clearBackground() {
		canvas.setBackground(null);
	}

	private void setEffect(ParticleEffect effect) {
		canvas.effect = effect;
		effect.start();
		emitters = effect != null ? effect.getEmitters() : new ArrayList<ParticleEmitter>();
		emittersList.setModel(new AutoListModel<ParticleEmitter>(emitters));
		emittersList.setSelectedIndex(0);
		((AutoListModel<ParticleEmitter>)emittersList.getModel()).forceRefresh();
	}

	private void updateCanvas() {
		canvas.bgColor.r = cvBgRSlider.getValue();
		canvas.bgColor.g = cvBgGSlider.getValue();
		canvas.bgColor.b = cvBgBSlider.getValue();
		canvas.linearFilter = cvLinearFilterChk.isSelected();
		canvas.renderToTexture = cvRenderToTextureChk.isSelected();
		canvas.drawHelpLines = cvDrawMeasureInfoChk.isSelected();
		canvas.drawBgSprite = cvDrawBgImageChk.isSelected();

		ParticleEmitter emitter = (ParticleEmitter) emittersList.getSelectedValue();
		if (emitter == null) return;

		emitter.maxParticles = (int) emMaxParticlesSlider.getValue();
		emitter.continuous = emContinuousChk.isSelected();
		emitter.duration = emDurationSlider.getValue();
		emitter.delay = emDelaySlider.getValue();
		emitter.gravityX = emGravityXSlider.getValue();
		emitter.gravityY = emGravityYSlider.getValue();
		emitter.additiveBlending = emAdditiveBlendingChk.isSelected();

		emitter.pAttrs.lifespan = pLifespanSlider.getValue();
		emitter.pAttrs.startSize = pStartSizeSlider.getValue();
		emitter.pAttrs.endSize = pEndSizeSlider.getValue();
		emitter.pAttrs.startRot = pStartRotSlider.getValue();
		emitter.pAttrs.endRot = pEndRotSlider.getValue();
		emitter.pAttrs.startColor.r = pStartColorRSlider.getValue();
		emitter.pAttrs.startColor.g = pStartColorGSlider.getValue();
		emitter.pAttrs.startColor.b = pStartColorBSlider.getValue();
		emitter.pAttrs.startColor.a = pStartColorASlider.getValue();
		emitter.pAttrs.endColor.r = pEndColorRSlider.getValue();
		emitter.pAttrs.endColor.g = pEndColorGSlider.getValue();
		emitter.pAttrs.endColor.b = pEndColorBSlider.getValue();
		emitter.pAttrs.endColor.a = pEndColorASlider.getValue();
		emitter.pAttrs.speed = pSpeedSlider.getValue();
		emitter.pAttrs.angle = pAngleSlider.getValue();

		emitter.pAttrsVar.lifespan = pLifespanVarianceSlider.getValue();
		emitter.pAttrsVar.startSize = pStartSizeVarianceSlider.getValue();
		emitter.pAttrsVar.endSize = pEndSizeVarianceSlider.getValue();
		emitter.pAttrsVar.startRot = pStartRotVarianceSlider.getValue();
		emitter.pAttrsVar.endRot = pEndRotVarianceSlider.getValue();
		emitter.pAttrsVar.startColor.r = pStartColorRVarianceSlider.getValue();
		emitter.pAttrsVar.startColor.g = pStartColorGVarianceSlider.getValue();
		emitter.pAttrsVar.startColor.b = pStartColorBVarianceSlider.getValue();
		emitter.pAttrsVar.startColor.a = pStartColorAVarianceSlider.getValue();
		emitter.pAttrsVar.endColor.r = pEndColorRVarianceSlider.getValue();
		emitter.pAttrsVar.endColor.g = pEndColorGVarianceSlider.getValue();
		emitter.pAttrsVar.endColor.b = pEndColorBVarianceSlider.getValue();
		emitter.pAttrsVar.endColor.a = pEndColorAVarianceSlider.getValue();
		emitter.pAttrsVar.x = pXVarianceSlider.getValue();
		emitter.pAttrsVar.y = pYVarianceSlider.getValue();
		emitter.pAttrsVar.speed = pSpeedVarianceSlider.getValue();
		emitter.pAttrsVar.angle = pAngleVarianceSlider.getValue();
	}

	private void updateWidgets(ParticleEmitter emitter) {
		if (emitter == null) return;

		emMaxParticlesSlider.setValue(emitter.maxParticles);
		emContinuousChk.setSelected(emitter.continuous);
		emDurationSlider.setValue(emitter.duration);
		emDelaySlider.setValue(emitter.delay);
		emGravityXSlider.setValue(emitter.gravityX);
		emGravityYSlider.setValue(emitter.gravityY);
		emAdditiveBlendingChk.setSelected(emitter.additiveBlending);

		ParticleAttrs pAttrs = emitter.pAttrs;
		pLifespanSlider.setValue(pAttrs.lifespan);
		pStartSizeSlider.setValue(pAttrs.startSize);
		pEndSizeSlider.setValue(pAttrs.endSize);
		pStartRotSlider.setValue(pAttrs.startRot);
		pEndRotSlider.setValue(pAttrs.endRot);
		pStartColorRSlider.setValue(pAttrs.startColor.r);
		pStartColorGSlider.setValue(pAttrs.startColor.g);
		pStartColorBSlider.setValue(pAttrs.startColor.b);
		pStartColorASlider.setValue(pAttrs.startColor.a);
		pEndColorRSlider.setValue(pAttrs.endColor.r);
		pEndColorGSlider.setValue(pAttrs.endColor.g);
		pEndColorBSlider.setValue(pAttrs.endColor.b);
		pEndColorASlider.setValue(pAttrs.endColor.a);
		pSpeedSlider.setValue(pAttrs.speed);
		pAngleSlider.setValue(pAttrs.angle);

		ParticleAttrs pAttrsVar = emitter.pAttrsVar;
		pLifespanVarianceSlider.setValue(pAttrsVar.lifespan);
		pStartSizeVarianceSlider.setValue(pAttrsVar.startSize);
		pEndSizeVarianceSlider.setValue(pAttrsVar.endSize);
		pStartRotVarianceSlider.setValue(pAttrsVar.startRot);
		pEndRotVarianceSlider.setValue(pAttrsVar.endRot);
		pStartColorRVarianceSlider.setValue(pAttrsVar.startColor.r);
		pStartColorGVarianceSlider.setValue(pAttrsVar.startColor.g);
		pStartColorBVarianceSlider.setValue(pAttrsVar.startColor.b);
		pStartColorAVarianceSlider.setValue(pAttrsVar.startColor.a);
		pEndColorRVarianceSlider.setValue(pAttrsVar.endColor.r);
		pEndColorGVarianceSlider.setValue(pAttrsVar.endColor.g);
		pEndColorBVarianceSlider.setValue(pAttrsVar.endColor.b);
		pEndColorAVarianceSlider.setValue(pAttrsVar.endColor.a);
		pXVarianceSlider.setValue(pAttrsVar.x);
		pYVarianceSlider.setValue(pAttrsVar.y);
		pSpeedVarianceSlider.setValue(pAttrsVar.speed);
		pAngleVarianceSlider.setValue(pAttrsVar.angle);
	}

	// -------------------------------------------------------------------------
	// Generated stuff
	// -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        renderPanel = new javax.swing.JPanel();
        renderOptionPanel = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        cvBgRSlider = new aurelienribon.fxeditor.CompactSlider();
        cvBgGSlider = new aurelienribon.fxeditor.CompactSlider();
        cvBgBSlider = new aurelienribon.fxeditor.CompactSlider();
        cvLinearFilterChk = new aurelienribon.fxeditor.CompactCheckBox();
        cvRenderToTextureChk = new aurelienribon.fxeditor.CompactCheckBox();
        cvDrawMeasureInfoChk = new aurelienribon.fxeditor.CompactCheckBox();
        bgSetBtn = new javax.swing.JButton();
        bgClearBtn = new javax.swing.JButton();
        cvDrawBgImageChk = new aurelienribon.fxeditor.CompactCheckBox();
        effectPanel = new javax.swing.JPanel();
        headerPanel = new javax.swing.JPanel();
        jToolBar4 = new javax.swing.JToolBar();
        newEmitterBtn = new javax.swing.JButton();
        editEmitterBtn = new javax.swing.JButton();
        deleteEmitterBtn = new javax.swing.JButton();
        moveUpEmitterBtn = new javax.swing.JButton();
        moveDownEmitterBtn = new javax.swing.JButton();
        jToolBar7 = new javax.swing.JToolBar();
        saveEffectBtn = new javax.swing.JButton();
        loadEffectBtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        emittersList = new javax.swing.JList();
        emitterConfigPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        emMaxParticlesSlider = new aurelienribon.fxeditor.CompactSlider();
        emDurationSlider = new aurelienribon.fxeditor.CompactSlider();
        emDelaySlider = new aurelienribon.fxeditor.CompactSlider();
        jLabel36 = new javax.swing.JLabel();
        emGravityXSlider = new aurelienribon.fxeditor.CompactSlider();
        emGravityYSlider = new aurelienribon.fxeditor.CompactSlider();
        jLabel37 = new javax.swing.JLabel();
        emContinuousChk = new aurelienribon.fxeditor.CompactCheckBox();
        emAdditiveBlendingChk = new aurelienribon.fxeditor.CompactCheckBox();
        particleConfigPanel = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        pLifespanSlider = new aurelienribon.fxeditor.CompactSlider();
        pLifespanVarianceSlider = new aurelienribon.fxeditor.CompactSlider();
        pAngleSlider = new aurelienribon.fxeditor.CompactSlider();
        pStartSizeSlider = new aurelienribon.fxeditor.CompactSlider();
        pEndSizeSlider = new aurelienribon.fxeditor.CompactSlider();
        pStartRotSlider = new aurelienribon.fxeditor.CompactSlider();
        pEndRotSlider = new aurelienribon.fxeditor.CompactSlider();
        jLabel5 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        pAngleVarianceSlider = new aurelienribon.fxeditor.CompactSlider();
        jLabel9 = new javax.swing.JLabel();
        pStartSizeVarianceSlider = new aurelienribon.fxeditor.CompactSlider();
        jLabel10 = new javax.swing.JLabel();
        pEndSizeVarianceSlider = new aurelienribon.fxeditor.CompactSlider();
        jLabel15 = new javax.swing.JLabel();
        pStartRotVarianceSlider = new aurelienribon.fxeditor.CompactSlider();
        jLabel16 = new javax.swing.JLabel();
        pEndRotVarianceSlider = new aurelienribon.fxeditor.CompactSlider();
        jLabel7 = new javax.swing.JLabel();
        pXVarianceSlider = new aurelienribon.fxeditor.CompactSlider();
        pYVarianceSlider = new aurelienribon.fxeditor.CompactSlider();
        jLabel6 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        pSpeedSlider = new aurelienribon.fxeditor.CompactSlider();
        pSpeedVarianceSlider = new aurelienribon.fxeditor.CompactSlider();
        jLabel19 = new javax.swing.JLabel();
        startColorPanel = new javax.swing.JPanel();
        pStartColorBSlider = new aurelienribon.fxeditor.CompactSlider();
        jLabel23 = new javax.swing.JLabel();
        pStartColorASlider = new aurelienribon.fxeditor.CompactSlider();
        jLabel20 = new javax.swing.JLabel();
        pStartColorGSlider = new aurelienribon.fxeditor.CompactSlider();
        jLabel22 = new javax.swing.JLabel();
        pStartColorRSlider = new aurelienribon.fxeditor.CompactSlider();
        jLabel21 = new javax.swing.JLabel();
        endColorPanel = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        pEndColorGSlider = new aurelienribon.fxeditor.CompactSlider();
        jLabel32 = new javax.swing.JLabel();
        pEndColorASlider = new aurelienribon.fxeditor.CompactSlider();
        pEndColorBSlider = new aurelienribon.fxeditor.CompactSlider();
        pEndColorRSlider = new aurelienribon.fxeditor.CompactSlider();
        jLabel33 = new javax.swing.JLabel();
        startColorVariancePanel = new javax.swing.JPanel();
        pStartColorBVarianceSlider = new aurelienribon.fxeditor.CompactSlider();
        jLabel28 = new javax.swing.JLabel();
        pStartColorAVarianceSlider = new aurelienribon.fxeditor.CompactSlider();
        jLabel29 = new javax.swing.JLabel();
        pStartColorGVarianceSlider = new aurelienribon.fxeditor.CompactSlider();
        jLabel30 = new javax.swing.JLabel();
        pStartColorRVarianceSlider = new aurelienribon.fxeditor.CompactSlider();
        jLabel31 = new javax.swing.JLabel();
        endColorVariancePanel = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        pEndColorGVarianceSlider = new aurelienribon.fxeditor.CompactSlider();
        jLabel40 = new javax.swing.JLabel();
        pEndColorAVarianceSlider = new aurelienribon.fxeditor.CompactSlider();
        pEndColorBVarianceSlider = new aurelienribon.fxeditor.CompactSlider();
        pEndColorRVarianceSlider = new aurelienribon.fxeditor.CompactSlider();
        jLabel41 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        comment = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Lil' Fx-Editor");

        renderPanel.setLayout(new java.awt.BorderLayout());

        jLabel25.setText("Background R");

        jLabel26.setText("Background G");

        jLabel27.setText("Background B");

        cvLinearFilterChk.setSelected(true);
        cvLinearFilterChk.setText("Linear filtering");

        cvRenderToTextureChk.setText("Render to texture");

        cvDrawMeasureInfoChk.setText("Draw measure info");

        bgSetBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/gfx/ic_texture.png"))); // NOI18N
        bgSetBtn.setToolTipText("Set background image");

        bgClearBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/gfx/ic_delete.png"))); // NOI18N
        bgClearBtn.setToolTipText("Clear background image");

        cvDrawBgImageChk.setSelected(true);
        cvDrawBgImageChk.setText("Draw background image");

        javax.swing.GroupLayout renderOptionPanelLayout = new javax.swing.GroupLayout(renderOptionPanel);
        renderOptionPanel.setLayout(renderOptionPanelLayout);
        renderOptionPanelLayout.setHorizontalGroup(
            renderOptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(renderOptionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(renderOptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(renderOptionPanelLayout.createSequentialGroup()
                        .addComponent(jLabel25)
                        .addGap(10, 10, 10)
                        .addComponent(cvBgRSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(renderOptionPanelLayout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cvBgGSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(renderOptionPanelLayout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addGap(10, 10, 10)
                        .addComponent(cvBgBSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(renderOptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cvRenderToTextureChk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cvDrawMeasureInfoChk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(renderOptionPanelLayout.createSequentialGroup()
                        .addComponent(cvLinearFilterChk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cvDrawBgImageChk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addGroup(renderOptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bgClearBtn, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(bgSetBtn, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        renderOptionPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel25, jLabel26, jLabel27});

        renderOptionPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cvDrawMeasureInfoChk, cvLinearFilterChk, cvRenderToTextureChk});

        renderOptionPanelLayout.setVerticalGroup(
            renderOptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(renderOptionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(renderOptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(renderOptionPanelLayout.createSequentialGroup()
                        .addComponent(bgSetBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bgClearBtn))
                    .addGroup(renderOptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(cvDrawBgImageChk, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(renderOptionPanelLayout.createSequentialGroup()
                            .addGroup(renderOptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel25)
                                .addComponent(cvBgRSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cvLinearFilterChk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(renderOptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(renderOptionPanelLayout.createSequentialGroup()
                                    .addGroup(renderOptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel26)
                                        .addComponent(cvBgGSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(renderOptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel27)
                                        .addComponent(cvBgBSlider, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(renderOptionPanelLayout.createSequentialGroup()
                                    .addComponent(cvRenderToTextureChk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cvDrawMeasureInfoChk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        effectPanel.setLayout(new java.awt.BorderLayout());

        headerPanel.setLayout(new java.awt.BorderLayout());

        jToolBar4.setFloatable(false);
        jToolBar4.setRollover(true);

        newEmitterBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/gfx/ic_add.png"))); // NOI18N
        newEmitterBtn.setText("Add emitter");
        newEmitterBtn.setToolTipText("Add a new emitter");
        newEmitterBtn.setFocusable(false);
        newEmitterBtn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        newEmitterBtn.setMargin(new java.awt.Insets(2, 3, 2, 3));
        jToolBar4.add(newEmitterBtn);

        editEmitterBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/gfx/ic_edit.png"))); // NOI18N
        editEmitterBtn.setToolTipText("Edit the selected emitter");
        editEmitterBtn.setFocusable(false);
        editEmitterBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editEmitterBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar4.add(editEmitterBtn);

        deleteEmitterBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/gfx/ic_delete.png"))); // NOI18N
        deleteEmitterBtn.setToolTipText("Delete the selected emitter");
        deleteEmitterBtn.setFocusable(false);
        deleteEmitterBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteEmitterBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar4.add(deleteEmitterBtn);

        moveUpEmitterBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/gfx/ic_up.png"))); // NOI18N
        moveUpEmitterBtn.setToolTipText("Move the selected emitter up in the list");
        moveUpEmitterBtn.setFocusable(false);
        moveUpEmitterBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        moveUpEmitterBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar4.add(moveUpEmitterBtn);

        moveDownEmitterBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/gfx/ic_down.png"))); // NOI18N
        moveDownEmitterBtn.setToolTipText("Move the selected emitter down in the list");
        moveDownEmitterBtn.setFocusable(false);
        moveDownEmitterBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        moveDownEmitterBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar4.add(moveDownEmitterBtn);

        headerPanel.add(jToolBar4, java.awt.BorderLayout.WEST);

        jToolBar7.setFloatable(false);
        jToolBar7.setRollover(true);

        saveEffectBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/gfx/ic_save.png"))); // NOI18N
        saveEffectBtn.setText("Save");
        saveEffectBtn.setToolTipText("Add a new emitter");
        saveEffectBtn.setFocusable(false);
        saveEffectBtn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        saveEffectBtn.setMargin(new java.awt.Insets(2, 3, 2, 3));
        jToolBar7.add(saveEffectBtn);

        loadEffectBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/gfx/ic_open.png"))); // NOI18N
        loadEffectBtn.setText("Load");
        loadEffectBtn.setToolTipText("Rename the selected emitter");
        loadEffectBtn.setFocusable(false);
        loadEffectBtn.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jToolBar7.add(loadEffectBtn);

        headerPanel.add(jToolBar7, java.awt.BorderLayout.EAST);

        effectPanel.add(headerPanel, java.awt.BorderLayout.NORTH);

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        emittersList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        emittersList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(emittersList);

        effectPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jLabel1.setText("Duration");

        jLabel3.setText("Max particles");

        jLabel2.setText("Delay");

        emMaxParticlesSlider.setFloat(false);
        emMaxParticlesSlider.setMax(200.0F);

        emDurationSlider.setMax(10.0F);

        emDelaySlider.setMax(10.0F);

        jLabel36.setText("Gravity X");

        emGravityXSlider.setMax(10.0F);
        emGravityXSlider.setMin(-10.0F);

        emGravityYSlider.setMax(10.0F);
        emGravityYSlider.setMin(-10.0F);

        jLabel37.setText("Gravity Y");

        emContinuousChk.setText("Continuous");

        emAdditiveBlendingChk.setText("Additive blending");

        javax.swing.GroupLayout emitterConfigPanelLayout = new javax.swing.GroupLayout(emitterConfigPanel);
        emitterConfigPanel.setLayout(emitterConfigPanelLayout);
        emitterConfigPanelLayout.setHorizontalGroup(
            emitterConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(emitterConfigPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(emitterConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, emitterConfigPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(emMaxParticlesSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE))
                    .addGroup(emitterConfigPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(emDelaySlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(emitterConfigPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(emDurationSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(emitterConfigPanelLayout.createSequentialGroup()
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(emGravityXSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(emitterConfigPanelLayout.createSequentialGroup()
                        .addComponent(jLabel37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(emGravityYSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(emitterConfigPanelLayout.createSequentialGroup()
                        .addComponent(emContinuousChk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(emAdditiveBlendingChk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        emitterConfigPanelLayout.setVerticalGroup(
            emitterConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(emitterConfigPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(emitterConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(emMaxParticlesSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(emitterConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(emDurationSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(emitterConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(emDelaySlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(emitterConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(emGravityXSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(emitterConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(emGravityYSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(emitterConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(emContinuousChk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(emAdditiveBlendingChk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel13.setText("Angle");

        jLabel17.setText("End rotation");

        jLabel11.setText("End size");

        jLabel14.setText("Start rotation");

        jLabel4.setText("Lifespan");

        jLabel8.setText("Start size");

        pLifespanSlider.setMax(5.0F);

        pLifespanVarianceSlider.setMax(5.0F);

        pAngleSlider.setMax(360.0F);

        pStartRotSlider.setMax(360.0F);

        pEndRotSlider.setMax(360.0F);

        jLabel5.setText("Lifespan variance");

        jLabel12.setText("Angle variance");

        pAngleVarianceSlider.setMax(180.0F);

        jLabel9.setText("Start size variance");

        jLabel10.setText("End size variance");

        jLabel15.setText("Start rotation variance");

        pStartRotVarianceSlider.setMax(360.0F);

        jLabel16.setText("End rotation variance");

        pEndRotVarianceSlider.setMax(360.0F);

        jLabel7.setText("X variance");

        jLabel6.setText("Y variance");

        jLabel18.setText("Speed");

        pSpeedSlider.setMax(2.0F);

        pSpeedVarianceSlider.setMax(2.0F);

        jLabel19.setText("Speed variance");

        javax.swing.GroupLayout particleConfigPanelLayout = new javax.swing.GroupLayout(particleConfigPanel);
        particleConfigPanel.setLayout(particleConfigPanelLayout);
        particleConfigPanelLayout.setHorizontalGroup(
            particleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(particleConfigPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(particleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(particleConfigPanelLayout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pEndRotSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(particleConfigPanelLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pAngleSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(particleConfigPanelLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pLifespanSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(particleConfigPanelLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pStartSizeSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(particleConfigPanelLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pEndSizeSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(particleConfigPanelLayout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pStartRotSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(particleConfigPanelLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pLifespanVarianceSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(particleConfigPanelLayout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pAngleVarianceSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(particleConfigPanelLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pStartSizeVarianceSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(particleConfigPanelLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pEndSizeVarianceSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(particleConfigPanelLayout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pStartRotVarianceSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(particleConfigPanelLayout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pEndRotVarianceSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(particleConfigPanelLayout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pSpeedSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(particleConfigPanelLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pXVarianceSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(particleConfigPanelLayout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pSpeedVarianceSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(particleConfigPanelLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pYVarianceSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        particleConfigPanelLayout.setVerticalGroup(
            particleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(particleConfigPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(particleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(pLifespanSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(particleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pLifespanVarianceSlider, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(particleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(pAngleSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(particleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pAngleVarianceSlider, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(particleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pSpeedSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(particleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19)
                    .addComponent(pSpeedVarianceSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(particleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7)
                    .addComponent(pXVarianceSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(particleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(pYVarianceSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(particleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8)
                    .addComponent(pStartSizeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(particleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pStartSizeVarianceSlider, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(particleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel11)
                    .addComponent(pEndSizeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(particleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pEndSizeVarianceSlider, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(particleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel14)
                    .addComponent(pStartRotSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(particleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel15)
                    .addComponent(pStartRotVarianceSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(particleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel17)
                    .addComponent(pEndRotSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(particleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel16)
                    .addComponent(pEndRotVarianceSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel23.setText("B");

        jLabel20.setText("R");

        jLabel22.setText("A");

        jLabel21.setText("G");

        javax.swing.GroupLayout startColorPanelLayout = new javax.swing.GroupLayout(startColorPanel);
        startColorPanel.setLayout(startColorPanelLayout);
        startColorPanelLayout.setHorizontalGroup(
            startColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(startColorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(startColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21)
                    .addComponent(jLabel23)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(startColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pStartColorASlider, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                    .addComponent(pStartColorBSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(pStartColorRSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(pStartColorGSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        startColorPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel20, jLabel21, jLabel22, jLabel23});

        startColorPanelLayout.setVerticalGroup(
            startColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(startColorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(startColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel20)
                    .addComponent(pStartColorRSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(startColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel21)
                    .addComponent(pStartColorGSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(startColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel23)
                    .addComponent(pStartColorBSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(startColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel22)
                    .addComponent(pStartColorASlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel34.setText("G");

        jLabel35.setText("B");

        jLabel32.setText("R");

        jLabel33.setText("A");

        javax.swing.GroupLayout endColorPanelLayout = new javax.swing.GroupLayout(endColorPanel);
        endColorPanel.setLayout(endColorPanelLayout);
        endColorPanelLayout.setHorizontalGroup(
            endColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(endColorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(endColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel34)
                    .addComponent(jLabel32)
                    .addComponent(jLabel35)
                    .addComponent(jLabel33))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(endColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pEndColorASlider, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                    .addComponent(pEndColorBSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(pEndColorGSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(pEndColorRSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        endColorPanelLayout.setVerticalGroup(
            endColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(endColorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(endColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel32)
                    .addComponent(pEndColorRSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(endColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel34)
                    .addComponent(pEndColorGSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(endColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel35)
                    .addComponent(pEndColorBSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(endColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel33)
                    .addComponent(pEndColorASlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel28.setText("B");

        jLabel29.setText("R");

        jLabel30.setText("A");

        jLabel31.setText("G");

        javax.swing.GroupLayout startColorVariancePanelLayout = new javax.swing.GroupLayout(startColorVariancePanel);
        startColorVariancePanel.setLayout(startColorVariancePanelLayout);
        startColorVariancePanelLayout.setHorizontalGroup(
            startColorVariancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(startColorVariancePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(startColorVariancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel29)
                    .addComponent(jLabel31)
                    .addComponent(jLabel28)
                    .addComponent(jLabel30))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(startColorVariancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pStartColorAVarianceSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                    .addComponent(pStartColorBVarianceSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(pStartColorRVarianceSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(pStartColorGVarianceSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        startColorVariancePanelLayout.setVerticalGroup(
            startColorVariancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(startColorVariancePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(startColorVariancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel29)
                    .addComponent(pStartColorRVarianceSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(startColorVariancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel31)
                    .addComponent(pStartColorGVarianceSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(startColorVariancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel28)
                    .addComponent(pStartColorBVarianceSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(startColorVariancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel30)
                    .addComponent(pStartColorAVarianceSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel38.setText("G");

        jLabel39.setText("B");

        jLabel40.setText("R");

        jLabel41.setText("A");

        javax.swing.GroupLayout endColorVariancePanelLayout = new javax.swing.GroupLayout(endColorVariancePanel);
        endColorVariancePanel.setLayout(endColorVariancePanelLayout);
        endColorVariancePanelLayout.setHorizontalGroup(
            endColorVariancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(endColorVariancePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(endColorVariancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel38)
                    .addComponent(jLabel40)
                    .addComponent(jLabel39)
                    .addComponent(jLabel41))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(endColorVariancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pEndColorAVarianceSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                    .addComponent(pEndColorBVarianceSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(pEndColorGVarianceSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(pEndColorRVarianceSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        endColorVariancePanelLayout.setVerticalGroup(
            endColorVariancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(endColorVariancePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(endColorVariancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel40)
                    .addComponent(pEndColorRVarianceSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(endColorVariancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel38)
                    .addComponent(pEndColorGVarianceSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(endColorVariancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel39)
                    .addComponent(pEndColorBVarianceSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(endColorVariancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel41)
                    .addComponent(pEndColorAVarianceSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/gfx/logo.png"))); // NOI18N

        comment.setText("<html> Right click on a slider to precisely adjust its value.<br/> On the canvas: hold left button to move the effect, hold right button to move the emitter. </html>");
        comment.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(renderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(renderOptionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(startColorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(startColorVariancePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(endColorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(endColorVariancePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(particleConfigPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                    .addComponent(effectPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(emitterConfigPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(comment, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {endColorPanel, endColorVariancePanel, startColorPanel, startColorVariancePanel});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(effectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(emitterConfigPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(particleConfigPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(11, 11, 11)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(comment, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel24))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(startColorVariancePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(startColorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(endColorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(endColorVariancePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(renderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(renderOptionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bgClearBtn;
    private javax.swing.JButton bgSetBtn;
    private javax.swing.JLabel comment;
    private aurelienribon.fxeditor.CompactSlider cvBgBSlider;
    private aurelienribon.fxeditor.CompactSlider cvBgGSlider;
    private aurelienribon.fxeditor.CompactSlider cvBgRSlider;
    private aurelienribon.fxeditor.CompactCheckBox cvDrawBgImageChk;
    private aurelienribon.fxeditor.CompactCheckBox cvDrawMeasureInfoChk;
    private aurelienribon.fxeditor.CompactCheckBox cvLinearFilterChk;
    private aurelienribon.fxeditor.CompactCheckBox cvRenderToTextureChk;
    private javax.swing.JButton deleteEmitterBtn;
    private javax.swing.JButton editEmitterBtn;
    private javax.swing.JPanel effectPanel;
    private aurelienribon.fxeditor.CompactCheckBox emAdditiveBlendingChk;
    private aurelienribon.fxeditor.CompactCheckBox emContinuousChk;
    private aurelienribon.fxeditor.CompactSlider emDelaySlider;
    private aurelienribon.fxeditor.CompactSlider emDurationSlider;
    private aurelienribon.fxeditor.CompactSlider emGravityXSlider;
    private aurelienribon.fxeditor.CompactSlider emGravityYSlider;
    private aurelienribon.fxeditor.CompactSlider emMaxParticlesSlider;
    private javax.swing.JPanel emitterConfigPanel;
    private javax.swing.JList emittersList;
    private javax.swing.JPanel endColorPanel;
    private javax.swing.JPanel endColorVariancePanel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JToolBar jToolBar7;
    private javax.swing.JButton loadEffectBtn;
    private javax.swing.JButton moveDownEmitterBtn;
    private javax.swing.JButton moveUpEmitterBtn;
    private javax.swing.JButton newEmitterBtn;
    private aurelienribon.fxeditor.CompactSlider pAngleSlider;
    private aurelienribon.fxeditor.CompactSlider pAngleVarianceSlider;
    private aurelienribon.fxeditor.CompactSlider pEndColorASlider;
    private aurelienribon.fxeditor.CompactSlider pEndColorAVarianceSlider;
    private aurelienribon.fxeditor.CompactSlider pEndColorBSlider;
    private aurelienribon.fxeditor.CompactSlider pEndColorBVarianceSlider;
    private aurelienribon.fxeditor.CompactSlider pEndColorGSlider;
    private aurelienribon.fxeditor.CompactSlider pEndColorGVarianceSlider;
    private aurelienribon.fxeditor.CompactSlider pEndColorRSlider;
    private aurelienribon.fxeditor.CompactSlider pEndColorRVarianceSlider;
    private aurelienribon.fxeditor.CompactSlider pEndRotSlider;
    private aurelienribon.fxeditor.CompactSlider pEndRotVarianceSlider;
    private aurelienribon.fxeditor.CompactSlider pEndSizeSlider;
    private aurelienribon.fxeditor.CompactSlider pEndSizeVarianceSlider;
    private aurelienribon.fxeditor.CompactSlider pLifespanSlider;
    private aurelienribon.fxeditor.CompactSlider pLifespanVarianceSlider;
    private aurelienribon.fxeditor.CompactSlider pSpeedSlider;
    private aurelienribon.fxeditor.CompactSlider pSpeedVarianceSlider;
    private aurelienribon.fxeditor.CompactSlider pStartColorASlider;
    private aurelienribon.fxeditor.CompactSlider pStartColorAVarianceSlider;
    private aurelienribon.fxeditor.CompactSlider pStartColorBSlider;
    private aurelienribon.fxeditor.CompactSlider pStartColorBVarianceSlider;
    private aurelienribon.fxeditor.CompactSlider pStartColorGSlider;
    private aurelienribon.fxeditor.CompactSlider pStartColorGVarianceSlider;
    private aurelienribon.fxeditor.CompactSlider pStartColorRSlider;
    private aurelienribon.fxeditor.CompactSlider pStartColorRVarianceSlider;
    private aurelienribon.fxeditor.CompactSlider pStartRotSlider;
    private aurelienribon.fxeditor.CompactSlider pStartRotVarianceSlider;
    private aurelienribon.fxeditor.CompactSlider pStartSizeSlider;
    private aurelienribon.fxeditor.CompactSlider pStartSizeVarianceSlider;
    private aurelienribon.fxeditor.CompactSlider pXVarianceSlider;
    private aurelienribon.fxeditor.CompactSlider pYVarianceSlider;
    private javax.swing.JPanel particleConfigPanel;
    private javax.swing.JPanel renderOptionPanel;
    private javax.swing.JPanel renderPanel;
    private javax.swing.JButton saveEffectBtn;
    private javax.swing.JPanel startColorPanel;
    private javax.swing.JPanel startColorVariancePanel;
    // End of variables declaration//GEN-END:variables

}
