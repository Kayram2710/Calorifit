package database;

public class CalorieEntry {
    public String name; // Optional name for the entry
    public int amount; // Calorie amount
    public String type;

    public CalorieEntry() {}

    public CalorieEntry(String name, int amount, String type) {
        this.name = name;
        this.amount = amount;
        this.type = type;
    }
    @Override
    public boolean equals(Object obj) {
        CalorieEntry ce = (CalorieEntry) obj;
        return name.equals(ce.name) && this.amount==ce.amount && this.type.equals(ce.type);
    }
}
