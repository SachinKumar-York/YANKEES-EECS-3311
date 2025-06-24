package Models;

import Models.Food;

public class SwapSuggestion {
    private Food original;
    private Food suggested;
    private String justification;

    public SwapSuggestion(Food original, Food suggested, String justification) {
        this.original = original;
        this.suggested = suggested;
        this.justification = justification;
    }

    public Food getOriginal() {
        return original;
    }

    public Food getSuggested() {
        return suggested;
    }

    public String getJustification() {
        return justification;
    }
}