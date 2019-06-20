package com.example.gamefinal;

import android.graphics.Bitmap;

import static java.lang.Math.pow;

public class Treasure {
    private int kind;// 0-石头 1-金块 2-钻石 3-精灵 4-炸弹 5-Skull
    private boolean available;
    private Bitmap bitmapResource;
    private int weight;
    private int value;
    private int width;
    private int height;
    private float x;
    private float y;
    private float radius;
    private float centerX;

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    private float centerY;

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }


    public Treasure(int kind, int weight, int value, int width, int height, boolean available){
        this.kind = kind;
        this.weight = weight;
        this.value = value;
        this.width = width;
        this.height = height;
        this.available = available;
    }

    public void calculateRadius(){
        this.radius = (float) (pow(width * width + height*height, 0.5)/2);
    }

    public void calculateCenter(){
        this.centerX = this.x + this.width/2;
        this.centerY = this.y + this.height/2;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Bitmap getBitmapResource() {
        return bitmapResource;
    }

    public void setBitmapResource(Bitmap bitmapResource) {
        this.bitmapResource = bitmapResource;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
