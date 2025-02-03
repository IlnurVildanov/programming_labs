package model;

import exceptions.PlateOverflowException;

public class Plate {
    private final String name;
    private final int capacity;
    private int currentFood;
    private boolean isClean;

    public Plate(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
        this.currentFood = 0;
        this.isClean = true;
    }

    public String getName() {
        return name;
    }

    public int getCurrentFood() {
        return currentFood;
    }

    public boolean isClean() {
        return isClean;
    }

    public void addFood(int amount) {
        if (!isClean) {
            System.out.println("Тарелка " + name + " грязная, еду класть не хочется!");
            return;
        }
        if (currentFood + amount > capacity) {
            throw new PlateOverflowException("Тарелка " + name + " не может вместить ещё " + amount + " порций!");
        }
        currentFood += amount;
        isClean = false;
        System.out.println("Теперь в " + name + " " + currentFood + " порций. Тарелка стала грязной.");
    }

    public int removeFood(int amount) {
        if (currentFood == 0) {
            return 0;
        }
        int eaten = Math.min(amount, currentFood);
        currentFood -= eaten;
        return eaten;
    }

    public void clean() {
        currentFood = 0;
        isClean = true;
        System.out.println("Тарелка " + name + " теперь чистая.");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Plate other)) return false;
        return name.equals(other.name)
                && capacity == other.capacity
                && currentFood == other.currentFood
                && isClean == other.isClean;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + capacity;
        result = 31 * result + currentFood;
        result = 31 * result + Boolean.hashCode(isClean);
        return result;
    }

    @Override
    public String toString() {
        return "Plate{name='" + name + "', capacity=" + capacity + ", currentFood=" + currentFood + ", isClean=" + isClean + "}";
    }
}
