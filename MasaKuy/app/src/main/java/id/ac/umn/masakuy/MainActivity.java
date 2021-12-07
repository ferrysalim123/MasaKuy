package id.ac.umn.masakuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    public Button CheckButton;
    public Button CheckButton2;
    String dbname, dbemail, dbphonenumber, dbprofilepict;
    int dbuserid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CheckButton = findViewById(R.id.buttonNext);
        CheckButton2 = findViewById(R.id.buttonNext2);

        CheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegistrasi();
            }
        });

        CheckButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLogin();
            }
        });

        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sh.edit();
        myEdit.putString("ThisLocalhost", "http://192.168.0.124/MasaKuy/");
        myEdit.apply();

        Boolean login = sh.getBoolean("Login", false);
        dbname = sh.getString("ThisUsername", "");
        dbemail = sh.getString("ThisEmail", "");
        dbphonenumber = sh.getString("ThisPhoneNumber", "");
        dbprofilepict = sh.getString("ThisProfilePict", "");
        sh.getInt("ThisUserId", dbuserid);

        if (login){
            User thisUser = new User(dbuserid, dbname, dbemail, dbphonenumber, dbprofilepict);
            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void openRegistrasi(){
        Intent registrasi = new Intent(MainActivity.this, SignUp.class);
        startActivity(registrasi);
        overridePendingTransition(R.anim.stay, R.anim.stay);
    }

    public void openLogin(){
        Intent login = new Intent(MainActivity.this, Login.class);
        startActivity(login);
        overridePendingTransition(R.anim.stay, R.anim.stay);
    }
}