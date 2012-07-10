package aurelienribon.fx;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Utility class used to serialize/deserialize paticle effects to/from files.
 *
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class ParticleEffectIo {
	private static final StringBuilder sb = new StringBuilder();
	private static final Color color = new Color();

	/**
	 * Deserializes an effect from a file. The images of the particles are
	 * searched in the given {@link TextureAtlas}.
	 */
	public static ParticleEffect loadFromFile(FileHandle file, TextureAtlas atlas) {
		return loadFromString(file.readString(), atlas);
	}

	/**
	 * Deserializes an effect from a string. The images of the particles are
	 * searched in the given {@link TextureAtlas}.
	 */
	public static ParticleEffect loadFromString(String input, TextureAtlas atlas) {
		ParticleEffect effect = ParticleEffect.create();
		String[] descs = input.split("---");

		for (int i=0; i<descs.length; i++) {
			String desc = descs[i];
			if (desc.trim().equals("")) continue;

			ParticleEmitter emitter = ParticleEmitter.create();
			effect.getEmitters().add(emitter);

			emitter.name = findString(desc, "name", "unamed");
			emitter.imageName = findString(desc, "imageName", null);
			emitter.maxParticles = findInt(desc, "maxParticles", 100);
			emitter.continuous = findBoolean(desc, "continuous", true);
			emitter.duration = findFloat(desc, "duration", 0);
			emitter.delay = findFloat(desc, "delay", 0);
			emitter.gravityX = findFloat(desc, "gravityX", 0);
			emitter.gravityY = findFloat(desc, "gravityY", 0);
			emitter.additiveBlending = findBoolean(desc, "additiveBlending", true);

			emitter.initialize(atlas);

			emitter.pAttrs.lifespan = findFloat(desc, "p-lifespan", 1);
			emitter.pAttrs.startSize = findFloat(desc, "p-startSize", 1);
			emitter.pAttrs.endSize = findFloat(desc, "p-endSize", 1);
			emitter.pAttrs.startRot = findFloat(desc, "p-startRot", 1);
			emitter.pAttrs.endRot = findFloat(desc, "p-endRot", 1);
			emitter.pAttrs.startColor.set(intToColor(findInt(desc, "p-startColor", 0xFFFFFFFF)));
			emitter.pAttrs.endColor.set(intToColor(findInt(desc, "p-endColor", 0xFFFFFFFF)));
			emitter.pAttrs.x = findFloat(desc, "p-x", 0);
			emitter.pAttrs.y = findFloat(desc, "p-y", 0);
			emitter.pAttrs.speed = findFloat(desc, "p-speed", 1);
			emitter.pAttrs.angle = findFloat(desc, "p-angle", 1);

			emitter.pAttrsVar.lifespan = findFloat(desc, "p-lifespan-var", 0);
			emitter.pAttrsVar.startSize = findFloat(desc, "p-startSize-var", 0);
			emitter.pAttrsVar.endSize = findFloat(desc, "p-endSize-var", 0);
			emitter.pAttrsVar.startRot = findFloat(desc, "p-startRot-var", 0);
			emitter.pAttrsVar.endRot = findFloat(desc, "p-endRot-var", 0);
			emitter.pAttrsVar.startColor.set(intToColor(findInt(desc, "p-startColor-var", 0)));
			emitter.pAttrsVar.endColor.set(intToColor(findInt(desc, "p-endColor-var", 0)));
			emitter.pAttrsVar.x = findFloat(desc, "p-x-var", 0);
			emitter.pAttrsVar.y = findFloat(desc, "p-y-var", 0);
			emitter.pAttrsVar.speed = findFloat(desc, "p-speed-var", 0);
			emitter.pAttrsVar.angle = findFloat(desc, "p-angle-var", 0);
		}

		return effect;
	}

	/**
	 * Serializes an effect to a file. This method should mainly be used by
	 * editors, either offline or in-game.
	 */
	public static void saveToFile(ParticleEffect effect, FileHandle file) {
		file.writeString(saveToString(effect), false);
	}

	/**
	 * Serializes an effect to a string. This method should mainly be used by
	 * editors, either offline or in-game.
	 */
	public static String saveToString(ParticleEffect effect) {
		sb.delete(0, sb.length());

		for (int i=0, n=effect.getEmitters().size(); i<n; i++) {
			ParticleEmitter emitter = effect.getEmitters().get(i);

			if (i > 0) sb.append("\n\n---\n\n");
			sb.append("name=").append(emitter.name).append("\n");
			sb.append("imageName=").append(emitter.imageName).append("\n");
			sb.append("maxParticles=").append(emitter.maxParticles).append("\n");
			sb.append("continuous=").append(emitter.continuous).append("\n");
			sb.append("duration=").append(emitter.duration).append("\n");
			sb.append("delay=").append(emitter.delay).append("\n");
			sb.append("gravityX=").append(emitter.gravityX).append("\n");
			sb.append("gravityY=").append(emitter.gravityY).append("\n");
			sb.append("additiveBlending").append(emitter.additiveBlending).append("\n");
			sb.append("p-lifespan=").append(emitter.pAttrs.lifespan).append("\n");
			sb.append("p-startSize=").append(emitter.pAttrs.startSize).append("\n");
			sb.append("p-endSize=").append(emitter.pAttrs.endSize).append("\n");
			sb.append("p-startRot=").append(emitter.pAttrs.startRot).append("\n");
			sb.append("p-endRot=").append(emitter.pAttrs.endRot).append("\n");
			sb.append("p-startColor=").append(colorToInt(emitter.pAttrs.startColor)).append("\n");
			sb.append("p-endColor=").append(colorToInt(emitter.pAttrs.endColor)).append("\n");
			sb.append("p-x=").append(emitter.pAttrs.x).append("\n");
			sb.append("p-y=").append(emitter.pAttrs.y).append("\n");
			sb.append("p-speed=").append(emitter.pAttrs.speed).append("\n");
			sb.append("p-angle=").append(emitter.pAttrs.angle).append("\n");
			sb.append("p-lifespan-var=").append(emitter.pAttrsVar.lifespan).append("\n");
			sb.append("p-startSize-var=").append(emitter.pAttrsVar.startSize).append("\n");
			sb.append("p-endSize-var=").append(emitter.pAttrsVar.endSize).append("\n");
			sb.append("p-startRot-var=").append(emitter.pAttrsVar.startRot).append("\n");
			sb.append("p-endRot-var=").append(emitter.pAttrsVar.endRot).append("\n");
			sb.append("p-startColor-var=").append(colorToInt(emitter.pAttrsVar.startColor)).append("\n");
			sb.append("p-endColor-var=").append(colorToInt(emitter.pAttrsVar.endColor)).append("\n");
			sb.append("p-x-var=").append(emitter.pAttrsVar.x).append("\n");
			sb.append("p-y-var=").append(emitter.pAttrsVar.y).append("\n");
			sb.append("p-speed-var=").append(emitter.pAttrsVar.speed).append("\n");
			sb.append("p-angle-var=").append(emitter.pAttrsVar.angle);
		}

		return sb.toString();
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private static float findFloat(String input, String name, float defaultValue) {
		int start = input.indexOf(name+"=");
		if (start >= 0) {
			start += name.length()+1;
			int end = input.indexOf('\n', start);
			if (end < 0) end = input.length();
			return Float.parseFloat(input.substring(start, end).trim());
		}
		return defaultValue;
	}

	private static int findInt(String input, String name, int defaultValue) {
		int start = input.indexOf(name+"=");
		if (start >= 0) {
			start += name.length()+1;
			int end = input.indexOf('\n', start);
			if (end < 0) end = input.length();
			return Integer.parseInt(input.substring(start, end).trim());
		}
		return defaultValue;
	}

	private static boolean findBoolean(String input, String name, boolean defaultValue) {
		int start = input.indexOf(name+"=");
		if (start >= 0) {
			start += name.length()+1;
			int end = input.indexOf('\n', start);
			if (end < 0) end = input.length();
			return Boolean.parseBoolean(input.substring(start, end).trim());
		}
		return defaultValue;
	}

	private static String findString(String input, String name, String defaultValue) {
		int start = input.indexOf(name+"=");
		if (start >= 0) {
			start += name.length()+1;
			int end = input.indexOf('\n', start);
			if (end < 0) end = input.length();
			return input.substring(start, end).trim();
		}
		return defaultValue;
	}

	private static Color intToColor(int i) {
		float r = (i >> 24 & 0xFF) / 255f;
		float g = (i >> 16 & 0xFF) / 255f;
		float b = (i >> 8 & 0xFF) / 255f;
		float a = (i & 0xFF) / 255f;
		color.set(r, g, b, a);
		return color;
	}

	private static int colorToInt(Color color) {
		int r = (int) (color.r * 255) << 24;
		int g = (int) (color.g * 255) << 16;
		int b = (int) (color.b * 255) << 8;
		int a = (int) (color.a * 255);
		return r | g | b | a;
	}
}
