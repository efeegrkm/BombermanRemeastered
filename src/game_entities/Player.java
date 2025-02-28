package game_entities;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import main_folder.BomberManStartScreen;
import main_folder.EvPanel;
import main_folder.Input_Handler;
import main_folder.SoundManager;

public class Player extends Creature {
    EvPanel panel;
    Input_Handler handler;
    public boolean deadFlagPlayer = true;
    int xa;
    BufferedImage[] bloodAnimSet = new BufferedImage[20];
    int bloodAnimIndex = -1;
    String endType;
    public final int screenX;
    public final int screenY;
    public int refPosX;
    public int refPosY;
    int midCurrentCol;
    int midCurrentRow;
    //bomb ayarları
    String hint = "";
    long deadTime;
    public int lifeCount = 3;
    public int bombTimer = 3000;//milisec cinsinden
    public boolean bombDeployed = false;
    public boolean bombAnimSelection = false;
    public long bombStartTimeMillis;
    public final int bombDuration = 3000;//milisec cinsinden
    public int bombStartCol;
    public int bombStartRow;
    private double defPlayerSpeed;
    SoundManager sm = new SoundManager();
    boolean flag2 = true;
    public boolean flag3 = true;
    boolean bloodFlag = true;
    boolean countFlag = true;
    public int örtüNumarasi = -1;
    //Enviroment ayarları:
    public final float WATER_SPEED_DECREMENT = 0.8f;
    public String currentSkill = "null";
    public boolean bButtonActive = false;
    public boolean kapiAcildi;

    float alpha = 0;
    
    public Player(EvPanel panel, Input_Handler handler) {
        currentSkill = BomberManStartScreen.selectedColor;
        speed = 3;
        defPlayerSpeed = speed;
        this.panel = panel;

        this.handler = handler;
        screenX = panel.screenWidth/2-(panel.relativetileSize/2);//göreceli konum
        screenY = panel.screenHeight/2-(panel.relativetileSize/2);//göreceli konum(gerek kalmadı çünkü y kamera sabit)
        refPosX = panel.relativetileSize*3/2;
        refPosY = panel.relativetileSize*3/2;

        //Collider defs:
        solidArea = new Rectangle();
        solidArea.width = panel.relativetileSize-29;
        solidArea.height = panel.relativetileSize-23;
        setDefs();
    }
    public boolean amIDead(){
        if(!DEAD){
            ArrayList<Monster> monsters = panel.monsters;
            for(int i = 0;i<monsters.size();i++){
                if(monsters.get(i).DEAD == false){
                    int monsterCol = monsters.get(i).midCurrentCol;
                    int monsterRow = monsters.get(i).midCurrentRow;
                    if(midCurrentCol == monsterCol && midCurrentRow == monsterRow){
                        endType = "KATLEDILDI";
                        return true;
                    }
                }
            }
            for(int i = -panel.tm.bombFireZone; i< panel.tm.bombFireZone+1; i++){
                if(panel.tm.killFree && (midCurrentCol == panel.tm.ölüMerkezCol + i && midCurrentRow == panel.tm.ölüMerkezRow)){
                    if((i==-panel.tm.bombFireZone && panel.tm.tiles[panel.tm.tilemapNums[panel.tm.ölüMerkezCol-1][panel.tm.ölüMerkezRow]].collable) 
                    || (i== panel.tm.bombFireZone && panel.tm.tiles[panel.tm.tilemapNums[panel.tm.ölüMerkezCol+1][panel.tm.ölüMerkezRow]].collable))
                    return false;
                    else{
                        endType = "PATLADI";
                        return true;
                    } 
                }
                else if(panel.tm.killFree && (midCurrentCol == panel.tm.ölüMerkezCol && midCurrentRow == panel.tm.ölüMerkezRow + i)){
                    if((i==-panel.tm.bombFireZone && panel.tm.tiles[panel.tm.tilemapNums[panel.tm.ölüMerkezCol][panel.tm.ölüMerkezRow-1]].collable) 
                    || (i== panel.tm.bombFireZone && panel.tm.tiles[panel.tm.tilemapNums[panel.tm.ölüMerkezCol][panel.tm.ölüMerkezRow+1]].collable))
                    return false;
                    else{
                        endType = "PATLADI";
                        return true;
                    } 
                }
            }
            return false;
        }
        return false;
    }
    public void setDefs() {
        x = panel.relativetileSize;
        y = panel.relativetileSize + 50;
        
        
        // player anim defs:
        direction = 'd';
        try {
            up1 = loadImage("Assets\\BombermanAnim\\bomberman_back1.png");
            up2 = loadImage("Assets\\BombermanAnim\\bomberman_back2.png");
            down1 = loadImage("Assets\\BombermanAnim\\bomberman_Forward1.png");
            down2 = loadImage("Assets\\BombermanAnim\\bomberman_Forward2.png");
            right1 = loadImage("Assets\\BombermanAnim\\bomberman_Right1.png");
            right2 = loadImage("Assets\\BombermanAnim\\bomberman_Right2.png");
            left1 = loadImage("Assets\\BombermanAnim\\bomberman_left1.png");
            left2 = loadImage("Assets\\BombermanAnim\\bomberman_left2.png");
            idle1 = loadImage("Assets\\BombermanAnim\\bomberman_idle1.png");
            idle2 = loadImage("Assets\\BombermanAnim\\bomberman_idle2.png");

            for(int i = 0;i<bloodAnimSet.length;i++){
                String s_i = String.valueOf(i);
                bloodAnimSet[i] = loadImage("Assets\\Enviroment\\Blood_Shed\\1_"+ s_i +".png");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void bloodAnimCreator(boolean active){
        if(active) {
            countFlag = false;
            Thread bloodAnimCreatorThrd = new Thread(new Runnable(){
                @Override
                public void run(){
                    try{
                        for(int i = 0; i<bloodAnimSet.length;i++){
                            bloodAnimIndex=i;
                            Thread.sleep(50);
                        }
                        bloodAnimIndex = -1;
                    }
                    catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            });
            bloodAnimCreatorThrd.start();
        }
        
    }
    public static BufferedImage loadImage(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new IOException("File not found: " + path);
        }
        return ImageIO.read(file);
    }
    // private boolean ortalandi(){
    //     midCurrentCol = (solidArea.x + panel.relativetileSize/2)/panel.relativetileSize;
    //     midCurrentRow = (solidArea.y-50 + panel.relativetileSize/2)/panel.relativetileSize;
    //     int colL = (midCurrentCol) * panel.relativetileSize;
    //     int colR = (midCurrentCol + 1) * panel.relativetileSize;
    //     int rowU = (midCurrentRow) * panel.relativetileSize;
    //     int rowD = (midCurrentRow+1) * panel.relativetileSize;
    //     if((solidArea.x-5 >colL) && ((solidArea.x + solidArea.width) <colR-5) && ((solidArea.y-55) >rowU) && ((solidArea.y-80 + panel.relativetileSize) <(rowD-5))){
    //         return true;
    //     }
    //     else {
    //         return false;
    //     }
    // }
    public void ressurectPlayer(boolean newMap){
        deadFlagPlayer = true;
        if(newMap){
            panel.sm1.stop();
            panel.sm2.stop();
            panel.sm1.setFile(0);
            panel.sm1.play();
            panel.flag = true;
        }
        bloodFlag = true;
        countFlag = true;
        if(newMap)
            panel.currentMonsterCount = panel.DEFAULT_MONSTER_COUNT;
        x = panel.relativetileSize;
        y = panel.relativetileSize + 50;
        if(newMap)
            panel.tm.loadMap("src\\tile_map\\TileMap1.txt");

        for(int i = 0; i<panel.monsters.size(); i++){
            if(newMap)
            {panel.monsters.get(i).startPosSetter(panel);
            panel.monsters.get(i).x = panel.monsters.get(i).startPosX;
            panel.monsters.get(i).y = panel.monsters.get(i).startPosY;}
            panel.monsters.get(i).DEAD = false;
        }
        if(newMap)
            panel.KILL_ALL_TYPE_OF(1);
        DEAD = false;
    }
    private void alphaThreadSkimmer(){
        new Thread(new Runnable() {
            @Override
            public void run(){
                try{
                    deadFlagPlayer = true;
                    panel.game_State = "DEAD";
                    
                    while(alpha < 0.9){
                        alpha += 0.01f;
                        Thread.sleep(30);
                    }
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //oyun bitince çalışacak metot:
    public void endGame(){
        alphaThreadSkimmer();
        bloodFlag = true;
        countFlag = true;
    }
    private void resetSkill(){
                bButtonActive = false;
                defPlayerSpeed = 3;
                speed = defPlayerSpeed;
                panel.tm.tiles[1].playerColDetector = true;
                panel.tm.bombFireZone = 1;
    }
    public void skillSetter(){
        switch(currentSkill){
            case("null"):
                resetSkill();
            break;
            case("GREEN"):
                panel.tm.bombFireZone = 2;
            break;
            case("ORANGE"):
                speed = 4;
                defPlayerSpeed = speed;
            break;
            case("CYAN"):
                bButtonActive = true;
            break;
            case("BLUE"):
                panel.tm.tiles[1].playerColDetector = false;
                panel.tm.bombFireZone = 1;
            break;
            default:
                currentSkill = "null";
            break;
        }
    }
    private void skillChecker(){
        if(panel.tm.skillPoses[midCurrentCol][midCurrentRow] >1){
            switch(panel.tm.skillPoses[midCurrentCol][midCurrentRow]){
                case(6):
                if(panel.tm.tiles[panel.tm.tilemapNums[midCurrentCol][midCurrentRow]].colDetector == false){
                    System.out.println("Kapıya temas gerçekleşti");
                    if(kapiAcildi){
                        endGame();
                        endType = "WON";
                    }
                }
                break;
                case(2):
                 if(panel.tm.tiles[panel.tm.tilemapNums[midCurrentCol][midCurrentRow]].colDetector == false){
                    System.out.println("ORANGE");
                    panel.tm.tilemapNums[midCurrentCol][midCurrentRow] = 0;
                    panel.tm.skillPoses[midCurrentCol][midCurrentRow] = 1;
                    resetSkill();
                    currentSkill = "ORANGE";
                 }
                    
                break;
                case(3):
                if(panel.tm.tiles[panel.tm.tilemapNums[midCurrentCol][midCurrentRow]].colDetector == false){
                    System.out.println("BLUE");
                    panel.tm.tilemapNums[midCurrentCol][midCurrentRow] = 0;
                    panel.tm.skillPoses[midCurrentCol][midCurrentRow] = 1;
                    resetSkill();
                    currentSkill = "BLUE";                   
                }
                    
                break;
                case(4):
                if(panel.tm.tiles[panel.tm.tilemapNums[midCurrentCol][midCurrentRow]].colDetector == false){
                    System.out.println("CYAN");
                    panel.tm.tilemapNums[midCurrentCol][midCurrentRow] = 0;
                    panel.tm.skillPoses[midCurrentCol][midCurrentRow] = 1;
                    resetSkill();
                    currentSkill = "CYAN";    
                }
                    
                break;
                case(5):
                if(panel.tm.tiles[panel.tm.tilemapNums[midCurrentCol][midCurrentRow]].colDetector == false){
                    System.out.println("GREEN");
                    panel.tm.tilemapNums[midCurrentCol][midCurrentRow] = 0;
                    panel.tm.skillPoses[midCurrentCol][midCurrentRow] = 1;
                    resetSkill();
                    currentSkill = "GREEN";
                }
                    
                break;
            }
        }
    }
    public void update() {
        if(panel.game_State.equals("RUNTIME")){
            if(panel.currentMonsterCount == 0){
                kapiAcildi = true;
            }
            skillChecker();
            skillSetter();
            if(bButtonActive && handler.bPressed && bombDeployed && flag3){
                flag3 = false;
                bombStartTimeMillis += bombStartTimeMillis-3000-System.currentTimeMillis();
                panel.stopMusic();
            }
            if(DEAD && System.currentTimeMillis() >deadTime + 2000){
                if(lifeCount > 1){
                    ressurectPlayer(false);
                }
                
                lifeCount--;
            }
            if(DEAD && lifeCount<1){
                endGame();
            }
            if(panel.tm.killFree){
                if(panel.tm.killTimeStart + 500 < System.currentTimeMillis())
                    panel.tm.killFree = false;
            }
            if(!DEAD){
                if(amIDead() && !DEAD){
                    deadTime = System.currentTimeMillis();
                    stopMusic();
                    direction = 'i';
                    DEAD = true;
                }
                midCurrentCol = (solidArea.x + panel.relativetileSize/2)/panel.relativetileSize;
                midCurrentRow = (solidArea.y-50 + panel.relativetileSize/2)/panel.relativetileSize;
                
                if(bombDeployed && ((midCurrentCol != bombStartCol) || (midCurrentRow != bombStartRow))
                   && panel.tm.tilemapNums[bombStartCol][bombStartRow]<6){ //bomba tile solidify
        
                    if(panel.tm.tilemapNums[bombStartCol][bombStartRow] == 3)
                        panel.tm.tilemapNums[bombStartCol][bombStartRow] = 7;
                    else
                        panel.tm.tilemapNums[bombStartCol][bombStartRow] = 6;
                }
                //Bomb control
                if(handler.zPressed){
                    if(panel.tm.tiles[panel.tm.tilemapNums[midCurrentCol][midCurrentRow]].colDetector == false && bombDeployed == false){
                        bombDeployed = true;
                        bombStartTimeMillis = System.currentTimeMillis();
                        panel.cM.checkTile(this);
                        bombStartCol = midCurrentCol;
                        bombStartRow = midCurrentRow;
                        // bombLocationX = ((refPosX + panel.relativetileSize / 2) / panel.relativetileSize) * panel.relativetileSize;
                        // bombLocationY = ((refPosY + panel.relativetileSize / 2) / panel.relativetileSize) * panel.relativetileSize;
                    }
                }
                //Water behaviour:
                if(panel.tm.tilemapNums[midCurrentCol][midCurrentRow] == 3){
                    speed = defPlayerSpeed - WATER_SPEED_DECREMENT;
                }
                else speed = defPlayerSpeed;
                
                if (handler.wPressed) {
                    direction = 'u';
                    if(flag2){
                        playMusic(3);
                        flag2 = false;
                    }
                    //y -= speed;
                } else if (handler.aPressed) {
                    direction = 'l';
                    if(flag2){
                        playMusic(3);
                        flag2 = false;
                    }
                    //x -= speed;
                } else if (handler.sPressed) {
                    direction = 'd';
                    if(flag2){
                        playMusic(3);
                        flag2 = false;
                    }
                    //y += speed;
                } else if (handler.dPressed) {
                    direction = 'r';
                    if(flag2){
                        playMusic(3);
                        flag2 = false;
                    }
                    //x += speed;
                } else{
                    direction = 'i';
                    if(!flag2){
                        stopMusic();
                        flag2 = true;
                    }
                   
                } 
                
                collisionOn = false;
                panel.cM.checkTile(this);
        
                if(!collisionOn){
                    switch(direction){
                        case 'u':// yukarı
                            y -= speed;
                            if(speed != defPlayerSpeed)
                                y+=1;
                        break;
                        case 'd':// aşağı
                            y += speed;
                        break;
                        case 'r':// sağ
                            x += speed;
                            // if(bombDeployed && (x>panel.screenWidth/2-panel.relativetileSize/2 
                            // && x<panel.worldWidth-panel.screenWidth/2-panel.relativetileSize/2))
                            //     bombLocationX -= speed;
                        break;
                        case 'l':// sol
                            x -= speed;
                            if(speed != defPlayerSpeed)
                                x+=1;
                            // if(bombDeployed && (x>panel.screenWidth/2-panel.relativetileSize/2 
                            // && x<panel.worldWidth-panel.screenWidth/2-panel.relativetileSize/2))
                            //     bombLocationX += speed;
                        break;
                        //idle iken biyere deyemez
        
                    }
                }
                //Animation setter
                animCnt++;
                if(animCnt>100/ANIM_SPEED){
                    if(spriteNum == 1) spriteNum = 2;
                    else if(spriteNum == 2) spriteNum = 1;
                    bombAnimSelection= !bombAnimSelection;
                    animCnt = 0;
                }        
            }
            
        }
        
    }
    public void playMusic(int i){
        sm.setFile(i);
        sm.play();
        sm.loop();
    }
    public void stopMusic(){
        sm.stop();
    }

    public void draw(Graphics2D g2d) {
        
        if(panel.game_State.equals("DEAD")){
            
            String endCause ="";
            if(panel.timeLeft == 0){
                endCause = "Yeterince Hızlı Değildin...";
            }
            else if(endType.equals("PATLADI")){
                endCause = "Kendi bomban tarafından parçalara ayrıldın...";
            }
            else if(endType.equals("KATLEDILDI")){
                endCause = "Diyarın sapkın yaratıkları tarafından katledildin...";
            }
            else if(endType.equals("WON")){
                endCause = "Tebrikler bu diyarda 1 gün daha ayakta kaldın...";
            }
            
            AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2d.setComposite(alphaComposite);
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, panel.screenWidth+50, panel.screenHeight+50);
            Font font = new Font("Arial", Font.BOLD, 35);
            g2d.setFont(font);
            if(endType.equals("WON")){
                g2d.setColor(Color.GREEN);
            }
            else
                g2d.setColor(Color.RED);
            FontMetrics fm = g2d.getFontMetrics();
            String newJ = "Press 'ENTER' to start a new journey";
            String[] hints = {"Hint: Sulak araziler(mavi bloklar) seni yavaşlatır, ancak öte diyarın sulak bataklıklarında yaratılmış iblisler için oyun havuzundan ibarettir...",
                                "Hint: Kapı diyardan kurtulman için tek çıkışın onu zorla açmaya çalışırsan öte diyarın kanasusamış suikastçılarını peşine bulursun...",
                                "Hint: Kırmızı iblisler özellikle suikastçiler özgürken bombandan kolaylıkla kaçabilir onları gafil avlamak istiyorsan köşeye sıkıştır..."};
            
            if(deadFlagPlayer){
                deadFlagPlayer = false;
                xa = (int) (Math.random()*3);
                hint = hints[xa];
                System.out.println(hint);
            }
                
            int x = (panel.getWidth() - fm.stringWidth(endCause)) / 2;
            int x1 = (panel.getWidth() - fm.stringWidth(newJ)) / 2;
            int x2 = (panel.getWidth() - fm.stringWidth(hint)) / 2;
            int y = (panel.getHeight() / 2) + (fm.getAscent() / 4);
            g2d.drawString(endCause, x, y);
            g2d.setColor(Color.WHITE);
            Font font1 = new Font("Arial", Font.PLAIN, 18);
            g2d.setFont(font1);
            g2d.drawString(hint, (xa==0)?x2+560:x2+550, y+ 80);
            Font font2 = new Font("Arial", Font.PLAIN, 25);
            g2d.setFont(font2);
            g2d.setColor(Color.YELLOW);
            g2d.drawString(newJ, x1+80, y+ 160);
        }

        BufferedImage toDraw = null;
        switch (direction) {
            case 'u':// yukarı
                if(spriteNum == 1)
                    toDraw = up1;
                else if(spriteNum == 2)
                    toDraw = up2;
                break;
            case 'd':// aşağı
                if(spriteNum == 1)
                    toDraw = down1;
                else if(spriteNum == 2)
                    toDraw = down2;
                break;
            case 'r':// sağ
                if(spriteNum == 1)
                    toDraw = right1;
                else if(spriteNum == 2)
                    toDraw = right2;
                break;
            case 'l':// sol
                if(spriteNum == 1)
                    toDraw = left1;
                else if(spriteNum == 2)
                    toDraw = left2;
                break;
            case 'i':// idle
                if(spriteNum == 1)
                    toDraw = idle1;
                else if(spriteNum == 2)
                    toDraw = idle2;
                break;
        }
        
        if(x>panel.screenWidth/2-panel.relativetileSize/2 && x<panel.worldWidth-panel.screenWidth/2-panel.relativetileSize/2 ){
            if(DEAD){
                if(bloodFlag){
                    bloodAnimCreator(countFlag);
                    if(bloodAnimIndex!=-1){
                        g2d.drawImage(bloodAnimSet[bloodAnimIndex],screenX-35,y-35,panel.relativetileSize+70,panel.relativetileSize+70,null);
                        if(bloodAnimIndex == bloodAnimSet.length-1)
                            bloodFlag = false;
                    }
                }
            }
            else if(!panel.game_State.equals("DEAD")){
                g2d.drawImage(toDraw, screenX, y, panel.relativetileSize, panel.relativetileSize, null);
                refPosX = screenX;
                refPosY = y;
            }
        }
            
        else if(x <= panel.screenWidth/2-panel.relativetileSize/2){
            if(DEAD){
                if(bloodFlag){
                    bloodAnimCreator(countFlag);
                    if(bloodAnimIndex!=-1){
                        g2d.drawImage(bloodAnimSet[bloodAnimIndex],x-35,y-35,panel.relativetileSize+70,panel.relativetileSize+70,null);
                        if(bloodAnimIndex == bloodAnimSet.length-1)
                            bloodFlag = false;
                    }
                }
            }
            else if(!panel.game_State.equals("DEAD"))
            {g2d.drawImage(toDraw, x, y, panel.relativetileSize, panel.relativetileSize, null);
            refPosX = x;
            refPosY = y;
            }
        }
           
        else if(x >= panel.worldWidth-panel.screenWidth/2-panel.relativetileSize/2){
            if(DEAD){
                if(bloodFlag){
                    bloodAnimCreator(countFlag);
                    if(bloodAnimIndex!=-1){
                        g2d.drawImage(bloodAnimSet[bloodAnimIndex],x-12*panel.relativetileSize-35,y-35,panel.relativetileSize+70,panel.relativetileSize+70,null);
                        if(bloodAnimIndex == bloodAnimSet.length-2)
                            bloodFlag = false;
                    }
                }
            }
            else if(!panel.game_State.equals("DEAD"))
            {g2d.drawImage(toDraw, x-12*panel.relativetileSize, y, panel.relativetileSize, panel.relativetileSize, null);
            refPosX = x-12*panel.relativetileSize;
            refPosY = y;
            }
        }
        // solidArea.setBounds(refPosX+14,refPosY+20,panel.relativetileSize-29, panel.relativetileSize-20);
        // g2d.draw(solidArea);
        solidArea.x = x+14;
        solidArea.y = y+20;
        
    }
    
}