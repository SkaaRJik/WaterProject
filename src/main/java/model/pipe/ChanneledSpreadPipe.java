package model.pipe;

import model.pipe.BasePipe;
import model.pipe.mode.Mode;
import model.river.River;

/**
 * Created by SkaaRJ on 08.03.2018.
 */
public class ChanneledSpreadPipe extends BasePipe {

    double length;
    double padding;
    double sapceToFirstTube;
    int[] rowLocation;
    int columnsLocation;
    int cellsOccupied = 0;


    public ChanneledSpreadPipe(double x, boolean coast, Mode mode, double length,  double padding, double spaceToFirstTube, double concentartion, double wastewaterConsumption){
        this.x = x * 1000;
        this.endX = this.x;
        this.coast = coast;
        this.length = length;
        this.padding = padding;
        this.sapceToFirstTube = spaceToFirstTube;
        this.mode = mode;
        this.concentration = concentartion;
        this.wastewaterConsumption = wastewaterConsumption;
    }

    @Override
    public void mergeWaste(double[][] river) {

    }

    @Override
    public void calculateInitialDilution(River riverInfo, int rows) {

    }

    @Override
    public void putPipeOnRiver(River riverInfo, double cellSizeX, double cellSizeY, int rows, int columns) {

    }
}
