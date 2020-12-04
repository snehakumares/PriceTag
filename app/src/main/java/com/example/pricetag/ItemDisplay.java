package com.example.pricetag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemDisplay extends AppCompatActivity {
    public static final String TAG = "TAG";
    Button buynow,wishlistbtn;
    TextView itemname;
    TextView itemprice;
    TextView itemorgpricedis;
    String itemnametext;
    String itempricetext;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    String userID;
    String itemorgpricedistext;
    //TextView itemdiscount;
    ProgressDialog prodiag;
    String url;
    String site;
    ViewPager viewPager;

    //ArrayList<Bitmap> imagesdisp1 = new ArrayList<>();
    ArrayList<String> imagesdisp = new ArrayList<>();
  /*  @Override
   public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(ItemDisplay.this, MainActivity.class));
        finish();

    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_display);
        Intent intent = getIntent();
        fstore=FirebaseFirestore.getInstance();
        fAuth=FirebaseAuth.getInstance();
        url = intent.getStringExtra("url");
        site = intent.getStringExtra("site");
        itemname = findViewById(R.id.itemname);
        itemprice = findViewById(R.id.itemprice);
        itemorgpricedis = findViewById(R.id.itemorgpricedis);
//        itemdiscount = findViewById(R.id.itemdis);
        Log.d("my",url);
        Log.d("my",site);
        wishlistbtn=findViewById(R.id.addwishlist);
        buynow = findViewById(R.id.buynow);

        wishlistbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            try{
                userID=fAuth.getCurrentUser().getUid();}
            catch (Exception e){
                userID="";
            }
            if(!userID.equals("")) {

                CollectionReference collection = fstore.collection(userID);
                collection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int found=0;
                        for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                            String a=documentSnapshot.getString("wishurl");
                            if(a.equals(url)){
                                Toast.makeText(ItemDisplay.this,"Already in wishlist",Toast.LENGTH_SHORT).show();
                                found=1;
                                break;
                            }
                        }
                        if(found==0){
                            DocumentReference collectionReference = fstore.collection(userID).document();
                            Map<String, Object> wishlist = new HashMap<>();
                            wishlist.put("wishurl", url);
                            wishlist.put("wishsite", site);
                            wishlist.put("wishid",collectionReference.getId());
                            collectionReference.set(wishlist);
                            Toast.makeText(ItemDisplay.this, "Added to wishlist", Toast.LENGTH_SHORT).show();
                        }
                    }

            });

            }
            else{
                Toast.makeText(ItemDisplay.this, "Log in to add", Toast.LENGTH_SHORT).show();
            }
            }

        });


        buynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        ItemDisplay.GetData data = new ItemDisplay.GetData();
        data.execute();
    }
    public void putData(){
        itemname.setText(itemnametext);
        itemprice.setText(itempricetext);
        itemorgpricedis.setText(itemorgpricedistext);
    }
    private class GetData extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            prodiag = new ProgressDialog(ItemDisplay.this);
            prodiag.setMessage("loading");
            prodiag.setIndeterminate(false);
            prodiag.show();
            //Toast.makeText(getApplicationContext(), "Best price you will get on Flipkart !!", Toast.LENGTH_LONG).show();
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            Document document;
            //   Document document2;
            if(site.equals("Flipkart")) {
                try {
                    document = Jsoup.connect(url).get();
                    try {
                        Elements name = document.select(".B_NuCI");
                        itemnametext=name.text();
                    }
                    catch (Exception e){}
                    try {
                        Elements price = document.select("._30jeq3._16Jk6d");
                        itempricetext=price.text();
                    }
                    catch (Exception e){}
                    String orgprice1="";
                    String offprice1="";
                    try{
                        Elements originalprice = document.select("._3I9_wc._2p6lqe");
                        orgprice1 = originalprice.text();
                        // itemorgprice.setText(originalprice.text());
                    }
                    catch (Exception e){}
                    try {
                        Elements off = document.select("._3Ay6Sb._31Dcoz");
                        Elements offprice = off.select("span");
                        offprice1 = offprice.text();
                        // itemdiscount.setText(offprice.text());
                    }
                    catch (Exception e){}
                    itemorgpricedistext=orgprice1+"   "+offprice1;
                    try {
                        Elements rating = document.select("._3LWZlK");
                        Log.d("my",rating.text().substring(0,3));
                    }
                    catch (Exception e){}
                    /*try {
                        Elements availoffers = document.select(".XUp0WS");
                        Elements availableoffers = availoffers.select("._16eBzU.col");
                        for (int i = 0; i < availableoffers.size(); i++) {
                            Elements offerdetails = availableoffers.get(i).getElementsByTag("span");
                            for (int j = 0; j < offerdetails.size(); j++) {
                                Log.d("my", offerdetails.get(j).text());
                            }

                        }
                    }
                    catch (Exception e){} */


                    Elements images = document.select(".q6DClP");
                    Log.d("my", String.valueOf(images.size()));
                    if(images.size()==0) {
                        //  imagesdisp.add("No image");
                     /*   Element images1 = document.select("._396cs4._2amPTt._3qGmMb._3exPp9").first();
                        String img1 = images1.attr("src");
                        Log.d("my", img1);
                        imagesdisp.add(img1);
                        Log.d("my", img1); */
                    }
                    else {
                        for (int i = 0; i < images.size(); i++) {
                            try {
                                String img = images.get(i).attr("style").substring(21);
                                img = img.substring(0, img.length() - 1);
                                if (!img.startsWith("//")) {
                                    img = img.substring(45);
                                    img = "https://rukminim1.flixcart.com/image/352/352/" + img;
                                    //Bitmap bmp = BitmapFactory.decodeStream(new URL(img).openStream());
                                    imagesdisp.add(img);
                                    //imagesdisp1.add(bmp);
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.d("my", "Connection Error");
                }

            }
            else if(site.equals("Snapdeal")){
                try {
                    document = Jsoup.connect(url).get();
                    try {
                        Elements name = document.select(".pdp-e-i-head");
                        Log.d("my", name.text());
                        itemnametext=name.text();
                    }
                    catch (Exception e){}
                    try {
                        Elements price = document.select(".payBlkBig");
                        Log.d("my",price.text());
                        itempricetext="₹"+price.text();
                    }catch (Exception e){}
                    String[] orgsplit={"",""};
                    String offprice1="";
                    try {
                        Elements originalprice = document.select(".pdpCutPrice");
                        Log.d("my",originalprice.text());
                        orgsplit = originalprice.text().split(" ");
                        //itemdiscount.setText(offprice.get(0).text());
                    }
                    catch (Exception e){}
                    try {
                        Elements off = document.select(".pdpDiscount");
                        Elements offprice = off.select("span");
                        Log.d("my",offprice.get(0).text());
                        offprice1=offprice.get(0).text();
                    }
                    catch (Exception e){}
                    itemorgpricedistext="₹"+orgsplit[2]+"    "+offprice1;
                    try{
                        Elements avgrating = document.select(".avrg-rating");
                        Log.d("my",avgrating.text().substring(1,4));}
                    catch (Exception e){}
                    try{
                        Elements ratingcount = document.select(".total-rating.showRatingTooltip");
                        Log.d("my",ratingcount.text());}
                    catch (Exception e){}
                    try {
                        Element imgview1 = document.getElementById("bx-pager-left-image-panel");
                        Elements imagesa = imgview1.select("a");
                        for (int i = 0; i < imagesa.size(); i++) {
                            Elements images = imagesa.get(i).select("img");
                            String img1 = images.get(0).attr("src");
                            String img2 = images.get(0).attr("lazySrc");
                            if (!img1.equals("")) {
                                //Bitmap bmp = BitmapFactory.decodeStream(new URL(img1).openStream());
                                imagesdisp.add(img1);
                                //imagesdisp1.add(bmp);
                            }
                            if (!img2.equals("")) {
                                //Bitmap bmp = BitmapFactory.decodeStream(new URL(img2).openStream());
                                //imagesdisp1.add(bmp);
                                imagesdisp.add(img2);
                            }
                        }
                    }
                    catch (Exception e){}
                    try{
                        Elements imgview2 = document.select(".cloudzoom");
                        String img3=imgview2.attr("src");
                        imagesdisp.add(img3);
                        //Bitmap bmp = BitmapFactory.decodeStream(new URL(img3).openStream());
                        //imagesdisp1.add(bmp);
                    }catch (Exception e){}
                } catch (Exception e) {
                    Log.d("my", "Connection Error");
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(ItemDisplay.this,imagesdisp);
            viewPager = findViewById(R.id.viewPager);
            viewPager.setAdapter(viewPagerAdapter);
            prodiag.dismiss();
            super.onPostExecute(aVoid);
            putData();
        }
    }
}