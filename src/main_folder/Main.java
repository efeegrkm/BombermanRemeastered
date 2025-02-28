package main_folder;


import javax.swing.ImageIcon;
import javax.swing.JFrame;


public class Main {
    public static JFrame frame;
    public static void main(String[] args) {
         frame = new JFrame();
        ImageIcon gameIcon = new ImageIcon("Assets\\BombermanAnim\\bomb1.png");
        frame.setIconImage(gameIcon.getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Bomberman Remeastered");

        new BomberManStartScreen(frame);
        
    }
    public static void startBombermanRemeastered(JFrame frame){
        EvPanel myPanel = new EvPanel();
        frame.add(myPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
