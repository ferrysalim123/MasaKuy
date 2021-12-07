package id.ac.umn.masakuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddComment extends AppCompatActivity {

    EditText textInputEditTextComment;
    AppCompatButton btnComment,  btnBack;
    int user_id = -1, recipeId = -1;
    String thisLocalhost = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        RecipeSource svRecipe = (RecipeSource) bundle.getSerializable("RecipeDetail");

        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        user_id = sh.getInt("ThisUserId", -1);
        recipeId = svRecipe.getRecipeId();
        thisLocalhost = sh.getString("ThisLocalhost","");

        textInputEditTextComment = findViewById(R.id.comments);
        btnComment = findViewById(R.id.buttonPost);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(AddComment.this, RecipeDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("RecipeDetail", svRecipe);
                intent.putExtras(bundle);
                startActivity(intent);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
            }
        });

        btnComment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String comments;
                String userId = String.valueOf(user_id);
                String recipe_id = String.valueOf(recipeId);

                comments = String.valueOf(textInputEditTextComment.getText());

                Log.d("Info",comments);

                if (!comments.equals("")) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String[] field = new String[3];
                            field[0] = "userId";
                            field[1] = "comments";
                            field[2] = "recipe_id";
                            //Creating array for data
                            String[] data = new String[3];
                            data[0] = userId;
                            data[1] = comments;
                            data[2] = recipe_id;

                            PutData putData = new PutData(thisLocalhost + "postComment.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    String result = putData.getResult();
                                    if(result.equals("Comment Posted")){
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(AddComment.this, RecipeDetailActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("RecipeDetail", svRecipe);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                        finish();
                                    } else{
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "Kotak tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}