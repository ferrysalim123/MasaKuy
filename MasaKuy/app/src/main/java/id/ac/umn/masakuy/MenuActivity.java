package id.ac.umn.masakuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class MenuActivity extends AppCompatActivity {
    RecyclerView rvRecipeList;
    RecipeListAdapter mAdapter;
    LinkedList<RecipeSource> recipeList = new LinkedList<>();
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    String thisLocalhost = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        thisLocalhost = sh.getString("ThisLocalhost","");

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.recipeBtn);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.profileBtn:
                        goToProfile();
                        return true;
                    case R.id.recipeBtn:
                        return true;
                }
                return false;
            }
        });

        Intent intent = getIntent();

        recipeListValue();
        rvRecipeList = (RecyclerView) findViewById(R.id.recyclerView);
        mAdapter = new RecipeListAdapter(this, recipeList);
        rvRecipeList.setAdapter(mAdapter);
        rvRecipeList.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, AddRecipe.class);
                startActivity(intent);
            }
        });
    }

    public void recipeListValue() {
        String[] field = new String[0];
        String[] data = new String[0];

        PutData putData = new PutData(thisLocalhost + "readRecipeTable.php", "GET", field, data);

        String result = "";

        int dbrecipeid = 0;
        String dbrecipename = "";
        String dbdescription = "";
        int dbuserid = 0;
        String dbsteps = "";
        String dbingredients = "";
        String dbrecipepict = "";

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
                        dbrecipeid = arr.getJSONObject(i).getInt("recipe_id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        dbrecipename = arr.getJSONObject(i).getString("recipe_name");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        dbdescription = arr.getJSONObject(i).getString("description");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        dbuserid = arr.getJSONObject(i).getInt("user_id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        dbsteps = arr.getJSONObject(i).getString("steps");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        dbingredients = arr.getJSONObject(i).getString("ingredients");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        dbrecipepict = arr.getJSONObject(i).getString("recipe_pict");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    recipeList.add(new RecipeSource(dbrecipeid, dbrecipename, dbdescription, dbuserid, dbsteps, dbingredients, dbrecipepict));
                    //mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_menu, menu);

        for(int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spanString = new SpannableString(menu.getItem(i).getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spanString.length(), 0); //fix the color to white
            item.setTitle(spanString);
        }

        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    public void goToProfile() {
        Intent userProfileIntent = new Intent(MenuActivity.this, UserProfile.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("ThisUser", sv);
//        userProfileIntent.putExtras(bundle);
        startActivity(userProfileIntent);
        userProfileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public void logOut() {
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sh.edit();
        myEdit.putString("ThisUsername", "");
        myEdit.putString("ThisEmail", "");
        myEdit.putString("ThisPhoneNumber", "");
        myEdit.putString("ThisProfilePict", "");
        myEdit.putInt("ThisUserId", -1);
        myEdit.putBoolean("Login", false);
        myEdit.apply();

        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.logOutBtn:
                logOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}