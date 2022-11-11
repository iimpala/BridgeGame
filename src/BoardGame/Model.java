package BoardGame;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Model extends Observable{
    private GameManager gm;
    private UpdatePacket packet;

    public Model(int num_player, String map_file){
        gm = new GameManager(num_player, map_file);
        packet = new UpdatePacket(gm.getCurr_player());
    }

    //Getter(For Initialization View instance)
    public int[][] getMapdata(){
        return gm.getBoard().getMapdata();
    }

    //Change States (Caller : Controller)
    void rollDice(){
        packet.setDiceNum(gm.getCurr_player().rollDice());
        packet.setMovable(false);

        setChanged();
        notifyObservers(packet);
    }

    void stayTurn(){
        if(gm.checkStayable())
            gm.stayTurn();

        packet.setPlayer(gm.getCurr_player());
        packet.setStayable(gm.checkStayable());

        setChanged();
        notifyObservers(packet);
    }

    void move(String[] dir){

        packet.setMovable(gm.movePiece(dir));
        
        if(gm.isPlaying()){
            packet.setPlayer(gm.getCurr_player());
            packet.setMap_data(gm.getBoard().getMapdata());
            packet.setStayable(gm.checkStayable());

            setChanged();
            notifyObservers(packet);
        }

        else{
            packet.setPlaying(false);
            packet.setWinners(gm.getWinners());

            setChanged();
            notifyObservers(packet);
        }
    }
}

class GameManager{
    //Start Cell Position
    static final int START_POS_X = 5;
    static final int START_POS_Y = 1;

    private int num_player;
    private Board board;
    private List<Player> players;
    private List<Player> finished;
    private List<Player> winners;
    private CardManager cm;
    private Player curr_player;
    private int curr_player_num;
    private boolean playing = true;

    public GameManager(int num_player, String map_file){
        this.num_player = num_player;
        board = new Board(map_file, new int[] {START_POS_X, START_POS_Y});
        
        players = new ArrayList<Player>();
        for(int i = 0; i < num_player; i++)
            players.add(new Player("Player ".concat(String.valueOf(i + 1)), i));

        finished = new ArrayList<Player>();

        cm = new CardManager(players);
        curr_player_num = 0;
        curr_player = players.get(curr_player_num);

    }

    //Getter
    public Player getCurr_player() {
        return curr_player;
    }

    public Board getBoard() {
        return board;
    }

    public List<Player> getWinners() {
        return winners;
    }

    public boolean isPlaying(){
        return playing;
    }

    //Funtional Methods
    boolean movePiece(String[] dir){
        int curr_cell = curr_player.getPiece().getCell_num();
        int[] curr_pos = board.getCell_list().get(curr_cell).getPos();
        int[][] map_data = board.getMapdata();
        int[] next_pos = curr_pos.clone();
        int bridge_crossed = 0;

        for(int i = 0; i < dir.length; i++){
            int[] next_dir = {0,0};
            switch(dir[i]){
                case "U" : next_dir[0]--; break;
                case "D" : next_dir[0]++; break;
                case "L" : next_dir[1]--; break;
                case "R" : next_dir[1]++; break;
                default : break;
            }
            
            if(checkValidMove(next_dir, next_pos, map_data)){
                next_pos[0] += next_dir[0];
                next_pos[1] += next_dir[1];

                if(board.isFinish(board.posToCell(next_pos))){
                    scoreFinish();
                    cm.scoreCards(curr_player_num);
                    checkAnotherPlayer(curr_pos, map_data);

                    finished.add(curr_player);
                    players.remove(curr_player);
                    curr_player_num--;
                    num_player--;

                    map_data[curr_pos[0]][curr_pos[1]] = board.getCell_list().get(curr_cell).getAttribute();

                    if(num_player == 1){
                        curr_player = players.get(0);

                        scoreFinish();
                        cm.scoreCards(0);

                        finished.add(players.get(0));
                        winners = announceWinner();
                        playing = false;
                        return false;
                    }
                    else{
                        passTurn();
                        return true;
                    }
                }

                else if(board.isBridge(next_pos)){
                    next_pos[0] += next_dir[0];
                    next_pos[1] += next_dir[1];
                    bridge_crossed++;
                }
            }
            else
                return false;
        }

        if(finished.size() > 0 && board.isMoveBack(curr_pos, next_pos))
            return false;
        else{
            if(board.isTool(board.posToCell(next_pos))){
                int attribute = board.getCell_list().get(board.posToCell(next_pos)).getAttribute();
                cm.giveToolCard(curr_player_num, attribute);
            }
    
            for(int i = 0; i < bridge_crossed; i++)
                cm.giveBridgeCard(curr_player_num);
    
            map_data[curr_pos[0]][curr_pos[1]] = board.getCell_list().get(curr_cell).getAttribute();
            checkAnotherPlayer(curr_pos, map_data);
    
            curr_player.movePiece(board.posToCell(next_pos));
            map_data[next_pos[0]][next_pos[1]] = curr_player.getPiece().getPlayer_num();
    
            passTurn();
            return true;
        }
    }

    void checkAnotherPlayer(int[] curr_pos, int[][] map_data){
        for(int i = 0; i < num_player; i++){
            
            if(players.get(i) != curr_player){
                int[] pos = board.getCell_list().get(players.get(i).getPiece().getCell_num()).getPos();

                if(map_data[pos[0]][pos[1]] < 10 && (curr_pos[0] == pos[0] && curr_pos[1] == pos[1])){
                    map_data[curr_pos[0]][curr_pos[1]] = players.get(i).getPiece().getPlayer_num();
                    break;
                }
            }
        }
    }

    void stayTurn(){
        cm.takeBridgeCard(curr_player_num);
        passTurn();
    }

    void passTurn(){
        curr_player_num = (curr_player_num + 1) % num_player;
        curr_player = players.get(curr_player_num);

        int cell = curr_player.getPiece().getCell_num();
        int[] pos = board.getCell_list().get(cell).getPos();
        int[][] map_data = board.getMapdata();

        if(map_data[pos[0]][pos[1]] < 10)
            map_data[pos[0]][pos[1]] = curr_player.getPiece().getPlayer_num();
    }

    void scoreFinish(){
        int score = curr_player.getScore();

        //1st = +7 / 2nd = +3 / 3rd = +1
        switch(finished.size()){
            case 0 : score += 7; break;
            case 1 : score += 3; break;
            case 2 : score += 1; break;
            default : break;
        }

        curr_player.setScore(score);
    }

    List<Player> announceWinner(){
        List<Player> winnerList = new ArrayList<Player>();
        winnerList.add(finished.get(0));
        Player winner = winnerList.get(0);

        for(int i = 1; i < finished.size(); i++){
            if(winner.getScore() < finished.get(i).getScore()){
                winner = finished.get(i);
                winnerList.clear();
                winnerList.add(winner);
            }
            else if(winner.getScore() == finished.get(i).getScore()){
                winnerList.add(finished.get(i));
            }
        }
        return winnerList;
    }

    //Check State Methods
    boolean checkValidMove(int[] next_dir, int[] curr_pos, int[][] map_data){
        int[] next_pos = {curr_pos[0] + next_dir[0], curr_pos[1] + next_dir[1]};
        
        if(map_data[next_pos[0]][next_pos[1]] == 0)
            return false;
        else
            return true;
    }

    boolean checkStayable(){
        if(curr_player.hasBridgeCard())
            return true;
        else
            return false;
    }   
}

class CardManager{
    private List<Player> players;

    public CardManager(List<Player> players){
        this.players = players;
    }

    //Give Cards to Player
    void giveToolCard(int player_num, int tool){
        addToCardList(mkCard(tool), player_num);
    }

    void giveBridgeCard(int player_num){
        int num_bridges = players.get(player_num).getNum_bridges();

        players.get(player_num).setNum_bridges(num_bridges + 1);
        addToCardList(mkCard(2), player_num);
    }

    //Take Cards from Player
    void takeBridgeCard(int player_num){
        List<Card> cList = players.get(player_num).getCards();
        int num_bridges = players.get(player_num).getNum_bridges();

        for(int i = 0; i < cList.size(); i++){
            if(cList.get(i).getAttribute() == 2){
                cList.remove(i);
                break;
            }
        }

        players.get(player_num).setNum_bridges(num_bridges - 1);
    }

    //Supporting Methods
    Card mkCard(int attribute){
        Card new_card = new Card(attribute);
        return new_card;
    }

    void addToCardList(Card c, int player_num){
        List<Card> cList = players.get(player_num).getCards();
        cList.add(c);
        players.get(player_num).setCards(cList);
    }

    //Scoring Method
    void scoreCards(int player_num){
        /*cards[0] bridge [1]Driver [2]Hammer [3]Saw */
        int[] cards = players.get(player_num).countCards();
        int score = players.get(player_num).getScore();
        
        score = score + cards[1] + 2 * cards[2] + 3 * cards[3];
        players.get(player_num).setScore(score);
    }
}

class Player{
    private String name;
    private Piece piece;
    private List<Card> cards;
    private int num_bridges;
    private int score;

    public Player(String name, int player_num){
        this.name = name;
        piece = new Piece(player_num + 6);
        cards = new ArrayList<Card>();
        num_bridges = 0;
        score = 0;
    }

    //Getter
    public String getName() {
        return name;
    }

    public List<Card> getCards() {
        return cards;
    }

    public Piece getPiece() {
        return piece;
    }

    public int getNum_bridges() {
        return num_bridges;
    }

    public int getScore() {
        return score;
    }

    //Setter
    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public void setNum_bridges(int num_bridges) {
        this.num_bridges = num_bridges;
    }

    public void setScore(int score) {
        this.score = score;
    }

    //Funtional Methods
    void movePiece(int next_cell){
        piece.setCell_num(next_cell);
    }

    int rollDice(){
        return Dice.rollDice();
    }

    int[] countCards(){
        int[] num_cards = new int[] {0,0,0,0};
        for(int i = 0; i < cards.size(); i++){
            switch(cards.get(i).getAttribute()){
                case 2 : num_cards[0]++; break;
                case 3 : num_cards[1]++; break;
                case 4 : num_cards[2]++; break;
                case 5 : num_cards[3]++; break;
                default : break;
            }
        }
        return num_cards;
    }

    //Check State Methods
    boolean hasBridgeCard(){
        if(num_bridges > 0)
            return true;
        else
            return false;
    }
}

class Board{
    private int[] start_pos;
    private int[][] map_data;
    private List<Cell> cell_list;

    public Board(String map_file, int[] start_pos){

        this.start_pos = start_pos;
        cell_list = new ArrayList<Cell>();
        map_data = new int[20][20];
        for(int i = 0; i < 20; i++){
            for(int j = 0; j < 20; j++)
                map_data[i][j] = 0;
        }

        map_data = readMap(map_file);
    }

    //Getter
    public int[][] getMapdata(){
        return map_data;
    }

    public List<Cell> getCell_list() {
        return cell_list;
    }

    //Funtional Method
    int[][] readMap(String map_file){
        /* ------------cell_data------------
            0 no cell           6 Player1
            1 default cell      7 Player2
            2 Bridge            8 Player3
            3 PhilipsDriver     9 Player4
            4 Hammer            10 Start cell
            5 Saw               11 End cell
        */
        
        File file = new File(map_file);
        List<String> lines = new ArrayList<String>();
        Scanner input;

        try 
		{
			input = new Scanner(file);

            while (input.hasNext())
                lines.add(input.nextLine());

            input.close();
		} 
		catch(Exception e) 
		{ 
			System.out.println("File Not Found");
            System.exit(0);
		}

        String[] cell = lines.get(0).split(" ");
        String[] dir = Arrays.copyOfRange(cell, 1, cell.length);
        int[] curr_pos = start_pos;
        addCell(curr_pos,10, dir);

        for(int i = 1; i < lines.size(); i++){
            cell = lines.get(i).split(" ");
            dir = Arrays.copyOfRange(cell, 1, cell.length);

            switch(cell[0]){
                case "C": 
                    addCell(curr_pos,1, dir);
                    break;
                case "B": 
                    addBridge(curr_pos, "R");
                    addCell(curr_pos,1, dir);
                    break;
                case "b": 
                    addBridge(curr_pos, "L");
                    addCell(curr_pos,1, dir);
                    break;
                case "P": 
                    addCell(curr_pos,3, dir);
                    break;
                case "H":
                    addCell(curr_pos,4, dir);
                    break;
                case "S":
                    addCell(curr_pos,5, dir);
                    break;
                case "E":
                    addCell(curr_pos,11, dir);
                    break;
                default : 
                    break;
            }
        }
        return map_data;
    }

    //Supporting Methods
    private void addBridge(int[] curr_pos, String dir){
        if(dir == "R")
            map_data[curr_pos[0]][curr_pos[1] + 1] = 2;
        else if(dir == "L")
            map_data[curr_pos[0]][curr_pos[1] - 1] = 2;
    }

    private void addCell(int[] curr_pos, int attribute, String[] dir){
        int[] next_pos = {0,0};
        map_data[curr_pos[0]][curr_pos[1]] = attribute;
        cell_list.add(new Cell(attribute, curr_pos));

        for(int i = 0; i < dir.length; i++){
            switch(dir[i]){
                case "U" : 
                    next_pos[0] = curr_pos[0] - 1; 
                    next_pos[1] = curr_pos[1];
                    break;

                case "D" : 
                    next_pos[0] = curr_pos[0] + 1; 
                    next_pos[1] = curr_pos[1];
                    break;

                case "L" : 
                    next_pos[0] = curr_pos[0]; 
                    next_pos[1] = curr_pos[1] - 1;
                    break;

                case "R" : 
                    next_pos[0] = curr_pos[0]; 
                    next_pos[1] = curr_pos[1] + 1;
                    break;

                default : 
                    break;
            }

            if(next_pos[0] < 0){
                moveMapDown();
                next_pos[0]++;
            }
            else if (next_pos[0] >= 20){
                moveMapUP();
                next_pos[0]--;
            }
            
            if(map_data[next_pos[0]][next_pos[1]] == 0)
            map_data[next_pos[0]][next_pos[1]] = 1;
                 
        }
        curr_pos[0] = next_pos[0];
        curr_pos[1] = next_pos[1];
    }

    private void moveMapDown(){
        int[] pos;

        for(int i = 19; i > 0; i--){
            for(int j = 0; j < 20; j++)
                map_data[i][j] = map_data[i-1][j];
        }

        for(int i = 0; i < cell_list.size(); i++){
            pos = cell_list.get(i).getPos();
            cell_list.get(i).setPos(new int[] {pos[0] + 1, pos[1]});
        }
    }

    private void moveMapUP(){
        int[] pos;

        for(int i = 0; i < 19; i++){
            for(int j = 0; j < 20; j++)
                map_data[i][j] = map_data[i+1][j];
        }

        for(int i = 0; i < cell_list.size(); i++){
            pos = cell_list.get(i).getPos();
            cell_list.get(i).setPos(new int[] {pos[0] - 1, pos[1]});
        }
    }

    //Utility Method
    int posToCell(int[] pos){
        int cell = -1;

        for(int i = 0; i <cell_list.size(); i++){
            int[] cell_pos = cell_list.get(i).getPos();
            if(pos[0] == cell_pos[0] && pos[1] == cell_pos[1])
                cell = i;
        }

        return cell;
    }
    
    //Check State Methods
    boolean isBridge(int[] pos){
        if(map_data[pos[0]][pos[1]] == 2)
            return true;
        else
            return false;
    }

    boolean isTool(int cell_num){
        int attribute = cell_list.get(cell_num).getAttribute(); 
        if(attribute >= 3 && attribute <= 5)
            return true;
        else
            return false;
    }

    boolean isFinish(int cell_num){
        if(cell_num >= cell_list.size() - 1)
            return true;
        else
            return false;
    }

    boolean isMoveBack(int[] curr_pos, int[] next_pos){
        if(posToCell(curr_pos) > posToCell(next_pos))
            return true;
        else
            return false;
    }
    
}

//Card ADT
class Card{
    /* ----------CardInfo----------     
        2 Bridge    3 PhilipsDriver 
        4 Hammer    5 Saw               
    */
    private int attribute;

    public Card(int attribute){
        this.attribute = attribute;
    }

    //Getter
    public int getAttribute() {
        return attribute;
    }

}

//Cell ADT
class Cell{
    /* ------------cell_data------------
            0 no cell           6 Player1
            1 default cell      7 Player2
            2 Bridge            8 Player3
            3 PhilipsDriver     9 Player4
            4 Hammer            10 Start cell
            5 Saw               11 End cell
    */
    private int attribute;
    private int[] pos;

    public Cell(int attribute, int[] pos){
        this.attribute = attribute;
        this.pos = pos.clone();
    }

    //Getter
    public int getAttribute(){
        return attribute;
    }

    public int[] getPos() {
        return pos;
    }

    //Setter
    public void setPos(int[] pos) {
        this.pos = pos;
    }
}

//Dice ADT
class Dice{
    static int rollDice(){
        return (int)(Math.random() * 6) + 1;
    }
}

//Piece ADT
class Piece{
    private int cell_num;
    private int player_num;

    public Piece(int player_num){
        cell_num = 0;
        this.player_num = player_num;
    }

    //Getter
    public int getCell_num() {
        return cell_num;
    }

    public int getPlayer_num() {
        return player_num;
    }

    //Setter
    public void setCell_num(int cell_num) {
        this.cell_num = cell_num;
    }
}