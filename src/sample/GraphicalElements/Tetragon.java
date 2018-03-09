package sample.GraphicalElements;

public class Tetragon extends WorldPolygon {

    public Tetragon(Vector3 topLeft, Vector3 topRight, Vector3 bottomRight, Vector3 bottomLeft)  {
        this((float)topLeft.x, (float)topLeft.y, (float)topLeft.z,
             (float)topRight.x, (float)topRight.y, (float)topRight.z,
             (float)bottomRight.x, (float)bottomRight.y, (float)bottomRight.z,
             (float)bottomLeft.x, (float)bottomLeft.y, (float)bottomLeft.z);
    }

    public Tetragon(float tlX, float tlY, float tlZ, float trX, float trY, float trZ,
                    float brX, float brY, float brZ, float blX, float blY, float blZ) {
        super(new float[] {
                tlX, tlY, tlZ,
                trX, trY, trZ,
                brX, brY, brZ,
                blX, blY, blZ},
              new float[] {0, 0, 1, 0, 1, 1, 0, 1},
              new int[]{0, 0, 1, 1, 2, 2, 2, 2, 3, 3, 0, 0});
    }
}
