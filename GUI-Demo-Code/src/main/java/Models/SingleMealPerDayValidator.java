package Models;
import DAO.FoodDAO;

import java.util.Date;

public class SingleMealPerDayValidator implements MealValidator {
    private final FoodDAO foodDAO;
    private String errorMessage = "";

    public SingleMealPerDayValidator(FoodDAO foodDAO) {
        this.foodDAO = foodDAO;
    }

    @Override
    public boolean validate(int userId, String mealType, Date mealDate) {
        if (!mealType.equalsIgnoreCase("snack") && foodDAO.hasMealTypeLogged(userId, mealType, mealDate)) {
            errorMessage = "You have already logged a " + mealType + " for this date.";
            return false;
        }
        return true;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}

