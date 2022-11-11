package BoardGame;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    int player_num;
    String file_name;

    public ConsoleUI(int player_num, String file_name, String player_name){
        this.player_num = player_num;
        this.file_name = file_name;

        Scanner input = new Scanner(System.in);

        System.out.println("Write File name(without '.map')");
        this.file_name = input.nextLine();

        System.out.println("Write Player number");
        this.player_num = Integer.valueOf(input.nextLine());

        String map_file = "map/" + this.file_name + ".map";
        new TUIView(this.player_num, map_file, player_name, input);
    }
}

class TUIView implements Observer{
    Model model;
    String player_name;
    String[] dir;
    int[][] map_data;
    int[] cards;
    int stepSize;
    boolean playing;

    public TUIView(int player_num, String map_file, String player_name, Scanner input){
        model = new Model(player_num, map_file);
        model.addObserver(this);

        String command = "";

        this.player_name = player_name;
        dir = new String[] {"","","","","",""};
        map_data = model.getMapdata();
        cards = new int[] {0,0,0,0};
        playing = true;

        clear();
        drawMap();
        drawState();

        while(playing){
            System.out.println("Choose 'stay' or 'roll'");
            command = input.nextLine();

            if(command.equals("roll")){
                model.rollDice();

                System.out.println("write direction");
                String moveDir = inputMove(stepSize, input);

                for(int i = 0; i < stepSize; i++){
                    dir[i] = String.valueOf(moveDir.charAt(i));
                }

                model.move(dir);
                clearDir();

                clear();
                drawMap();
                drawState();
            }
            else if(command.equals("stay")){
                model.stayTurn();
                clear();
                drawMap();
                drawState();
            } 

        }

        input.close();
    }

    @Override
    public void update(Observable o, Object arg){
        UpdatePacket packet = (UpdatePacket)arg;
        boolean playing = packet.isPlaying();
        if(playing){
            boolean movable = packet.isMovable();
            if(movable){
                map_data = packet.getMap_data();
                player_name = packet.getPlayer().getName();
                cards = packet.getPlayer().countCards();
            }
            else{
                int dice_num = packet.getDiceNum();
                int numBridge = packet.getPlayer().getNum_bridges();
				stepSize = dice_num - numBridge;
                if(stepSize < 0)
					stepSize = 0;

                drawDice(dice_num);
            }
        }
        else{
            List<Player> winners = packet.getWinners();
            int numWinners = winners.size();
            System.out.println("***WInner***");

            for(int i = 0; i < numWinners; i++){
                String name = winners.get(i).getName();
                System.out.println(name);
            }

            System.exit(0);
        }

    }

    String inputMove(int stepSize, Scanner input){
        String dir = "";

        dir = input.nextLine();
        while(true){
            if(dir.length() != stepSize){
                System.out.println("rewrite direction");
                dir = input.nextLine();
            }
            else
                break;
        }
        
        dir = dir.toUpperCase();
        return dir;
    }

    void drawDice(int dice_num){
        System.out.printf("DICE : %d\n", dice_num);
    }

    void drawMap(){
        for(int i = 0; i < 20; i++){
            for(int j = 0; j < 20; j++){
                switch(map_data[i][j]){
                    case 0 : System.out.print("   "); break;
                    case 1 : System.out.print("C  "); break;
                    case 2 : System.out.print("B  "); break;
                    case 3 : System.out.print("P  "); break;
                    case 4 : System.out.print("H  "); break;
                    case 5 : System.out.print("S  "); break;
                    case 6 : System.out.print("P1 "); break;
                    case 7 : System.out.print("P2 "); break;
                    case 8 : System.out.print("P3 "); break;
                    case 9 : System.out.print("P4 "); break;
                    case 10 : System.out.print("ST "); break;
                    case 11 : System.out.print("E  "); break;
                    default : break;
                }
            }
            System.out.println();
        }
    }

    void drawState(){
        System.out.printf("current player is %s \n", player_name);
        System.out.println("*cards*");
        System.out.printf("bridge : %d\n", cards[0]);
        System.out.printf("Philips driver : %d\n", cards[1]);
        System.out.printf("Hammer : %d\n", cards[2]);
        System.out.printf("Saw : %d\n", cards[3]);
    }

    void clear(){
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    void clearDir(){
        for(int i = 0; i < 6; i++)
            dir[i] = "";
    }
}
