package com.example.starsgallery.beans;

public class Star {
    private int id;
    private String name;
    private String img;
    private float star;
    private static int counter = 0;

    public Star(String name, String img, float star) {
        this.id     = ++counter;
        this.name   = name;
        this.img    = img;
        this.star   = star;
    }

    public int    getId()    { return id; }
    public String getName()  { return name; }
    public String getImg()   { return img; }
    public float  getStar()  { return star; }

    public void setName(String name)  { this.name = name; }
    public void setImg(String img)    { this.img  = img;  }
    public void setStar(float star)   { this.star = star; }
}