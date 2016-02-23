package com.example.ciclo.jumpingrace;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

/**
 * Created by AlbertoManuel on 22/02/2016.
 */
public class Missiles extends GameObject{
    private int score;
    private int speed;
    private Random rand = new Random();
    private Animation animation = new Animation();
    private Bitmap spritesheet;

    public Missiles (Bitmap res, int x, int y, int w, int h, int s, int numFrames){

        //posicion
        super.x=x;
        super.y=y;
        //tamaño
        width = w;
        height = h;
        //puntuacion
        score = s;
        //velocidad
        speed = 7 + (int) (rand.nextDouble()*score/30);
            //velocidad máxima
        if (speed >= 40){

            speed = 40;
        }

        Bitmap[] image = new Bitmap[numFrames];

        spritesheet = res;

        for(int i = 0; i < image.length; i++){

            image[i] = Bitmap.createBitmap(spritesheet, 0, i*height, width, height);
        }

        animation.setFrames(image);
        animation.setDelay(100-speed);
    }

    public void update(){

        x-=speed;
        animation.update();
    }

    public void draw(Canvas canvas){
        try{
            canvas.drawBitmap(animation.getImage(),x, y, null);
        }catch (Exception e){
        }
    }

    public int getWidth(){

        return width-10;
    }
}
