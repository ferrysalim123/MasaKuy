package id.ac.umn.masakuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SignUp extends AppCompatActivity {

    EditText textInputEditTextUsername, textInputEditTextEmail, textInputEditTextNomorTelpon, textInputEditTextPassword, textInputEditTextKonfirmPass;
    AppCompatButton buttonSignUp;
    AppCompatButton buttonBack;
    ProgressBar progressBar;
    String thisLocalhost = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        textInputEditTextUsername = findViewById(R.id.username);
        textInputEditTextEmail = findViewById(R.id.Email);
        textInputEditTextNomorTelpon = findViewById(R.id.NoTelp);
        textInputEditTextPassword = findViewById(R.id.password);
        textInputEditTextKonfirmPass = findViewById(R.id.konfirmPass);

        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonBack = findViewById(R.id.buttonBack2);

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

        buttonSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String name, email, phone_number,  password, konfirmpass;

                name = String.valueOf(textInputEditTextUsername.getText());
                email = String.valueOf(textInputEditTextEmail.getText());
                phone_number = String.valueOf(textInputEditTextNomorTelpon.getText());
                password = String.valueOf(textInputEditTextPassword.getText());
                konfirmpass = String.valueOf(textInputEditTextKonfirmPass.getText());

                Log.d("Info",name);

                if (!name.equals("") && !phone_number.equals("") && !email.equals("") && !password.equals("") && !konfirmpass.equals("")) {
                    if(password.equals(konfirmpass)){
                        progressBar.setVisibility(View.VISIBLE);
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //Starting Write and Read data with URL
                                //Creating array for parameters
                                String[] field = new String[5];
                                field[0] = "name";
                                field[1] = "email";
                                field[2] = "phone_number";
                                field[3] = "password";
                                field[4] = "konfirmpass";
                                //Creating array for data
                                String[] data = new String[5];
                                data[0] = name;
                                data[1] = email;
                                data[2] = phone_number;
                                data[3] = password;
                                data[4] = konfirmpass;

                                PutData putData = new PutData(thisLocalhost + "signup.php", "POST", field, data);
                                if (putData.startPut()) {
                                    if (putData.onComplete()) {
                                        progressBar.setVisibility(View.GONE);
                                        String result = putData.getResult();
                                        if(result.equals("Sign Up Success")){
                                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else{
                                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                //End Write and Read data with URL
                            }
                        });
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Konfirm password salah", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Kotak tidak boleh kosong", Toast.LENGTH_SHORT).show();
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