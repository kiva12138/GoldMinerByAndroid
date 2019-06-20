package com.example.gamefinal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.HashMap;

import static java.lang.Math.pow;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder surfaceHolder;
    private Canvas canvas;
    private Paint paint;
    private Thread thread;
    private boolean runFlag;
    private int screenWidth;
    private int screenHeight;
    //private GameControl gameControl;
    private ScoreControl scoreControl;
    private Bitmap minerAction;
    private int minerActionControl;
    private int minerCount;
    private int minerWidth;
    private int minerHeight;
    private Bitmap background;
    private Bitmap topback;
    private Bitmap timeback;
    private Bitmap scoreback;
    private Bitmap hook;
    private int rotateAngle;
    private int rotateDirection;
    private Bitmap trigger;
    private boolean beTrigger;
    private boolean hookBacking;
    private int hookLength;
    private int hookPosX;
    private int hookPosY;
    private float hookWidth = 50;
    private float hookHeight = 100;
    private float hookDistanceToCenter = (float) pow(pow(hookHeight * 0.75, 2) + pow(hookWidth*0.5, 2), 0.5);
    //private float hookRadius = (float) (pow(hookHeight * hookHeight + hookWidth*hookWidth, 0.5)/2);
    private float hookX;
    private float hookY;
    private Treasure[] treasures; // Sum to 17
    private int caughtNo = -1;
    private float caughtDistanceToHookX;
    private float caughtDistanceToHookY;
    private TimeControl timeControl;
    private int hookRate = 5;
    private boolean gameStart = false;
    private Bitmap startBack;
    private boolean gameOver = false;
    private Bitmap gameover;
    private Bitmap congratulations;
    private Bitmap money;
    private int moneyWidth = 300;
    private int moneyY1 = 0;
    private MediaPlayer backPlayer;
    private SoundPool sound;
    HashMap<Integer,Integer> soundMap;

    public GameView(Context context) {
        super(context);
        this.surfaceHolder = this.getHolder();
        this.surfaceHolder.addCallback(this);
        this.paint = new Paint();
        this.screenHeight = getResources().getDisplayMetrics().heightPixels;
        this.screenWidth = getResources().getDisplayMetrics().widthPixels;

        backPlayer = MediaPlayer.create(context,R.raw.backmusic);
        backPlayer.start();
        backPlayer.setLooping(true);
        this.sound = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        this.sound.load(context, R.raw.bomb, 1);
        this.sound.load(context, R.raw.level, 1);
        this.sound.load(context, R.raw.laend, 1);
        this.sound.load(context, R.raw.largegold, 1);
        this.sound.load(context, R.raw.select, 1);
        soundMap = new HashMap<Integer,Integer>();
        soundMap.put(1,sound.load(context, R.raw.bomb, 1));
        soundMap.put(2,sound.load(context, R.raw.level, 1));
        soundMap.put(3,sound.load(context, R.raw.laend, 1));
        soundMap.put(4,sound.load(context, R.raw.largegold, 1));
        soundMap.put(5,sound.load(context, R.raw.select, 1));
        //boomSound.play(soundmap.get(1),1,1,0,0,1);


        startBack = BitmapFactory.decodeResource(this.getResources(), R.mipmap.start);
        startBack = Bitmap.createScaledBitmap(startBack, screenWidth+20, screenHeight+20, true);

        congratulations = BitmapFactory.decodeResource(this.getResources(), R.mipmap.congratulations);
        congratulations = Bitmap.createScaledBitmap(congratulations, screenWidth, screenHeight, true);

        gameover = BitmapFactory.decodeResource(this.getResources(), R.mipmap.gameover);
        gameover = Bitmap.createScaledBitmap(gameover, screenWidth, screenHeight, true);

        money = BitmapFactory.decodeResource(this.getResources(), R.mipmap.money);
        money = Bitmap.createScaledBitmap(this.money, moneyWidth, screenHeight, true);


        //this.gameControl = new GameControl();

        this.minerActionControl = 0;
        this.minerCount = 0;
        this.minerAction = BitmapFactory.decodeResource(this.getResources(), R.mipmap.mineraction);
        this.minerWidth = minerAction.getWidth()/4 - 10;
        this.minerHeight = minerAction.getHeight()/8 - 10;

        this.background = BitmapFactory.decodeResource(this.getResources(), R.mipmap.minebg1);
        this.background = Bitmap.createScaledBitmap(this.background, this.screenWidth, this.screenHeight, true);
        this.topback = BitmapFactory.decodeResource(this.getResources(), R.mipmap.topbg);
        this.topback = Bitmap.createScaledBitmap(this.topback, this.screenWidth, minerHeight*3/4, true);
        this.timeback = BitmapFactory.decodeResource(this.getResources(), R.mipmap.timeboard);
        this.timeback = Bitmap.createScaledBitmap(this.timeback, 200, 200, true);
        this.scoreback = BitmapFactory.decodeResource(this.getResources(), R.mipmap.scoreboard);
        this.scoreback = Bitmap.createScaledBitmap(this.scoreback, 200, 200, true);
        this.hook = BitmapFactory.decodeResource(this.getResources(), R.mipmap.hook);
        this.hook = Bitmap.createScaledBitmap(this.hook, (int) hookWidth, (int) hookHeight, true);

        /*this.rotateAngle = 0;
        this.rotateDirection = 1;*/

        this.trigger = BitmapFactory.decodeResource(this.getResources(), R.mipmap.trigger);
        this.trigger = Bitmap.createScaledBitmap(this.trigger, 200, 200, true);

        //this.beTrigger = false;
        //this.hookLength = 0;
        this.hookPosX = screenWidth/2 - 25;
        this.hookPosY = 250;
        //this.hookBacking = false;

        // 0-石头4 1-金块4 2-钻石2 3-精灵1 4-炸弹1 5-Skull2
        treasures = new Treasure[17];
        for(int i=0; i<2; i++){
            treasures[i] = new Treasure(0,1,10,
                    150,150,true);
            treasures[i].setBitmapResource(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.mipmap.stone1),
                    150, 150, true));
        }
        for(int i=2; i<4; i++){
            treasures[i] = new Treasure(0,1,10,
                    150,150,true);
            treasures[i].setBitmapResource(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.mipmap.stone2),
                    150, 150, true));
        }
        treasures[4] = new Treasure(1,2,100,
                150,150,true);
        treasures[4].setBitmapResource(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.mipmap.gold1),
                    150, 150, true));
        treasures[5] = new Treasure(1,2,100,
                150,150,true);
        treasures[5].setBitmapResource(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.mipmap.gold2),
                150, 150, true));
        treasures[6] = new Treasure(1,2,100,
                150,150,true);
        treasures[6].setBitmapResource(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.mipmap.gold3),
                150, 150, true));
        treasures[7] = new Treasure(1,2,100,
                150,150,true);
        treasures[7].setBitmapResource(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.mipmap.gold4),
                150, 150, true));
        treasures[8] = new Treasure(2,5,200,
                50,50,true);
        treasures[8].setBitmapResource(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.mipmap.diamond1),
                50, 50, true));
        treasures[9] = new Treasure(2,5,200,
                50,50,true);
        treasures[9].setBitmapResource(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.mipmap.diamond2),
                50, 50, true));
        treasures[10] = new Treasure(4,0,0,
                150,150,true);
        treasures[10].setBitmapResource(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.mipmap.boom1),
                150, 150, true));
        treasures[11] = new Treasure(5,3,10,
                150,150,true);
        treasures[11].setBitmapResource(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.mipmap.skull1),
                150, 150, true));
        treasures[12] = new Treasure(5,3,10,
                150,150,true);
        treasures[12].setBitmapResource(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.mipmap.skull2),
                150, 150, true));
        treasures[13] = new Treasure(3,5,300,
                50,50,true);
        treasures[13].setBitmapResource(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.mipmap.sprite1),
                50, 50, true));
        treasures[14] = new Treasure(3,5,300,
                50,50,true);
        treasures[14].setBitmapResource(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.mipmap.sprite2),
                50, 50, true));
        treasures[15] = new Treasure(3,5,300,
                50,50,true);
        treasures[15].setBitmapResource(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.mipmap.sprite3),
                50, 50, true));
        treasures[16] = new Treasure(3,5,300,
                50,50,true);
        treasures[16].setBitmapResource(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.mipmap.sprite4),
                50, 50, true));

        /*for(int i=0;i<17; i++){
            if(i>=14){
                treasures[i].setX(treasures[i-1].getX());
                treasures[i].setY(treasures[i-1].getY());
                treasures[i].setRadius(treasures[i-1].getRadius());
                treasures[i].setCenterX(treasures[i-1].getCenterX());
                treasures[i].setCenterY(treasures[i-1].getCenterY());
            }else{
                // left 50 remain right 200 remain
                // top 500 remain bottom 200 remain
                float tempx;
                float tempy;
                do {
                    tempx = (float) (Math.random() * (screenWidth - 250) + 50);
                    tempy = (float) (Math.random() * (screenHeight - 700) + 500);
                }while (tempx > screenWidth-400 && tempy > screenHeight/2-300 && tempy < screenHeight/2+200);
                treasures[i].setX(tempx);
                treasures[i].setY(tempy);
                treasures[i].calculateRadius();
                treasures[i].calculateCenter();
            }*/
            //timeControl = new TimeControl();
            //timeControl.setCurrentTime(5000);
            //scoreControl = new ScoreControl();
            dataInitiate();
        }

    private void dataInitiate(){
        this.gameStart = false;
        this.gameOver = false;
        this.hookRate = 5;
        this.caughtNo = -1;

        this.rotateAngle = 0;
        this.rotateDirection = 1;

        this.beTrigger = false;
        this.hookLength = 0;
        this.hookBacking = false;
        for(int i=0;i<17; i++){
            treasures[i].setAvailable(true);
            if(i>=14){
                treasures[i].setX(treasures[i-1].getX());
                treasures[i].setY(treasures[i-1].getY());
                treasures[i].setRadius(treasures[i-1].getRadius());
                treasures[i].setCenterX(treasures[i-1].getCenterX());
                treasures[i].setCenterY(treasures[i-1].getCenterY());
            }else{
                // left 50 remain right 200 remain
                // top 500 remain bottom 200 remain
                float tempx;
                float tempy;
                do {
                    tempx = (float) (Math.random() * (screenWidth - 250) + 50);
                    tempy = (float) (Math.random() * (screenHeight - 700) + 500);
                }while (tempx > screenWidth-400 && tempy > screenHeight/2-300 && tempy < screenHeight/2+200);
                treasures[i].setX(tempx);
                treasures[i].setY(tempy);
                treasures[i].calculateRadius();
                treasures[i].calculateCenter();
            }
            timeControl = new TimeControl();
            scoreControl = new ScoreControl();
            timeControl.setCurrentTime(8000);

            moneyY1 = 0;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.thread = new Thread(this);
        this.runFlag = true;
        this.thread.start();
        Log.i("Format", "Created");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("Format", width + " "+height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        this.runFlag = false;
        Log.i("Format", "Destroyed");
    }

    @Override
    public void run() {
        while (runFlag){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(gameStart){
                if(!gameOver){
                    timeControl.decreaseTime();
                    if(timeControl.getCurrentTime()<=0){
                        gameOver = true;
                    }
                    // Miner Animation Control
                    this.minerCount++;
                    if(minerCount >= 60){
                        minerCount = 0;
                    }
                    if(minerCount <= 30){
                        this.minerActionControl = 0;
                    }else {
                        this.minerActionControl = 1;
                    }
                    gameDraw();
                    crashDetect();
                }else{
                    gameOverDraw();
                }
            }else{
                gameStartDraw();
            }
        }
    }

    public void gameDraw(){
        canvas = surfaceHolder.lockCanvas();
        if(canvas == null){
            Log.i("Format", "NULL canvas");
            return;
        }

        // Clear
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        // draw background
        canvas.drawBitmap(this.background, 0, 0, paint);
        canvas.drawBitmap(this.topback,0, 0, paint);

        // Time Hint
        canvas.drawBitmap(timeback, 50, 0, paint);

        // Score Hint
        canvas.drawBitmap(scoreback, screenWidth-250, 20, paint);

        // Texts
        paint.setColor(Color.YELLOW);
        paint.setTextSize(35);
        canvas.drawText("沙漠矿工", 115, 90,  paint);
        paint.setColor(Color.RED);
        canvas.drawText(String.valueOf(timeControl.getCurrentTime()/100), 130, 180,  paint);
        paint.setColor(Color.YELLOW);
        paint.setTextSize(40);
        canvas.drawText(String.valueOf(scoreControl.getCurrentScore()), screenWidth-150, 75,  paint);
        paint.setColor(Color.RED);
        canvas.drawText(String.valueOf(scoreControl.getShootNum()), screenWidth-120, 155,  paint);

        // Hook
        if(beTrigger){
            hookX = (float) (hookPosX - (this.hookLength*Math.sin(rotateAngle*Math.PI/180)));
            hookY = (float)(hookPosY + (this.hookLength*Math.cos(rotateAngle*Math.PI/180)));
            if(!hookBacking) {
                this.hookLength += hookRate;
            }else{
                this.hookLength -= hookRate;
            }
            if(hookX <= 100
                || hookX >= screenWidth-100
                || hookY >= screenHeight-100){
                hookBacking = true;
            }
            if(this.hookLength <= 0){
                this.beTrigger = false;
                this.hookBacking = false;
                this.hookRate = 5;
                if(caughtNo >= 0){
                    this.scoreControl.addScore(treasures[caughtNo].getKind(), treasures[caughtNo].getValue());
                    this.treasures[caughtNo].setAvailable(false);
                }
                this.caughtNo = -1;
                if(this.scoreControl.getShootNum() == 12){
                    gameOver = true;
                }
            }
            canvas.rotate(rotateAngle, hookX, hookY);
            canvas.drawBitmap(this.hook, hookX, hookY, paint);
            canvas.rotate(-rotateAngle, hookX, hookY);
            paint.setColor(Color.GRAY);
            paint.setStrokeWidth(5);
            canvas.drawLine(hookPosX, hookPosY,hookX, hookY, paint);
            /*paint.setColor(Color.RED);
            canvas.drawRect((float) (hookX- (hookDistanceToCenter*Math.sin(rotateAngle*Math.PI/180))),
                    (float) (hookY + (hookDistanceToCenter*Math.cos(rotateAngle*Math.PI/180))),
                    (float) ((hookX- (hookDistanceToCenter*Math.sin(rotateAngle*Math.PI/180)))+hookWidth),
                    (float) (hookY + (hookDistanceToCenter*Math.cos(rotateAngle*Math.PI/180)))+10, paint);*/
        }else{
            canvas.rotate(rotateAngle, screenWidth/2, 250);
            canvas.drawBitmap(this.hook, hookPosX, hookPosY, paint);
            canvas.rotate(-rotateAngle, screenWidth/2, 250 );
            this.rotateAngle += rotateDirection;
            if(this.rotateAngle <= -70){
                this.rotateDirection = 1;
            }
            if(this.rotateAngle >= 70){
                this.rotateDirection = -1;
            }
        }

        // Trigger
        canvas.drawBitmap(this.trigger, screenWidth-250, screenHeight/2, paint);

        // draw Action
        Rect minerSrc = new Rect(minerActionControl*minerWidth,minerHeight*2,
                (minerActionControl+1)*minerWidth, minerHeight*3);
        Rect minerDst = new Rect(screenWidth/2-minerWidth/4,0,
                screenWidth/2 + minerWidth/2, minerHeight*3/4);
        canvas.drawBitmap(minerAction, minerSrc, minerDst, paint);

        // Treasures
        for(int i=0; i<13; i++){
            if(!treasures[i].isAvailable()){
                continue;
            }
            if(i == caughtNo){
                treasures[i].setX(hookX - caughtDistanceToHookX);
                treasures[i].setY(hookY - caughtDistanceToHookY);
            }
            canvas.drawBitmap(treasures[i].getBitmapResource(), treasures[i].getX(), treasures[i].getY(), paint);
        }

        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    public void gameStartDraw(){
        canvas = surfaceHolder.lockCanvas();
        if(canvas == null){
            Log.i("Format", "NULL canvas");
            return;
        }

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.drawBitmap(this.startBack, -10, -10, paint);
        canvas.drawBitmap(this.money, screenWidth-moneyWidth, moneyY1, paint);
        canvas.drawBitmap(this.money, screenWidth-moneyWidth, moneyY1+screenHeight, paint);
        moneyY1-=5;
        if(moneyY1 <= -screenHeight){
            moneyY1 = 0;
        }
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    public void gameOverDraw(){
        surfaceHolder.lockCanvas();
        if(canvas == null){
            Log.i("Format", "NULL canvas");
            return;
        }
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        if(this.scoreControl.getShootNum() < 12){
            canvas.drawBitmap(this.gameover, 0, 0, paint);
            paint.setColor(Color.RED);
        }else{
            canvas.drawBitmap(this.congratulations, 0, 0, paint);
            paint.setColor(Color.GREEN);
        }
        paint.setTextSize(60);
        canvas.drawText("你获得了"+this.scoreControl.getSettleScore()+"金币, 点击屏幕继续",
                screenWidth/2-400, screenHeight/2+400, paint);
        surfaceHolder.unlockCanvasAndPost(canvas);

    }

    public void crashDetect(){
        if(beTrigger){
            for(int i=0; i<13; i++){
                if(!treasures[i].isAvailable()){
                    continue;
                }
                Rect treasureRect;
                if(treasures[i].getKind() == 2){
                    treasureRect = new Rect((int)treasures[i].getX(), (int)treasures[i].getY(),
                            (int)treasures[i].getX()+treasures[i].getWidth(),
                            (int)treasures[i].getY()+treasures[i].getHeight());
                }else{
                    treasureRect = new Rect((int)treasures[i].getX() + 10, (int)treasures[i].getY() + 10,
                            (int)treasures[i].getX()+treasures[i].getWidth()-10,
                            (int)treasures[i].getY()+treasures[i].getHeight()-10);
                }
                Rect hookRect = new Rect((int) (hookX- (hookDistanceToCenter*Math.sin(rotateAngle*Math.PI/180))),
                        (int) (hookY + (hookDistanceToCenter*Math.cos(rotateAngle*Math.PI/180))),
                        (int) ((hookX- (hookDistanceToCenter*Math.sin(rotateAngle*Math.PI/180)))+hookWidth),
                        (int) (hookY + (hookDistanceToCenter*Math.cos(rotateAngle*Math.PI/180)))+10);
                if(treasureRect.intersect(hookRect)){
                    if(treasures[i].getKind() == 4){
                        sound.play(soundMap.get(1),1,1,0,0,1);
                        gameOver = true;;
                    }
                    if(treasures[i].getKind() != 4){
                        sound.play(soundMap.get(4),1,1,0,0,1);
                    }
                    hookBacking = true;
                    this.caughtDistanceToHookX = hookX - treasures[i].getX();
                    this.caughtDistanceToHookY = hookY - treasures[i].getY();
                    this.hookRate = treasures[i].getWeight();
                    this.caughtNo = i;
                    return;
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!gameStart && event.getAction() == MotionEvent.ACTION_DOWN){
            // X:340-760 Y:330-710
            if(event.getX()>340 && event.getX()<760 && event.getY()>330 && event.getY()<710){
                sound.play(soundMap.get(2),1,1,0,0,1);
                this.gameStart = true;
            }
            return true;
        }
        if(gameStart && gameOver && event.getAction() == MotionEvent.ACTION_DOWN){
            gameStart = false;
            sound.play(soundMap.get(2),1,1,0,0,1);
            dataInitiate();
        }
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(event.getX() > screenWidth-250 && event.getX() < screenWidth-50
                    && event.getY()>screenHeight/2 && event.getY() < screenHeight/2+200){
                // Trigger The Hook
                sound.play(soundMap.get(5),1,1,0,0,1);
                this.beTrigger = true;
            }
        }
        return true;
    }
}
