package Models;

public class Food {
    private long foodId;
    private String foodDescription;

    public Food(long foodId, String foodDescription) {
        this.foodId = foodId;
        this.foodDescription = foodDescription;
    }

    public long getFoodId() {
        return foodId;
    }

    public String getFoodDescription() {
        return foodDescription;
    }

    @Override
    public String toString() {
        return foodDescription;
    }
}
