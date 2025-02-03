package model;

import enums.Mood;
import exceptions.NoCleanPlatesException;
import interfaces.Action;
import records.Location;

public class Kid extends AbstractPerson implements Action {
    public Kid(String name, Mood mood, Location location) {
        super(name, mood, location);
    }

    @Override
    public void speak(String text) {
        System.out.println("Малыш (" + getMood() + ", энергия=" + getEnergyLevel() + "): \"" + text + "\"");
    }

    @Override
    public void doAction() {
        System.out.println("Малыш резво бегает по дому.");
        randomMove();
    }

    public Plate takePlateFromSideboard(Sideboard sideboard) throws NoCleanPlatesException {
        Plate plate = sideboard.getPlate();
        speak("Достаю тарелку '" + plate.getName() + "' из буфета " + sideboard.getName());
        reduceEnergy(5);
        return plate;
    }

    public void washPlates(Sideboard sideboard) {
        if (sideboard.getPlates().isEmpty()) {
            speak("В буфете нет грязных тарелок для мытья!");
        } else {
            speak("Решил вымыть все тарелки в буфете!");
            for (Plate p : sideboard.getPlates()) {
                p.clean();
            }
            reduceEnergy(10);
            setMood(Mood.NEUTRAL);
        }
    }

    public void play() {
        speak("Играю с игрушками!");
        reduceEnergy(10);
        if (getEnergyLevel() <= 20) {
            speak("Я устал, пора отдохнуть!");
            setMood(Mood.BORED);
            sleep();
        } else {
            setMood(Mood.EXCITED);
        }
    }

    public void offerJam(Fridge fridge) {
        speak("У нас есть варенье, Карлсон! Хочешь?");
        reduceEnergy(5);
        if (fridge.getJamPortions() > 0) {
            speak("Я могу дать тебе немного варенья из холодильника!");
        } else {
            speak("Ой, кажется варенье закончилось...");
            setMood(Mood.SAD);
        }
    }
}
