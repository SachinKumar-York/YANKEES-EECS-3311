package Models;

import Models.MealIngredient;
import java.util.List;

public class Meal {
    private List<MealIngredient> items;

    public Meal(List<MealIngredient> items) {
        this.items = items;
    }

    public List<MealIngredient> getItems() {
        return items;
    }
}
