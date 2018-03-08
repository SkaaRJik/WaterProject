package model.pipe.mode;

/**
 * Залповый режим трубы. Поведение такое же как у трубы однократного действия, однако другие показатели выброса.
 */
public class VolleyMode extends SingleEntryMode {

    public VolleyMode(int timeOfWorking) {
        super(timeOfWorking);
    }
}
