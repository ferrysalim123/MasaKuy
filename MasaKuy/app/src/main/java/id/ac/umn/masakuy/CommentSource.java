package id.ac.umn.masakuy;

import java.io.Serializable;

public class CommentSource implements Serializable {
    private int comment_id;
    private int user_id;
    private String comments;
    private int recipe_id;

    public CommentSource(int comment_id, int user_id, String comments, int recipe_id) {
        this.comment_id = comment_id;
        this.user_id = user_id;
        this.comments = comments;
        this.recipe_id = recipe_id;
    }

    public int getCommentId() {
        return this.comment_id;
    }

    public int getUserId() {
        return this.user_id;
    }

    public String getComments() {
        return this.comments;
    }

    public int getRecipeId() {
        return this.recipe_id;
    }
}