package main;

import enums.Mood;
import exceptions.NoCleanPlatesException;
import exceptions.OutOfPorridgeException;
import exceptions.StrangeBehaviorException;
import model.*;
import records.Location;

public class MainScenario {
    public static void main(String[] args) {
        Kid kid = new Kid("Малыш", Mood.HAPPY, new Location(0, 0));
        Carlson carlson = new Carlson("Карлсон", Mood.SAD, new Location(2, 2));
        Pot pot = new Pot("Кастрюля с кашей", 20, (int) (Math.random() * 15));
        Sideboard sideboard = new Sideboard("Буфет");
        Fridge fridge = new Fridge("Холодильник", (int) (Math.random() * 5) + 2);

        for (int i = 1; i <= 3; i++) {
            sideboard.addPlate(new Plate("Тарелка №" + i, 5));
        }

        System.out.println("=== СЦЕНАРИЙ НАЧИНАЕТСЯ ===");
        kid.speak("Карлсон, ты сегодня какой-то грустный...");
        carlson.speak("Есть немного... Может, варенья покушать?");
        kid.offerJam(fridge);

        Plate plate = null;
        try {
            plate = kid.takePlateFromSideboard(sideboard);
        } catch (NoCleanPlatesException e) {
            System.out.println(e.getMessage());
            System.out.println("Сценарий завершается, потому что нет чистых тарелок.");
            return;
        }

        try {
            carlson.speak("Сейчас попробую наложить каши, а потом посмотрим насчет варенья...");
            for (int i = 0; i < 2; i++) {
                carlson.scoopPorridge(pot, plate);
            }
        } catch (OutOfPorridgeException e) {
            System.out.println(e.getMessage());
            System.out.println("Карлсон остался голодным...");
        } catch (StrangeBehaviorException e) {
            System.out.println(e.getMessage());
            System.out.println("Поведение Карлсона непредсказуемо...");
        }

        carlson.eatFromPlate(plate);
        kid.play();

        carlson.speak("А теперь попробую украсть варенья!");
        carlson.stealJam(fridge);

        if (Math.random() < 0.4) {
            try {
                carlson.scrapePotWalls(pot);
            } catch (StrangeBehaviorException e) {
                System.out.println(e.getMessage());
                kid.speak("Что ты творишь?!");
            }
        } else {
            kid.speak("Может, вместе доедим кашу из кастрюли?");
        }

        kid.speak("Надо бы помыть посуду, а то она вся грязная!");
        kid.washPlates(sideboard);

        kid.speak("Я проголодался, добавлю ещё каши в кастрюлю.");
        pot.addPorridge((int)(Math.random() * 10) + 5);

        kid.speak("Тоже съем ложечку...");
        if (plate != null) {
            int kidEats = plate.removeFood(2);
            if (kidEats > 0) {
                kid.speak("Съел " + kidEats + " порции. Вкусно!");
                kid.setMood(Mood.HAPPY);
                kid.reduceEnergy(5);
            } else {
                kid.speak("Тарелка пустая!");
                kid.setMood(Mood.SAD);
            }
        }

        System.out.println("=== СЦЕНАРИЙ ОКОНЧЕН ===");
    }
}
