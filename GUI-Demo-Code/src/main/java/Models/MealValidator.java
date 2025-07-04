package Models;

import java.util.Date;

public interface MealValidator {
    boolean validate(int userId, String mealType, Date mealDate);
    String getErrorMessage();  // for UI feedback
}