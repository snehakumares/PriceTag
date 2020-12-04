package com.example.pricetag;

public class Productwish {

    private String title;
    // private String shortdesc;
    private String rating;
    private String price;
    //  Bitmap image;
    private String image;
    private String site;
    private String link;
    private String id;
    public Productwish(String title/* ,String shortdesc */, String rating, String price ,String image,String site, String link,String id) {
        this.title = title;
        //    this.shortdesc = shortdesc;
        this.price = price;
        //   this.image = image;
        this.site = site;
        this.link = link;
        this.rating = rating;
        this.image = image;
        this.id = id;
    }

    public String getTitle() { return title; }

    //   public String getShortdesc() {
    //       return shortdesc;
    //     }

    public String getRating() {
        return rating;
    }

    public String getPrice() {
        return price;
    }

    public String getImage() { return image; }
    // public Bitmap getImage() { return image; }
    public String getsite() { return site; }
    public String getlink() { return link; }
    public String getid() { return id; }
}

