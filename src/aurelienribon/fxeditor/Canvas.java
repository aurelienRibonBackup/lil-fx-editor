package aurelienribon.fxeditor;

import aurelienribon.fx.ParticleEffect;
import aurelienribon.fx.ParticleEmitter;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import java.util.Locale;

public class Canvas extends ApplicationAdapter {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private BitmapFont font;
	private FrameBuffer frameBuffer;
	private ShapeRenderer drawer;
	private Sprite infoLabel;
	private Sprite infoSprite;
	private Sprite bgSprite;

	public ParticleEffect effect;
	public ParticleEmitter selectedEmitter;
	public Color bgColor = new Color(0, 0, 0, 1);
	public boolean linearFilter = true;
	public boolean renderToTexture = false;
	public boolean drawHelpLines = false;
	public boolean drawBgSprite = true;

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	@Override
	public void create() {
		Texture.setEnforcePotImages(false);
		Assets.loadAll();

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera(3, 3*h/w);
		batch = new SpriteBatch();
		font = new BitmapFont();
		frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int)w, (int)h, false);
		drawer = new ShapeRenderer();

		infoLabel = new Sprite(Assets.getWhiteTex());
		infoLabel.setPosition(0, 0);
		infoLabel.setSize(130, 75);
		infoLabel.setColor(new Color(0x2A/255f, 0x3B/255f, 0x56/255f, 180/255f));

		infoSprite = new Sprite(Assets.getInfoTex());

		Gdx.input.setInputProcessor(inputProcessor);
	}

	@Override
	public void render() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		if (drawBgSprite && bgSprite != null) {
			batch.getProjectionMatrix().setToOrtho2D(0, 0, w, h);
			batch.begin();
			bgSprite.draw(batch);
			batch.end();
		}

		if (drawHelpLines) {
			drawer.setProjectionMatrix(camera.combined);
			drawer.begin(ShapeRenderer.ShapeType.Rectangle);
			drawer.setColor(Color.WHITE);
			drawer.rect(-0.5f+effect.getX(), -0.5f+effect.getY(), 1, 1);
			drawer.rect(effect.getX()-0.025f, effect.getY()-0.025f, 0.05f, 0.05f);
			drawer.end();

			Vector2 infoSpritePos = worldToScreen(effect.getX()-0.5f, effect.getY()-0.5f).sub(0, infoSprite.getHeight());

			batch.getProjectionMatrix().setToOrtho2D(0, 0, w, h);
			batch.begin();
			infoSprite.setPosition(infoSpritePos.x, infoSpritePos.y);
			infoSprite.draw(batch);
			batch.end();
		}

		if (effect != null) {
			if (effect.getProgress() >= 1) effect.restart();

			for (ParticleEmitter emitter : effect.getEmitters()) {
				TextureRegion region = emitter.getRegion();
				if (region != null && linearFilter) region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
				else if (region != null) region.getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
			}

			if (renderToTexture) {
				frameBuffer.begin();
				Gdx.gl.glClearColor(0, 0, 0, 0);
				Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			}

			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			effect.draw(batch, Gdx.graphics.getDeltaTime());
			batch.end();

			if (renderToTexture) {
				frameBuffer.end();
				batch.getProjectionMatrix().setToOrtho2D(0, h, w, -h);
				batch.begin();
				batch.draw(frameBuffer.getColorBufferTexture(), 0, 0);
				batch.end();
			}
		}

		batch.getProjectionMatrix().setToOrtho2D(0, 0, w, h);
		batch.begin();
		font.setColor(Color.WHITE);
		infoLabel.draw(batch);
		if (effect != null) {
			font.draw(batch, "# particles: " + effect.getParticlesCount(), 10, 65);
		} else {
			font.draw(batch, "# particles: ---", 10, 65);
		}
		font.draw(batch, String.format(Locale.US, "Zoom: %.0f %%", 100f / camera.zoom), 10, 45);
		font.draw(batch, "Fps: " + Gdx.graphics.getFramesPerSecond(), 10, 25);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		Gdx.gl.glViewport(0, 0, width, height);
		camera.viewportWidth = 3;
		camera.viewportHeight = 3f*height/width;
		camera.update();
		frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
	}

	public void setBackground(String imgPath) {
		if (imgPath == null) {
			if (bgSprite != null) bgSprite.getTexture().dispose();
			bgSprite = null;
			return;
		}

		Texture tex = new Texture(Gdx.files.absolute(imgPath));
		tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		float tw = tex.getWidth();
		float th = tex.getHeight();

		bgSprite = new Sprite(tex);
		if (tw/th > w/h) bgSprite.setSize(h*tw/th, h);
		else bgSprite.setSize(w, w*th/tw);
		bgSprite.setPosition(w/2-bgSprite.getWidth()/2, h/2-bgSprite.getHeight()/2);
	}

	// -------------------------------------------------------------------------
	// Internals
	// -------------------------------------------------------------------------

	private Vector2 screenToWorld(int x, int y) {
		Vector3 v3 = new Vector3(x, y, 0);
		camera.unproject(v3);
		return new Vector2(v3.x, v3.y);
	}

	private Vector2 worldToScreen(float x, float y) {
		Vector3 v3 = new Vector3(x, y, 0);
		camera.project(v3);
		return new Vector2(v3.x, v3.y);
	}

	private final InputProcessor inputProcessor = new InputAdapter() {
		@Override
		public boolean touchDragged(int x, int y, int pointer) {
			if (Gdx.input.isButtonPressed(Buttons.RIGHT) && selectedEmitter != null) {
				Vector2 p = screenToWorld(x, y);
				selectedEmitter.pAttrs.x = p.x - effect.getX();
				selectedEmitter.pAttrs.y = p.y - effect.getY();
			} else if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
				Vector2 p = screenToWorld(x, y);
				effect.setPosition(p.x, p.y);
			}

			return false;
		}

		@Override
		public boolean scrolled(int amount) {
			camera.zoom *= amount > 0 ? 1.2f : 1/1.2f;
			camera.update();
			return false;
		}
	};
}
