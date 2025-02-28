package main_folder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//Start screen tasarımı,
public class BomberManStartScreen extends JFrame {
    public static String selectedColor = "null";
    public BomberManStartScreen(JFrame frame) {
         setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("Bomberman Remastered");

        ImageIcon gameIcon = new ImageIcon("Assets\\BombermanAnim\\bomb1.png");
        setIconImage(gameIcon.getImage());
        JLabel background = new JLabel(new ImageIcon("background.jpg"));
        setContentPane(background);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("BOMBERMAN REMASTERED", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        add(titleLabel, BorderLayout.NORTH);

        JButton startButton = new JButton("START");
        startButton.setFont(new Font("Arial", Font.BOLD, 24));
        startButton.setBackground(Color.RED);
        startButton.setForeground(Color.BLACK);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.startBombermanRemeastered(frame);
            }
        });
        String[] colors = {"ORANGE", "CYAN", "BLUE","GREEN","YELLOW"};
        JComboBox<String> colorComboBox = new JComboBox<>(colors);
        colorComboBox.setFont(new Font("Arial", Font.PLAIN, 24));
        colorComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedColor = (String)colorComboBox.getSelectedItem();
            }
        });
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);  
        centerPanel.add(startButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20))); 
        centerPanel.add(colorComboBox);
        add(centerPanel, BorderLayout.CENTER);

        setVisible(true);
    }

}