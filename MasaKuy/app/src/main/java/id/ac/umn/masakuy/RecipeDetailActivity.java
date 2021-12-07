package id.ac.umn.masakuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class RecipeDetailActivity extends AppCompatActivity {
    private TextView etRecipeName;
    private TextView etDescription;
    private TextView etIngredients;
    private TextView etSteps;
    private ImageView ivRecipePict;
    String thisLocalhost = "";
    AppCompatButton btnComment, deletePost;

    int thisUserId = -1;

    RecyclerView rvCommentList;
    CommentListAdapter mAdapter;
    LinkedList<CommentSource> commentList = new LinkedList<>();

    RecipeSource svRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        thisUserId = sh.getInt("ThisUserId", -1);
        thisLocalhost = sh.getString("ThisLocalhost","");

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        svRecipe = (RecipeSource) bundle.getSerializable("RecipeDetail");

        deletePost = findViewById(R.id.btn_delete);

        if(svRecipe.getUserId() != thisUserId) {
            deletePost.setVisibility(View.INVISIBLE);
        }

        deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String recipe_id = String.valueOf(svRecipe.getRecipeId());

                String[] field = new String[1];
                field[0] = "recipe_id";
                //Creating array for data
                String[] data = new String[1];
                data[0] = recipe_id;

                PutData putData = new PutData(thisLocalhost + "deleteRecipe.php", "POST", field, data);

                if (putData.startPut()) {
                    if (putData.onComplete()) {}
                }
                Intent intent = new Intent(RecipeDetailActivity.this, MenuActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("ThisUser", svRecipe);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        etRecipeName = findViewById(R.id.etRecipeName);
        etDescription = findViewById(R.id.etDescription);
        etIngredients = findViewById(R.id.etIngredients);
        etSteps = findViewById(R.id.etSteps);
        ivRecipePict = findViewById(R.id.ivRecipePict);

        commentListValue();
        rvCommentList = (RecyclerView) findViewById(R.id.recyclerView);
        mAdapter = new CommentListAdapter(this, commentList);
        rvCommentList.setAdapter(mAdapter);
        rvCommentList.setLayoutManager(new LinearLayoutManager(this));

        etRecipeName.setText(svRecipe.getRecipeName());
        etDescription.setText(svRecipe.getDescription());
        etIngredients.setText(svRecipe.getIngredients());
        etSteps.setText(svRecipe.getSteps());

        if(!svRecipe.getRecipePict().isEmpty()) {
            Picasso.with(getApplicationContext()) //The context of your activity
                    .load(svRecipe.getRecipePict())
                    .into(ivRecipePict);
        }

        btnComment = findViewById(R.id.btnComment);
        btnComment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(RecipeDetailActivity.this, AddComment.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("RecipeDetail", svRecipe);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
    }

    public void commentListValue() {
        String[] field = new String[0];
        String[] data = new String[0];

        PutData putData = new PutData(thisLocalhost +"readCommentTable.php", "GET", field, data);

        String result = "";

        int dbcommentid = 0;
        int dbuserid = 0;
        String dbcomments = "";
        int dbrecipeid = 0;

        if (putData.startPut()) {
            if (putData.onComplete()) {
                result = putData.getResult();
                JSONObject responseJSON = null;

                try {
                    responseJSON = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONArray arr = null;
                try {
                    arr = responseJSON.getJSONArray("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < arr.length(); i++) {

                    try {
                        dbcommentid = arr.getJSONObject(i).getInt("comment_id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        dbuserid = arr.getJSONObject(i).getInt("user_id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        dbcomments = arr.getJSONObject(i).getString("comments");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        dbrecipeid = arr.getJSONObject(i).getInt("recipe_id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(dbrecipeid == svRecipe.getRecipeId()) {
                        commentList.add(new CommentSource(dbcommentid, dbuserid, dbcomments, dbrecipeid));
                    }
                }
            }
        }
    }
}