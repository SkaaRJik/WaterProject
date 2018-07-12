package model.pipe.mode;

/**
 * Режим однократного действия. Имеет один таймер, по истечению времени которого, труба перестает работать.
 */
public class SingleEntryMode extends Mode {

    int currentTime = 0;
    int timeOfWorking;
    boolean status = true;

    /**
     * Конструктор
     * @param timeOfWorking время работы
     */
    public SingleEntryMode(int timeOfWorking){
        this.timeOfWorking = timeOfWorking;
    }

    /**
     * Обновляет таймер работы выпускной трубы.
     * @return true - труба работает, false - труба остановилась.
     */
    @Override
    public boolean update(double currentTime) {
        if (timeOfWorking >= timeOfWorking) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("");
        str.append("Режим работы: Однократного действия\n");
        str.append("\tВремя работы(мин): "+this.timeOfWorking/60 + "\n");
        return str.toString();
    }
}
