
import javaVectors.Mat4;
import javaVectors.Vec2;
import javaVectors.Vec3;
import javaVectors.Vec4;

public class Cube {
	private Vec4[] points = new Vec4[] { new Vec4(-1, -1, -1), new Vec4(1, -1, -1), new Vec4(1, 1, -1),
			new Vec4(-1, 1, -1),

			new Vec4(-1, -1, 1), new Vec4(1, -1, 1), new Vec4(1, 1, 1), new Vec4(-1, 1, 1), };

	private int[][] triangles = new int[][] { { 0, 1, 2 }, { 0, 2, 3 }, { 7, 6, 5 }, { 7, 5, 4 }, { 0, 3, 7 },
			{ 0, 7, 4 }, { 2, 1, 5 }, { 2, 5, 6 }, { 3, 2, 6 }, { 3, 6, 7 }, { 1, 0, 4 }, { 1, 4, 5 }, };

	public Cube() {
		this.translate(0f, 0f, 5f);
	}

	public void translate(float x, float y, float z) {
		Mat4 translatedMatrix = Mat4.translate(x, y, z);
		for (int i = 0; i < points.length; i++) {
			points[i] = translatedMatrix.transform(points[i]);
		}
	}

	public void rotate(float angle, Vec3 axis) {
		translate(0f, 0f, -5f);
		Mat4 rotate = Mat4.rotate(angle, axis);
		for (int i = 0; i < points.length; i++) {
			points[i] = rotate.transform(points[i]);
		}
		translate(0f, 0f, 5f);
	}

	public MyPolygon[] createPolygons(float width, float height) {

		MyPolygon[] polygons = new MyPolygon[12];

		Mat4 projection = new Mat4(width, 0f, width / 2f, 0f, 0f, width, height / 2f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f,
				0f).transpose();

		int i = 0;
		for (int[] triangle : triangles) {
			// Projected coordinates
			Vec4 p1 = projection.transform(points[triangle[0]]);
			Vec4 p2 = projection.transform(points[triangle[1]]);
			Vec4 p3 = projection.transform(points[triangle[2]]);

			Vec4[] projectedCoordinates = { p1, p2, p3 };

			int[] xPoints = new int[] { (int) (p1.x / p1.w), (int) (p2.x / p2.w), (int) (p3.x / p3.w) };
			int[] yPoints = new int[] { (int) (p1.y / p1.w), (int) (p2.y / p2.w), (int) (p3.y / p3.w) };

			float[] xPointsF = new float[] { (p1.x / p1.w), (p2.x / p2.w), (p3.x / p3.w) };
			float[] yPointsF = new float[] { (p1.y / p1.w), (p2.y / p2.w), (p3.y / p3.w) };

			// Projected normal
			Vec3 p1p2 = new Vec3(p2.subtract(p1));
			Vec3 p1p3 = new Vec3(p3.subtract(p1));

			Vec3 normal = p1p2.cross(p1p3).normalize();

			// Object coordinate
			Vec4[] objectCoordinates = new Vec4[] { new Vec4(points[triangle[0]].x / points[triangle[0]].z,
					points[triangle[0]].y / points[triangle[0]].z, points[triangle[0]].z / points[triangle[0]].z,
					points[triangle[0]].w / points[triangle[0]].z),
					new Vec4(points[triangle[1]].x / points[triangle[1]].z,
							points[triangle[1]].y / points[triangle[1]].z,
							points[triangle[1]].z / points[triangle[1]].z,
							points[triangle[1]].w / points[triangle[1]].z),
					new Vec4(points[triangle[2]].x / points[triangle[2]].z,
							points[triangle[2]].y / points[triangle[2]].z,
							points[triangle[2]].z / points[triangle[2]].z,
							points[triangle[2]].w / points[triangle[2]].z) };

			// Colors
			Vec4 aColor = gammaCorrection(new Vec4(255, 0, 0, 1));
			Vec4 bColor = gammaCorrection(new Vec4(0, 255, 0, 1));
			Vec4 cColor = gammaCorrection(new Vec4(0, 0, 255, 1));
			Vec4[] colors = new Vec4[] { aColor, bColor, cColor };

			// Texture
			Vec2[] textureCoordinates = { new Vec2(0, 0), new Vec2(1, 0), new Vec2(0, 1) };

			polygons[i] = new MyPolygon(xPoints, yPoints, xPointsF, yPointsF, 3, objectCoordinates, colors,
					textureCoordinates, normal, projectedCoordinates);
			++i;
		}

		return polygons;
	}

	private static Vec4 gammaCorrection(Vec4 vec) {
		float gamma = (float) 2.2;
		float newX = (float) Math.pow(vec.x / 255, gamma);
		float newY = (float) Math.pow(vec.y / 255, gamma);
		float newZ = (float) Math.pow(vec.z / 255, gamma);
		return new Vec4(newX, newY, newZ, vec.w);
	}
}