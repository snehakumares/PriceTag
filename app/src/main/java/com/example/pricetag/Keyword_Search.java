package com.example.pricetag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Locale;

public class Keyword_Search extends AppCompatActivity {
    RecyclerView recyclerView;
    ProgressDialog prodiag;
    String productName;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    EditText item;
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
        setContentView(R.layout.activity_keyword__search);
        item=findViewById(R.id.keyitem);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        item.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    showDetail();
                    return true;
                }
                return false;
            }
        });
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        try {
            userID = fAuth.getCurrentUser().getUid();
        }
        catch (Exception e){
            userID = "";
        }
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view);
        navigation.setSelectedItemId(R.id.Keyword_Search);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Keyword_Search:
                        return true;
                    case R.id.Image_Search:
                        Intent a = new Intent(getApplicationContext(),camPage.class);
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
    public void getSpeechInput(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault());
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,10);
        }
        else{
            Toast.makeText(this,"This feature is not supported in your device",Toast.LENGTH_SHORT).show();
        }
    }
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch(requestCode){
            case 10:
                if(resultCode == RESULT_OK && data!=null){
                    ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    item.setText(result.get(0));
                    showDetail();
                }
                break;
        }
    }
    public void showDetail() {
        p_images = new ArrayList<>();
        productName = String.valueOf(item.getText());
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
        GetData data = new GetData();
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

                    list.add(new Product(name2,Float.parseFloat(rating2),Float.parseFloat(price2),/*image2,*/"Snapdeal",link2));
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
       /* for (int i = 0; i < p_price.size() && i < p_name.size(); i++) {
           name =(String) p_name.get(i);
            price =(String) p_price.get(i);

            /*imgurl =(String) p_image.get(i);
            try {
                InputStream is = (InputStream) new URL(imgurl).getContent();
                Drawable d = Drawable.createFromStream(is, "src name");
                }
            catch (Exception e){
            }*/
        /*  }*/
      /*  for (int i = 0; i < amaz_p_price.size() && i < amaz_p_name.size(); i++) {
            name =(String) amaz_p_name.get(i);
            price =(String) amaz_p_price.get(i);

            /*imgurl =(String) p_image.get(i);
            try {
                InputStream is = (InputStream) new URL(imgurl).getContent();
                Drawable d = Drawable.createFromStream(is, "src name");
                }
            catch (Exception e){
            }*/
        /*  }*/
         /* for (int i = 0; i < snap_p_name.size() && i < snap_p_price.size(); i++) {
                name = (String) snap_p_name.get(i);
                price = (String) snap_p_price.get(i); */
        /*     }*/
           /* for (int i = 0; i < shop_p_name.size() && i < shop_p_price.size(); i++) {
                name = (String) shop_p_name.get(i);
                price = (String) shop_p_price.get(i);
            } */
    }

    @SuppressLint("StaticFieldLeak")
    private class GetData extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            prodiag = new ProgressDialog(Keyword_Search.this);
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
            int i=0;
            for (String a : productSplit){
                if(i==0){
                    productStringflip=a;
                    productStringamaz=a;
                    i++;
                }
                else {
                    productStringflip = productStringflip + "%20" + a;
                    productStringamaz=productStringamaz+"+"+a;
                }
            }
            //  String url = "https://www.flipkart.com/search?q=" + productStringflip + "&otracker=search&otracker1=search&marketplace=FLIPKART&as-show=on&as=off";
            String url= "https://www.flipkart.com/search?q=" + productStringflip + "&otracker=search&otracker1=search&marketplace=FLIPKART&as-show=on&as=off&sort=price_asc";
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
                for(i=0;i<all.size();i++) {
                    //   Elements name = all.get(i).select("._3wU53n"); //get name
                    Elements name = all.get(i).select("._4rR01T"); //get name
                    //    Elements price = all.get(i).select("._1vC4OE._2rQ-NK"); //Get price
                    Elements price = all.get(i).select("._30jeq3._1_WHN1"); //Get price
                    //    Elements rating = all.get(i).select(".hGSR34");
                    Elements rating = all.get(i).select("._3LWZlK");
                    //      String images = all.get(i).select("._1Nyybr ._30XEf0").attr("src");
                    Elements link = all.get(i).select("._1fQZEK");
                    String linksend = "https://www.flipkart.com"+link.attr("href");
                    p_link.add(linksend);
                    p_name.add(name.text());
                    p_price.add(price.text());
                    if(rating.text().equals("")){
                        p_rating.add(("0"));
                    }
                    else {
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


            }
            catch (Exception e){Log.d("my","flip1error");}

            try {
                document = Jsoup.connect(url).get();
                // Elements all = document.select("._3liAhj");
                Elements all = document.select("._4ddWXP");
                for(i=0;i<all.size();i++){
                    // Elements name1 = all.get(i).select("._2cLu-l");
                    // Elements price1 = all.get(i).select("._1vC4OE"); //Get price
                    //  Elements rating1 = all.get(i).select(".hGSR34");
                    Elements name1 = all.get(i).select(".s1Q9rs");
                    Elements price1 = all.get(i).select("._30jeq3"); //Get price
                    Elements rating1 = all.get(i).select("._3LWZlK");
                    Elements link1 = all.get(i).select("._2rpwqI");
                    String linksend = "https://www.flipkart.com"+link1.attr("href");
                    p_link.add(linksend);
                    // Elements images1 = document.getElementsByClass("_1Nyybr _30XEf0");
                    //  Elements link1=document.select("._2cLu-l");
                    p_name.add(name1.text());
                    p_price.add(price1.text());
                    if(rating1.text().equals("")){
                        p_rating.add(("0"));
                    }
                    else {
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


            }
            catch (Exception e){ Log.d("my","flip2error"); }
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
                Document document3=Jsoup.connect(urlsnap).get();
                Elements all = document3.select(".col-xs-6.favDp.product-tuple-listing.js-tuple");
                //  Elements namesnap = document3.select(".product-title");
                //  Elements pricesnap = document3.select(".lfloat product-price");
                // Elements linksnap = document3.select(".dp-widget-link.hashAdded");
                //    Elements imagesnap = document3.select(".product-image ");

                //  Elements ratingsnap = document3.select(".filled-stars");
                for(int j=0;j<all.size();j++) {
                    Elements namesnap = all.get(j).select(".product-title");
                    Elements pricesnap = all.get(j).select(".lfloat .product-price");
                    Elements linkstup = all.get(j).select(".product-tuple-image");
                    Elements linksnap = linkstup.select("a");
                    Elements ratingsnap = all.get(j).select(".filled-stars");
                    snap_p_name.add(namesnap.text());
                    snap_p_price.add(pricesnap.text());
                    snap_p_link.add(linksnap.attr("href"));
                    Float rating;
                    try{
                        rating=Float.parseFloat(ratingsnap.attr("style").substring(6, 9)) * 5 / 100;}
                    catch (Exception e){
                        rating = Float.parseFloat("0");
                    }
                    snap_p_rating.add(Float.toString(rating));
                }

            }
            catch (Exception e){}
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
                for(i=0;i<all.size();i++) {
                    //   Elements name = all.get(i).select("._3wU53n"); //get name
                    Elements name = all.get(i).select(".IRpwTa"); //get name
                    //    Elements price = all.get(i).select("._1vC4OE._2rQ-NK"); //Get price
                    Elements price = all.get(i).select("._30jeq3"); //Get price
                    //    Elements rating = all.get(i).select(".hGSR34");
                    //      Elements rating = all.get(i).select("._3eWWd-");
                    //      String images = all.get(i).select("._1Nyybr ._30XEf0").attr("src");
                    Elements link = all.get(i).select("._2UzuFa");
                    String linksend = "https://www.flipkart.com"+link.attr("href");
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

            }
            catch (Exception e){Log.d("my","flip3error");}

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
