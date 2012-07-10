package aurelienribon.fx;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class ParticleEffect {
	// -------------------------------------------------------------------------
	// Pool
	// -------------------------------------------------------------------------

	private static final List<ParticleEffect> pool = new ArrayList<ParticleEffect>();

	public static ParticleEffect create() {
		ParticleEffect pe = pool.isEmpty() ? new ParticleEffect() : pool.remove(pool.size()-1);
		pe.reset();
		return pe;
	}

	public static void free(ParticleEffect pe) {
		pool.add(pe);
		pe.dispose();
	}

	// -------------------------------------------------------------------------
	// ParticleEffect
	// -------------------------------------------------------------------------

	// Config
	private final List<ParticleEmitter> emitters = new ArrayList<ParticleEmitter>();
	private float delay = 0;
	private float x, y;

	// System
	private float currentTime;
	private boolean paused;

	private ParticleEffect() {
	}

	private void reset() {
		delay = 0;
		x = y = 0;
		currentTime = 0;
		paused = true;
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public void dispose() {
		for (int i=emitters.size()-1; i>=0; i--) ParticleEmitter.free(emitters.remove(i));
	}

	public void copy(ParticleEffect pe) {
		dispose();
		for (int i=0, n=pe.getEmitters().size(); i<n; i++) {
			ParticleEmitter em = ParticleEmitter.create();
			em.copy(pe.getEmitters().get(i));
			emitters.add(em);
		}
	}

	public void draw(SpriteBatch batch, float delta) {
		if (paused) return;

		currentTime += delta;
		if (currentTime < delay) return;

		for (int i=0, n=emitters.size(); i<n; i++) {
			emitters.get(i).draw(batch, delta);
		}
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;

		for (int i=0, n=emitters.size(); i<n; i++) {
			emitters.get(i).setPosition(x, y);
		}
	}

	public void scale(float scale) {
		for (int i=0, n=emitters.size(); i<n; i++) {
			emitters.get(i).scale(scale);
		}
	}

	public void start() {
		paused = false;
	}

	public void kill() {
		for (int i=0, n=emitters.size(); i<n; i++) {
			emitters.get(i).kill();
		}
	}

	public void pause() {
		paused = true;
	}

	public void resume() {
		paused = false;
	}

	public void restart() {
		currentTime = 0;
		paused = false;
		for (int i=0, n=emitters.size(); i<n; i++) {
			emitters.get(i).restart();
		}
	}

	public void delay(float delay) {
		this.delay += delay;
	}

	public List<ParticleEmitter> getEmitters() {
		return emitters;
	}

	public float getDuration() {
		float duration = 0;
		for (int i=0, n=emitters.size(); i<n; i++) {
			ParticleEmitter em = emitters.get(i);
			if (em.continuous) return -1;
			duration = Math.max(duration, em.getDuration());
		}
		return duration;
	}

	public float getProgress() {
		float progress = emitters.isEmpty() ? 0 : 1000;
		for (int i=0, n=emitters.size(); i<n; i++) {
			ParticleEmitter em = emitters.get(i);
			if (em.continuous) return -1;
			progress = Math.min(progress, em.getProgress());
		}
		return progress;
	}

	public float getCurrentTime() {
		return currentTime;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public boolean isFinished() {
		return isKilled() || currentTime > getDuration();
	}

	public boolean isKilled() {
		for (int i=0, n=emitters.size(); i<n; i++) {
			if (!emitters.get(i).isKilled()) return false;
		}
		return true;
	}
}
