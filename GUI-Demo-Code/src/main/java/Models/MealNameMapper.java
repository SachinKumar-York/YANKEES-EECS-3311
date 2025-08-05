//MealNameMapper.java start

package Models;

import DAO.FoodDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MealNameMapper {
    public static Map<Integer, String> buildMealNameMap(int userId, FoodDAO foodDAO) {
        Map<Integer, String> mealNameMap = new HashMap<>();
        List<String> loggedMeals = foodDAO.getLoggedMealsWithCaloriesForUser(userId);
        Pattern pattern = Pattern.compile("<span style='display:none'>([0-9]+)</span>.*?<b>(.*?)</b>");
        for (String mealStr : loggedMeals) {
            Matcher matcher = pattern.matcher(mealStr);
            if (matcher.find()) {
                int mealId = Integer.parseInt(matcher.group(1));
                String mealName = matcher.group(2);
                mealNameMap.put(mealId, mealName);
            }
        }
        return mealNameMap;
    }
}
