
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.border.Border;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI_exp implements ActionListener{
    static JButton button;
    int clickCnt;
    {clickCnt = 0;}
    public static void main(String[] args) {
        //frame
        // frami frame child classı oluşturup yapabilirsin
        JFrame frame = new JFrame();
        frame.setSize(824,845);
        frame.setTitle("exp window");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        ImageIcon myIcon1 = new ImageIcon("Assets\\BombermanAnim\\bomberman_back1.png");
        ImageIcon myIcon = new ImageIcon("Assets\\BombermanAnim\\bomberman_back1.png");
        frame.setIconImage(myIcon1.getImage());
        frame.setVisible(true);
        frame.setLayout(null);

        frame.getContentPane().setBackground(Color.gray);
        //frame.getContentPane().setBackground(new Color(R,G,B));
        //Border
        Border border = BorderFactory.createLineBorder(Color.green, 3);
        // Labels
        JLabel label = new JLabel("Hello World");
        label.setText(label.getText() + "++");
        label.setIcon(myIcon);
        label.setHorizontalTextPosition(JLabel.CENTER); 
        label.setVerticalTextPosition(JLabel.TOP);      
        label.setFont(new Font("MV Boli",Font.PLAIN,25));
        label.setForeground(Color.red);
        label.setBackground(Color.black);
        label.setOpaque(true);//to display bc color
        label.setIconTextGap(0);
        label.setBorder(border);
        
        label.setBounds(0,0,400,400);

        label.setVerticalAlignment(JLabel.CENTER);
        label.setHorizontalAlignment(JLabel.CENTER);
        frame.add(label);
        //frame.pack();

        // Panels (Not Border Layout)
        JPanel topLeftPanel = new JPanel();
        topLeftPanel.setBackground(Color.RED);
        topLeftPanel.setBounds(400,0,400,400);
        topLeftPanel.setBorder(border);
        topLeftPanel.setLayout(null);
        //Panele bsi ekleme
        JLabel tplLabel = new JLabel("I am in the top right    ");
        topLeftPanel.add(tplLabel);
        //tplLabel.setHorizontalAlignment(JLabel.RIGHT); (for border layout)
        //tplLabel.setVerticalAlignment(JLabel.TOP);    (for border layout)
        tplLabel.setFont(new Font("MV Boli",Font.PLAIN,20));
        tplLabel.setForeground(Color.BLACK);
        tplLabel.setBackground(Color.PINK);
        tplLabel.setOpaque(true);
        tplLabel.setBounds(100,30,280,30);
        //
        frame.add(topLeftPanel);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.BLUE);
        bottomPanel.setBounds(0,400,800,400);
        bottomPanel.setBorder(border);
        //bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setLayout(null);
        frame.add(bottomPanel);

            //BUTTONS:
        button = new JButton("First Button");
        button.setBounds(260,140,300,60);
        //button.setHorizontalAlignment(JButton.CENTER);
        //button.setVerticalAlignment(JButton.CENTER);
        button.setFont(new Font("Comic Sans",Font.BOLD,25));
        button.setFocusable(false);
        button.setBorder(BorderFactory.createEtchedBorder());
        button.setForeground(Color.BLACK);
        button.setBackground(Color.LIGHT_GRAY);
        button.addActionListener(new GUI_exp());
        button.setIcon(myIcon);
        button.setIconTextGap(-100);
        bottomPanel.add(button);
        //button.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == button){
            clickCnt++;
            System.out.println(clickCnt);
        }
        else
            throw new UnsupportedOperationException("Unimplemented method 'actionPerformed'");
    }

}
