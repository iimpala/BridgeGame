package BoardGame;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;

public class StartMenu extends JFrame{
    private int player_num;

    public StartMenu(int player_num, String file_name, String player_name){
        this.player_num = player_num;

		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setLayout(new GridLayout(3,1));
        
        JPanel playerNumPanel = new JPanel();
        JPanel mapFilePanel = new JPanel();

        //playerNumPanel
        playerNumPanel.setBorder(BorderFactory.createEmptyBorder(10 , 10 , 10 , 10));
        playerNumPanel.setLayout(new GridLayout(1,2, 10, 10));

        JLabel pNumPanelTitle = new JLabel("Choose the number of player");
        pNumPanelTitle.setHorizontalAlignment(JLabel.CENTER);
        pNumPanelTitle.setFont(new Font(null, Font.PLAIN, 30));

        JPanel btns = new JPanel();
        btns.setLayout(new GridLayout(1,3, 10, 10));

        JButton btn2 = new JButton("2P");
        JButton btn3 = new JButton("3P");
        JButton btn4 = new JButton("4P");

        btn2.setFont(new Font(null, Font.PLAIN, 30));
        btn3.setFont(new Font(null, Font.PLAIN, 30));
        btn4.setFont(new Font(null, Font.PLAIN, 30));

        btn2.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                setPlayer_num(2);
                btn2.setEnabled(false);
                btn3.setEnabled(true);
                btn4.setEnabled(true);
            }
        });

        btn3.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                setPlayer_num(3);
                btn2.setEnabled(true);
                btn3.setEnabled(false);
                btn4.setEnabled(true);
            }
        });

        btn4.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                setPlayer_num(4);
                btn2.setEnabled(true);
                btn3.setEnabled(true);
                btn4.setEnabled(false);
            }
        });

        btns.add(btn2);
        btns.add(btn3);
        btns.add(btn4);

        playerNumPanel.add(pNumPanelTitle);
        playerNumPanel.add(btns);

        //mapFilePanel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(1,3));

        JLabel mapFilePanelTitle = new JLabel("Write map name");
        mapFilePanelTitle.setHorizontalAlignment(JLabel.CENTER);
        mapFilePanelTitle.setFont(new Font(null, Font.PLAIN, 30));

        JTextField mapName = new JTextField(file_name, 20);
        mapName.setFont(new Font(null, Font.PLAIN, 20));
        mapName.setHorizontalAlignment(JTextField.RIGHT);        

        JLabel extension = new JLabel(".map");
        extension.setHorizontalAlignment(JLabel.LEFT);
        extension.setVerticalAlignment(JLabel.BOTTOM);
        extension.setFont(new Font(null, Font.PLAIN, 20));

        inputPanel.add(mapFilePanelTitle);
        inputPanel.add(mapName);
        inputPanel.add(extension);

        mapFilePanel.add(inputPanel);

        //start button
        JButton start = new JButton("Start");
        start.setFont(new Font(null, Font.PLAIN, 30));

        start.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String map_file = "map/" + mapName.getText() + ".map";

                dispose();
                new View(getPlayer_num(), map_file, player_name);
            }
        });

        
        add(playerNumPanel);
        add(mapFilePanel);
        add(start);
        pack();
    }

    //Getter
    public int getPlayer_num() {
        return player_num;
    }

    //Setter
    public void setPlayer_num(int player_num) {
        this.player_num = player_num;
    }
}