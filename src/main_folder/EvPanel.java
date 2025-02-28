package main_folder;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import game_entities.Monster;
import game_entities.Player;
import tile_map.Tile_Manager;

public class EvPanel extends JPanel implements Runnable{
    public String game_State = "RUNTIME";
    public boolean insKiller = false;
    long killersRelaesedTime;
    BufferedImage hearth;
    public boolean flag = true;
    //Ekran değerleri
    public final int realTileSize = 32;
    public final double scaleCount = 1.8;
    public final int relativetileSize = (int)(realTileSize * scaleCount);
    public final int screenColCnt = 20;
    public final int screenRowCnt = 10;
    public final int screenWidth = screenColCnt*relativetileSize;
    public final int screenHeight = screenRowCnt*relativetileSize + 50;
    //TileMap değerleri
    public final int maxWorldCol = 32;
    public final int maxWorldRow = 11;
    public final int worldWidth = relativetileSize*maxWorldCol;
    public final int worldHeight = relativetileSize*maxWorldRow;

    public final int FPS = 60;
    Input_Handler keyHandler = new Input_Handler();
    Thread gameThread;
    public Player player = new Player(this,keyHandler);
    public ColliderManager cM = new ColliderManager(this);
    public Tile_Manager tm = new Tile_Manager(this);

    //monsters:
    public ArrayList<Monster> monsters = new ArrayList<>(); 
    public ArrayList<Thread> monsterThreads = new ArrayList<>();
    public final int DEFAULT_MONSTER_COUNT = 6;
    public int currentMonsterCount = 0;

    //Sound atama
    public SoundManager sm = new SoundManager();
    public SoundManager sm1 = new SoundManager();
    public SoundManager sm2 = new SoundManager();
    
    JLabel time;
    public int timeLeft = 200;
    long startingTime;

    public EvPanel(){
        try{
            hearth = Player.loadImage("Assets\\BombermanAnim\\Pixel_Heart.png");
        }catch(IOException e){e.printStackTrace();}
        keyHandler.tm = tm;
        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.GRAY);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);
        initializeMonsters();
        this.startThread();
        sm1.setFile(0);
        sm1.loop();
    }

    public void KILL_ALL_TYPE_OF(int type){
        for(int i = 0;i<monsters.size();i++){
            if(monsters.get(i).monsterMod == type)
                monsters.get(i).DEAD = true;
        }
    }

    public void initializeMonsters(){
        for(int i = 0;i<DEFAULT_MONSTER_COUNT; i++){
            addMonster(0);
        }
    }
    public void InstantiateSerailKillers(int killerCount){
        if(flag){
            flag = false;
            sm1.stop();
            sm2.setFile(4);
            sm2.play();
        }
        
        player.kapiAcildi = false;
        new Thread(new Runnable(){
            @Override 
            public void run(){
                try{
                    if(game_State.equals("RUNTIME")){
                        Thread.sleep(1000);
                        for(int i = 0;i<killerCount;i++){
                            addMonster(1); 
                            Thread.sleep(300);
                        }
                    }
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void addMonster(int mode){
        Monster mnstr = new Monster(mode, this);
        monsters.add(mnstr);
        Thread thread = new Thread(new Runnable() {
            
                @Override
                public void run(){
                    double drawInterval = 1000000000/FPS;
                    double nextTime = System.nanoTime() + drawInterval;
                    while(this != null){
                        mnstr.update();
                        double remainingTime = nextTime- System.nanoTime();
                        try {
                            if(remainingTime<0) remainingTime = 0;
                            Thread.sleep((long) remainingTime/1000000);
                            nextTime += drawInterval;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            
        });
        monsterThreads.add(thread);
        thread.start();
    }
    public void startThread(){
        gameThread = new Thread(this);
        gameThread.start();
        startingTime = System.currentTimeMillis()/1000;
    }
    @Override
    public void run() {
        if(game_State.equals("RUNTIME")){
            // fps display:
            //zaman ayarı:
            double drawInterval = 1000000000/FPS;
            double nextTime = System.nanoTime() + drawInterval;
            
            while(gameThread!=null){
                //veri güncelle yeni verilere göre haritayı çiz
                update();
                repaint();//calls paint comp.
                double remainingTime = nextTime- System.nanoTime();
                try {
                    if(remainingTime<0) remainingTime = 0;
                    Thread.sleep((long) remainingTime/1000000);//as milisec
                    nextTime += drawInterval;
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }        
        }
    }
    private void drawUI(Graphics2D g2d){
        
        
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("DIN",Font.BOLD,25));
            g2d.drawString("Time Left: ", relativetileSize-35, realTileSize/2+20);
            g2d.drawString("monster remains", 15*relativetileSize, realTileSize/2+20);
            g2d.setColor(Color.yellow);
            g2d.drawString(" "+timeLeft, relativetileSize+90, realTileSize/2+20);
            g2d.drawString(currentMonsterCount+" ", 15*relativetileSize-30, realTileSize/2+20);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(8));
            g2d.fillRoundRect(8*relativetileSize+5, 3, 3*relativetileSize, relativetileSize-15,25,25);
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRoundRect(8*relativetileSize+8, 6, 3*relativetileSize-6, relativetileSize-21,25,25);
            if(player.lifeCount==3){
                g2d.drawImage(hearth, 10*relativetileSize, 0,relativetileSize-10,relativetileSize-10,null);
            }
            if(player.lifeCount>1){
                g2d.drawImage(hearth, 9*relativetileSize+10, 0,relativetileSize-10,relativetileSize-10,null);
            }
            if(player.lifeCount>0){
                g2d.drawImage(hearth, 8*relativetileSize+20, 0,relativetileSize-10,relativetileSize-10,null);
            }
    }
    private void updateLeftTime(){
        long currentTime = (System.currentTimeMillis()/1000)-startingTime;
        if(timeLeft>0)
            timeLeft = 200-(int)currentTime;
    }
    public void update(){
        if(game_State.equals("RUNTIME")){
            if(timeLeft <=0)
            player.endGame();
            player.update();
            updateLeftTime();
            if(insKiller){
                insKiller = false;
                InstantiateSerailKillers(4);
            }
        }
    }
    public void playMusic(int i){
        sm.setFile(i);
        sm.loop();
    }
    public void stopMusic(){
        sm.stop();
    }
    public void playSound(int i){
        sm.setFile(i);
        sm.play();
    }
    private void drawPausedScreen(Graphics2D g2d){
        float alpha = 0.5f;
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g2d.setComposite(alphaComposite);
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, screenWidth+50, screenHeight+50);
        Font font = new Font("Arial", Font.BOLD, 45);
        g2d.setFont(font);
        g2d.setColor(Color.RED);
        String text = "PAUSED";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = (getHeight() / 2) + (fm.getAscent() / 4);
        g2d.drawString(text, x, y);

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(8));
        g2d.drawRoundRect(6, 5, 4*relativetileSize-10, relativetileSize-15,25,25);

        drawUI(g2d);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        tm.draw(g2d);

        for(Monster mnstr:monsters){
            if(mnstr != null)
                mnstr.draw(g2d);
        }

        player.draw(g2d);

        
        if(game_State.equals("PAUSED")){
            drawPausedScreen(g2d);
        }

        drawUI(g2d);

        g2d.dispose();
    }
    
}
