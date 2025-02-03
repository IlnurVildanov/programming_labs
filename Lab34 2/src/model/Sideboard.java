package model;

import exceptions.NoCleanPlatesException;
import java.util.ArrayList;
import java.util.List;

public class Sideboard {
    private final String name;
    private final List<Plate> plates;

    public Sideboard(String name) {
        this.name = name;
        this.plates = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Plate> getPlates() {
        return plates;
    }

    public void addPlate(Plate plate) {
        plates.add(plate);
    }

    public Plate getPlate() throws NoCleanPlatesException {
        if (plates.isEmpty()) {
            throw new NoCleanPlatesException("В буфете " + name + " нет чистых тарелок!");
        }
        return plates.remove(0);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Sideboard other)) return false;
        return name.equals(other.name) && plates.equals(other.plates);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + plates.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Sideboard{name='" + name + "', plates=" + plates + "}";
    }
}
