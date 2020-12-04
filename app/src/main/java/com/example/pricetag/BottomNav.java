package com.example.pricetag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;



import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class BottomNav extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        try {
            userID = fAuth.getCurrentUser().getUid();
        }
        catch (Exception e){
            userID = "";
        }
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view);
        navigation.setSelectedItemId(R.id.Image_Search);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Image_Search:
                        return true;
                    case R.id.Keyword_Search:
                        Intent a = new Intent(getApplicationContext(),Keyword_Search.class);
                        startActivity(a);
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.Wishlist:
                        Intent b = new Intent(getApplicationContext(),wishList.class);
                        startActivity(b);
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.My_Account:
                        if(!userID.equals("")){
                            Intent c  = new Intent(getApplicationContext(),userData.class);
                            startActivity(c);
                            overridePendingTransition(0,0);
                            finish();
                        }
                        else{
                            Intent c  = new Intent(getApplicationContext(),account.class);
                            startActivity(c);
                            overridePendingTransition(0,0);
                            finish();
                        }

                        return true;
                }
                return false;
            }
        });
    }
}


