package model.pipe;

import model.pipe.mode.Mode;
import model.river.River;
import utils.RiverMath;

/**
 * Русловой рассеивающий выпуск
 */
public class ChanneledConcentratedPipe extends BasePipe {

    int rowLocation;
    int columnLocation;
    double length;

    public ChanneledConcentratedPipe(double x, boolean coast, Mode mode, double length, double concentartion, double wastewaterConsumption){
        this.x = x * 1000;
        this.endX = this.x;
        this.bank = coast;
        this.mode = mode;
        this.length = length;
        this.concentration = concentartion;
        this.wastewaterConsumption = wastewaterConsumption;
        this.cellsOccupied = 1;
    }




    @Override
    public void putPipeOnRiver(River riverInfo, double cellSizeX, double cellSizeY, int rows, int columns) {

        this.columnLocation = (int)Math.ceil(this.x / cellSizeX);
         /* bank  false - правый берег; true - левый берег;  */
        this.rowLocation = (int)Math.ceil(this.length/cellSizeY)+1;
        this.rowLocation  = (this.bank) ? rows - this.rowLocation : this.rowLocation;
    }

    @Override
    public boolean isPipeLocated(int i, int j) {
        if(i  == this.columnLocation && j == this.rowLocation) return true;
        return false;
    }

    @Override
    public String toString() {

        String strSuper = super.toString();
        StringBuilder str = new StringBuilder();
        str.append("Тип выпуска: Русловой сосредоточенный\n");
        str.append("\tРасстояние от берега до точки выпуска (м): " + this.length + "\n");
        str.append(strSuper);
        return str.toString();
    }
}
