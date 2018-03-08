package model.pipe;

import model.pipe.mode.Mode;
import model.river.River;
import utils.RiverMath;

/**
 * Береговой распределенный выпуск.
 */
public class CoastalSpreadPipe extends BasePipe{

    double length;
    double padding;
    int rowLocation;
    int[] columnsLocation;
    int cellsOccupied = 0;

    /**
     * Береговой распределенный выпуск.
     * @param x Координата начальной точки выпуска(км);
     * @param coast берег расположения: false - правый; true - левый;
     * @param length длина распределенной части выпска(м);
     * @param padding расстояние между патрубками(м);
     * @param turnedOn состояние выпуска: false - выключен; true - включен;
     * @param concentartion концентрация примеси в стоках (мг/л)
     * @param wastewaterConsumption расход сточных вод (мг<sup>3</sup>/с)
     */
    public CoastalSpreadPipe(double x, boolean coast, Mode mode, double length, double padding, double concentartion, double wastewaterConsumption ) {
        this.x = x * 1000; //Переводим км в м.
        this.endX = this.x + length; //Помечаем конец трубы
        this.coast = coast;
        this.length = length;
        this.padding = padding;
        this.mode = mode;
        this.concentration = concentartion;
        this.wastewaterConsumption = wastewaterConsumption;
    }



    @Override
    public void mergeWaste(double[][] river) {
        if(this.mode.update()) {
            for (int i = 0; i < this.cellsOccupied; i++) {
                river[columnsLocation[i]][rowLocation] = this.initialDilution;
            }
        }
    }

    @Override
    public void putPipeOnRiver(double cellSizeX, double cellSizeY, int rows, int columns) {
        int startIndex = (int)(this.x / cellSizeX);

        /* Упрощенная модель рассчета */
        int endIndex = (int) ((this.endX) / cellSizeX);
        this.cellsOccupied = endIndex - startIndex;
        this.columnsLocation = new int[this.cellsOccupied];
        for (int i = 0; i < cellsOccupied; i++) {
            this.columnsLocation[i] = startIndex+i;
        }
        /*________________________________ */

        /*  Усложненная модель рассчета, доделать потом
        int numberOfMiniPipes = (int) (length / padding);
        int scaleOfPadding = (int)(this.length/cellSizeX);
        for (int i = 1; i < numberOfMiniPipes; i++) {

        }
        */
         /* coast  false - правый берег; true - левый берег;  */
        if(this.coast == false) this.rowLocation = 0;
        else this.rowLocation = rows - 1;
    }

    @Override
    public void calculateInitialDilution(River riverInfo, int rows) {
        double Ccp = RiverMath.averageImpurityConcentration(this.concentration,
                riverInfo.backgroundConcentration, this.wastewaterConsumption, riverInfo.riverWidth,
                riverInfo.riverDepth,riverInfo.flowSpeed);
        this.initialDilution = (Ccp * rows - riverInfo.backgroundConcentration * rows - this.cellsOccupied) / this.cellsOccupied;
    }
}
