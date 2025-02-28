package main_folder;

import game_entities.Creature;
import game_entities.Monster;
import game_entities.Player;

public class ColliderManager {
    EvPanel ep;
    public ColliderManager(EvPanel ep){
        this.ep = ep;
    }
    public void checkTile(Creature entity){
        //entitinin solid area sınırları.
        int entityLeftX = entity.solidArea.x;
        int entityRightX = entity.solidArea.x + entity.solidArea.width;
        int entityTopY =  entity.solidArea.y-50;
        int entityBottomY = entity.solidArea.y + entity.solidArea.height-50;

        int leftCurrentCol = entityLeftX/ep.relativetileSize;
        int rightCurrentCol = entityRightX/ep.relativetileSize;
        int topCurrentRow = entityTopY/ep.relativetileSize;
        int bottomCurrentRow = entityBottomY/ep.relativetileSize;


        int olasiNum1, olasiNum2;
        switch(entity.direction){
            case 'u':// yukarı
                    topCurrentRow = (int)(entityTopY-entity.speed)/ep.relativetileSize;
                    olasiNum1 = ep.tm.tilemapNums[leftCurrentCol][topCurrentRow];
                    olasiNum2 = ep.tm.tilemapNums[rightCurrentCol][topCurrentRow];
                    if((entity instanceof Monster)&&(ep.tm.tiles[olasiNum1].colDetector|| ep.tm.tiles[olasiNum2].colDetector)
                    ||(entity instanceof Player)&&(ep.tm.tiles[olasiNum1].playerColDetector|| ep.tm.tiles[olasiNum2].playerColDetector)){
                        entity.collisionOn = true;
                    }
                break;
            case 'd':// aşağı
                    bottomCurrentRow = (int)(entityBottomY+entity.speed)/ep.relativetileSize;
                    olasiNum1 = ep.tm.tilemapNums[leftCurrentCol][bottomCurrentRow];
                    olasiNum2 = ep.tm.tilemapNums[rightCurrentCol][bottomCurrentRow];
                    if((entity instanceof Monster)&&(ep.tm.tiles[olasiNum1].colDetector|| ep.tm.tiles[olasiNum2].colDetector)
                    ||(entity instanceof Player)&&(ep.tm.tiles[olasiNum1].playerColDetector|| ep.tm.tiles[olasiNum2].playerColDetector)){
                        entity.collisionOn = true;
                    }
                break;
            case 'r':// sağ
                    rightCurrentCol = (int)(entityRightX+entity.speed)/ep.relativetileSize;
                    olasiNum1 = ep.tm.tilemapNums[rightCurrentCol][topCurrentRow];
                    olasiNum2 = ep.tm.tilemapNums[rightCurrentCol][bottomCurrentRow];
                    if((entity instanceof Monster)&&(ep.tm.tiles[olasiNum1].colDetector|| ep.tm.tiles[olasiNum2].colDetector)
                    ||(entity instanceof Player)&&(ep.tm.tiles[olasiNum1].playerColDetector|| ep.tm.tiles[olasiNum2].playerColDetector)){
                        entity.collisionOn = true;
                    }
                break;
            case 'l':// sol
                    leftCurrentCol = (int)(entityLeftX-entity.speed)/ep.relativetileSize;
                    olasiNum1 = ep.tm.tilemapNums[leftCurrentCol][topCurrentRow];
                    olasiNum2 = ep.tm.tilemapNums[leftCurrentCol][bottomCurrentRow];
                    if((entity instanceof Monster)&&(ep.tm.tiles[olasiNum1].colDetector|| ep.tm.tiles[olasiNum2].colDetector)
                    ||(entity instanceof Player)&&(ep.tm.tiles[olasiNum1].playerColDetector|| ep.tm.tiles[olasiNum2].playerColDetector)){
                        entity.collisionOn = true;
                    }
                break;
            //idle bakmaya gerek duymadım.
        }
    }
}
