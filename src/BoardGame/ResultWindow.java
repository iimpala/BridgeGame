package BoardGame;

import java.awt.Font;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class ResultWindow extends JFrame{
    private static final int WIDTH = 640;
    private static final int HEIGHT = 360;

    public ResultWindow(List<Player> winners){
        setSize(new Dimension(WIDTH, HEIGHT));
        setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        int numWinners = winners.size();
        setLayout(new GridLayout(numWinners + 1,1, 10, 10));

        JLabel title = new JLabel("*Winner is*", JLabel.CENTER);
        title.setFont(new Font(null, Font.PLAIN, 50));
        add(title);

        for(int i = 0; i < numWinners; i++){
            String name = winners.get(i).getName();
            JLabel winner = new JLabel(name, JLabel.CENTER);
            winner.setFont(new Font(null, Font.PLAIN, 40));
            add(winner);
        }
    }
}
