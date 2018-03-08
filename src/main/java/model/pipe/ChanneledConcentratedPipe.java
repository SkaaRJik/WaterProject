package model.pipe;

import model.pipe.mode.Mode;
import model.river.River;

/**
 * Русловой рассеивающий выпуск
 */
public class ChanneledConcentratedPipe extends BasePipe {

    double length;

    public ChanneledConcentratedPipe(double x, boolean coast, Mode mode, double length, double concentartion, double wastewaterConsumption){
        this.x = x * 1000;
        this.endX = this.x;
        this.coast = coast;
        this.mode = mode;
        this.length = length;
        this.concentration = concentartion;
        this.wastewaterConsumption = wastewaterConsumption;
    }

    @Override
    public void mergeWaste(double[][] river) {

    }

    @Override
    public void calculateInitialDilution(River riverInfo, int rows) {
        this.initialDilution
    }

    @Override
    public void putPipeOnRiver(double cellSizeX, double cellSizeY, int rows, int columns) {

    }
}
