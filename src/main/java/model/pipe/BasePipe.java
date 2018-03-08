package model.pipe;

import model.pipe.mode.Mode;
import model.river.River;

/**
 * Родительский класс сточных труб
 */

public abstract class BasePipe implements IPipe, Comparable<BasePipe> {
    /**
     * false - правый берег;
     * true - левый берег;
     */
    boolean coast;
    double x;
    double endX;
    Mode mode;
    double concentration;
    double wastewaterConsumption;
    double initialDilution;

    /**
     * Сливает сточные воды в реку, согласно своему положению на реке
     * @param river модель, в ячейки которой сливаются воды.
     */
    @Override
    public abstract void mergeWaste(double[][] river);

    /**
     * Инициализация {@link #initialDilution}
     * Подсчитывает коэффициент начального разбавления для трубы.<br>
     * Это значение будет сбрасываться в реку
     * @param riverInfo информация о реке, введенная пользователем;
     * @param rows количество ячеек вдоль реки;
     */
    @Override
    public abstract void calculateInitialDilution(River riverInfo, int rows);

    /**
     * Масштабирует и устанавливает трубу на реку.
     * @param cellSizeX Масштаб. Обозначает длину 1 ячейки
     * @param cellSizeY Масштаб. Обозначает ширину 1 ячейки
     * @param rows Количесвто рядов ячеек у реки
     * @param columns Количесвто столбцов ячеек у реки
     */
    @Override
    public abstract void putPipeOnRiver(double cellSizeX, double cellSizeY, int rows, int columns);

    /*Блок Геттеров и Сетеров */
    public void setX(double x) {
        this.x = x;
    }

    public double getX() { return x;     }

    public double getConcentration() {
        return concentration;
    }

    public void setConcentration(double concentration) {
        this.concentration = concentration;
    }

    public double getWastewaterConsumption() {
        return wastewaterConsumption;
    }

    public void setWastewaterConsumption(double wastewaterConsumption) {
        this.wastewaterConsumption = wastewaterConsumption;
    }

    public double getEndX() {
        return this.endX;
    }
    /*___________________________________________*/

    /**
     * Метод-компаратор.<br>
     *     Необходим для осуществления сортировки методом {@link java.util.Arrays#sort(Object[])}
     * @param o сравниваемый объект унаследованный от {@link BasePipe}
     * @return 0 - равны, 1 - текущий больше сравниваемого, -1 - текущий меньше сравниваемого
     */
    @Override
    public int compareTo(BasePipe o) {
        if(this.endX > o.endX) return 1;
        else if (this.endX < o.endX) return -1;
        else return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BasePipe basePipe = (BasePipe) o;

        return Double.compare(basePipe.endX, endX) == 0;

    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(endX);
        return (int) (temp ^ (temp >>> 32));
    }
}
