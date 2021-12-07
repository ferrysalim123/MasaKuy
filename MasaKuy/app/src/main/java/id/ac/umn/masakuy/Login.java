package id.ac.umn.masakuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    EditText textInputEditTextEmail, textInputEditTextPassword;
    AppCompatButton buttonLogin;
    AppCompatButton buttonBack;
    ProgressBar progressBar;
    String thisLocalhost = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textInputEditTextEmail = findViewById(R.id.emailLogin);
        textInputEditTextPassword = findViewById(R.id.passwordLogin);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonBack = findViewById(R.id.buttonBack);
        progressBar = findViewById(R.id.progress);
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        thisLocalhost = sh.getString("ThisLocalhost","");

        buttonBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String email, password;

                email = String.valueOf(textInputEditTextEmail.getText());
                password = String.valueOf(textInputEditTextPassword.getText());

                if (!email.equals("") && !password.equals("")) {
                    progressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            String[] field = new String[2];
                            field[0] = "email";
                            field[1] = "password";

                            String[] data = new String[2];
                            data[0] = email;
                            data[1] = password;

                            PutData putData = new PutData(thisLocalhost + "login.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    progressBar.setVisibility(View.GONE);
                                    String result = putData.getResult();
                                    String loginMessage = "";

                                    JSONObject responseJSON = null;

                                    try {
                                        responseJSON = new JSONObject(result);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        loginMessage = responseJSON.getString("loginMessage");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if(loginMessage.equals("Login Success")){
                                        Toast.makeText(getApplicationContext(), loginMessage, Toast.LENGTH_SHORT).show();

                                        int dbuserid = 0;
                                        String dbname = "";
                                        String dbemail = "";
                                        String dbphonenumber = "";
                                        String dbprofilepict = "";

                                        try {
                                            dbuserid = responseJSON.getInt("user_id");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        try {
                                            dbname = responseJSON.getString("name");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        try {
                                            dbemail = responseJSON.getString("email");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        try {
                                            dbphonenumber = responseJSON.getString("phone_number");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        try {
                                            dbprofilepict = responseJSON.getString("profile_pict");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                                        SharedPreferences.Editor myEdit = sh.edit();
                                        myEdit.putString("ThisUsername", dbname);
                                        myEdit.putString("ThisEmail", dbemail);
                                        myEdit.putString("ThisPhoneNumber", dbphonenumber);
                                        myEdit.putString("ThisProfilePict", dbprofilepict);
                                        myEdit.putInt("ThisUserId", dbuserid);
                                        myEdit.putBoolean("Login", true);
                                        myEdit.apply();

                                        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                                        startActivity(intent);
                                        finish();

                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    } else{
                                        Toast.makeText(getApplicationContext(), loginMessage, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(0,0);
    }
}