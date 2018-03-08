package model.pipe.mode;

/**
 * Стационарный режим трубы.
 */
public class StationaryMode extends Mode {

    public StationaryMode(){    }


    /**
     * Работает на протяжении всего функционирования модели, поэтому всегда возвращает true.
     * @return true
     */
    @Override
    public boolean update() {return true;}
}
