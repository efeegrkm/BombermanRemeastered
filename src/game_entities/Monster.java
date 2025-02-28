package game_entities;


import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;


import main_folder.EvPanel;
import tile_map.Tile_Manager;

public class Monster extends Creature{
    private final Object lock = new Object();
    boolean bloodFlag = true;
    boolean countFlag = true;
    public int startPosX;
    public int startPosY;
    public Thread thread;
    private EvPanel ep;
    public boolean isDestroyed = false;
    //ek animler
    BufferedImage[] imgAr = new BufferedImage[5];
    char[] dirs = {'u','d','r','l'};
    int entityLeftX;
    int entityTopY;
    int midCurrentCol;
    int midCurrentRow;
    int bloodAnimIndex = -1;
    Tile_Manager tm;
    long currentTime;
    boolean flag = true;
    boolean turnFlag = true;
    public int monsterMod;
    long deadTime;

    public Monster(int monsterMode,EvPanel panel){
        panel.currentMonsterCount++;
        this.monsterMod = monsterMode;
        ANIM_SPEED = 20;
        collisionOn = false;
        getImages();
        ep = panel;
        tm = ep.tm;
        switch(monsterMod){
            case(1): 
                speed = 3;

            break;
            default: 
                speed = 2; 
            break;
        }
        direction = dirs[(int) (Math.random()*4)];
        startPosSetter(panel);
        x = startPosX;
        y = startPosY;
        solidArea = new Rectangle(x+19,y+22,ep.relativetileSize-10,ep.relativetileSize-6);
    }
    public void getImages(){
        try{
            if(monsterMod == 1){
                imgAr[0] = loadImage("Assets\\Monster_Run\\masked_orc_run_anim_f0.png");
                imgAr[1] = loadImage("Assets\\Monster_Run\\masked_orc_run_anim_f1.png");
                imgAr[2] = loadImage("Assets\\Monster_Run\\masked_orc_run_anim_f2.png");
                imgAr[3] = loadImage("Assets\\Monster_Run\\masked_orc_run_anim_f3.png");
                imgAr[4] = loadImage("Assets\\Monster_Run\\muddy_anim_f0.png");
            }
            else{
                imgAr[0] = loadImage("Assets\\Monster_Run\\big_demon_run_anim_f0.png");
                imgAr[1] = loadImage("Assets\\Monster_Run\\big_demon_run_anim_f1.png");
                imgAr[2] = loadImage("Assets\\Monster_Run\\big_demon_run_anim_f2.png");
                imgAr[3] = loadImage("Assets\\Monster_Run\\big_demon_run_anim_f3.png");
                imgAr[4] = loadImage("Assets\\Monster_Run\\muddy_anim_f0.png");
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    private static BufferedImage loadImage(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new IOException("File not found: " + path);
        }
        return ImageIO.read(file);
    }
    public int[] startPosSetter(EvPanel panel){
        if(monsterMod == 1){
            startPosX = tm.doorPosX*panel.relativetileSize+panel.relativetileSize/2-40;
            startPosY = tm.doorPosY*panel.relativetileSize+panel.relativetileSize/2+2;
            return null;
        }
        else{
            ArrayList<Integer> alX = new ArrayList<>();
            ArrayList<Integer> alY = new ArrayList<>();
            int[][] tileMap = tm.tilemapNums;
            for(int i = 0; i<tileMap.length; i++){
                for(int j = 0; j<tileMap[i].length; j++){
                    if(tileMap[i][j] == 0 || tileMap[i][j] == 3){
                        if(i>4&&j>4){
                            alX.add(i);
                            alY.add(j);
                        }
                    }
                }
            }
            int a = (int) (Math.random()*alX.size()-1);
            int gettenX = alX.get(a);
            int gettenY = alY.get(a);
            startPosX = panel.relativetileSize *gettenX-10;
            startPosY = panel.relativetileSize *gettenY + 30;
            int[] ar = {startPosX,startPosY};
            return ar;            
        }
    }
    public void directionSetter(){
        char[] freeDirs = freeDirs(direction);
        if(freeDirs.length !=0)
        direction = freeDirs[(int) (Math.random()*freeDirs.length)];
        
        if(!collisionOn){
            switch(direction){
                case 'u':
                    y -= speed;
                break;
                case 'd':
                    y += speed;
                break;
                case 'r':
                    x += speed;
                break;
                case 'l':
                    x -= speed;
                break;
            }
        }
    }
    public char[] freeDirs(char direction){
        entityLeftX = solidArea.x;
        entityTopY = solidArea.y - 50;
        midCurrentCol = (entityLeftX + ep.relativetileSize/2)/ep.relativetileSize;
        midCurrentRow = (entityTopY + ep.relativetileSize/2)/ep.relativetileSize;
        char[] freeDirs;
        char notDir = (direction == 'u'||direction == 'd') ? ((direction == 'u') ? 'd':'u'):((direction == 'r')? 'l':'r');
        if(collisionOn){
             turnFlag = true;
             freeDirs = new char[1]; freeDirs[0] = notDir;
        }
        else if(ortalandi() && turnFlag){
            turnFlag = false;
            boolean yFormat = (direction == 'u'||direction == 'd') ? true: false;
            if(yFormat){
                int i = 1;
                String chars = "";
                if(tm.tiles[tm.tilemapNums[midCurrentCol+1][midCurrentRow]].colDetector == false){
                    chars += 'r'; i++;
                }
                if(tm.tiles[tm.tilemapNums[midCurrentCol-1][midCurrentRow]].colDetector == false){
                    chars += 'l'; i++;
                }
                freeDirs = new char[i];
                freeDirs[freeDirs.length-1] = direction;
                for(int j = 0; j<chars.length(); j++){
                    freeDirs[j] = chars.charAt(j);
                }
            }
            else{
                int i = 0;
                String chars = "";
                if(tm.tiles[tm.tilemapNums[midCurrentCol][midCurrentRow+1]].colDetector == false){
                    chars += 'd'; i++;
                }
                if(tm.tiles[tm.tilemapNums[midCurrentCol][midCurrentRow-1]].colDetector == false){
                    chars += 'u'; i++;
                }
                freeDirs = new char[i];
                if(freeDirs.length!=0)
                    freeDirs[freeDirs.length-1] = direction;
                for(int j = 0; j<chars.length(); j++){
                    freeDirs[j] = chars.charAt(j);
                }
            }
        }
        else{
            if(!ortalandi())
                turnFlag = true;
            freeDirs = new char[1]; 
            freeDirs[0] = direction;
        }
        //if(collisionOn) array tek elemanlı ve directionun zıttıdır.
        //else if(ortalandı()) directiona dik olan tilelara bakılır !solid olan yönler arraye eklenir.
        return freeDirs;
    }
    private boolean ortalandi(){
        midCurrentCol = (solidArea.x + ep.relativetileSize/2)/ep.relativetileSize;
        midCurrentRow = (solidArea.y-50 + ep.relativetileSize/2)/ep.relativetileSize;
        int colL = (midCurrentCol) * ep.relativetileSize;
        int colR = (midCurrentCol + 1) * ep.relativetileSize;
        int rowU = (midCurrentRow) * ep.relativetileSize;
        int rowD = (midCurrentRow+1) * ep.relativetileSize;
        if((solidArea.x-5 >colL) && ((solidArea.x + solidArea.width) <colR+1) && ((solidArea.y-50) >rowU) && ((solidArea.y-80 + ep.relativetileSize) <(rowD-27))){
            return true;
        }
        else {
            return false;
        } 
    }
    public boolean amIDead(){
        for(int i = -2; i< ep.tm.bombFireZone+1; i++){
            if(ep.tm.killFree && (midCurrentCol == ep.tm.ölüMerkezCol + i && midCurrentRow == ep.tm.ölüMerkezRow)){
                if((i==-2 && ep.tm.tiles[ep.tm.tilemapNums[ep.tm.ölüMerkezCol-1][ep.tm.ölüMerkezRow]].collable) 
                || (i== 2 && ep.tm.tiles[ep.tm.tilemapNums[ep.tm.ölüMerkezCol+1][ep.tm.ölüMerkezRow]].collable))
                return false;
                else return true;
            }
            else if(ep.tm.killFree && (midCurrentCol == ep.tm.ölüMerkezCol && midCurrentRow == ep.tm.ölüMerkezRow + i)){
                if((i==-2 && ep.tm.tiles[ep.tm.tilemapNums[ep.tm.ölüMerkezCol][ep.tm.ölüMerkezRow-1]].collable) 
                || (i== 2 && ep.tm.tiles[ep.tm.tilemapNums[ep.tm.ölüMerkezCol][ep.tm.ölüMerkezRow+1]].collable))
                return false;
                else return true;
            }
        }
        return false;
    }
    public void update() {
        if(ep.game_State.equals("RUNTIME")){
            if(monsterMod != 1 && (tm.tilemapNums[midCurrentCol][midCurrentRow] == 1 || tm.tilemapNums[midCurrentCol][midCurrentRow] == 2)){
                int[] ar = startPosSetter(ep);
                x = ar[0];
                y = ar[1];
            }
            if(!DEAD){
                solidArea.setBounds(x+19,y+22,ep.relativetileSize-12,ep.relativetileSize-6);
                collisionOn = false;
                ep.cM.checkTile(this);
                
                directionSetter();
        
                animCnt++;
                if(animCnt>100/ANIM_SPEED){
                    if(spriteNum == 0) spriteNum = 1;
                    else if(spriteNum == 1) spriteNum = 2;
                    else if(spriteNum == 2) spriteNum = 3;
                    else if(spriteNum == 3) spriteNum = 0;
                    animCnt = 0;
                }
                if(amIDead() && !DEAD){
                    DEAD = true;
                    synchronized(lock){
                        ep.currentMonsterCount-=1;
                    }
                    deadTime = System.currentTimeMillis();
                }
            }
        }
        
        
    }
    public void bloodAnimCreator(boolean active){
        if(active) {
            countFlag = false;
            Thread bloodAnimCreatorThrd = new Thread(new Runnable(){
                @Override
                public void run(){
                    try{
                        for(int i = 0; i<ep.player.bloodAnimSet.length;i++){
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
    public void draw(Graphics2D g){
        if(!DEAD || System.currentTimeMillis() <= deadTime + 3000){
            BufferedImage image = (!DEAD) ? imgAr[spriteNum]:imgAr[4];
            int size = (!DEAD) ? ep.relativetileSize: ep.relativetileSize -37;
            int ek = (!DEAD) ? 0: 20;
                int screenX = x-ep.player.x + ep.player.screenX;
                if(ep.player.x>ep.screenWidth/2-ep.relativetileSize/2 && ep.player.x<ep.worldWidth-ep.screenWidth/2-ep.relativetileSize/2){
                    if(DEAD){
                        if(bloodFlag){
                            bloodAnimCreator(countFlag);
                            if(bloodAnimIndex!=-1){
                                g.drawImage(ep.player.bloodAnimSet[bloodAnimIndex],screenX-25,y-25,ep.relativetileSize+70,ep.relativetileSize+70,null);
                                if(bloodAnimIndex == ep.player.bloodAnimSet.length-1)
                                    bloodFlag = false;
                            }
                        }
                    }
                    else
                    g.drawImage(image,(monsterMod!=1)?(screenX + ek):(screenX + ek)+10,(monsterMod!=1)?(y+ek):(y+ek)+10,(monsterMod != 1)?(size+23):size,(monsterMod != 1)?(size+23):size,null);
                }
                else if(ep.player.x <= ep.screenWidth/2-ep.relativetileSize/2)
                {
                    if(DEAD){
                        if(bloodFlag){
                            bloodAnimCreator(countFlag);
                            if(bloodAnimIndex!=-1){
                                g.drawImage(ep.player.bloodAnimSet[bloodAnimIndex],x-25,y-25,ep.relativetileSize+70,ep.relativetileSize+70,null);
                                if(bloodAnimIndex == ep.player.bloodAnimSet.length-1)
                                    bloodFlag = false;
                            }
                        }
                    }
                    else
                    g.drawImage(image,(monsterMod!=1)?(x + ek):(x + ek)+10,(monsterMod!=1)?(y+ek):(y+ek)+10,(monsterMod != 1)?(size+23):size,(monsterMod != 1)?(size+23):size,null);
                }
                else{
                    if(DEAD){
                        if(bloodFlag){
                            bloodAnimCreator(countFlag);
                            if(bloodAnimIndex!=-1){
                                g.drawImage(ep.player.bloodAnimSet[bloodAnimIndex],x-12*ep.relativetileSize-25,y-25,ep.relativetileSize+70,ep.relativetileSize+70,null);
                                if(bloodAnimIndex == ep.player.bloodAnimSet.length-1)
                                    bloodFlag = false;
                            }
                        }
                    }
                    else
                    g.drawImage(image,(monsterMod!=1)?(x-12*ep.relativetileSize + ek):(x-12*ep.relativetileSize + ek)+10,(monsterMod!=1)?(y+ek):(y+ek)+10,(monsterMod != 1)?(size+23):size,(monsterMod != 1)?(size+23):size,null);
                }
        }
    }
}
// package game_entities;


// import java.awt.Graphics2D;
// import java.awt.Rectangle;
// import java.awt.image.BufferedImage;
// import java.io.File;
// import java.io.IOException;

// import javax.imageio.ImageIO;

// import main_folder.EvPanel;
// import tile_map.Tile_Manager;

// public class Monster extends Creature{
//     public int startPosX;
//     public int startPosY;
//     public Thread thread;
//     private EvPanel ep;
//     public boolean isDestroyed = false;

//     char[] dirs = {'u','d','r','l'};
//     int entityLeftX;
//     int entityTopY;
//     int midCurrentCol;
//     int midCurrentRow;

//     Tile_Manager tm;
//     long currentTime;
//     boolean flag = true;
//     public Monster(int monsterMode,EvPanel panel){
//         getImages();
//         ep = panel;
//         tm = ep.tm;
//         switch(monsterMode){
//             case(0): speed = 2; break;
//             case(1): speed = 3; break;
//         }
//         direction = dirs[(int) Math.random()*4];
//         startPosSetter(panel);
//         x = startPosX;
//         y = startPosY;
//         solidArea = new Rectangle(x+5,y+3,panel.relativetileSize-10,panel.relativetileSize-6);
//     }
//     public void getImages(){
//         try{
//             up1 = loadImage("Assets\\Monster_Run\\big_demon_run_anim_f0.png");
//             up2 = loadImage("Assets\\BombermanAnim\\bomberman_back2.png");
//             down1 = loadImage("Assets\\BombermanAnim\\bomberman_Forward1.png");
//             down2 = loadImage("Assets\\BombermanAnim\\bomberman_Forward2.png");
//             right1 = loadImage("Assets\\BombermanAnim\\bomberman_Right1.png");
//             right2 = loadImage("Assets\\BombermanAnim\\bomberman_Right2.png");
//             left1 = loadImage("Assets\\BombermanAnim\\bomberman_left1.png");
//             left2 = loadImage("Assets\\BombermanAnim\\bomberman_left2.png");
//         }
//         catch(IOException e){
//             e.printStackTrace();
//         }
//     }
//     private static BufferedImage loadImage(String path) throws IOException {
//         File file = new File(path);
//         if (!file.exists()) {
//             throw new IOException("File not found: " + path);
//         }
//         return ImageIO.read(file);
//     }
//     public void startPosSetter(EvPanel panel){
//         // int[][] map = panel.tm.tilemapNums;
//         // int x = (int) (Math.random() * (map.length));
//         // int y = (int) (Math.random() * (map[0].length));
//         // while(map[x][y] == 2||map[x][y] == 1){
//         //     x = (int) (Math.random() * (map.length + 1));
//         //     y = (int) (Math.random() * (map[0].length + 1));
//         // }
//         startPosX = panel.relativetileSize*5;
//         startPosY = panel.relativetileSize*5;
//     }
//     public void directionSetter(){
//         entityLeftX = solidArea.x;
//         entityTopY = solidArea.y - 50;
//         midCurrentCol = (entityLeftX + ep.relativetileSize/2)/ep.relativetileSize;
//         midCurrentRow = (entityTopY + ep.relativetileSize/2)/ep.relativetileSize;
//         if(flag){
//             currentTime = System.currentTimeMillis();
//             flag = false;
//         }
//         if(!flag && System.currentTimeMillis() > currentTime + 1000){
//             direction = randomDirection();
//             flag = true;
//         }
            
//         switch(direction){
//             case 'u':
//                 y -= speed;
//             break;
//             case 'd':
//                 y += speed;
//             break;
//             case 'r':
//                 x += speed;
//             break;
//             case 'l':
//                 x -= speed;
//             break;
//         }
//     }
//     private char randomDirection(){
//         boolean whileKapa = false;
//         boolean directionFound = false; 
        
//         int rand = (int) (Math.random() * 4);
//         char randDir;
//         if(dirs[rand] == 'u' && direction == 'd') randDir = 'd';
//         else if(dirs[rand] == 'd' && direction == 'u') randDir = 'u';
//         else if(dirs[rand] == 'r' && direction == 'l') randDir = 'l';
//         else if(dirs[rand] == 'l' && direction == 'r') randDir = 'r';
//         else randDir = dirs[rand];
//         char direc = randDir;
//         while(!directionFound && !whileKapa){
//             switch(direc){
//                 case('u'):
//                     if(midCurrentRow > 0&&tm.tiles[tm.tilemapNums[midCurrentCol][midCurrentRow-1]].colDetector == false){
//                         if(midCurrentCol*ep.relativetileSize + 28/50*ep.relativetileSize < x && x < (midCurrentCol+1)*ep.relativetileSize -28/50*ep.relativetileSize){
//                             directionFound = true;
//                         }
//                         else whileKapa = true;
//                     }
//                     else{
//                         int randomNumber = (int) (Math.random() * 4);
//                         direc = dirs[randomNumber];
//                     }
//                 break;
//                 case('d'):
//                     if(midCurrentRow<tm.tilemapNums[0].length-1&&tm.tiles[tm.tilemapNums[midCurrentCol][midCurrentRow+1]].colDetector == false){
//                         if(midCurrentCol*ep.relativetileSize + 28/50*ep.relativetileSize < x && x < (midCurrentCol+1)*ep.relativetileSize -28/50*ep.relativetileSize){
//                             directionFound = true;
//                         }
                            
//                         else whileKapa = true;
//                     }
//                     else{
//                         int randomNumber = (int) (Math.random() * 4);
//                         direc = dirs[randomNumber];
//                     }
//                 break;
//                 case('r'):
//                     if(midCurrentCol<tm.tilemapNums.length-1 &&tm.tiles[tm.tilemapNums[midCurrentCol+1][midCurrentRow]].colDetector == false){
//                         if((midCurrentRow*ep.relativetileSize + 28/50*ep.relativetileSize) + 50 < x && x < ((midCurrentCol+1)*ep.relativetileSize -28/50*ep.relativetileSize)+50){
//                             directionFound = true;
//                         }
                            
//                         else whileKapa = true;
//                     }
//                     else{
//                         int randomNumber = (int) (Math.random() * 4);
//                         direc = dirs[randomNumber];
//                     }
//                 break;
//                 case('l'):
//                     if(midCurrentCol>0&&tm.tiles[tm.tilemapNums[midCurrentCol-1][midCurrentRow]].colDetector == false){
//                         if((midCurrentRow*ep.relativetileSize + 28/50*ep.relativetileSize) + 50 < x && x < ((midCurrentCol+1)*ep.relativetileSize -28/50*ep.relativetileSize)+50){
//                             directionFound = true;
//                         }
                            
//                         else whileKapa = true;
//                     }
//                     else{
//                         int randomNumber = (int) (Math.random() * 4);
//                         direc = dirs[randomNumber];
//                     }
//                 break;
//             }
//         }
//         return direc;
//     }
//     public void update() {
//         collisionOn = false;
//         ep.cM.checkTile(this);
//         solidArea.setBounds(x+5,y+3,ep.relativetileSize-10,ep.relativetileSize-6);
//         directionSetter();
//     }
//     public void draw(Graphics2D g){
//         int screenX = x-ep.player.x + ep.player.screenX;
        
//         if(ep.player.x>ep.screenWidth/2-ep.relativetileSize/2 && x<ep.worldWidth-ep.screenWidth/2-ep.relativetileSize/2 )
//             g.drawImage(up1,screenX,y,ep.relativetileSize+23,ep.relativetileSize+23,null);
//         else if(ep.player.x <= ep.screenWidth/2-ep.relativetileSize/2)
//             g.drawImage(up1,x,y,ep.relativetileSize+23,ep.relativetileSize+23,null);
//         else{
//             g.drawImage(up1,x-12*ep.relativetileSize,y,ep.relativetileSize+23,ep.relativetileSize+23,null);
//         }
//     }
// }
