package BoardGame;

public class NewGame {
    public static void main(String[] args){

    int player_num = 2;
    String file_name = "default";
    String player_name = "Player1";


	StartMenu newGame = new StartMenu(player_num, file_name, player_name);
	newGame.setVisible(true);
    newGame.setLocationRelativeTo(null);

    // new ConsoleUI(player_num, file_name, player_name);
    }
}
