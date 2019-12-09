
import javax.imageio.ImageIO;
import javax.swing.*;

import javaVectors.Vec2;
import javaVectors.Vec3;
import javaVectors.Vec4;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class Main extends JPanel {
	private static final long serialVersionUID = 1L;
	private static Cube cube1 = new Cube();
	private static Cube cube2 = new Cube();

	Vec3 L = new Vec3(0, 1000, 0);
	Vec3 EYE = new Vec3(1000, 0, 0);

	boolean useTexture = false;
	static boolean twoCubes = false;
	boolean shading = true;
	boolean bilinearInterpolation = false;

	static BufferedImage img;
	static String imagePath = "C:\\Users\\JonasFriedli\\Desktop\\papier-peint-two-birds-mc-escher.jpg";
	boolean clamping = true;
	boolean repeating = false;

	public static void main(String[] args) {
		JFrame frame = new JFrame("");
		frame.add(new Main());

		try {
			img = ImageIO.read(new File(imagePath));
		} catch (IOException e) {
			e.printStackTrace();
		}

		frame.setSize(900, 900);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		int timerDelay = 50;
		new Timer(timerDelay, e -> {
			cube1.rotate(1, new Vec3(1f, -.5f, 1f).normalize());
			frame.repaint();
		}).start();

		if (twoCubes) {
			new Timer(timerDelay * 4, e -> {
				cube2.rotate(1, new Vec3(1f, -.5f, 1f).normalize());
				frame.repaint();
			}).start();
		}

	}

	public void paintComponent(Graphics g) {
		MyPolygon[] polygons;
		if (twoCubes) {
			MyPolygon[] polygons1 = cube1.createPolygons(900, 900);
			MyPolygon[] polygons2 = cube2.createPolygons(900, 900);

			polygons = new MyPolygon[polygons1.length + polygons2.length];
			System.arraycopy(polygons1, 0, polygons, 0, polygons1.length);
			System.arraycopy(polygons2, 0, polygons, polygons1.length, polygons2.length);
		} else {
			polygons = cube1.createPolygons(900, 900);
		}

		final BufferedImage image;
		int[] iArray = { 0, 0, 0, 255 };

		image = (BufferedImage) createImage(900, 900);
		WritableRaster raster = image.getRaster();

		float[][] zBuffer = new float[900][900];

		for (int i = 0; i < 900; i++) {
			for (int j = 0; j < 900; j++) {
				zBuffer[i][j] = Float.POSITIVE_INFINITY;
			}
		}

		for (MyPolygon polygon : polygons) {
			if (new Vec3(polygon.xpoints[0] - polygon.xpoints[1], polygon.ypoints[0] - polygon.ypoints[1], 0)
					.cross(new Vec3(polygon.xpoints[0] - polygon.xpoints[2], polygon.ypoints[0] - polygon.ypoints[2],
							0)).z > 0) {

				// Projected
				float[] xPoints = polygon.xpointsF;
				float[] yPoints = polygon.ypointsF;

				Vec2 projectedA = new Vec2(xPoints[0], yPoints[0]);
				Vec2 projectedB = new Vec2(xPoints[1], yPoints[1]);
				Vec2 projectedC = new Vec2(xPoints[2], yPoints[2]);

				Vec4 p1 = polygon.projectedCoordinates[0];
				Vec4 p2 = polygon.projectedCoordinates[1];
				Vec4 p3 = polygon.projectedCoordinates[2];

				// Object
				Vec4 objectA = polygon.objectCoordinates[0];
				Vec4 objectB = polygon.objectCoordinates[1];
				Vec4 objectC = polygon.objectCoordinates[2];

				// Texture
				Vec2 pointTextureA = polygon.textureCoordinates[0];
				Vec2 pointTextureB = polygon.textureCoordinates[1];
				Vec2 pointTextureC = polygon.textureCoordinates[2];

				Vec2 AB = projectedB.subtract(projectedA);
				Vec2 AC = projectedC.subtract(projectedA);
				float det = 1 / (AB.x * AC.y - AC.x * AB.y);

				for (int y = 0; y < 900; ++y) {
					for (int x = 0; x < 900; ++x) {

						Vec2 P = new Vec2(x, y);
						Vec2 AP = P.subtract(projectedA);

						float u = det * (AC.y * AP.x - AC.x * AP.y);
						float v = det * (-AB.y * AP.x + AB.x * AP.y);

						Vec4 interpolatedProjectedCoordinates = p1.add(p2.subtract(p1).scale(u))
								.add(p3.subtract(p1).scale(v));
						interpolatedProjectedCoordinates = interpolatedProjectedCoordinates
								.scale(1f / interpolatedProjectedCoordinates.w);

						Vec4 interpolatedObjectCoordinates = objectA.add(objectB.subtract(objectA).scale(u))
								.add(objectC.subtract(objectA).scale(v));
						interpolatedObjectCoordinates = interpolatedObjectCoordinates
								.scale(1f / interpolatedObjectCoordinates.w);

						if (u >= 0 && v >= 0 && (u + v) < 1 && zBuffer[y][x] > interpolatedObjectCoordinates.z) {
							Vec2 interpolatedTextureCoordinates = pointTextureA
									.add(pointTextureB.subtract(pointTextureA).scale(u))
									.add(pointTextureC.subtract(pointTextureA).scale(v));

							zBuffer[y][x] = interpolatedObjectCoordinates.z;

							Vec3 newColor;

							if (useTexture) {
								newColor = gammaCorrection(getTextel(interpolatedTextureCoordinates.x,
										interpolatedTextureCoordinates.y, x, y));
							} else {
								Vec4[] colors = polygon.colors;
								Vec4 color = colors[0].add(colors[1].subtract(colors[0]).scale(u))
										.add(colors[2].subtract(colors[0]).scale(v));
								color = color.scale(1f / color.w);
								
								newColor = new Vec3(color);
							}

							if (shading) {
								// Lombert Diffuse Shading
								Vec3 n = polygon.normal;
								Vec3 worldP = new Vec3(interpolatedProjectedCoordinates.x,
										interpolatedProjectedCoordinates.y, interpolatedProjectedCoordinates.z);
								Vec3 l = L.subtract(worldP).normalize();
								if (n.dot(l.scale(-1f)) > 0) {
									newColor = newColor.add(new Vec3(1f, 1f, 1f).scale(n.dot(l.scale(-1f))));
								}

								// Phong Specular Shading
								float k = 25;
								Vec3 S = l.subtract(n.scale(l.dot(n)));
								Vec3 R = l.subtract(S.scale(2f)).normalize();
								Vec3 PE = EYE.subtract(worldP).normalize();
								if (R.dot(PE) > 0) {
									newColor = newColor.add(new Vec3(1f, 1f, 1f).scale((float) Math.pow(R.dot(PE), k)));
								}
							}

							newColor = gammaCorrectionUndo(newColor);

							iArray[0] = (int) (newColor.x);
							iArray[1] = (int) (newColor.y);
							iArray[2] = (int) (newColor.z);
							raster.setPixel(x, y, iArray);
						}
					}
				}

				g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
			}
		}
	}

	private static Vec3 gammaCorrectionUndo(Vec3 vec) {
		float gamma = (float) 2.2;
		float newX = (float) (((Math.pow(vec.x, 1.0 / gamma) * 255) > 255) ? 255
				: (Math.pow(vec.x, 1.0 / gamma) * 255));
		float newY = (float) (((Math.pow(vec.y, 1.0 / gamma) * 255) > 255) ? 255
				: (Math.pow(vec.y, 1.0 / gamma) * 255));
		float newZ = (float) (((Math.pow(vec.z, 1.0 / gamma) * 255) > 255) ? 255
				: (Math.pow(vec.z, 1.0 / gamma) * 255));
		return new Vec3(newX, newY, newZ);
	}

	private static Vec3 gammaCorrection(Vec3 vec) {
		float gamma = (float) 2.2;
		float newX = (float) Math.pow(vec.x / 255, gamma);
		float newY = (float) Math.pow(vec.y / 255, gamma);
		float newZ = (float) Math.pow(vec.z / 255, gamma);
		return new Vec3(newX, newY, newZ);
	}

	public Vec3 getTextel(float s, float t, int x, int y) {
		s = s * img.getWidth();
		t = t * img.getHeight();
		int iS = (int) s;
		int iT = (int) t;
		if (clamping) {
			if (iS < 0) iS = 0;
			if (iT < 0) iT = 0;
			if (iS > img.getWidth() - 1) iS = img.getWidth() - 1;
			if (iT > img.getHeight() - 1) iT = img.getHeight() - 1;
		}

		if (repeating) { // mask out bottom 8 bits
			iS &= 255;
			iT &= 255;
		}

		Color color;

		if (bilinearInterpolation && iS < img.getWidth() - 1 && iT < img.getHeight() - 1) {
			Color color00 = new Color(img.getRGB(iS, iT));
			Color color10 = new Color(img.getRGB(iS + 1, iT));
			Color color01 = new Color(img.getRGB(iS, iT + 1));
			Color color11 = new Color(img.getRGB(iS + 1, iT + 1));

			Vec3 color00Vector = gammaCorrection(new Vec3(color00.getRed(), color00.getGreen(), color00.getBlue()));
			Vec3 color10Vector = gammaCorrection(new Vec3(color10.getRed(), color10.getGreen(), color10.getBlue()));
			Vec3 color01Vector = gammaCorrection(new Vec3(color01.getRed(), color01.getGreen(), color01.getBlue()));
			Vec3 color11Vector = gammaCorrection(new Vec3(color11.getRed(), color11.getGreen(), color11.getBlue()));

			float diffT = (t - (float) iT);
			float diffS = (s - (float) iS);

			Vec3 colorVector = gammaCorrectionUndo(
					lerp(color00Vector, color10Vector, color01Vector, color11Vector, diffT, diffS));

			color = new Color((int) colorVector.x, (int) colorVector.y, (int) colorVector.z, 0);
		} else {
			color = new Color(img.getRGB(iS, iT));
		}

		return new Vec3(color.getRed(), color.getGreen(), color.getBlue());
	}

	private Vec3 lerp(Vec3 color00, Vec3 color01, Vec3 color10, Vec3 color11, float diffT, float diffS) {
		return lerp(lerp(color00, color10, diffT), lerp(color01, color11, diffT), diffS);

	}

	private Vec3 lerp(Vec3 v1, Vec3 v2, float diff) {
		return v2.scale(1f - diff).add(v2.scale(diff));
	}
}