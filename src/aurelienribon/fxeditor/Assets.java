package aurelienribon.fxeditor;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class Assets extends AssetManager {
	private static AssetManager manager;

	public static void loadAll() {
		manager = new AssetManager();
		manager.load("res/data/white.png", Texture.class);
		manager.load("res/data/transparent-light.png", Texture.class);
		manager.load("res/data/transparent-dark.png", Texture.class);
		manager.load("res/data/particles.pack", TextureAtlas.class);
		manager.load("res/data/info.png", Texture.class);
		manager.finishLoading();
	}

	public static Texture getTransparentLightTex() {return manager.get("res/data/transparent-light.png", Texture.class);}
	public static Texture getTransparentDarkTex() {return manager.get("res/data/transparent-dark.png", Texture.class);}
	public static Texture getWhiteTex() {return manager.get("res/data/white.png", Texture.class);}
	public static TextureAtlas getDefaultParticlesAtlas() {return manager.get("res/data/particles.pack", TextureAtlas.class);}
	public static Texture getInfoTex() {return manager.get("res/data/info.png", Texture.class);}
}
