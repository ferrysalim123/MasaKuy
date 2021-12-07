package id.ac.umn.masakuy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.squareup.picasso.Picasso;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    private ImageView profilePhoto;
    private EditText etUserName;
    private EditText etUserEmail;
    private EditText etUserPhoneNumber;
    private FloatingActionButton changePhoto;
    BottomNavigationView bottomNavigationView;
    String thisLocalhost = "";

    int userID;

    private String profile_pict = "";

    private Uri filePath;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private static final int STORAGE_PERMISSION_CODE = 123;

    Button save, cancel;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        userID = sh.getInt("ThisUserId", -1);
        thisLocalhost = sh.getString("ThisLocalhost","");

        requestStoragePermission();

        etUserName = findViewById(R.id.etUserName);
        etUserEmail = findViewById(R.id.etUserEmail);
        etUserPhoneNumber = findViewById(R.id.etUserPhoneNumber);
        profilePhoto = findViewById(R.id.photoProfile);
        changePhoto = findViewById(R.id.changePhoto);

        save = findViewById(R.id.btn_save);
        cancel = findViewById(R.id.btn_cancel);
        progressDialog = new ProgressDialog(UserProfile.this);

        etUserName.setText(sh.getString("ThisUsername", ""));
        etUserEmail.setText(sh.getString("ThisEmail", ""));
        etUserPhoneNumber.setText(sh.getString("ThisPhoneNumber", ""));

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.profileBtn);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfile.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.profileBtn:
                        return true;
                    case R.id.recipeBtn:
                        goToRecipe();
                        return true;
                }
                return false;
            }
        });

        if(!sh.getString("ThisProfilePict", "").isEmpty() && sh.getString("ThisProfilePict", "") != "") {
            Picasso.with(getApplicationContext()) //The context of your activity
                    .load(sh.getString("ThisProfilePict", ""))
                    .into(profilePhoto);
        }

        changePhoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etUserName.getText().toString().trim();
                String email = etUserEmail.getText().toString().trim();
                String phone = etUserPhoneNumber.getText().toString().trim();
                String user_id = String.valueOf(userID);

                if(name.isEmpty() || email.isEmpty() || phone.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Kotak tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }else{
                    uploadMultipart(user_id);
                    SharedPreferences.Editor myEdit = sh.edit();
                    myEdit.putString("ThisUsername", name);
                    myEdit.putString("ThisEmail", email);
                    myEdit.putString("ThisPhoneNumber", phone);
                    myEdit.putString("ThisProfilePict", profile_pict);
                    myEdit.apply();

                    progressDialog.setTitle("Updating...");
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, thisLocalhost + "update.php",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError{
                            Map<String, String> updateParams = new HashMap<>();
                            updateParams.put("email", email);
                            updateParams.put("name", name);
                            updateParams.put("phone_number", phone);
                            updateParams.put("user_id", user_id);
                            updateParams.put("profile_pict", profile_pict);

                            return updateParams;
                        }
                    };
                    RequestQueue queue = Volley.newRequestQueue(UserProfile.this);
                    queue.add(stringRequest);
                    Intent intent = new Intent(UserProfile.this, MenuActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    public void goToRecipe() {
        Intent intent = new Intent(UserProfile.this, MenuActivity.class);
        startActivity(intent);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profilePhoto.setImageURI(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Data null", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadMultipart(String username) {
        //getting name for the image
        String name = username;

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
}