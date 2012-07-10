package aurelienribon.fx;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Convenience class to manage your effects and start them in a
 * "fire-and-forget" way. Once you register an effect, you can fire it
 * anywhere you want at any time, and multiple times simultaneously if needed.
 * Everything is taken care of
 *
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class ParticleEffectManager {
	private final List<String> names = new ArrayList<String>();
	private final Map<String, ParticleEffect> effects = new HashMap<String, ParticleEffect>();
	private final Map<String, List<ParticleEffect>> runningEffects = new HashMap<String, List<ParticleEffect>>();

	/**
	 * Registers a {@link ParticleEffect} with a name.
	 */
	public void register(String name, ParticleEffect effect) {
		if (!names.contains(name)) names.add(name);
		effects.put(name, effect);
		runningEffects.put(name, new ArrayList<ParticleEffect>());
	}

	/**
	 * Loads an effect from a file, searches its particles images in the given
	 * atlas, and finally registers the effect with the given name. It is a
	 * convenience method for:
	 * <pre>
	 * ParticleEffect effect = ParticleEffectIo.loadFromFile(file, atlas);
	 * register(name, effect);
	 * </pre>
	 */
	public void register(String name, FileHandle file, TextureAtlas atlas) {
		ParticleEffect effect = ParticleEffectIo.loadFromFile(file, atlas);
		register(name, effect);
	}

	/**
	 * Loads an effect from a string, searches its particles images in the given
	 * atlas, and finally registers the effect with the given name. It is a
	 * convenience method for:
	 * <pre>
	 * ParticleEffect effect = ParticleEffectIo.loadFromString(description, atlas);
	 * register(name, effect);
	 * </pre>
	 */
	public void register(String name, String description, TextureAtlas atlas) {
		ParticleEffect effect = ParticleEffectIo.loadFromString(description, atlas);
		register(name, effect);
	}

	/**
	 * Gets the registered effect associated with the given name, or null if
	 * no effect was found..
	 */
	public ParticleEffect get(String name) {
		if (!effects.containsKey(name)) throw new RuntimeException("Effect '" + name + "' is not registered");
		return effects.get(name);
	}

	/**
	 * Starts the effect associated with the given name. It is placced at the
	 * location (x,y), and is scaled according to the given factor. An effect
	 * is normalized to a 1m x 1m square in the editor, so it may need to be
	 * scaled to fit your world units. If you work with pixels, the effect will
	 * fit in a single pixel by default, so you really need to scale it.
	 */
	public ParticleEffect fire(String name, float x, float y, float scale) {
		ParticleEffect effect = get(name);
		effect.setPosition(x, y);
		effect.scale(scale);
		effect.start();
		return effect;
	}

	/**
	 * Updates all the running effects with the given delta time, and draws
	 * them on screen. All finished effects are disposed and removed.
	 */
	public void draw(SpriteBatch batch, float delta) {
		for (int i=0, n=names.size(); i<n; i++) {
			List<ParticleEffect> list = runningEffects.get(names.get(i));

			for (int ii=0, nn=list.size(); ii<nn; ii++) {
				list.get(ii).draw(batch, delta);
			}

			for (int ii=list.size()-1; ii>=0; ii--) {
				ParticleEffect pe = list.get(ii);
				float duration = pe.getDuration();
				if (duration > 0 && pe.getCurrentTime() > duration) {
					ParticleEffect.free(pe);
				}
			}
		}
	}

	/**
	 * Kills every running effect.
	 */
	public void killAll() {
		for (int i=0, n=names.size(); i<n; i++) killAll(names.get(i));
	}

	/**
	 * Kills every running effect associated to the given name.
	 */
	public void killAll(String name) {
		List<ParticleEffect> list = runningEffects.get(name);
		for (int ii=0, nn=list.size(); ii<nn; ii++) {
			list.get(ii).kill();
		}
	}

	/**
	 * Kills the latest effect associated to the given name.
	 */
	public void killLatest(String name) {
		List<ParticleEffect> list = runningEffects.get(name);
		if (!list.isEmpty()) list.get(list.size()-1).kill();
	}

	/**
	 * Kills the oldest effect associated to the given name.
	 */
	public void killOldest(String name) {
		List<ParticleEffect> list = runningEffects.get(name);
		if (!list.isEmpty()) list.get(0).kill();
	}
}
