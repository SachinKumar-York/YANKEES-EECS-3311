package Models;


public class MealIngredient {
    private Food food;
    private double quantity;

    public MealIngredient(Food food, double quantity) {
        this.food = food;
        this.quantity = quantity;
    }

    public Food getFood() {
        return food;
    }

    public double getQuantity() {
        return quantity;
    }
}
