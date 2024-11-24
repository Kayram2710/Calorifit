package database;

public class CalorieEntry {
    public String id;
    public String name; // Optional name for the entry
    public int amount; // Calorie amount

    public CalorieEntry() {}

    public CalorieEntry(String id, String name, int amount) {
        this.id = id;
        this.name = name;
        this.amount = amount;
    }
}
