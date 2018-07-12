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
    boolean bank;
    double x;
    double endX;
    Mode mode;
    double concentration;
    double wastewaterConsumption;
    double initialDilution = -1;
    double firstLaunchConcentration = -1;
    int cellsOccupied = 1;

    /**
     * Сливает сточные воды в реку, согласно своему положению на реке
     * @param riverAverageConsumption расход реки
     * @param currentConcentration текущая концентрация в ячейке
     * @param currentTime текущее время моделирования
     * @return новая концентрация с расчетом сброса
     */
    @Override
    public double mergeWaste(double riverAverageConsumption, double currentConcentration, double currentTime){
        double q = 0;
        if(this.mode.update(currentTime)) { q = this.wastewaterConsumption/cellsOccupied; }
        double newConcentration = ((q * this.concentration) + riverAverageConsumption *
                currentConcentration) / (q + riverAverageConsumption);
        if(this.firstLaunchConcentration == -1 && newConcentration > currentConcentration) this.firstLaunchConcentration = newConcentration;
        return newConcentration;
    }

    /**
     * Инициализация {@link #initialDilution}
     * Подсчитывает коэффициент начального разбавления для трубы.<br>
     * Это значение будет сбрасываться в реку
     * @param riverInfo информация о реке, введенная пользователем;
     * @param rows количество ячеек вдоль реки;
     */
    @Override
    public void calculateInitialDilution(River riverInfo, int rows){
        this.initialDilution = (this.firstLaunchConcentration * rows - riverInfo.backgroundConcentration * rows - this.cellsOccupied) / this.cellsOccupied;
    }

    /**
     * Масштабирует и устанавливает трубу на реку.
     * @param cellSizeX Масштаб. Обозначает длину 1 ячейки
     * @param cellSizeY Масштаб. Обозначает ширину 1 ячейки
     * @param rows Количесвто рядов ячеек у реки
     * @param columns Количесвто столбцов ячеек у реки
     */
    @Override
    public abstract void putPipeOnRiver(River riverInfo, double cellSizeX, double cellSizeY, int rows, int columns);

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

    @Override
    public double getInitialDilution() {
        return this.initialDilution;
    }

    @Override
    public abstract boolean isPipeLocated(int i, int j);

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

    @Override
    public double getFirstLaunchConcentration() {
        return firstLaunchConcentration;
    }



    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append(String.format("Удаленность точки выпуска от контр. створа (КМ) : %.3f\n", (this.x / 1000)));
        /* bank  false - правый берег; true - левый берег;  */
        str.append("Берег, с которого производится выпуск: " + ((this.bank) ? "Правый" : "Левый") + "\n");
        str.append(this.mode.toString());
        str.append("Берег, с которого производится выпуск: " + ((this.bank) ? "Правый" : "Левый") + "\n");
        str.append("Концентрация примести в стоках(мг/л): " + this.concentration + "\n");
        str.append("Расход сточных вод (м^3/сек): " + this.wastewaterConsumption + "\n");
        return str.toString();
    }

    public void setCellsOccupied(int cellsOccupied) {
        this.cellsOccupied = cellsOccupied;
    }
}
