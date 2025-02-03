package model;

import enums.Mood;
import exceptions.OutOfPorridgeException;
import exceptions.PlateOverflowException;
import exceptions.StrangeBehaviorException;
import interfaces.Action;
import records.Location;

public class Carlson extends AbstractPerson implements Action {
    public Carlson(String name, Mood mood, Location location) {
        super(name, mood, location);
    }

    @Override
    public void speak(String text) {
        System.out.println("Карлсон (" + getMood() + ", энергия=" + getEnergyLevel() + "): \"" + text + "\"");
    }

    @Override
    public void doAction() {
        System.out.println("Карлсон заводит пропеллер и летит куда-то.");
        flyTo(new Location(getLocation().x() + 1, getLocation().y() + 1));
    }

    public void flyTo(Location target) {
        int distance = Math.abs(target.x() - getLocation().x()) + Math.abs(target.y() - getLocation().y());
        System.out.println(name + " пролетает " + distance + " условных единиц.");
        moveTo(target);
        reduceEnergy(distance * 5);
        if (getEnergyLevel() <= 0) {
            speak("Я совсем выдохся! Пойду спать...");
            sleep();
        }
    }

    public void scoopPorridge(Pot pot, Plate plate) throws OutOfPorridgeException {
        int randomAmount = (int) (Math.random() * 5) + 1;
        int actualScooped = pot.scoop(randomAmount);
        try {
            plate.addFood(actualScooped);
            speak("Наложил " + actualScooped + " порций каши в " + plate.getName());
        } catch (PlateOverflowException e) {
            System.out.println(e.getMessage() + " Но Карлсон не унывает.");
            setMood(Mood.ANGRY);
        }
        reduceEnergy(10);
    }

    public void eatFromPlate(Plate plate) {
        int toEat = (int) (Math.random() * 3) + 1;
        int eaten = plate.removeFood(toEat);
        if (eaten > 0) {
            speak("Съедаю " + eaten + " порций из " + plate.getName());
            setMood(Mood.HAPPY);
            gainEnergy(eaten * 3);
        } else {
            speak("Тарелка пуста, меня это огорчает.");
            setMood(Mood.SAD);
        }
    }

    public void scrapePotWalls(Pot pot) {
        if (Math.random() < 0.7) {
            speak("Водит пальцем по стенкам кастрюли и слизывает все остатки.");
            pot.scrapeWalls();
            gainEnergy(2);
        } else {
            throw new StrangeBehaviorException("Карлсон внезапно выбрасывает кастрюлю в окно!");
        }
    }

    public void stealJam(Fridge fridge) {
        if (Math.random() < 0.5) {
            int stolen = fridge.takeJam((int)(Math.random() * 3) + 1);
            if (stolen > 0) {
                speak("Утащил " + stolen + " порций варенья из холодильника!");
                setMood(Mood.EXCITED);
                gainEnergy(stolen * 5);
            } else {
                speak("В холодильнике не оказалось варенья! Какой позор!");
                setMood(Mood.SAD);
            }
        } else {
            speak("Пожалуй, не буду сейчас воровать варенье...");
        }
    }
}
