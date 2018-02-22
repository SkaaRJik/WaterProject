package tube;

/**
 * Created by SkaaRJ on 22.02.2018.
 */
public interface IPipe {
    void mergeWaste(double[][] river);
    void putPipeOnRiver(double cellSizeX, double cellSizeY, int rows, int columns);
}
