package tile_map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import main_folder.EvPanel;

public class Tile_Manager{
    /* 
        bombAnimElection metodu her framede tüm tilelar için çalışıyor fpsi çok düşürüyor. Vaktim kalırsa optimize edeceğim.
    */
    public EvPanel panel;
    public Tile[] tiles;
    public int[][] tilemapNums; 
    public int door;
    public int doorPosX;
    public int doorPosY;
    public int[][] skillPoses;
    
    //bomba patlama anim:
    public int bombFireZone = 1;
    BufferedImage m,xn,yn,du,ru,lu,uu;
    {patlamaInitializer();}
    private boolean patlamaTetik = false;
    private long patlamaStartTime;
    private int patlamaSüresi = 600;
    int bombStartCol;
    int bombStartRow;
    //ölü karelerin tanımlanması.
    public int ölüMerkezCol;
    public int ölüMerkezRow;
    //teknik değişkenler
    public boolean flag1 = true;
    public boolean flag = true;
    public boolean killFree = false;
    public long killTimeStart;

    public Tile_Manager(EvPanel panel){
        this.panel = panel;
        tiles = new Tile[15];
        getTileImage();
        loadMap("src\\tile_map\\TileMap1.txt");
    }
    private void skillPosInitializer(){
        skillPoses = new int[panel.maxWorldCol][panel.maxWorldRow];
        for(int i = 0;i<tilemapNums.length;i++){
            for(int j = 0;j<tilemapNums[i].length;j++){
                skillPoses[i][j] = tilemapNums[i][j];
            }
        }
        int availableCnt = 0;
            for(int x = 0; x<skillPoses.length;x++){
                for(int y = 0; y<skillPoses[x].length;y++){
                    if(skillPoses[x][y] != 1){
                        skillPoses[x][y] = 0;
                    }
                    else{
                        availableCnt++;
                    }
                }
            }
            door = getRandomTile(availableCnt);
            final int type1 = getRandomTile(availableCnt);
            int type2 = getRandomTile(availableCnt);
            int type3 = getRandomTile(availableCnt);
            int type4 = getRandomTile(availableCnt);
            int cnt = 0;
            for(int x = 0; x<skillPoses.length;x++){
                for(int y = 0; y<skillPoses[x].length;y++){
                    if(skillPoses[x][y] == 1){
                        if  (cnt == door){
                            skillPoses[x][y] = 6;
                            doorPosX = x;
                            doorPosY = y;
                        } 
                        else if (cnt == type1) skillPoses[x][y] = 2;
                        else if (cnt == type2) skillPoses[x][y] = 3; 
                        else if (cnt == type3) skillPoses[x][y] = 4; 
                        else if (cnt == type4) skillPoses[x][y] = 5;  
                        cnt++;
                        System.out.println(x + " " + y + " " + skillPoses[x][y]);
                    }
                }
            }
    }
    public void loadMap(String path){
        tilemapNums = new int[panel.maxWorldCol][panel.maxWorldRow];
        try{
            BufferedReader reader = new BufferedReader(new FileReader(path));
            int j = 0;
            String line;
            while((line = reader.readLine()) != null){
                String[] elements = (line.split(" "));
                for(int i = 0; i<elements.length; i++){
                    if(Integer.parseInt(elements[i]) == 9)
                        tilemapNums[i][j] = getRandomTile(2);
                    else
                        tilemapNums[i][j] = Integer.parseInt(elements[i]);
                }
                j++;
            }
            skillPosInitializer();
            reader.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    private static int getRandomTile(int i) {
        Random random = new Random();
        return random.nextInt(i); // 0 veya 1 döndürür
    }
    public void getTileImage(){ //0 çim, 1 kirikduvar, 2 saglam duvar, 3 su. 
        try{
            tiles[0] = new Tile();
            tiles[0].tileImage = loadImage("Assets\\Enviroment\\EnviromentTiles\\çim_Tile.png");
            
            tiles[1] = new Tile();
            tiles[1].tileImage = loadImage("Assets\\Enviroment\\EnviromentTiles\\kirikDuvar.png");
            tiles[1].colDetector = true;
            tiles[1].playerColDetector = true;
            tiles[1].isBreakable = true;
            tiles[1].collable = true;

            tiles[2] = new Tile();
            tiles[2].tileImage = loadImage("Assets\\Enviroment\\EnviromentTiles\\saglamDuvar.png");
            tiles[2].colDetector = true;
            tiles[2].playerColDetector = true;
            tiles[2].collable = true;

            tiles[3] = new Tile();
            tiles[3].tileImage = loadImage("Assets\\Enviroment\\EnviromentTiles\\waterTile.png");

            tiles[4] = new Tile();
            tiles[4].tileImage = loadImage("Assets\\Enviroment\\EnviromentTiles\\deformedCim.png");
            

            tiles[5] = new Tile();
            tiles[5].tileImage = loadImage("Assets\\Enviroment\\EnviromentTiles\\deformedCim2.png");
            

            tiles[6] = new Tile();
            tiles[6].tileImage = loadImage("Assets\\Enviroment\\EnviromentTiles\\çim_Tile.png");// solid çim
            tiles[6].colDetector = true;
            tiles[6].playerColDetector = true;

            tiles[7] = new Tile();
            tiles[7].tileImage = loadImage("Assets\\Enviroment\\EnviromentTiles\\waterTile.png");// solid su
            tiles[7].colDetector = true;
            tiles[7].playerColDetector = true;

            tiles[8] = new Tile();
            tiles[8].tileImage = loadImage("Assets\\Enviroment\\EnviromentTiles\\door.png");// kapı
            tiles[8].activateString = "DOOR";

            tiles[9] = new Tile();
            tiles[9].tileImage = loadImage("Assets\\Enviroment\\EnviromentTiles\\hız.png");// ORANGE
            tiles[9].activateString = "ORANGE";

            tiles[10] = new Tile();
            tiles[10].tileImage = loadImage("Assets\\Enviroment\\EnviromentTiles\\hayalet.png");// BLUE
            tiles[10].activateString = "BLUE";

            tiles[11] = new Tile();
            tiles[11].tileImage = loadImage("Assets\\Enviroment\\EnviromentTiles\\patlatıcı.png");// CYAN
            tiles[11].activateString = "CYAN";

            tiles[12] = new Tile();
            tiles[12].tileImage = loadImage("Assets\\Enviroment\\EnviromentTiles\\double_range.png");// GREEN
            tiles[12].activateString = "GREEN";
            
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    private BufferedImage loadImage(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new IOException("File not found: " + path);
        }
        return ImageIO.read(file);
    }
    private void bombDeployer(Graphics2D g2d,int xS,int yS,int col,int row){
        if(panel.player.bombDeployed){
            if(flag){
                panel.playSound(1);
                flag = false;
            }
            if(System.currentTimeMillis()< (panel.player.bombStartTimeMillis + panel.player.bombDuration)){
                if(col == panel.player.bombStartCol && row == panel.player.bombStartRow){
                    BufferedImage bombImage = null;
                    try{bombImage = (panel.player.bombAnimSelection) ? loadImage("Assets\\BombermanAnim\\bomb1.png"):
                    loadImage("Assets\\BombermanAnim\\bomb2.png");}
                    catch(IOException e){e.printStackTrace();} 
                    g2d.drawImage(bombImage, xS, yS, panel.relativetileSize, panel.relativetileSize,null);
                }
            }
            else{
                //Bomba patlayınca yapılacaklar:
                    //yukarıya patlama fireZone default 1.
                        if(flag1){
                            panel.playSound(2);
                            flag1 = false;
                            ölüMerkezCol = panel.player.bombStartCol;
                            ölüMerkezRow = panel.player.bombStartRow;
                            killFree = true;
                            killTimeStart = System.currentTimeMillis();
                        }
                        patlamaStartTime = System.currentTimeMillis();
                        patlamaTetik = true;
                        tilemapNums[panel.player.bombStartCol][panel.player.bombStartRow] = 4;
                        bombStartCol = panel.player.bombStartCol;
                        bombStartRow = panel.player.bombStartRow;
                
                        boolean a = true,b = true,c = true,d = true;   
                for(int i = 1;i<bombFireZone + 1;i++){
                    if(a &&panel.player.bombStartRow>1 && tilemapNums[panel.player.bombStartCol][panel.player.bombStartRow-i] == 2) a = false;
                    if(a &&panel.player.bombStartCol<tilemapNums.length-1 && (bombStartCol) == doorPosX && bombStartRow-i == doorPosY && tiles[tilemapNums[doorPosX][doorPosY]].colDetector==false){
                        panel.insKiller = true;
                    }
                    if(a &&panel.player.bombStartRow>1 && tiles[tilemapNums[panel.player.bombStartCol][panel.player.bombStartRow-i]].isBreakable == true){
                        if(skillPoses[panel.player.bombStartCol][panel.player.bombStartRow-i] == 1)
                            tilemapNums[panel.player.bombStartCol][panel.player.bombStartRow-i] = 5;//yıkılanları çim yap
                        else{
                            switch(skillPoses[panel.player.bombStartCol][panel.player.bombStartRow-i]){
                                case(6): tilemapNums[panel.player.bombStartCol][panel.player.bombStartRow-i] = 8; break;
                                case(2): tilemapNums[panel.player.bombStartCol][panel.player.bombStartRow-i] = 9; break;
                                case(3): tilemapNums[panel.player.bombStartCol][panel.player.bombStartRow-i] = 10; break;
                                case(4): tilemapNums[panel.player.bombStartCol][panel.player.bombStartRow-i] = 11; break;
                                case(5): tilemapNums[panel.player.bombStartCol][panel.player.bombStartRow-i] = 12; break;
                            }
                        }
                        a = false;
                    }
                    if(b &&panel.player.bombStartCol<tilemapNums.length-1 && tilemapNums[panel.player.bombStartCol+i][panel.player.bombStartRow] == 2) b = false;
                    if(b &&panel.player.bombStartCol<tilemapNums.length-1 && (bombStartCol +i) == doorPosX && bombStartRow == doorPosY && tiles[tilemapNums[doorPosX][doorPosY]].colDetector==false){
                        panel.insKiller = true;
                    }
                    if(b &&panel.player.bombStartCol<tilemapNums.length-1 && tiles[tilemapNums[panel.player.bombStartCol+i][panel.player.bombStartRow]].isBreakable == true){
                        if(skillPoses[panel.player.bombStartCol + i][panel.player.bombStartRow ] == 1)
                            tilemapNums[panel.player.bombStartCol + i][panel.player.bombStartRow ] = 5;//yıkılanları çim yap
                        else{
                            switch(skillPoses[panel.player.bombStartCol + i][panel.player.bombStartRow ]){
                                case(6): tilemapNums[panel.player.bombStartCol + i][panel.player.bombStartRow ] = 8; break;
                                case(2): tilemapNums[panel.player.bombStartCol + i][panel.player.bombStartRow ] = 9; break;
                                case(3): tilemapNums[panel.player.bombStartCol + i][panel.player.bombStartRow ] = 10; break;
                                case(4): tilemapNums[panel.player.bombStartCol + i][panel.player.bombStartRow ] = 11; break;
                                case(5): tilemapNums[panel.player.bombStartCol + i][panel.player.bombStartRow ] = 12; break;
                            }
                        }
                        b = false;
                    }
                    if(c &&panel.player.bombStartCol<tilemapNums[0].length-1 && tilemapNums[panel.player.bombStartCol][panel.player.bombStartRow + i] == 2) c = false;
                    if(c &&panel.player.bombStartCol<tilemapNums.length-1 && (bombStartCol) == doorPosX && bombStartRow+i == doorPosY && tiles[tilemapNums[doorPosX][doorPosY]].colDetector==false){
                        panel.insKiller = true;
                    }
                    if(c &&panel.player.bombStartRow<tilemapNums[0].length-2 &&  tiles[tilemapNums[panel.player.bombStartCol][panel.player.bombStartRow+i]].isBreakable == true){
                        if(skillPoses[panel.player.bombStartCol][panel.player.bombStartRow+i] == 1)
                            tilemapNums[panel.player.bombStartCol][panel.player.bombStartRow+i] = 5;//yıkılanları çim yap
                        else{
                            switch(skillPoses[panel.player.bombStartCol][panel.player.bombStartRow+i]){
                                case(6): tilemapNums[panel.player.bombStartCol][panel.player.bombStartRow+i] = 8; break;
                                case(2): tilemapNums[panel.player.bombStartCol][panel.player.bombStartRow+i] = 9; break;
                                case(3): tilemapNums[panel.player.bombStartCol][panel.player.bombStartRow+i] = 10; break;
                                case(4): tilemapNums[panel.player.bombStartCol][panel.player.bombStartRow+i] = 11; break;
                                case(5): tilemapNums[panel.player.bombStartCol][panel.player.bombStartRow+i] = 12; break;
                            }
                        }
                        c = false;
                    }
                    if(d &&panel.player.bombStartCol>1 && tilemapNums[panel.player.bombStartCol-i][panel.player.bombStartRow] == 2) d = false;
                    if(d &&panel.player.bombStartCol<tilemapNums.length-1 && (bombStartCol -i) == doorPosX && bombStartRow == doorPosY && tiles[tilemapNums[doorPosX][doorPosY]].colDetector==false){
                        panel.insKiller = true;
                    }
                     if(d &&panel.player.bombStartCol>1 &&  tiles[tilemapNums[panel.player.bombStartCol-i][panel.player.bombStartRow]].isBreakable == true){
                        if(skillPoses[panel.player.bombStartCol - i][panel.player.bombStartRow ] == 1)
                            tilemapNums[panel.player.bombStartCol - i][panel.player.bombStartRow ] = 5;//yıkılanları çim yap
                        else{
                            switch(skillPoses[panel.player.bombStartCol - i][panel.player.bombStartRow ]){
                                case(6): tilemapNums[panel.player.bombStartCol - i][panel.player.bombStartRow ] = 8; break;
                                case(2): tilemapNums[panel.player.bombStartCol - i][panel.player.bombStartRow ] = 9; break;
                                case(3): tilemapNums[panel.player.bombStartCol - i][panel.player.bombStartRow ] = 10; break;
                                case(4): tilemapNums[panel.player.bombStartCol - i][panel.player.bombStartRow ] = 11; break;
                                case(5): tilemapNums[panel.player.bombStartCol - i][panel.player.bombStartRow ] = 12; break;
                            }
                        }
                        d = false;
                    }
                    
                }
                panel.player.bombDeployed = false;
                panel.player.flag3 = true;
            }
        }
    }//m,xn,yn,du,ru,lu,uu
    
    private void patlamaInitializer(){
        try{m = loadImage("Assets\\Enviroment\\EnviromentTiles\\Patlama\\patlama_Merkez.png"); xn = loadImage("Assets\\Enviroment\\EnviromentTiles\\Patlama\\patlama_Sag.png");
        yn = loadImage("Assets\\Enviroment\\EnviromentTiles\\Patlama\\patlama_Yukari.png"); du = loadImage("Assets\\Enviroment\\EnviromentTiles\\Patlama\\patlama_Assagi_Uc.png");
        ru = loadImage("Assets\\Enviroment\\EnviromentTiles\\Patlama\\patlama_Sag_Uc.png"); lu = loadImage("Assets\\Enviroment\\EnviromentTiles\\Patlama\\patlama_Sol_Uc.png");
        uu = loadImage("Assets\\Enviroment\\EnviromentTiles\\Patlama\\patlama_Yukari_Uc.png");} catch(IOException e){e.printStackTrace();} 
        
    }
    private void patlama(Graphics2D g2d,int xS,int yS,int col,int row){
        if(patlamaTetik){
            long timeLeft = patlamaStartTime + patlamaSüresi - System.currentTimeMillis();
            
            if(timeLeft>0){
                bombAnimElection(g2d, xS, yS, col, row, timeLeft);
            }
            else{
                patlamaTetik = false;
            }
        }
    }
    public void draw(Graphics2D g2d){
        int col = 0;
        int row = 0;
        int y = 50;//y ekseni kamera pos sabit kalacak.
        int x = 0;
        int x1 = -12*panel.relativetileSize;
        while(col<panel.maxWorldCol && row<panel.maxWorldRow){
            int worldX = col*panel.relativetileSize;
            //int worldY = row*panel.relativetileSize; (y sabit olcak)
            
            int screenX = worldX -panel.player.x + panel.player.screenX;
            if(panel.player.x>panel.screenWidth/2-panel.relativetileSize/2 && panel.player.x<panel.worldWidth-panel.screenWidth/2-panel.relativetileSize/2){
                g2d.drawImage(tiles[tilemapNums[col][row]].tileImage, screenX, y, panel.relativetileSize, panel.relativetileSize,null);
                if(col == doorPosX && row == doorPosY && tiles[tilemapNums[col][row]].colDetector==true){
                    g2d.setColor(Color.MAGENTA);
                    g2d.drawImage(tiles[8].tileImage,screenX+15, y+15, panel.relativetileSize-30, panel.relativetileSize-30,null);
                }
                bombDeployer(g2d, screenX, y, col, row);
                patlama(g2d, screenX, y, col, row);
            }
            else if(panel.player.x <= panel.screenWidth/2-panel.relativetileSize/2){
                g2d.drawImage(tiles[tilemapNums[col][row]].tileImage, x, y, panel.relativetileSize, panel.relativetileSize,null);
                if(col == doorPosX && row == doorPosY && tiles[tilemapNums[col][row]].colDetector==true){
                    g2d.setColor(Color.MAGENTA);
                    g2d.drawImage(tiles[8].tileImage,x+15, y+15, panel.relativetileSize-30, panel.relativetileSize-30,null);
                }
                bombDeployer(g2d, x, y, col, row);
                patlama(g2d, x, y, col, row);
            }
                
            else if(panel.player.x >= panel.worldWidth-panel.screenWidth/2-panel.relativetileSize/2){
                g2d.drawImage(tiles[tilemapNums[col][row]].tileImage, x1, y, panel.relativetileSize, panel.relativetileSize,null);
                if(col == doorPosX && row == doorPosY && tiles[tilemapNums[col][row]].colDetector==true){
                    g2d.setColor(Color.MAGENTA);
                    g2d.drawImage(tiles[8].tileImage,x1+15, y+15, panel.relativetileSize-30, panel.relativetileSize-30,null);
                }
                bombDeployer(g2d, x1, y, col, row);
                patlama(g2d, x1, y, col, row);
            }
                
            col++;
            x += panel.relativetileSize;
            x1 += panel.relativetileSize;
            if(col == panel.maxWorldCol){
                col = 0;
                row+=1;
                y+=panel.relativetileSize;
                x = 0;
                x1 = -12*panel.relativetileSize;
            }
        }
    }
    private void bombAnimElection(Graphics2D g2d,int xS,int yS,int col,int row, long timeLeft){// Optimizasyon sıkıntıları barındırıyor vaktim kalırsa burayı düzelteceğim.
        
        if(col == bombStartCol && row ==  bombStartRow)
                g2d.drawImage(m, xS, yS, panel.relativetileSize, panel.relativetileSize,null);
                if(timeLeft<500)
                if(bombFireZone > 1){
                    if(col ==  bombStartCol + 1 && row ==  bombStartRow
                    && tilemapNums[ bombStartCol + 1][ bombStartRow] != 2){
                       if(tilemapNums[ bombStartCol + 2][ bombStartRow] == 1)
                           g2d.drawImage(ru, xS, yS, panel.relativetileSize, panel.relativetileSize,null);
                       else
                       g2d.drawImage(xn, xS, yS, panel.relativetileSize, panel.relativetileSize,null);
                    }
                       
                   else if(col ==  bombStartCol  && row ==  bombStartRow+ 1
                   && tilemapNums[ bombStartCol][ bombStartRow + 1] != 2){
                       if(tilemapNums[ bombStartCol][ bombStartRow +2] == 1)
                           g2d.drawImage(du, xS, yS, panel.relativetileSize, panel.relativetileSize,null);
                       else
                       g2d.drawImage(yn, xS , yS, panel.relativetileSize, panel.relativetileSize,null);
                   }
                       
                   else if(col ==  bombStartCol - 1 && row ==  bombStartRow
                   && tilemapNums[ bombStartCol - 1][ bombStartRow] != 2){
                       if(tilemapNums[bombStartCol - 2][ bombStartRow] == 1)
                           g2d.drawImage(lu, xS, yS, panel.relativetileSize, panel.relativetileSize,null);
                       else
                       g2d.drawImage(xn, xS, yS, panel.relativetileSize, panel.relativetileSize,null);
                   }
                       
                   else if(col ==  bombStartCol  && row ==  bombStartRow- 1
                   && tilemapNums[ bombStartCol][ bombStartRow-1] != 2){
                       if(tilemapNums[ bombStartCol][ bombStartRow-2] == 1)
                           g2d.drawImage(uu, xS, yS, panel.relativetileSize, panel.relativetileSize,null);
                       else
                       g2d.drawImage(yn, xS , yS, panel.relativetileSize, panel.relativetileSize,null);
                   }
                       
               }
               if(timeLeft<400||(bombFireZone == 1 && timeLeft<500)){
                    if(col == bombStartCol + bombFireZone && row ==  bombStartRow 
                        && tilemapNums[bombStartCol + bombFireZone][ bombStartRow] != 2
                        && tilemapNums[bombStartCol + bombFireZone][ bombStartRow] != 1
                        && ((tilemapNums[bombStartCol + bombFireZone-1][ bombStartRow] == 0||bombFireZone==1)
                        ||tilemapNums[bombStartCol + bombFireZone-1][bombStartRow] == 3))
                g2d.drawImage(ru, xS, yS, panel.relativetileSize, panel.relativetileSize,null);
                else if(col == bombStartCol  && row ==  bombStartRow+ bombFireZone
                        && tilemapNums[bombStartCol][ bombStartRow + bombFireZone] != 2
                        && tilemapNums[bombStartCol][ bombStartRow + bombFireZone] != 1
                        && ((tilemapNums[bombStartCol][ bombStartRow + bombFireZone-1] == 0||bombFireZone==1)
                        ||tilemapNums[bombStartCol][ bombStartRow  + bombFireZone-1] == 3))
                g2d.drawImage(du, xS , yS, panel.relativetileSize, panel.relativetileSize,null);
                else if(col == bombStartCol - bombFireZone && row ==  bombStartRow
                        && tilemapNums[bombStartCol- bombFireZone][ bombStartRow ] != 2
                        && tilemapNums[bombStartCol- bombFireZone][ bombStartRow ] != 1
                        && ((tilemapNums[bombStartCol- bombFireZone+1][ bombStartRow ] == 0||bombFireZone==1)
                        ||tilemapNums[bombStartCol- bombFireZone+1][ bombStartRow] == 3))
                g2d.drawImage(lu, xS, yS, panel.relativetileSize, panel.relativetileSize,null);
                else if(col == bombStartCol  && row == bombStartRow- bombFireZone
                        && tilemapNums[bombStartCol][bombStartRow - bombFireZone] != 2
                        && tilemapNums[bombStartCol][bombStartRow - bombFireZone] != 1
                        && ((tilemapNums[bombStartCol][bombStartRow - bombFireZone+1] == 0||bombFireZone==1)
                        ||tilemapNums[bombStartCol][bombStartRow - bombFireZone+1] == 3))   
                g2d.drawImage(uu, xS , yS, panel.relativetileSize, panel.relativetileSize,null);
               }
                
                
                
    }
}
