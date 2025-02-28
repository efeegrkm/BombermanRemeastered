package game_entities;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Creature {
    public double speed;
    public int x,y;// world x,y
    public BufferedImage up1,up2,down1,down2,left1,left2,right1,right2,idle1,idle2;
    public char direction;//up->u, down->d, right->r, left->l, idle->i

    public int animCnt = 0;
    public int spriteNum = 1;
    public int ANIM_SPEED = 8;
    public Rectangle solidArea;
    public boolean collisionOn = false;
    public boolean DEAD = false;
}
