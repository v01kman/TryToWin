package com.kwl.TryToWin;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

class Obstacle
{
    private float x;
    private float y;
    private int width;
    private int height;
    private int type;

    public Obstacle(float _x, float _y, int _width, int _height, int _type)
    {
        x = _x;
        y = _y;
        width = _width;
        height = _height;
        type = _type;
    }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setX(float _x)
    {
        x = _x;
    }

    public void setY(float _y)
    {
        y = _y;
    }
}

public class MyActivity extends Activity
{
    private int obstacles_num = 25;
    private int obstacles_current_num;

    private boolean doubleBackToExitPressedOnce = false;

    //common vars
    private RelativeLayout layout;
    private float background_offset;
    private Timer main_timer;
    private boolean isPlaying;
    private boolean isJumping;
    private boolean isJumpingUp;
    private boolean isJumpingDown;
    private boolean isSliding;
    private float global_speed;
    private float last_obstacle;
    private Obstacle[] obstacles;
    private ImageView[] obstacles_view;
    private float player_y;
    private float player_x;
    private float y_high;
    private float distance;

    //screen resolution
    private Display display;
    private int screenWidth;
    private int screenHeight;

    //images
    private ImageView player;
    private float current_player;
    private ImageView background1;
    private ImageView background2;
    private ImageView background3;
    private ImageView line1;
    private ImageView line2;
    private ImageView button_jump;
    private ImageView button_slide;
    private ImageView button_menu;
    private Bitmap[] t_obstacle;

    //text
    private TextView distance_txt;

    SharedPreferences results;


    private void makeMenu()
    {
        layout.removeAllViews();

        global_speed = 5.0f;

        Bitmap bitmap;
        Bitmap t_bitmap;

        background1 = new ImageView(this);
        background2 = new ImageView(this);
        background3 = new ImageView(this);
        try
        {
            InputStream ims = getAssets().open("background.png");
            bitmap = BitmapFactory.decodeStream(ims);
        }
        catch(IOException ex)
        {
            return;
        }
        t_bitmap = Bitmap.createBitmap(bitmap, 0, 0, 1000, screenHeight);
        background1.setImageBitmap(t_bitmap);
        background2.setImageBitmap(t_bitmap);
        background3.setImageBitmap(t_bitmap);
        background1.setX(0);
        background2.setX(1000);
        background3.setX(2000);
        background1.setY(0);
        background2.setY(0);
        background3.setY(0);
        layout.addView(background1);
        layout.addView(background2);
        layout.addView(background3);

        //player
        player = new ImageView(this);
        final Bitmap[] t_player = new Bitmap[6];
        try
        {
            InputStream ims = getAssets().open("tiles.png");
            bitmap = BitmapFactory.decodeStream(ims);
        }
        catch(IOException ex)
        {
            return;
        }
        /*t_player[0] = Bitmap.createBitmap(bitmap, 0, 0, 128, 256);
        t_player[1] = Bitmap.createBitmap(bitmap, 128, 0, 128, 256);
        t_player[2] = Bitmap.createBitmap(bitmap, 256, 0, 128, 256);
        t_player[3] = Bitmap.createBitmap(bitmap, 384, 0, 128, 256);
        t_player[4] = Bitmap.createBitmap(bitmap, 512, 0, 128, 256);
        t_player[5] = Bitmap.createBitmap(bitmap, 640, 0, 128, 256);*/
        t_bitmap = Bitmap.createBitmap(bitmap, 0, 0, 128, 256);
        t_player[0] = Bitmap.createScaledBitmap(t_bitmap, 64, 128, false);
        t_bitmap = Bitmap.createBitmap(bitmap, 128, 0, 128, 256);
        t_player[1] = Bitmap.createScaledBitmap(t_bitmap, 64, 128, false);
        t_bitmap = Bitmap.createBitmap(bitmap, 256, 0, 128, 256);
        t_player[2] = Bitmap.createScaledBitmap(t_bitmap, 64, 128, false);
        t_bitmap = Bitmap.createBitmap(bitmap, 384, 0, 128, 256);
        t_player[3] = Bitmap.createScaledBitmap(t_bitmap, 64, 128, false);
        t_bitmap = Bitmap.createBitmap(bitmap, 512, 0, 128, 256);
        t_player[4] = Bitmap.createScaledBitmap(t_bitmap, 64, 128, false);
        t_bitmap = Bitmap.createBitmap(bitmap, 640, 0, 128, 256);
        t_player[5] = Bitmap.createScaledBitmap(t_bitmap, 64, 128, false);
        current_player = 0.0f;
        player.setImageBitmap(t_player[0]);
        player_x = 100f;
        player.setX(player_x);
        player_y = screenHeight / 2 - 52;
        player.setY(player_y);
        layout.addView(player);

        ImageView button_play = new ImageView(this);
        t_bitmap = Bitmap.createBitmap(bitmap, 768, 384, 256, 256);
        t_bitmap = Bitmap.createScaledBitmap(t_bitmap, 192, 192, false);
        button_play.setImageBitmap(t_bitmap);
        button_play.setX(screenWidth-192);
        button_play.setY(screenHeight-192);
        button_play.setOnClickListener(new Listener(6));
        layout.addView(button_play);

        ImageView button_scores = new ImageView(this);
        t_bitmap = Bitmap.createBitmap(bitmap, 256, 640, 256, 256);
        t_bitmap = Bitmap.createScaledBitmap(t_bitmap, 192, 192, false);
        button_scores.setImageBitmap(t_bitmap);
        button_scores.setX(0);
        button_scores.setY(screenHeight-192);
        button_scores.setOnClickListener(new Listener(5));
        layout.addView(button_scores);

        line1 = new ImageView(this);
        t_bitmap = Bitmap.createBitmap(bitmap, 0, 1018, 1024, 6);
        line1.setImageBitmap(t_bitmap);
        line1.setX(0);
        line1.setY(screenHeight/2+74);
        layout.addView(line1);

        line2 = new ImageView(this);
        t_bitmap = Bitmap.createBitmap(bitmap, 0, 1018, 1024, 6);
        line2.setImageBitmap(t_bitmap);
        line2.setX(1000);
        line2.setY(screenHeight/2+74);
        layout.addView(line2);

        main_timer.cancel();
        main_timer = new Timer();
        main_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                MyActivity.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        //move background
                        background1.setX(background1.getX() - global_speed);
                        background2.setX(background2.getX() - global_speed);
                        background3.setX(background3.getX() - global_speed);
                        if(background1.getX() < -1000)
                        {
                            background1.setX(background3.getX()+1000);
                        }
                        if(background2.getX() < -1000)
                        {
                            background2.setX(background1.getX()+1000);
                        }
                        if(background3.getX() < -1000)
                        {
                            background3.setX(background2.getX()+1000);
                        }
                        //-------------------------------------

                        //change player tile
                        current_player += 0.2f;
                        if (current_player > 5)
                        {
                            current_player = 0;
                        }
                        player.setImageBitmap(t_player[Math.round(current_player - current_player % 1)]);
                    }
                });
            }
        }, 0, 16);
    }

    private void makeScores()
    {
        layout.removeAllViews();

        main_timer.cancel();
        main_timer = new Timer();

        Bitmap bitmap;
        Bitmap t_bitmap;

        background1 = new ImageView(this);
        background2 = new ImageView(this);
        background3 = new ImageView(this);
        try
        {
            InputStream ims = getAssets().open("background.png");
            bitmap = BitmapFactory.decodeStream(ims);
        }
        catch(IOException ex)
        {
            return;
        }
        t_bitmap = Bitmap.createBitmap(bitmap, 0, 0, 1000, screenHeight);
        background1.setImageBitmap(t_bitmap);
        background2.setImageBitmap(t_bitmap);
        background3.setImageBitmap(t_bitmap);
        background1.setX(0);
        background2.setX(1000);
        background3.setX(2000);
        background1.setY(0);
        background2.setY(0);
        background3.setY(0);
        layout.addView(background1);
        layout.addView(background2);
        layout.addView(background3);

        try
        {
            InputStream ims = getAssets().open("tiles.png");
            bitmap = BitmapFactory.decodeStream(ims);
        }
        catch(IOException ex)
        {
            return;
        }

        button_menu = new ImageView(this);
        t_bitmap = Bitmap.createBitmap(bitmap, 0, 640, 256, 256);
        t_bitmap = Bitmap.createScaledBitmap(t_bitmap, 192, 192, false);
        button_menu.setImageBitmap(t_bitmap);
        button_menu.setX(screenWidth-192);
        button_menu.setY(screenHeight-192);
        button_menu.setOnClickListener(new Listener(4));
        layout.addView(button_menu);

        Typeface type = Typeface.createFromAsset(getAssets(), "game_font.ttf");
        TextView stat1 = new TextView(this);
        stat1.setTypeface(type);
        stat1.setTextSize(20);
        stat1.setTextColor(Color.BLACK);
        stat1.setWidth(screenWidth);
        stat1.setHeight(75);
        stat1.setX(0);
        stat1.setY(screenHeight/2 - 75*2);
        stat1.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        layout.addView(stat1);

        TextView stat2 = new TextView(this);
        stat2.setTypeface(type);
        stat2.setTextSize(20);
        stat2.setTextColor(Color.BLACK);
        stat2.setWidth(screenWidth);
        stat2.setHeight(75);
        stat2.setX(0);
        stat2.setY(screenHeight/2 - 75);
        stat2.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        layout.addView(stat2);

        TextView stat3 = new TextView(this);
        stat3.setTypeface(type);
        stat3.setTextSize(20);
        stat3.setTextColor(Color.BLACK);
        stat3.setWidth(screenWidth);
        stat3.setHeight(75);
        stat3.setX(0);
        stat3.setY(screenHeight/2);
        stat3.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        layout.addView(stat3);

        TextView stat4 = new TextView(this);
        stat4.setTypeface(type);
        stat4.setTextSize(20);
        stat4.setTextColor(Color.BLACK);
        stat4.setWidth(screenWidth);
        stat4.setHeight(75);
        stat4.setX(0);
        stat4.setY(screenHeight/2 + 75);
        stat4.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        layout.addView(stat4);

        TextView stat5 = new TextView(this);
        stat5.setTypeface(type);
        stat5.setTextSize(20);
        stat5.setTextColor(Color.BLACK);
        stat5.setWidth(screenWidth);
        stat5.setHeight(75);
        stat5.setX(0);
        stat5.setY(screenHeight/2 + 75*2);
        stat5.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        layout.addView(stat5);

        if (results.contains("attempt1"))
        {
            String t = results.getString("attempt1", "");
            stat1.setText("1.  "+t);
        }
        if (results.contains("attempt2"))
        {
            String t = results.getString("attempt2", "");
            stat2.setText("2.  "+t);
        }
        if (results.contains("attempt3"))
        {
            String t = results.getString("attempt3", "");
            stat3.setText("3.  "+t);
        }
        if (results.contains("attempt4"))
        {
            String t = results.getString("attempt4", "");
            stat4.setText("4.  "+t);
        }
        if (results.contains("attempt5"))
        {
            String t = results.getString("attempt5", "");
            stat5.setText("5.  "+t);
        }
    }

    private void makeGame()
    {
        layout.removeAllViews();

        global_speed = 5.0f;
        obstacles = new Obstacle[obstacles_num];
        obstacles_view = new ImageView[obstacles_num];
        last_obstacle = screenWidth;
        obstacles_current_num = 0;
        isJumping = false;
        isSliding = false;
        isJumpingUp = false;
        isJumpingDown = false;
        distance = 0f;

        Bitmap bitmap;
        Bitmap t_bitmap;

        background1 = new ImageView(this);
        background2 = new ImageView(this);
        background3 = new ImageView(this);
        try
        {
            InputStream ims = getAssets().open("background.png");
            bitmap = BitmapFactory.decodeStream(ims);
        }
        catch(IOException ex)
        {
            return;
        }
        t_bitmap = Bitmap.createBitmap(bitmap, 0, 0, 1000, screenHeight);
        background1.setImageBitmap(t_bitmap);
        background2.setImageBitmap(t_bitmap);
        background3.setImageBitmap(t_bitmap);
        background1.setX(0);
        background2.setX(1000);
        background3.setX(2000);
        background1.setY(0);
        background2.setY(0);
        background3.setY(0);
        layout.addView(background1);
        layout.addView(background2);
        layout.addView(background3);

        //player
        player = new ImageView(this);
        final Bitmap[] t_player = new Bitmap[6];
        try
        {
            InputStream ims = getAssets().open("tiles.png");
            bitmap = BitmapFactory.decodeStream(ims);
        }
        catch(IOException ex)
        {
            return;
        }
        /*t_player[0] = Bitmap.createBitmap(bitmap, 0, 0, 128, 256);
        t_player[1] = Bitmap.createBitmap(bitmap, 128, 0, 128, 256);
        t_player[2] = Bitmap.createBitmap(bitmap, 256, 0, 128, 256);
        t_player[3] = Bitmap.createBitmap(bitmap, 384, 0, 128, 256);
        t_player[4] = Bitmap.createBitmap(bitmap, 512, 0, 128, 256);
        t_player[5] = Bitmap.createBitmap(bitmap, 640, 0, 128, 256);*/
        t_bitmap = Bitmap.createBitmap(bitmap, 0, 0, 128, 256);
        t_player[0] = Bitmap.createScaledBitmap(t_bitmap, 64, 128, false);
        t_bitmap = Bitmap.createBitmap(bitmap, 128, 0, 128, 256);
        t_player[1] = Bitmap.createScaledBitmap(t_bitmap, 64, 128, false);
        t_bitmap = Bitmap.createBitmap(bitmap, 256, 0, 128, 256);
        t_player[2] = Bitmap.createScaledBitmap(t_bitmap, 64, 128, false);
        t_bitmap = Bitmap.createBitmap(bitmap, 384, 0, 128, 256);
        t_player[3] = Bitmap.createScaledBitmap(t_bitmap, 64, 128, false);
        t_bitmap = Bitmap.createBitmap(bitmap, 512, 0, 128, 256);
        t_player[4] = Bitmap.createScaledBitmap(t_bitmap, 64, 128, false);
        t_bitmap = Bitmap.createBitmap(bitmap, 640, 0, 128, 256);
        t_player[5] = Bitmap.createScaledBitmap(t_bitmap, 64, 128, false);
        current_player = 0.0f;
        player.setImageBitmap(t_player[0]);
        player_x = 100f;
        player.setX(player_x);
        player_y = screenHeight / 2 - 52;
        player.setY(player_y);
        layout.addView(player);

        button_jump = new ImageView(this);
        t_bitmap = Bitmap.createBitmap(bitmap, 0, 384, 256, 256);
        t_bitmap = Bitmap.createScaledBitmap(t_bitmap, 192, 192, false);
        button_jump.setImageBitmap(t_bitmap);
        button_jump.setX(screenWidth - 192);
        button_jump.setY(screenHeight - 192);
        button_jump.setOnClickListener(new Listener(1));
        layout.addView(button_jump);

        button_slide = new ImageView(this);
        t_bitmap = Bitmap.createBitmap(bitmap, 256, 384, 256, 256);
        t_bitmap = Bitmap.createScaledBitmap(t_bitmap, 192, 192, false);
        button_slide.setImageBitmap(t_bitmap);
        button_slide.setX(0);
        button_slide.setY(screenHeight - 192);
        button_slide.setOnClickListener(new Listener(2));
        layout.addView(button_slide);

        button_menu = new ImageView(this);
        t_bitmap = Bitmap.createBitmap(bitmap, 0, 640, 256, 256);
        t_bitmap = Bitmap.createScaledBitmap(t_bitmap, 192, 192, false);
        button_menu.setImageBitmap(t_bitmap);
        button_menu.setX(screenWidth-192);
        button_menu.setY(0);
        button_menu.setOnClickListener(new Listener(4));
        layout.addView(button_menu);

        line1 = new ImageView(this);
        t_bitmap = Bitmap.createBitmap(bitmap, 0, 1018, 1024, 6);
        line1.setImageBitmap(t_bitmap);
        line1.setX(0);
        line1.setY(screenHeight/2+74);
        layout.addView(line1);

        line2 = new ImageView(this);
        t_bitmap = Bitmap.createBitmap(bitmap, 0, 1018, 1024, 6);
        line2.setImageBitmap(t_bitmap);
        line2.setX(1000);
        line2.setY(screenHeight/2+74);
        layout.addView(line2);

        Typeface type = Typeface.createFromAsset(getAssets(), "game_font.ttf");
        distance_txt = new TextView(this);
        distance_txt.setText("Distance: 0");
        distance_txt.setTypeface(type);
        distance_txt.setTextSize(25);
        distance_txt.setTextColor(Color.BLACK);
        distance_txt.setWidth(screenWidth/3);
        distance_txt.setHeight(75);
        distance_txt.setX(0);
        distance_txt.setY(0);
        layout.addView(distance_txt);


        //obstacles
        t_obstacle = new Bitmap[1];
        try
        {
            InputStream ims = getAssets().open("tiles.png");
            bitmap = BitmapFactory.decodeStream(ims);
        }
        catch(IOException ex)
        {
            return;
        }
        t_obstacle[0] = Bitmap.createBitmap(bitmap, 0, 256, 50, 50);

        obstacles = new Obstacle[obstacles_num];
        generateObstacle();

        main_timer.cancel();
        main_timer = new Timer();
        main_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                MyActivity.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        //move background
                        background1.setX(background1.getX() - global_speed);
                        background2.setX(background2.getX() - global_speed);
                        background3.setX(background3.getX() - global_speed);
                        if(background1.getX() < -1000)
                        {
                            background1.setX(background3.getX()+1000);
                        }
                        if(background2.getX() < -1000)
                        {
                            background2.setX(background1.getX()+1000);
                        }
                        if(background3.getX() < -1000)
                        {
                            background3.setX(background2.getX()+1000);
                        }
                        //-------------------------------------

                        for (int i = 0; i < obstacles_num; i++)
                        {
                            if (obstacles[i] != null)
                            {
                                obstacles[i].setX(obstacles[i].getX() - global_speed);
                                obstacles_view[i].setX(obstacles_view[i].getX() - global_speed);
                            }
                        }
                        last_obstacle -= global_speed;
                        if (last_obstacle < screenWidth-500)
                        {
                            generateObstacle();
                            if (obstacles_current_num != 0)
                            {
                                last_obstacle = obstacles[obstacles_current_num - 1].getX();
                            }
                            else
                            {
                                last_obstacle = obstacles[obstacles_num - 1].getX();
                            }
                        }

                        //change player tile
                        current_player += 0.2f;
                        if (current_player > 5)
                        {
                            current_player = 0;
                        }
                        if (!isJumping)
                        {
                            player.setImageBitmap(t_player[Math.round(current_player - current_player % 1)]);
                        }
                        else
                        {
                            player.setImageBitmap(t_player[5]);
                        }
                        //------------------

                        //draw jump
                        if (isJumping)
                        {
                            if (isJumpingUp)
                            {
                                player_y -= 7f;
                                player.setY(player_y);
                                if (player_y < y_high)
                                {
                                    isJumpingUp = false;
                                    isJumpingDown = true;
                                }
                            }

                            if (isJumpingDown)
                            {
                                player_y += 7f;
                                player.setY(player_y);
                                if (player_y > (screenHeight/2-52))
                                {
                                    player_y = screenHeight/2-52;
                                    player.setY(player_y);
                                    isJumpingDown = false;
                                    isJumping = false;
                                    current_player = 0;
                                }
                            }
                        }
                        //--------------------------------

                        //make common things
                        distance += global_speed/50;
                        distance_txt.setText("Distance: "+Math.round(distance));

                        checkCollision();
                        //----------------
                    }
                });
            }
        }, 0, 16);
    }

    private void makeGameOver()
    {
        //layout.removeAllViews();
        button_jump.setX(screenWidth);
        button_slide.setX(screenWidth);

        Bitmap bitmap;
        Bitmap t_bitmap;

        ImageView button_refresh = new ImageView(this);
        try
        {
            InputStream ims = getAssets().open("tiles.png");
            bitmap = BitmapFactory.decodeStream(ims);
        }
        catch(IOException ex)
        {
            return;
        }
        t_bitmap = Bitmap.createBitmap(bitmap, 512, 384, 256, 256);
        t_bitmap = Bitmap.createScaledBitmap(t_bitmap, 192, 192, false);
        button_refresh.setImageBitmap(t_bitmap);
        button_refresh.setX(screenWidth-192);
        button_refresh.setY(screenHeight-192);
        button_refresh.setOnClickListener(new Listener(3));
        layout.addView(button_refresh);

        button_menu = new ImageView(this);
        t_bitmap = Bitmap.createBitmap(bitmap, 0, 640, 256, 256);
        t_bitmap = Bitmap.createScaledBitmap(t_bitmap, 192, 192, false);
        button_menu.setImageBitmap(t_bitmap);
        button_menu.setX(0);
        button_menu.setY(screenHeight-192);
        button_menu.setOnClickListener(new Listener(4));
        layout.addView(button_menu);

        ImageView button_scores = new ImageView(this);
        t_bitmap = Bitmap.createBitmap(bitmap, 256, 640, 256, 256);
        t_bitmap = Bitmap.createScaledBitmap(t_bitmap, 192, 192, false);
        button_scores.setImageBitmap(t_bitmap);
        button_scores.setX(screenWidth/2-96);
        button_scores.setY(screenHeight-192);
        button_scores.setOnClickListener(new Listener(5));
        layout.addView(button_scores);

        //checking existing results and updating em if necessary
        //result sample: attempt1..5 -> distance
        String[] res = new String[5];
        boolean isOk = false;
        if (results.contains("attempt1"))
        {
            String t = results.getString("attempt1", "");
            res[0] = t;
        }
        else
        {
            SharedPreferences.Editor editor = results.edit();
            editor.putString("attempt1", Math.round(distance)+"");
            editor.apply();
            isOk = true;
            return;
        }
        if (results.contains("attempt2"))
        {
            String t = results.getString("attempt2", "");
            res[1] = t;
        }
        else
        {
            for (int i=0; i<2; i++)
            {
                int t = Math.round(Float.parseFloat(res[i]));
                if (Math.round(distance) >= t)
                {
                    for (int j=1; j>i; j--)
                    {
                        res[j] = res[j-1];
                    }
                    res[i] = String.valueOf(Math.round(distance));

                    SharedPreferences.Editor editor = results.edit();
                    editor.putString("attempt1", res[0]);
                    editor.apply();
                    editor.putString("attempt2", res[1]);
                    editor.apply();

                    break;
                }
            }
            isOk = true;
            return;
        }
        if (results.contains("attempt3"))
        {
            String t = results.getString("attempt3", "");
            res[2] = t;
        }
        else
        {
            for (int i=0; i<3; i++)
            {
                int t = Math.round(Float.parseFloat(res[i]));
                if (Math.round(distance) >= t)
                {
                    for (int j=2; j>i; j--)
                    {
                        res[j] = res[j-1];
                    }
                    res[i] = String.valueOf(Math.round(distance));

                    SharedPreferences.Editor editor = results.edit();
                    editor.putString("attempt1", res[0]);
                    editor.apply();
                    editor.putString("attempt2", res[1]);
                    editor.apply();
                    editor.putString("attempt3", res[2]);
                    editor.apply();

                    break;
                }
            }
            isOk = true;
            return;
        }
        if (results.contains("attempt4"))
        {
            String t = results.getString("attempt4", "");
            res[3] = t;
        }
        else
        {
            for (int i=0; i<4; i++)
            {
                int t = Math.round(Float.parseFloat(res[i]));
                if (Math.round(distance) >= t)
                {
                    for (int j=3; j>i; j--)
                    {
                        res[j] = res[j-1];
                    }
                    res[i] = String.valueOf(Math.round(distance));

                    SharedPreferences.Editor editor = results.edit();
                    editor.putString("attempt1", res[0]);
                    editor.apply();
                    editor.putString("attempt2", res[1]);
                    editor.apply();
                    editor.putString("attempt3", res[2]);
                    editor.apply();
                    editor.putString("attempt4", res[3]);
                    editor.apply();

                    break;
                }
            }
            isOk = true;
            return;
        }
        if (results.contains("attempt5"))
        {
            String t = results.getString("attempt5", "");
            res[4] = t;
        }
        else
        {
            for (int i=0; i<5; i++)
            {
                int t = Math.round(Float.parseFloat(res[i]));
                if (Math.round(distance) >= t)
                {
                    for (int j=4; j>i; j--)
                    {
                        res[j] = res[j-1];
                    }
                    res[i] = String.valueOf(Math.round(distance));

                    SharedPreferences.Editor editor = results.edit();
                    editor.putString("attempt1", res[0]);
                    editor.apply();
                    editor.putString("attempt2", res[1]);
                    editor.apply();
                    editor.putString("attempt3", res[2]);
                    editor.apply();
                    editor.putString("attempt4", res[3]);
                    editor.apply();
                    editor.putString("attempt5", res[4]);
                    editor.apply();

                    break;
                }
            }
            isOk = true;
            return;
        }

        if (!isOk)
        {
            for (int i=0; i<5; i++)
            {
                int t = Math.round(Float.parseFloat(res[i]));
                if (Math.round(distance) >= t)
                {
                    for (int j=4; j>i; j--)
                    {
                        res[j] = res[j-1];
                    }
                    res[i] = String.valueOf(Math.round(distance));

                    SharedPreferences.Editor editor = results.edit();
                    editor.putString("attempt1", res[0]);
                    editor.apply();
                    editor.putString("attempt2", res[1]);
                    editor.apply();
                    editor.putString("attempt3", res[2]);
                    editor.apply();
                    editor.putString("attempt4", res[3]);
                    editor.apply();
                    editor.putString("attempt5", res[4]);
                    editor.apply();

                    break;
                }
            }
        }
        //----------------------------------------------------
    }

    private void generateObstacle()
    {
        obstacles[obstacles_current_num] = new Obstacle(screenWidth, screenHeight/2+25, 50, 50, 1);
        obstacles_view[obstacles_current_num] = new ImageView(this);
        obstacles_view[obstacles_current_num].setImageBitmap(t_obstacle[0]);
        obstacles_view[obstacles_current_num].setX(obstacles[obstacles_current_num].getX());
        obstacles_view[obstacles_current_num].setY(obstacles[obstacles_current_num].getY());
        layout.addView(obstacles_view[obstacles_current_num]);
        obstacles_current_num++;
        if (obstacles_current_num > 24)
        {
            obstacles_current_num = 0;
        }
    }

    private void checkCollision()
    {
        boolean detected = false;

        for (int i=0; i<obstacles_num; i++)
        {
            if (obstacles[i] != null)
            {
                if ( (player_x+4 < obstacles[i].getX() + obstacles[i].getWidth()) && (player_x + 60 > obstacles[i].getX()) && (player_y < obstacles[i].getY() + obstacles[i].getHeight()) && (player_y + 120 > obstacles[i].getY()))
                {
                    detected = true;

                    if (isJumpingDown)
                    {
                        isJumping = false;
                        isJumpingDown = false;

                        player_y = obstacles[i].getY() - 120;
                        player.setY(player_y);
                    }
                    else
                    {
                        main_timer.cancel();
                        main_timer = new Timer();
                        layout.removeView(button_menu);
                        makeGameOver();
                    }
                }
            }
        }

        if (!detected && (player_y < screenHeight / 2 - 52) && !isJumping)
        {
            isJumping = true;
            isJumpingDown = true;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        layout = (RelativeLayout) findViewById(R.id.main);

        results = getSharedPreferences("results", Context.MODE_PRIVATE);

        main_timer = new Timer();

        //get screen resolution
        display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        makeMenu();

        /*SharedPreferences.Editor editor = results.edit();
        editor.putString("attempt1", "0");
        editor.apply();
        editor.putString("attempt2", "0");
        editor.apply();
        editor.putString("attempt3", "0");
        editor.apply();
        editor.putString("attempt4", "0");
        editor.apply();
        editor.putString("attempt5", "0");
        editor.apply();*/
    }

    @Override
    public void onBackPressed()
    {
        if (doubleBackToExitPressedOnce)
        {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable()
        {

            @Override
            public void run()
            {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public class Listener implements View.OnClickListener
    {
        private int id = 0;

        public Listener(int id)
        {
            this.id = id;
        }

        public void onClick(View view)
        {
            if (id == 1) // jump
            {
                if (!isJumping)
                {
                    isJumping = true;
                    isJumpingUp = true;
                    y_high = player_y - 150f;
                }
            }
            else if (id == 2) // slide
            {
                isSliding = true;
            }
            else if (id == 3) // refresh
            {
                makeGame();
            }
            else if (id == 4) // menu
            {
                makeMenu();
            }
            else if (id == 5) // scores
            {
                makeScores();
            }

            else if (id == 6) // play
            {
                makeGame();
            }
        }
    }
}
