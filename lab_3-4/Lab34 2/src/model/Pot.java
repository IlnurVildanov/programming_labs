package model;

import exceptions.OutOfPorridgeException;

public class Pot {
    private final String name;
    private final int capacity;
    private int currentPorridge;

    public Pot(String name, int capacity, int currentPorridge) {
        this.name = name;
        this.capacity = capacity;
        this.currentPorridge = currentPorridge;
    }

    public String getName() {
        return name;
    }

    public int getCurrentPorridge() {
        return currentPorridge;
    }

    public int scoop(int amount) throws OutOfPorridgeException {
        if (currentPorridge <= 0) {
            throw new OutOfPorridgeException("Кастрюля " + name + " пустая!");
        }
        int scooped = Math.min(amount, currentPorridge);
        currentPorridge -= scooped;
        return scooped;
    }

    public void addPorridge(int amount) {
        int freeSpace = capacity - currentPorridge;
        int added = Math.min(amount, freeSpace);
        currentPorridge += added;
        System.out.println("В кастрюлю " + name + " добавили " + added + " порций. Осталось места: " + (capacity - currentPorridge));
    }

    public void scrapeWalls() {
        if (currentPorridge > 0) {
            currentPorridge--;
            System.out.println("Остатки каши соскребли со стенок, в кастрюле теперь " + currentPorridge + " порций.");
        } else {
            System.out.println("В кастрюле " + name + " уже нет остатков.");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Pot other)) return false;
        return name.equals(other.name) && capacity == other.capacity && currentPorridge == other.currentPorridge;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + capacity;
        result = 31 * result + currentPorridge;
        return result;
    }

    @Override
    public String toString() {
        return "Pot{name='" + name + "', capacity=" + capacity + ", currentPorridge=" + currentPorridge + "}";
    }
}
