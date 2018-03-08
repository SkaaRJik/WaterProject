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
    public boolean update() {
        if(this.status) {
            if (currentTime >= timeOfWorking) {
                this.status = false;
                return this.status;
            }
            currentTime++;
        }
        return this.status;
    }
}
