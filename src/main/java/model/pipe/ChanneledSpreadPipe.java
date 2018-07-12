package model.pipe;

import model.pipe.mode.Mode;
import model.river.River;
import utils.RiverMath;

/**
 * Created by SkaaRJ on 08.03.2018.
 */
public class ChanneledSpreadPipe extends BasePipe {

    double length;
    double padding;
    double distanceToFirstMiniPipe;
    int[] rowsLocation;
    int columnLocation;
    int cellsOccupied = 0;


    public ChanneledSpreadPipe(double x, boolean coast, Mode mode, double length,  double padding, double spaceToFirstTube, double concentartion, double wastewaterConsumption){
        this.x = x * 1000;
        this.endX = this.x;
        this.bank = coast;
        this.length = length;
        this.padding = padding;
        this.distanceToFirstMiniPipe = spaceToFirstTube;
        this.mode = mode;
        this.concentration = concentartion;
        this.wastewaterConsumption = wastewaterConsumption;
    }






    @Override
    public void putPipeOnRiver(River riverInfo, double cellSizeX, double cellSizeY, int rows, int columns) {
        this.columnLocation = (int)Math.ceil(this.x / cellSizeX) - 1;
        int startIndex = (int)Math.ceil(this.distanceToFirstMiniPipe/cellSizeY)+1;
        startIndex  = (this.bank) ?   startIndex : rows - startIndex;
        int endIndex = (int)Math.ceil((this.distanceToFirstMiniPipe+this.length)/cellSizeY)+1;
        endIndex  = (this.bank) ?  endIndex : rows - endIndex;

        this.cellsOccupied = 0;

        //* bank  false - правый берег; true - левый берег;  *//*
        if(bank) {
            for (int i = startIndex; i <= endIndex; i++) {
                cellsOccupied++;
            }
        } else {
            for (int i = endIndex; i >= startIndex; i--) {
                cellsOccupied++;
            }
        }
        this.rowsLocation = new int[this.cellsOccupied];
        int counter = 0;
        if(bank) {
            for (int i = startIndex; i <= endIndex; i++) {
                rowsLocation[counter++] = i;
            }
        } else {
            for (int i = endIndex; i >= startIndex; i--) {
                rowsLocation[counter++] = i;
            }
        }
        this.setCellsOccupied(this.cellsOccupied);
    }

    @Override
    public boolean isPipeLocated(int i, int j) {
        for (int k = this.cellsOccupied-1; k >= 0; k--) {
            if(i  == this.columnLocation && j == this.rowsLocation[k]) return true;
        }
        return false;
    }

    @Override
    public String toString() {

        String strSuper = super.toString();
        StringBuilder str = new StringBuilder();
        str.append("Тип выпуска: Русловой распределенный\n");
        str.append("\tРасстояние от берега до 1-го патрубка (м): " + this.distanceToFirstMiniPipe + "\n");
        str.append("\tДлина распределенной части выпуска (м): " + this.length + "\n");
        str.append("\tРасстояние между патрубками (м): " + this.padding + "\n");
        str.append(strSuper);

        return str.toString();

    }

}
