package main_folder;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundManager {
    Clip clip;
    public Clip mainThemeClip;
    URL soundURL[] = new URL[8];
    public SoundManager(){

        soundURL[0] = getWavFileURL("src\\Sounds\\video-game-land.wav");
        soundURL[1] = getWavFileURL("src\\Sounds\\beep-bomb-countdown.wav");
        soundURL[2] = getWavFileURL("src\\Sounds\\bomb.wav");
        soundURL[3] = getWavFileURL("src\\Sounds\\158524-Feet-Footsteps-Wood-Parquet-Sandals-Rubber_Sole-Walk-Loop.wav");
        soundURL[4] = getWavFileURL("src\\Sounds\\zander_noriega_-_dragged_through_hellfire_abomination.wav");
        try{
            // AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[0]);
            // mainThemeClip = AudioSystem.getClip();
            // mainThemeClip.open(ais);
            // mainThemeClip.start();
        }catch(Exception e) {}
        setFile(0);
    }
     public static URL getWavFileURL(String filePath){
        try{
            return new URL("file", null, filePath);
        }
        catch(Exception e){}
        return null;
    }
    public void setFile(int i){
        try{
            if(soundURL[i] == null) System.out.println("Sound manager url null atandi");
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void play(){
        
        clip.start();

    }
    public void loop(){
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    public void stop(){
        clip.stop();
    }
}
