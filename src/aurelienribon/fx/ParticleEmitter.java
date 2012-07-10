package aurelienribon.fx;

import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class ParticleEmitter {
	// -------------------------------------------------------------------------
	// Pool
	// -------------------------------------------------------------------------

	private static final List<ParticleEmitter> pool = new ArrayList<ParticleEmitter>();

	public static ParticleEmitter create() {
		ParticleEmitter pe = pool.isEmpty() ? new ParticleEmitter() : pool.remove(pool.size()-1);
		pe.reset();
		return pe;
	}

	public static void free(ParticleEmitter pe) {
		pool.add(pe);
		pe.dispose();
	}

	// -------------------------------------------------------------------------
	// ParticleEmitter
	// -------------------------------------------------------------------------

	// Config
	public String name;
	public String imageName;
	public final ParticleAttrs pAttrs = new ParticleAttrs();
	public final ParticleAttrs pAttrsVar = new ParticleAttrs();
	public int maxParticles;
	public boolean continuous;
	public float duration;
	public float delay;
	public float gravityX, gravityY;
	public boolean additiveBlending;

	// System
	private final List<Particle> particles = new ArrayList<Particle>();
	private final ParticleAttrs pAttrsFinal = new ParticleAttrs();
	private float currentTime;
	private float nextParticleTime;
	private TextureRegion region;
	private float x, y;
	private boolean killed;

	private ParticleEmitter() {
	}

	private void reset() {
		name = null;
		currentTime = nextParticleTime = 0;
		region = null;
		x = y = 0;
		killed = false;
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public void initialize(TextureAtlas atlas) {
		if (imageName == null || atlas == null) return;
		region = atlas.findRegion(imageName);
	}

	public void initialize(TextureRegion region) {
		this.region = region;
	}

	public void dispose() {
		for (int i=particles.size()-1; i>=0; i--) Particle.free(particles.remove(i));
	}

	public void copy(ParticleEmitter pe) {
		name = pe.name;
		imageName = pe.imageName;
		pAttrs.copy(pe.pAttrs);
		pAttrsVar.copy(pe.pAttrsVar);
		maxParticles = pe.maxParticles;
		continuous = pe.continuous;
		duration = pe.duration;
		delay = pe.delay;
		gravityX = pe.gravityX;
		gravityY = pe.gravityY;
		additiveBlending = pe.additiveBlending;

		region = pe.region;
		x = pe.x;
		y = pe.y;
	}

	public void draw(SpriteBatch batch, float delta) {
		if (pAttrs == null) return;

		currentTime += delta;

		if (!killed
			&& (continuous || (currentTime > delay && currentTime < delay+duration))
			&& maxParticles > 0 && pAttrs.lifespan > 0) {

			float step = pAttrs.lifespan / maxParticles;

			while (nextParticleTime < currentTime) {
				nextParticleTime += step;
				pAttrsFinal.copy(pAttrs);
				pAttrsFinal.x += x;
				pAttrsFinal.y += y;

				if (pAttrsVar != null) {
					pAttrsFinal.lifespan += MathUtils.random(-pAttrsVar.lifespan, pAttrsVar.lifespan);
					pAttrsFinal.startSize += MathUtils.random(-pAttrsVar.startSize, pAttrsVar.startSize);
					pAttrsFinal.endSize += MathUtils.random(-pAttrsVar.endSize, pAttrsVar.endSize);
					pAttrsFinal.startRot += MathUtils.random(-pAttrsVar.startRot, pAttrsVar.startRot);
					pAttrsFinal.endRot += MathUtils.random(-pAttrsVar.endRot, pAttrsVar.endRot);
					pAttrsFinal.startColor.r += MathUtils.random(-pAttrsVar.startColor.r, pAttrsVar.startColor.r);
					pAttrsFinal.startColor.g += MathUtils.random(-pAttrsVar.startColor.g, pAttrsVar.startColor.g);
					pAttrsFinal.startColor.b += MathUtils.random(-pAttrsVar.startColor.b, pAttrsVar.startColor.b);
					pAttrsFinal.startColor.a += MathUtils.random(-pAttrsVar.startColor.a, pAttrsVar.startColor.a);
					pAttrsFinal.endColor.r += MathUtils.random(-pAttrsVar.endColor.r, pAttrsVar.endColor.r);
					pAttrsFinal.endColor.g += MathUtils.random(-pAttrsVar.endColor.g, pAttrsVar.endColor.g);
					pAttrsFinal.endColor.b += MathUtils.random(-pAttrsVar.endColor.b, pAttrsVar.endColor.b);
					pAttrsFinal.endColor.a += MathUtils.random(-pAttrsVar.endColor.a, pAttrsVar.endColor.a);
					pAttrsFinal.x += MathUtils.random(-pAttrsVar.x, pAttrsVar.x);
					pAttrsFinal.y += MathUtils.random(-pAttrsVar.y, pAttrsVar.y);
					pAttrsFinal.speed += MathUtils.random(-pAttrsVar.speed, pAttrsVar.speed);
					pAttrsFinal.angle += MathUtils.random(-pAttrsVar.angle, pAttrsVar.angle);
				}

				pAttrsFinal.clamp();
				particles.add(Particle.create(pAttrsFinal, region));
			}
		}

		for (int i=particles.size()-1; i>=0; i--) {
			if (!particles.get(i).isEnabled()) Particle.free(particles.remove(i));
		}

		if (additiveBlending) batch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE);

		for (int i=0, n=particles.size(); i<n; i++) {
			Particle p = particles.get(i);
			p.updateByGravity(delta, gravityX, gravityY);
			p.draw(batch);
		}

		if (additiveBlending) batch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public TextureRegion getRegion() {
		return region;
	}

	public float getProgress() {
		if (continuous || (getDuration() == 0)) return -1;
		return currentTime / getDuration();
	}

	public float getDuration() {
		return delay+duration+pAttrs.lifespan;
	}

	public float getCurrentTime() {
		return currentTime;
	}

	public void restart() {
		currentTime = 0;
		nextParticleTime = 0;
	}

	public void scale(float scale) {
		pAttrs.scale(scale);
		pAttrsVar.scale(scale);
	}

	public void kill() {
		killed = true;
	}

	public boolean isFinished() {
		return isKilled() || currentTime > getDuration();
	}

	public boolean isKilled() {
		return killed;
	}
}
