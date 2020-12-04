package com.example.pricetag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class wishList extends AppCompatActivity {
    ArrayList<Productwish> productList = new ArrayList<>();
    TextView textview;
    ProgressDialog prodiag;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    ArrayList<String> links = new ArrayList<>();
    ArrayList<String> sites = new ArrayList<>();
    ArrayList<String> noteid = new ArrayList<>();
    RecyclerView recyclerView;
    TextView notlogin;
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent a = new Intent(getApplicationContext(),camPage.class);
        startActivity(a);
        overridePendingTransition(0,0);
        finish();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);
        notlogin=findViewById(R.id.notlogin);
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        try {
            userID = fAuth.getCurrentUser().getUid();
        }
        catch (Exception e){
            userID = "";
        }
        if(!userID.equals("")) {
            notlogin.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            CollectionReference collectionReference = fStore.collection(userID);
            collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String url = documentSnapshot.getString("wishurl");
                        String site = documentSnapshot.getString("wishsite");
                        String id = documentSnapshot.getString("wishid");
                        links.add(url);
                        sites.add(site);
                        noteid.add(id);
                    }
                    wishList.GetData data = new wishList.GetData();
                    data.execute();
                }
            });
        }
        else{
            notlogin.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view);
        navigation.setSelectedItemId(R.id.Wishlist);
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
    public void putData(){
        Productwish[] listitems=new Productwish[productList.size()];
      /*  for(int i=0;i<productList.size();i++){
            listitems[i]=new Productwish(productList.get(i).getTitle(),productList.get(i).getRating(),productList.get(i).getPrice(),productList.get(i).getImage(),productList.get(i).getsite(),productList.get(i).getlink());
        }*/
        recyclerView.setAdapter(new MyAdapterwish(productList));
    }
    private class GetData extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            prodiag = new ProgressDialog(wishList.this);
            prodiag.setMessage("loading");
            prodiag.setIndeterminate(false);
            prodiag.show();
            //Toast.makeText(getApplicationContext(), "Best price you will get on Flipkart !!", Toast.LENGTH_LONG).show();
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            Document document;
            for (int i = 0; i < links.size(); i++) {
                String site = sites.get(i);
                String url = links.get(i);
                String id = noteid.get(i);
                if (site.equals("Flipkart")) {
                    try {
                        document = Jsoup.connect(url).get();
                        Elements name = document.select(".B_NuCI");
                        Elements price = document.select("._30jeq3._16Jk6d");
                        String image = "";
                        String ratingtext="0";
                        try{
                            Elements rating = document.select("._3LWZlK");
                            if(!rating.text().equals("")){
                                ratingtext=rating.text().substring(0, 3);
                            }
                        }catch (Exception e){}

                        try {
                            Elements images = document.select(".q6DClP");
                            if (images.size() != 0) {
                                for (int j = 0; j < images.size(); j++) {
                                    try {
                                        String img = images.get(j).attr("style").substring(21);
                                        img = img.substring(0, img.length() - 1);
                                        if (!img.startsWith("//")) {
                                            img = img.substring(45);
                                            img = "https://rukminim1.flixcart.com/image/352/352/" + img;
                                            image = img;
                                            break;
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        } catch (Exception e) {
                        }
                        productList.add(new Productwish(name.text(),ratingtext,price.text().substring(1),image,site,url,id));
                    } catch (Exception e) {
                    }
                }

                 else if (site.equals("Snapdeal")) {
                    try {
                        document = Jsoup.connect(url).get();
                            String itempricetext;
                            String image="";
                            Elements name = document.select(".pdp-e-i-head");
                            Elements price = document.select(".payBlkBig");
                            itempricetext = "₹" + price.text();
                            String ratingtext="0";
                            try {
                                Elements avgrating = document.select(".avrg-rating");
                                ratingtext=avgrating.text().substring(1, 4);
                            } catch (Exception e) {
                            }
                            try {
                                Element imgview1 = document.getElementById("bx-pager-left-image-panel");
                                Elements imagesa = imgview1.select("a");
                                for (int j = 0; j < imagesa.size(); j++) {
                                    Elements images = imagesa.get(j).select("img");
                                    String img1 = images.get(0).attr("src");
                                    String img2 = images.get(0).attr("lazySrc");
                                    if (!img1.equals("")) {
                                        image=img1;
                                        break;
                                    }
                                    if (!img2.equals("")) {
                                        image=img2;
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                            }
                            try {
                                Elements imgview2 = document.select(".cloudzoom");
                                String img3 = imgview2.attr("src");
                                image=img3;

                        } catch (Exception e) {
                        }
                        productList.add(new Productwish(name.text(),ratingtext,price.text(),image,site,url,id));
                    } catch (Exception e) {
                        Log.d("my", "Connection Error");
                    }
                }
        }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            prodiag.dismiss();
            super.onPostExecute(aVoid);
            putData();
        }
    }

}