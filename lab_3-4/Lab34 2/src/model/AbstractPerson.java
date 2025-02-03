package model;

import enums.Mood;
import records.Location;

public abstract class AbstractPerson {
    protected String name;
    protected Mood mood;
    protected Location location;
    protected int energyLevel;

    public AbstractPerson(String name, Mood mood, Location location) {
        this.name = name;
        this.mood = mood;
        this.location = location;
        this.energyLevel = 100;
    }

    public String getName() {
        return name;
    }

    public Mood getMood() {
        return mood;
    }

    public void setMood(Mood mood) {
        this.mood = mood;
    }

    public Location getLocation() {
        return location;
    }

    public void moveTo(Location newLocation) {
        this.location = newLocation;
        System.out.println(name + " перемещается в точку (" + newLocation.x() + ", " + newLocation.y() + ").");
    }

    public void randomMove() {
        int dx = (int)(Math.random() * 3) - 1;
        int dy = (int)(Math.random() * 3) - 1;
        this.location = this.location.move(dx, dy);
        System.out.println(name + " прогуливается и внезапно оказывается в (" + location.x() + ", " + location.y() + ").");
        reduceEnergy(5);
    }

    public int getEnergyLevel() {
        return energyLevel;
    }

    public void reduceEnergy(int amount) {
        energyLevel -= amount;
        if (energyLevel < 0) {
            energyLevel = 0;
        }
    }

    public void gainEnergy(int amount) {
        energyLevel += amount;
        if (energyLevel > 100) {
            energyLevel = 100;
        }
    }

    public void sleep() {
        System.out.println(name + " ложится спать, чтобы восстановить силы.");
        gainEnergy(50);
        setMood(Mood.NEUTRAL);
    }

    public abstract void speak(String text);

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AbstractPerson other)) return false;
        return name.equals(other.name)
                && mood == other.mood
                && location.equals(other.location)
                && energyLevel == other.energyLevel;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + mood.hashCode();
        result = 31 * result + location.hashCode();
        result = 31 * result + energyLevel;
        return result;
    }

    @Override
    public String toString() {
        return "AbstractPerson{name='" + name + "', mood=" + mood + ", location=" + location + ", energy=" + energyLevel + "}";
    }
}
