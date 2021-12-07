package id.ac.umn.masakuy;

import java.io.Serializable;

public class RecipeSource implements Serializable {
    private int recipe_id;
    private String recipe_name;
    private String description;
    private int user_id;
    private String steps;
    private String ingredients;
    private String recipe_pict;

    public RecipeSource(int recipe_id, String recipe_name, String description, int user_id, String steps, String ingredients, String recipe_pict) {
        this.recipe_id = recipe_id;
        this.recipe_name = recipe_name;
        this.description = description;
        this.user_id = user_id;
        this.steps = steps;
        this.ingredients = ingredients;
        this.recipe_pict = recipe_pict;
    }

    public int getRecipeId() {
        return this.recipe_id;
    }

    public String getRecipeName() {
        return this.recipe_name;
    }

    public String getDescription() {
        return this.description;
    }

    public int getUserId() {
        return this.user_id;
    }

    public String getSteps() {
        return this.steps;
    }

    public String getIngredients() {
        return this.ingredients;
    }

    public String getRecipePict() {
        return this.recipe_pict;
    }
}
