package id.ac.umn.masakuy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.IOException;
import java.util.UUID;

public class AddRecipe extends AppCompatActivity {

    EditText textInputEditTextRecipeName, textInputEditTextDescription, textInputEditTextSteps, textInputEditTextIngredients;
    ImageView imageViewRecipePict;
    AppCompatButton buttonPost;
    AppCompatButton buttonBack;
    ProgressBar progressBar;
    int userId;
    String recipe_pict = "";
    String thisLocalhost = "";

    private Uri filePath;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private static final int STORAGE_PERMISSION_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        userId = sh.getInt("ThisUserId", -1);
        thisLocalhost = sh.getString("ThisLocalhost","");

        requestStoragePermission();

        Intent intent = getIntent();
//        Bundle bundle = intent.getExtras();
//        User sv = (User) bundle.getSerializable("ThisUser");

        textInputEditTextRecipeName = findViewById(R.id.recipeName);
        textInputEditTextDescription = findViewById(R.id.description);
        textInputEditTextSteps = findViewById(R.id.steps);
        textInputEditTextIngredients = findViewById(R.id.ingredients);

        imageViewRecipePict = findViewById(R.id.recipePict);

        buttonPost = findViewById(R.id.buttonPost);
        buttonBack = findViewById(R.id.buttonBack2);

        progressBar = findViewById(R.id.progress);

        imageViewRecipePict.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(AddRecipe.this, MenuActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("ThisUser", sv);
//                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        buttonPost.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String recipe_name, description, steps, ingredients;
                String user_id = String.valueOf(userId);

                recipe_name = String.valueOf(textInputEditTextRecipeName.getText());
                description = String.valueOf(textInputEditTextDescription.getText());
                steps = String.valueOf(textInputEditTextSteps.getText());
                ingredients = String.valueOf(textInputEditTextIngredients.getText());

                Log.d("Info", recipe_name);

                uploadMultipart(recipe_name);

                if (!recipe_name.equals("") && !description.equals("") && !steps.equals("") && !ingredients.equals("")) {
                    progressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String[] field2 = new String[6];
                            field2[0] = "recipe_name";
                            field2[1] = "description";
                            field2[2] = "user_id";
                            field2[3] = "steps";
                            field2[4] = "ingredients";
                            field2[5] = "recipe_pict";
                            //Creating array for data
                            String[] data2 = new String[6];
                            data2[0] = recipe_name;
                            data2[1] = description;
                            data2[2] = user_id;
                            data2[3] = steps;
                            data2[4] = ingredients;
                            data2[5] = recipe_pict;

                            PutData putData2 = new PutData(thisLocalhost + "postRecipe.php", "POST", field2, data2);
                            if (putData2.startPut()) {
                                if (putData2.onComplete()) {
                                    progressBar.setVisibility(View.GONE);
                                    String result2 = putData2.getResult();
                                    if(result2.equals("Recipe Posted")){
                                        Toast.makeText(getApplicationContext(), result2, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(AddRecipe.this, MenuActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else{
                                        Toast.makeText(getApplicationContext(), result2, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            //End Write and Read data with URL
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "Kotak tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                imageViewRecipePict.setImageBitmap(bitmap);
                imageViewRecipePict.setImageURI(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Data null", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadMultipart(String recipe_name) {
        //getting name for the image
        String name = recipe_name;

        //getting the actual path of the image
        String path = getPath(filePath);

        Log.d("Path", path);

        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, thisLocalhost + "upload.php")
                    .addFileToUpload(path, "image") //Adding file
                    .addParameter("name", name) //Adding text parameter to the request
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload
        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(0,0);
    }
}