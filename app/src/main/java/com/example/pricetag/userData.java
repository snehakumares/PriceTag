package com.example.pricetag;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class userData extends AppCompatActivity {
    TextView fullName,email,phone;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    Button logoutbt;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);
        fullName=findViewById(R.id.profileName);
        phone=findViewById(R.id.userPhone);
        email=findViewById(R.id.userEmail);
        logoutbt=findViewById(R.id.logoubtn);
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        try {
            userID = fAuth.getCurrentUser().getUid();
        }
        catch (Exception e)
        {
            userID ="";
        }
       if(!userID.equals("")) {
           DocumentReference documentReference = fStore.collection("users").document(userID);
           documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
               @Override
               public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                   try {
                       phone.setText(documentSnapshot.getString("phoneno"));
                       fullName.setText(documentSnapshot.getString("fName"));
                       email.setText(documentSnapshot.getString("email"));
                   }
                   catch (Exception e){}
               }
           });
       }
       else{
           Intent c  = new Intent(getApplicationContext(),account.class);
           startActivity(c);
           overridePendingTransition(0,0);
           finish();
       }
        logoutbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fAuth.signOut();
                startActivity(new Intent(userData.this, account.class));
                finish();
            }
        });
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view);
        navigation.setSelectedItemId(R.id.My_Account);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Keyword_Search:
                        Intent b = new Intent(getApplicationContext(),Keyword_Search.class);
                        startActivity(b);
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.Image_Search:
                        Intent a = new Intent(getApplicationContext(),camPage.class);
                        startActivity(a);
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.Wishlist:
                        Intent c  = new Intent(getApplicationContext(),wishList.class);
                        startActivity(c);
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.My_Account:
                        return true;
                }
                return false;
            }
        });
    }

}