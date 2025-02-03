package model;

public class Fridge {
    private final String name;
    private int jamPortions;

    public Fridge(String name, int jamPortions) {
        this.name = name;
        this.jamPortions = jamPortions;
    }

    public String getName() {
        return name;
    }

    public int getJamPortions() {
        return jamPortions;
    }

    public int takeJam(int requested) {
        if (jamPortions <= 0) {
            return 0;
        }
        int actual = Math.min(requested, jamPortions);
        jamPortions -= actual;
        System.out.println("Из холодильника " + name + " взяли " + actual + " порций варенья. Осталось " + jamPortions);
        return actual;
    }

    public void refillJam(int amount) {
        jamPortions += amount;
        System.out.println("В холодильник " + name + " положили ещё " + amount + " порций варенья. Теперь всего " + jamPortions);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Fridge other)) return false;
        return name.equals(other.name) && jamPortions == other.jamPortions;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + jamPortions;
        return result;
    }

    @Override
    public String toString() {
        return "Fridge{name='" + name + "', jamPortions=" + jamPortions + "}";
    }
}
