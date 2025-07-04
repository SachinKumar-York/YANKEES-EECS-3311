package Models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MealValidationContext {
    private final List<MealValidator> validators = new ArrayList<>();
    private final List<String> errorMessages = new ArrayList<>();

    public void addValidator(MealValidator validator) {
        validators.add(validator);
    }

    public boolean validate(int userId, String mealType, Date mealDate) {
        errorMessages.clear();
        boolean isValid = true;
        for (MealValidator v : validators) {
            if (!v.validate(userId, mealType, mealDate)) {
                isValid = false;
                errorMessages.add(v.getErrorMessage());
            }
        }
        return isValid;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}

