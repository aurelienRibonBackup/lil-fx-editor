package aurelienribon.fx;

import com.badlogic.gdx.graphics.Color;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class ParticleAttrs {
	public float lifespan;
	public float startSize;
	public float endSize;
	public float startRot;
	public float endRot;
	public final Color startColor = new Color();
	public final Color endColor = new Color();
	public float x, y;
	public float speed;
	public float angle;

	public void copy(ParticleAttrs attrs) {
		lifespan = attrs.lifespan;
		startSize = attrs.startSize;
		endSize = attrs.endSize;
		startRot = attrs.startRot;
		endRot = attrs.endRot;
		startColor.set(attrs.startColor);
		endColor.set(attrs.endColor);
		x = attrs.x;
		y = attrs.y;
		speed = attrs.speed;
		angle = attrs.angle;
	}

	public void clamp() {
		lifespan = Math.max(lifespan, 0);
		startSize = Math.max(startSize, 0.01f);
		endSize = Math.max(endSize, 0.01f);
		startColor.clamp();
		endColor.clamp();
	}

	public void scale(float scale) {
		startSize *= scale;
		endSize *= scale;
		x *= scale;
		y *= scale;
		speed *= scale;
	}
}
