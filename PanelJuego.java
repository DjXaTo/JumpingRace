package com.example.ciclo.jumpingrace;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Rect;
import java.util.ArrayList;
import java.util.Random;

//esto es un comentario para pushear

public class PanelJuego extends SurfaceView implements SurfaceHolder.Callback
{
    public static final int WIDTH = 856;
    public static final int HEIGHT = 480;
    public static final int MOVESPEED = -5;
    private long smokeStartTime;
    private long misilStartTime;
    private MainThread thread;
    private Background bg;
    private Player player;
    private ArrayList<Smokepuff> smoke;
    private ArrayList<Missiles> misil;
    private Random rand = new Random();

    public PanelJuego(Context context)
    {
        super(context);

        //add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);

        thread = new MainThread(getHolder(), this);

        //make PanelJuego focusable so it can handle events
        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;
        int counter = 0;
        while(retry && counter <1000)
        {
            counter++;
            try{
                thread.setRunning(false);
                thread.join();
                retry = false;

            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){

        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.grassbg1));
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.helicopter), 65, 25, 3);
        smoke = new ArrayList<Smokepuff>();
        misil = new ArrayList<Missiles>();

        smokeStartTime = System.nanoTime();
        misilStartTime = System.nanoTime();

        //we can safely start the game loop
        thread.setRunning(true);
        thread.start();

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction()==MotionEvent.ACTION_DOWN){
            if(!player.getPlaying())
            {
                player.setPlaying(true);
            }
            else
            {
                player.setUp(true);
            }
            return true;
        }
        if(event.getAction()==MotionEvent.ACTION_UP)
        {
            player.setUp(false);
            return true;
        }

        return super.onTouchEvent(event);
    }
    public void update() {
        if (player.getPlaying()) {
            bg.update();
            player.update();

            //add misil on timer
            long missileElapsed = (System.nanoTime()-misilStartTime)/1000000;
            if(missileElapsed >(2000 - player.getScore()/4)){

                System.out.println("making missile");
                //first missile always goes down the middle
                if(misil.size()==0)
                {
                    misil.add(new Missiles(BitmapFactory.decodeResource(getResources(),R.drawable.
                            missile),WIDTH + 10, HEIGHT/2, 45, 15, player.getScore(), 13));
                }
                else
                {

                    misil.add(new Missiles(BitmapFactory.decodeResource(getResources(),R.drawable.missile),
                            WIDTH+10, (int)(rand.nextDouble()*(HEIGHT)),45,15, player.getScore(),13));
                }

                //reset timer
                misilStartTime = System.nanoTime();
            }
            //loop through every missile and check collision and remove
            for(int i = 0; i<misil.size();i++)
            {
                //update missile
                misil.get(i).update();

                if(collision(misil.get(i),player))
                {
                    misil.remove(i);
                    player.setPlaying(false);
                    break;
                }
                //remove missile if it is way off the screen
                if(misil.get(i).getX()<-100)
                {
                    misil.remove(i);
                    break;
                }
            }

            long elapsed = (System.nanoTime() - smokeStartTime)/1000000;
            if (elapsed > 120){
                smoke.add(new Smokepuff(player.getX(), player.getY()+10));
                smokeStartTime = System.nanoTime();

            }

            for(int i = 0; i < smoke.size();i++){

                smoke.get(i).update();
                if (smoke.get(i).getX()<-10){

                    smoke.remove(i);
                }
            }
        }
    }

    public boolean collision(GameObject a, GameObject b)
    {
        if(Rect.intersects(a.getRectangle(),b.getRectangle()))
        {
            return true;
        }
        return false;
    }

    public void draw(Canvas canvas)
    {
        final float scaleFactorX = getWidth()/(WIDTH*1.f);
        final float scaleFactorY = getHeight()/(HEIGHT*1.f);
        if(canvas!=null) {
            final int savedState = canvas.save();

            canvas.scale(scaleFactorX, scaleFactorY);
            bg.draw(canvas);
            player.draw(canvas);

            for(Smokepuff sp : smoke){

                sp.draw(canvas);
            }

            for(Missiles m: misil)
            {
                m.draw(canvas);
            }

            canvas.restoreToCount(savedState);
        }
    }

}