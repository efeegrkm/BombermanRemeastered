package main_folder;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import tile_map.Tile_Manager;

public class Input_Handler implements KeyListener {

    public Tile_Manager tm;
    public boolean wPressed,aPressed,sPressed,dPressed,zPressed,bPressed;
    {
        wPressed = false; aPressed = false;
        dPressed = false; sPressed = false;
        zPressed = false; bPressed = false;
    }
    @Override
    public void keyTyped(KeyEvent e) {
        //lazm diil.
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int val = e.getKeyCode();
        switch(val){
            //Oyun dondurma 
            case(KeyEvent.VK_ENTER):
                if(tm.panel.game_State == "RUNTIME")
                    tm.panel.game_State = "PAUSED";
                else if(tm.panel.game_State == "PAUSED")
                    tm.panel.game_State = "RUNTIME";
                if(tm.panel.game_State == "DEAD"){
                    tm.panel.game_State = "RUNTIME";
                    tm.panel.player.ressurectPlayer(true);
                    tm.panel.timeLeft = 200;
                    tm.panel.player.lifeCount = 3;
                }
            break;
            //bomb
            case(KeyEvent.VK_B):
                bPressed = true;
            break;
            case(KeyEvent.VK_Z):
                zPressed = true;
                if(!tm.panel.player.bombDeployed){
                    tm.flag = true;
                    tm.flag1 = true;
                }
            break;
            //movement
            case(KeyEvent.VK_W):
                wPressed = true;
            break;
            case(KeyEvent.VK_UP):
                wPressed = true;
            break;
            case(KeyEvent.VK_A):
                aPressed = true;
            break;
            case(KeyEvent.VK_LEFT):
                aPressed = true;
            break;
            case(KeyEvent.VK_S):
                sPressed = true;
            break;
            case(KeyEvent.VK_DOWN):
                sPressed = true;
            break;
            case(KeyEvent.VK_D):
                dPressed = true;
            break;
            case(KeyEvent.VK_RIGHT):
                dPressed = true;
            break;
            
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int val = e.getKeyCode();
        switch(val){
            case(KeyEvent.VK_B):
                bPressed = false;
            break;
            case(KeyEvent.VK_W):
                wPressed = false;
            break;
            case(KeyEvent.VK_UP):
                wPressed = false;
            break;
            case(KeyEvent.VK_A):
                aPressed = false;
            break;
            case(KeyEvent.VK_LEFT):
                aPressed = false;
            break;
            case(KeyEvent.VK_S):
                sPressed = false;
            break;
            case(KeyEvent.VK_DOWN):
                sPressed = false;
            break;
            case(KeyEvent.VK_D):
                dPressed = false;
            break;
            case(KeyEvent.VK_RIGHT):
                dPressed = false;
            break;
            //bomb
            case(KeyEvent.VK_Z):
                zPressed = false;
            break;
            case(KeyEvent.VK_ENTER):
                if(tm.panel.game_State == "DEAD"){
                    tm.panel.game_State = "RUNTIME";
                    tm.panel.player.ressurectPlayer(true);
                    tm.panel.startingTime = System.currentTimeMillis()/1000;
                    tm.panel.timeLeft = 200;
                    tm.panel.player.lifeCount = 3;
                }
        }
    }
    
}
