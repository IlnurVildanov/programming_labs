package records;

public record Location(int x, int y) {
    public Location move(int dx, int dy) {
        return new Location(this.x + dx, this.y + dy);
    }
}
