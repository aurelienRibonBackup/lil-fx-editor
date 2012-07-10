package aurelienribon.fx;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class Particle {
	// -------------------------------------------------------------------------
	// Pool
	// -------------------------------------------------------------------------

	private static final List<Particle> pool = new ArrayList<Particle>();

	public static Particle create(ParticleAttrs attrs, TextureRegion region) {
		Particle p = pool.isEmpty() ? new Particle() : pool.remove(pool.size()-1);
		p.initialize(attrs, region);
		return p;
	}

	public static void free(Particle p) {
		pool.add(p);
		p.currentTime = 0;
		p.vx = p.vy = 0;
		p.enabled = false;
	}

	// -------------------------------------------------------------------------
	// Particle
	// -------------------------------------------------------------------------

	// Config
	private final ParticleAttrs attrs = new ParticleAttrs();

	// System
	private final Sprite sprite = new Sprite();
	private float currentTime;
	private float vx, vy;
	private boolean enabled;

	private Particle() {
	}

	private void initialize(ParticleAttrs attrs, TextureRegion region) {
		this.attrs.copy(attrs);
		this.attrs.clamp();
		sprite.setRegion(region);
		sprite.setSize(attrs.startSize, attrs.startSize*region.getRegionHeight()/region.getRegionWidth());
		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
		currentTime = 0;
		vx = attrs.speed*MathUtils.cosDeg(attrs.angle);
		vy = attrs.speed*MathUtils.sinDeg(attrs.angle);
		enabled = true;
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public void updateByGravity(float delta, float ax, float ay) {
		if (!enabled) return;

		currentTime += delta;
		vx += ax * delta;
		vy += ay * delta;
		attrs.x += vx * delta;
		attrs.y += vy * delta;
	}

	public void draw(SpriteBatch batch) {
		if (!enabled) return;

		if (currentTime > attrs.lifespan) {
			enabled = false;
		} else {
			float t = currentTime/attrs.lifespan;
			float size = t * (attrs.endSize-attrs.startSize) + attrs.startSize;
			float rot = t * (attrs.endRot-attrs.startRot) + attrs.startRot;
			float r = t * (attrs.endColor.r-attrs.startColor.r) + attrs.startColor.r;
			float g = t * (attrs.endColor.g-attrs.startColor.g) + attrs.startColor.g;
			float b = t * (attrs.endColor.b-attrs.startColor.b) + attrs.startColor.b;
			float a = t * (attrs.endColor.a-attrs.startColor.a) + attrs.startColor.a;
			sprite.setPosition(attrs.x-sprite.getWidth()/2, attrs.y-sprite.getHeight()/2);
			sprite.setRotation(rot+attrs.angle);
			sprite.setScale(size/attrs.startSize);
			sprite.setColor(r, g, b, a);
			sprite.draw(batch);
		}
	}

	public boolean isEnabled() {
		return enabled;
	}
}
