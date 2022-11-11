package BoardGame;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

class View extends JFrame implements Observer{
	private static final int WIDTH = 1280;
	private static final int HEIGHT = 720;
	
	Model model;

	MapPanel mapPanel;
	StatePanel statePanel;
	Controller controller;
	
	public View(int num_player, String map_file, String name) {
		model = new Model(num_player, map_file);
		
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setSize(WIDTH, HEIGHT);
		setVisible(true);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setLayout(new GridLayout(1,2));

		int[][] map_data = model.getMapdata();
		int[] cards = new int[] {0,0,0,0};
		mapPanel = new MapPanel(map_data);
		statePanel = new StatePanel(name, 1, cards);
		controller = new Controller(model);
		
		JPanel playerPanel= new JPanel();
		playerPanel.setLayout(new GridLayout(2,1));
		playerPanel.add(statePanel);
		playerPanel.add(controller);
		
		add(mapPanel);
		add(playerPanel); 

		model.addObserver(this);
	}

	@Override
	public void update(Observable o, Object arg){
		UpdatePacket packet = (UpdatePacket)arg;

		boolean playing = packet.isPlaying();
		if(playing){
			boolean movable = packet.isMovable();
			if(movable){
				//move->
				mapPanel.setMap_data(packet.getMap_data());
				mapPanel.redraw();

				//move->
				String name = packet.getPlayer().getName();
				statePanel.name.setPlayerName(name);
				statePanel.name.redraw();
				controller.action.setRollButtonEnable();
				controller.clear();

				//move, stay->
				int[] cards = packet.getPlayer().countCards();
				statePanel.cardPanel.setCards(cards);
				statePanel.cardPanel.redraw();

				//move, stay->
				boolean stayable = packet.isStayable();
				if(stayable)
					controller.action.setStayButtonEnable();
				else
					controller.action.setStayButtonDisable();
			}

			else{
				//Roll->
				int diceNum = packet.getDiceNum();
				int numBridge = packet.getPlayer().getNum_bridges();
				int stepSize = diceNum - numBridge;
				if(stepSize < 0)
					stepSize = 0;

				statePanel.dice.setDice_num(diceNum);
				statePanel.dice.redraw();

				controller.move.clear();
				controller.move.setMoveNum(stepSize);
				controller.action.setRollButtonDisable();
				controller.action.setStayButtonDisable();
				controller.appearMovePanel();
			}
		}

		else{
			//Gameover->
			List<Player> winners = packet.getWinners();
			ResultWindow result = new ResultWindow(winners);
			result.setVisible(true);
    		result.setLocationRelativeTo(null);
		}
	}
}

class MapPanel extends JPanel implements Redrawalble{
    private static final int CELLSIZE = 30;
	private int[][] map_data;
	private ImageIcon[] cell_img;

	public MapPanel(int[][] map_data){
        /* ------------cell_data------------
            0 no cell           6 Player1
            1 default cell      7 Player2
            2 Bridge            8 Player3
            3 PhilipsDriver     9 Player4
            4 Hammer            10 Start cell
            5 Saw               11 End cell
        */

		this.map_data = map_data;
        cell_img = loadImg();
		setPreferredSize(new Dimension(WIDTH / 2, HEIGHT));
        setBackground(Color.white);
        setLayout(new GridLayout(20,20));
        
        for(int i = 0; i < 20; i++){
            for(int j = 0; j < 20; j++){
                drawCell(cell_img[this.map_data[i][j]]);
            } 
        }
	}

    private ImageIcon[] loadImg(){

		ImageIcon[] cell_img = new ImageIcon[12];
        cell_img[0] = new ImageIcon("IMG/cell/Blank.png");
		cell_img[1] = new ImageIcon("IMG/cell/Default.png");
        cell_img[2] = new ImageIcon("IMG/cell/Bridge.png");
		cell_img[3] = new ImageIcon("IMG/cell/PhilipsDriver.png");
        cell_img[4] = new ImageIcon("IMG/cell/Hammer.png");
        cell_img[5] = new ImageIcon("IMG/cell/Saw.png");
        cell_img[6] = new ImageIcon("IMG/cell/Player1.png");
        cell_img[7] = new ImageIcon("IMG/cell/Player2.png");
        cell_img[8] = new ImageIcon("IMG/cell/Player3.png");
        cell_img[9] = new ImageIcon("IMG/cell/Player4.png");
        cell_img[10] = new ImageIcon("IMG/cell/Start.png");
        cell_img[11] = new ImageIcon("IMG/cell/End.png");
        
		for(int i = 0; i < cell_img.length; i++){
			Image img = cell_img[i].getImage();
			Image scaled_img = img.getScaledInstance(CELLSIZE, CELLSIZE, Image.SCALE_SMOOTH);
			ImageIcon scaled_icon = new ImageIcon(scaled_img);
			cell_img[i] = scaled_icon;
		}

		return cell_img;
	}
    
    private void drawCell(ImageIcon cell_img){
		JLabel cell = new JLabel(cell_img);
		cell.setHorizontalAlignment(JLabel.CENTER);
        add(cell);
    }

	//Setter
	public void setMap_data(int[][] map_data) {
		this.map_data = map_data;
	}

	@Override
	public void redraw(){
		removeAll();

		setPreferredSize(new Dimension(WIDTH / 2, HEIGHT));
        setBackground(Color.white);
        setLayout(new GridLayout(20,20));
        
        for(int i = 0; i < 20; i++){
            for(int j = 0; j < 20; j++){
                drawCell(cell_img[this.map_data[i][j]]);
            } 
        }

		revalidate();
		repaint();
	}

}

class StatePanel extends JPanel{
	Name name;
	DiceIcon dice;
	CardPanel cardPanel;

	public StatePanel(String name, int num, int[] cards){
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
		GridBagConstraints[] gbc = new GridBagConstraints[3];
		setLayout(new GridBagLayout());
		for(int i = 0; i < gbc.length; i++){
            gbc[i] = new GridBagConstraints();
            gbc[i].weightx = 1.0;
            gbc[i].weighty = 1.0;
            gbc[i].fill = GridBagConstraints.BOTH;
        }
        
		this.name =  new Name(name);
		dice = new DiceIcon(num);
		cardPanel = new CardPanel(cards);

        setGBLayout(this.name,0,0,4,1,gbc[0]);
        setGBLayout(dice,0,1,1,1,gbc[1]);
        setGBLayout(cardPanel,1,1,3,1,gbc[2]);
	}

	//LayoutManager
    private void setGBLayout(Component obj, int x, int y,int width, int height, GridBagConstraints c){
        c.gridx=x;
        c.gridy=y;
        c.gridwidth=width;
        c.gridheight=height;
        add(obj,c);
    }
}

class Name extends JLabel implements Redrawalble{
	private String name;

	public Name(String name){
		super();
		this.name = name;
		setText(this.name);
		setHorizontalAlignment(CENTER);
		setFont(new Font(null, Font.PLAIN, 50));
	}

	//Setter
	void setPlayerName(String name){
		this.name = name;
	}

	@Override
	public void redraw(){
		setText(this.name);
		setHorizontalAlignment(CENTER);
		revalidate();
		repaint();
	}
}

class DiceIcon extends JLabel implements Redrawalble{
	private static final int DICESIZE = 120;
	private int dice_num;
	private ImageIcon[] dice_img;

	public DiceIcon(int dice_num){
		super();
		this.dice_num = dice_num;
		dice_img = loadImg();
		setIcon(this.dice_img[this.dice_num - 1]);
		setHorizontalAlignment(CENTER);
	}

	private ImageIcon[] loadImg(){

		ImageIcon[] dice_img = new ImageIcon[6];
		dice_img[0] = new ImageIcon("IMG/dice/1.png");
		dice_img[1] = new ImageIcon("IMG/dice/2.png");
        dice_img[2] = new ImageIcon("IMG/dice/3.png");
        dice_img[3] = new ImageIcon("IMG/dice/4.png");
        dice_img[4] = new ImageIcon("IMG/dice/5.png");
        dice_img[5] = new ImageIcon("IMG/dice/6.png");

		for(int i = 0; i < dice_img.length; i++){
			Image img = dice_img[i].getImage();
			Image scaled_img = img.getScaledInstance(DICESIZE, DICESIZE, Image.SCALE_SMOOTH);
			ImageIcon scaled_icon = new ImageIcon(scaled_img);
			dice_img[i] = scaled_icon;
		}

		return dice_img;
	}

	//Setter
	public void setDice_num(int dice_num) {
		this.dice_num = dice_num;
	}

	@Override
	public void redraw(){
		setIcon(this.dice_img[this.dice_num - 1]);
		setHorizontalAlignment(CENTER);
		revalidate();
		repaint();
	}
}

class CardPanel extends JPanel implements Redrawalble{
	private static final int CARDSIZE = 30;
	private int[] cards;
	private ImageIcon[] card_img;


	public CardPanel(int[] cards){
		this.cards = cards;
		card_img = loadImg();

		GridBagConstraints[] gbc = new GridBagConstraints[IntStream.of(cards).sum()];
		setLayout(new GridBagLayout());

		int num = 0;
		for(int i = 0; i < this.cards.length; i++){
			for(int j = 0; j < cards[i]; j++){
				gbc[num] = new GridBagConstraints();
				gbc[num].gridx = j;
				gbc[num].gridy = i;
				
				add(new JLabel(card_img[i]), gbc[num]);
				num++;
			}
		}

	}

	private ImageIcon[] loadImg(){

		ImageIcon[] card_img = new ImageIcon[4];
		card_img[0] = new ImageIcon("IMG/cards/Bridge.png");
		card_img[1] = new ImageIcon("IMG/cards/PhilipsDriver.png");
		card_img[2] = new ImageIcon("IMG/cards/Hammer.png");
		card_img[3] = new ImageIcon("IMG/cards/Saw.png");

		for(int i = 0; i < card_img.length; i++){
			Image img = card_img[i].getImage();
			Image scaled_img = img.getScaledInstance(CARDSIZE, CARDSIZE*2 - 15, Image.SCALE_SMOOTH);
			ImageIcon scaled_icon = new ImageIcon(scaled_img);
			card_img[i] = scaled_icon;
		}

		return card_img;
	}

	//Setter
	public void setCards(int[] cards){
		this.cards = cards;
	}

	@Override
	public void redraw(){
		removeAll();

		GridBagConstraints[] gbc = new GridBagConstraints[IntStream.of(this.cards).sum()];
		setLayout(new GridBagLayout());

		int num = 0;
		for(int i = 0; i < this.cards.length; i++){
			for(int j = 0; j < cards[i]; j++){
				gbc[num] = new GridBagConstraints();
				gbc[num].gridx = j;
				gbc[num].gridy = i;
				
				add(new JLabel(card_img[i]), gbc[num]);
				num++;
			}
		}

		revalidate();
		repaint();
	}
}