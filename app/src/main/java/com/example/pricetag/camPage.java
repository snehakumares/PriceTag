package com.example.pricetag;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pricetag.classifier.ImageClassifier;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class camPage extends AppCompatActivity {
    RecyclerView recyclerView;
    ProgressDialog prodiag;
    ArrayList<Bitmap> p_images = new ArrayList<>();
    ArrayList<String> p_name = new ArrayList<>();
    ArrayList<String> p_price = new ArrayList<>();
    ArrayList<String> p_rating = new ArrayList<>();
    ArrayList<String> p_link = new ArrayList<>();
    ArrayList<String> amaz_p_name = new ArrayList<>();
    ArrayList<String> amaz_p_price = new ArrayList<>();
    ArrayList<String> snap_p_name = new ArrayList<>();
    ArrayList<String> snap_p_price = new ArrayList<>();
    ArrayList<String> snap_p_link = new ArrayList<>();
    ArrayList<String> snap_p_image = new ArrayList<>();
    ArrayList<String> snap_p_rating = new ArrayList<>();
    ArrayList<String> shop_p_name = new ArrayList<>();
    ArrayList<String> shop_p_price = new ArrayList<>();
    String productName;
    List<String>predicitonsList;
    FloatingActionButton retake;
    int flag=0;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1000;
    private static final int CAMERA_REQEUST_CODE = 10001;
    public static final int RequestPermissionCode = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);
        EnableRuntimePermission();
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        retake=findViewById(R.id.startCamera2);
        retake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnableRuntimePermission();
                try {
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 7);
                }
                catch (Exception e){}

            }
        });
        fAuth= FirebaseAuth.getInstance();
        fStore= FirebaseFirestore.getInstance();
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
                    case R.id.Keyword_Search:
                        Intent a = new Intent(getApplicationContext(),Keyword_Search.class);
                        startActivity(a);
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.Image_Search:
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        ImageView imageView;
        final TextView txtRec=findViewById(R.id.textRec);
        imageView = findViewById(R.id.iv_capture);
        TextView text = findViewById(R.id.detectedImage);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7 && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Bitmap rotPh;
            imageView.setImageBitmap(photo);
            Matrix matrix=new Matrix();
            matrix.preRotate(90);
            rotPh=Bitmap.createBitmap(photo,0,0,photo.getWidth(),photo.getHeight(),matrix,true);



            FirebaseVisionImage firebaseVisionImage=FirebaseVisionImage.fromBitmap(rotPh);
           // FirebaseVision firebaseVision=FirebaseVision.getInstance();
            FirebaseVisionTextDetector firebaseVisionTextDetector=FirebaseVision.getInstance().getVisionTextDetector();
            Task<FirebaseVisionText> task=firebaseVisionTextDetector.detectInImage(firebaseVisionImage);

            task.addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                   List<FirebaseVisionText.Block>blockList=firebaseVisionText.getBlocks();
                   if(blockList.size()==0){
                       Toast.makeText(camPage.this, "No Text Detected", Toast.LENGTH_SHORT).show();
                   }
                   else{
                       for (FirebaseVisionText.Block block : firebaseVisionText.getBlocks()){
                           String s=block.getText();
                           txtRec.setText(s);
                       }
                   }

                }
            });
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d("Error: ",e.getMessage());
                }
            });
            ImageClassifier imageClassifier = null;
            try {
                imageClassifier = new ImageClassifier(this);
            } catch (IOException e) {
               System.out.println("error in object creation of classifier  "+e);
            }
            List<ImageClassifier.Recognition> predicitons = imageClassifier.recognizeImage(
                    photo, 0);
             predicitonsList = new ArrayList<>();
            for (ImageClassifier.Recognition recog : predicitons) {
                predicitonsList.add(recog.getName());
            }
            productName=predicitonsList.get(0);
            text.setText(predicitonsList.get(0));
            showDetail();

        }
            super.onActivityResult(requestCode, resultCode, data);
        }



    private boolean hasAllPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED)
                return false;
        }
        return true;
    }
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                Toast.makeText(this, "Camera Permission Required", Toast.LENGTH_SHORT).show();
            }

            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);

        startActivityForResult(cameraIntent, CAMERA_REQEUST_CODE);
    }
    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }




    public void EnableRuntimePermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 7);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(camPage.this,
                Manifest.permission.CAMERA)) {
            Toast.makeText(camPage.this,"CAMERA permission allows us to Access CAMERA app",     Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(camPage.this,new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] result) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (result.length > 0 && result[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(camPage.this, "Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();
                } else {
//                    Toast.makeText(camPage.this, "Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();
                }
                break;
        }

    }

    public void showDetail() {
        p_images = new ArrayList<>();
        p_name = new ArrayList<>();
        p_price = new ArrayList<>();
        p_rating = new ArrayList<>();
        p_link = new ArrayList<>();
        amaz_p_name = new ArrayList<>();
        amaz_p_price = new ArrayList<>();
        snap_p_name = new ArrayList<>();
        snap_p_price = new ArrayList<>();
        snap_p_link = new ArrayList<>();
        snap_p_image = new ArrayList<>();
        snap_p_rating = new ArrayList<>();
        shop_p_name = new ArrayList<>();
        shop_p_price = new ArrayList<>();
        camPage.GetData data = new camPage.GetData();
        data.execute();
    }
    public void putData(){
        ArrayList<Product> list = new ArrayList<>();
        int i=0,j=0;
        try {
            while (i < p_name.size() && j < snap_p_name.size()) {
                String name1 = (String) p_name.get(i);
                String price1 = (String) p_price.get(i);
                //  Bitmap image1 = p_images.get(i);
                String rating1 = (String) p_rating.get(i);
                String link1 = (String) p_link.get(i);
                price1=price1.substring(1);
                String pricesplit[]=price1.split(",");
                price1="";
                for(String a:pricesplit){
                    price1=price1+a;
                }
                String name2 = (String) snap_p_name.get(j);
                String price2 = (String) snap_p_price.get(j);
                //  String image2 = (String) snap_p_image.get(j);
                String rating2 = (String) snap_p_rating.get(j);
                String link2 = (String) snap_p_link.get(j);

                price2=price2.substring(4);
                pricesplit=price2.split(",");
                price2="";
                for(String a:pricesplit){
                    price2=price2+a;
                }
                if(Float.parseFloat(price1) < Float.parseFloat(price2)) {
                    list.add(new Product(name1,Float.parseFloat(rating1),Float.parseFloat(price1),"Flipkart",link1));
                    i++;
                }
                else {

                    list.add(new Product(name2,Float.parseFloat(rating2),Float.parseFloat(price2),"Snapdeal",link2));
                    j++;
                }
            }
        }
        catch (Exception e){}
        try {
            if (i < p_name.size()) {
                while (i < p_name.size()) {
                    String name1 = (String) p_name.get(i);
                    String price1 = (String) p_price.get(i);
                    //  Bitmap image1 = p_images.get(i);
                    String rating1 = (String) p_rating.get(i);
                    String link1 = (String) p_link.get(i);
                    price1 = price1.substring(1);
                    String pricesplit[] = price1.split(",");
                    price1 = "";
                    for (String a : pricesplit) {
                        price1 = price1 + a;
                    }
                    //   Bitmap image1 = p_images.get(i);
                    //    String link1 = (String) p_link.get(i);
                    list.add(new Product(name1, Float.parseFloat(rating1), Float.parseFloat(price1), "Flipkart", link1));
                    i++;

                }
            }
        }
        catch (Exception e){}
        try {
            if (j < snap_p_name.size()) {
                while (j < snap_p_name.size()) {
                    String name2 = (String) snap_p_name.get(j);
                    String price2 = (String) snap_p_price.get(j);
                    //  String image2 = (String) snap_p_image.get(i);
                    String rating2 = (String) snap_p_rating.get(j);
                    String link2 = (String) snap_p_link.get(j);
                    price2 = price2.substring(4);
                    String pricesplit[] = price2.split(",");
                    price2 = "";
                    for (String a : pricesplit) {
                        price2 = price2 + a;
                    }
                    //      String image2 = (String) snap_p_image.get(i);
                    //    String link2 = (String) snap_p_link.get(j);

                    list.add(new Product(name2, Float.parseFloat(rating2), Float.parseFloat(price2),/*image2,*/"Snapdeal", link2));
                    j++;
                }
            }
        }
        catch (Exception e){}

        Product[] listitems=new Product[list.size()];
        for(i=0;i<list.size();i++){
            listitems[i]=new Product(list.get(i).getTitle(),list.get(i).getRating(),list.get(i).getPrice(),/*null,*/list.get(i).getsite(),list.get(i).getlink());
        }
        recyclerView.setAdapter(new MyAdapter(listitems));
    }

    @SuppressLint("StaticFieldLeak")
    private class GetData extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            prodiag = new ProgressDialog(camPage.this);
            prodiag.setMessage("loading");
            prodiag.setIndeterminate(false);
            prodiag.show();
            //Toast.makeText(getApplicationContext(), "Best price you will get on Flipkart !!", Toast.LENGTH_LONG).show();
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            String[] productSplit = productName.split(" ");
            String productStringamaz = new String();
            String productStringflip = new String();
            int i = 0;
            for (String a : productSplit) {
                if (i == 0) {
                    productStringflip = a;
                    productStringamaz = a;
                    i++;
                } else {
                    productStringflip = productStringflip + "%20" + a;
                    productStringamaz = productStringamaz + "+" + a;
                }
            }
            //  String url = "https://www.flipkart.com/search?q=" + productStringflip + "&otracker=search&otracker1=search&marketplace=FLIPKART&as-show=on&as=off";
            String url = "https://www.flipkart.com/search?q=" + productStringflip + "&otracker=search&otracker1=search&marketplace=FLIPKART&as-show=on&as=off&sort=price_asc";
            // String urlamaz = "https://www.amazon.in/s?k="+productStringamaz+"&ref=nb_sb_noss";
            // String urlsnap = "https://www.snapdeal.com/search?keyword=" + productStringflip + "&santizedKeyword=&sort=rlvncy";
            String urlsnap = "https://www.snapdeal.com/search?keyword=" + productStringflip + "&santizedKeyword=&sort=plth";
            //   String urlshop = "https://www.shopclues.com/search?q=" + productStringflip + "&sc_z=4444&z=0&sort_by=score&sort_order=desc";
            //  String urlamaz="https://www.amazon.in/s?k="+productStringamaz+"&s=relevanceblender&qid=1605100588&ref=sr_st_relevanceblender";
            Document document;
            //   Document document2;
            try {
                document = Jsoup.connect(url).get();
                // Elements all = document.select("._3O0U0u");
                Elements all = document.select("._2kHMtA");
                for (i = 0; i < all.size(); i++) {
                    //   Elements name = all.get(i).select("._3wU53n"); //get name
                    Elements name = all.get(i).select("._4rR01T"); //get name
                    //    Elements price = all.get(i).select("._1vC4OE._2rQ-NK"); //Get price
                    Elements price = all.get(i).select("._30jeq3._1_WHN1"); //Get price
                    //    Elements rating = all.get(i).select(".hGSR34");
                    Elements rating = all.get(i).select("._3LWZlK");
                    //      String images = all.get(i).select("._1Nyybr ._30XEf0").attr("src");
                    Elements link = all.get(i).select("._1fQZEK");
                    String linksend = "https://www.flipkart.com" + link.attr("href");
                    p_link.add(linksend);
                    p_name.add(name.text());
                    p_price.add(price.text());
                    if (rating.text().equals("")) {
                        p_rating.add(("0"));
                    } else {
                        p_rating.add(rating.text());
                    }
                    //    p_image.add(images);
          /*          try{
                        Document document12 =Jsoup.connect(linksend).get();
                        Element images = document12.select(".q6DClP").first();
                        //for(int k=0;k<images.size();k++){
                         //   String img = images.get(i).attr("style").substring(21);
                        String img = images.attr("style").substring(21);
                            img = img.substring(0,img.length()-1);
                            Log.d("my",img);
                            if(!img.startsWith("\\")){
                                Bitmap bmp = BitmapFactory.decodeStream(new java.net.URL(img).openStream());
                                p_images.add(bmp);
                                break;
                            }
                      //  }
                    }
                    catch (Exception e){} */
                }


            } catch (Exception e) {
                Log.d("my", "flip1error");
            }

            try {
                document = Jsoup.connect(url).get();
                // Elements all = document.select("._3liAhj");
                Elements all = document.select("._4ddWXP");
                for (i = 0; i < all.size(); i++) {
                    // Elements name1 = all.get(i).select("._2cLu-l");
                    // Elements price1 = all.get(i).select("._1vC4OE"); //Get price
                    //  Elements rating1 = all.get(i).select(".hGSR34");
                    Elements name1 = all.get(i).select(".s1Q9rs");
                    Elements price1 = all.get(i).select("._30jeq3"); //Get price
                    Elements rating1 = all.get(i).select("._3LWZlK");
                    Elements link1 = all.get(i).select("._2rpwqI");
                    String linksend = "https://www.flipkart.com" + link1.attr("href");
                    p_link.add(linksend);
                    // Elements images1 = document.getElementsByClass("_1Nyybr _30XEf0");
                    //  Elements link1=document.select("._2cLu-l");
                    p_name.add(name1.text());
                    p_price.add(price1.text());
                    if (rating1.text().equals("")) {
                        p_rating.add(("0"));
                    } else {
                        p_rating.add(rating1.text());
                    }
                /*    try{
                        Document document12 =Jsoup.connect(linksend).get();
                        Elements images = document12.select(".q6DClP");
                        for(int k=0;k<images.size();k++){
                            String img = images.get(i).attr("style").substring(21);
                            img = img.substring(0,img.length()-1);
                            if(img.startsWith("\\")){
                                Log.d("my",img);
                            }
                            else{
                                Bitmap bmp = BitmapFactory.decodeStream(new java.net.URL(img).openStream());
                                p_images.add(bmp);
                                break;
                            }
                        }
                    }
                    catch (Exception e){} */

                }


            } catch (Exception e) {
                Log.d("my", "flip2error");
            }
               /* document2 = Jsoup.connect(urlamaz).get();
                Elements all=document2.getElementsByAttributeValueStarting("data-asin","^B0");
                try {
                    Elements name2 = document2.getElementsByClass("a-size-base-plus a-color-base a-text-normal");
                    Elements price2 = document2.getElementsByClass("a-price-whole");
                    for (i = 0; i < price2.size() && i < name2.size(); i++) {
                        amaz_p_name.add(name2.get(i).text());
                        amaz_p_price.add(price2.get(i).text());
                    }
                }
                catch (Exception e){

                } */
            /*    try {
                    Elements name3 = document2.getElementsByClass("a-size-medium a-color-base a-text-normal");
                    Elements price3 = document2.getElementsByClass("a-offscreen");
                    for (i = 0; i < price3.size() && i < name3.size(); i++) {
                        amaz_p_name.add(name3.get(i).text());
                        amaz_p_price.add(price3.get(i).text());
                    }
                }
                catch (Exception e){

                } */
            try {
                Document document3 = Jsoup.connect(urlsnap).get();
                Elements all = document3.select(".col-xs-6.favDp.product-tuple-listing.js-tuple");
                //  Elements namesnap = document3.select(".product-title");
                //  Elements pricesnap = document3.select(".lfloat product-price");
                // Elements linksnap = document3.select(".dp-widget-link.hashAdded");
                //    Elements imagesnap = document3.select(".product-image ");

                //  Elements ratingsnap = document3.select(".filled-stars");
                for (int j = 0; j < all.size(); j++) {
                    Elements namesnap = all.get(j).select(".product-title");
                    Elements pricesnap = all.get(j).select(".lfloat .product-price");
                    Elements linkstup = all.get(j).select(".product-tuple-image");
                    Elements linksnap = linkstup.select("a");
                    Elements ratingsnap = all.get(j).select(".filled-stars");
                    snap_p_name.add(namesnap.text());
                    snap_p_price.add(pricesnap.text());
                    snap_p_link.add(linksnap.attr("href"));
                    Float rating;
                    try {
                        rating = Float.parseFloat(ratingsnap.attr("style").substring(6, 9)) * 5 / 100;
                    } catch (Exception e) {
                        rating = Float.parseFloat("0");
                    }
                    snap_p_rating.add(Float.toString(rating));
                }

            } catch (Exception e) {
            }
               /* Document document4=Jsoup.connect(urlshop).get();
                try{
                    Elements divs=document4.getElementsByClass("column col3 search_blocks");
                    Elements priceshop=divs.select("p_price");
                    for(i=0;i<divs.size();i++){
                        String nameshtml = divs.get(i).html();
                        Document namedoc = Jsoup.parseBodyFragment(nameshtml);
                        Elements h2=namedoc.getElementsByTag("h2");
                        shop_p_name.add(h2.get(i).text());
                        shop_p_price.add(priceshop.get(i).text());
                    }
                }
                catch (Exception e){

                } */
            try {
                document = Jsoup.connect(url).get();
                // Elements all = document.select("._3O0U0u");
                Elements all = document.select("._1xHGtK._373qXS");
                for (i = 0; i < all.size(); i++) {
                    //   Elements name = all.get(i).select("._3wU53n"); //get name
                    Elements name = all.get(i).select(".IRpwTa"); //get name
                    //    Elements price = all.get(i).select("._1vC4OE._2rQ-NK"); //Get price
                    Elements price = all.get(i).select("._30jeq3"); //Get price
                    //    Elements rating = all.get(i).select(".hGSR34");
                    //      Elements rating = all.get(i).select("._3eWWd-");
                    //      String images = all.get(i).select("._1Nyybr ._30XEf0").attr("src");
                    Elements link = all.get(i).select("._2UzuFa");
                    String linksend = "https://www.flipkart.com" + link.attr("href");
                    p_link.add(linksend);
                    p_rating.add(("0"));
                    p_name.add(name.text());
                    p_price.add(price.text());
                   /* if(rating.text().equals("")){
                        p_rating.add(("0"));
                    }
                    else {
                        p_rating.add(rating.text());
                    }*/
                    //    p_image.add(images);

                    //      Log.d("my",p_image.get(i));
             /*       try{
                        Document document12 =Jsoup.connect(linksend).get();
                        Elements images = document12.select(".q6DClP");
                        for(int k=0;k<images.size();k++){
                            String img = images.get(i).attr("style").substring(21);
                            img = img.substring(0,img.length()-1);
                            Log.d("my",img);
                            if(!img.startsWith("\\")){
                                Bitmap bmp = BitmapFactory.decodeStream(new java.net.URL(img).openStream());
                                p_images.add(bmp);
                                break;
                            }
                        }
                    }
                    catch (Exception e){} */
                }

            } catch (Exception e) {
                Log.d("my", "flip3error");
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
