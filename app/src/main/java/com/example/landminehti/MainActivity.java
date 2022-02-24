package com.example.landminehti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static NavController navController;
    public static Toolbar toolbar;
    private FirebaseAuth mAuth;
    private FirebaseApp firebaseApp;
    public static final String LONGITUDE="longitude";
    public static final String LATITUDE="latitude";
    public static final String RESULT="Result";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set English
        Configuration config =getResources().getConfiguration();
        Locale locale = new Locale("en");
        Locale.setDefault(locale);
        config.locale = locale;
        getResources().updateConfiguration(config,
                getResources().getDisplayMetrics());

        firebaseApp=FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        navController = Navigation.findNavController(this, R.id.main_fragment);

        // toolbar
        toolbar =findViewById(R.id.main_tool);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                mAuth.signOut();
                MainActivity.navController.navigate(R.id.action_homeFragment_to_loginFragment);
                return true;
        }
        return false;
    }
}