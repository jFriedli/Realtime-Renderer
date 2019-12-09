import java.awt.Polygon;

import javaVectors.Vec2;
import javaVectors.Vec3;
import javaVectors.Vec4;

public class MyPolygon extends Polygon {
	private static final long serialVersionUID = 1L;
	public float[] xpointsF;
	public float[] ypointsF;
	public Vec4[] objectCoordinates;
	public Vec4[] colors;
	public Vec2[] textureCoordinates;
	public Vec3 normal;
	public Vec4[] projectedCoordinates;
	
	public MyPolygon(int[] xPoints, int[] yPoints, float[] xpointsF, float[] ypointsF, int nPoints, Vec4[] objectCoordinates, Vec4[] colors, Vec2[] textureCoordinates, Vec3 normal, Vec4[] projectedCoordinates) {
		super(xPoints, yPoints, nPoints);
		this.xpointsF = xpointsF;
		this.ypointsF = ypointsF;
		this.objectCoordinates = objectCoordinates;
		this.colors = colors;
		this.textureCoordinates = textureCoordinates;
		this.normal = normal;
		this.projectedCoordinates = projectedCoordinates;
	}
}
