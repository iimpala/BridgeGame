package BoardGame;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Controller extends JPanel{
    Model model;
    ActionPanel action;
    MoveButtonPanel move;

    public Controller(Model model){
        this.model = model;
        action = new ActionPanel();
        move = new MoveButtonPanel();

        setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
		GridBagConstraints[] gbc = new GridBagConstraints[2]; 
		setLayout(new GridBagLayout());
		for(int i = 0; i < gbc.length; i++){
            gbc[i] = new GridBagConstraints();
            gbc[i].weightx = 1.0;
            gbc[i].weighty = 1.0;
            gbc[i].fill = GridBagConstraints.BOTH;
        }
        
        setGBLayout(action,0,0,1,1,gbc[0]);
        setGBLayout(move,0,1,1,3,gbc[1]);

        move.setVisible(false);
    }

    //LayoutManager
    private void setGBLayout(Component obj, int x, int y,int width, int height, GridBagConstraints c){
        c.gridx=x;
        c.gridy=y;
        c.gridwidth=width;
        c.gridheight=height;
        add(obj,c);
    }

    //Controller Methods
    public void clear(){
        move.clear();
        move.setVisible(false);
    }

    public void appearMovePanel(){
        move.setVisible(true);
    }

    //InnerClass - ActionPanel
    class ActionPanel extends JPanel{
        private StayButton stay;
        private RollButton roll;

        public ActionPanel(){
            setBorder(BorderFactory.createEmptyBorder(50 , 20 , 50 , 20));
            setLayout(new GridLayout(1,2, 10, 10));
    
            stay = new StayButton();
            roll = new RollButton();
    
            roll.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    model.rollDice();
                }
            });

            stay.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    model.stayTurn();
                }
            });

            add(stay);
            add(roll);

            setStayButtonDisable();
        }
    
        //InnerClass - StayButton
        private class StayButton extends JButton{
            public StayButton(){
                super("STAY");
                setFont(new Font(null, Font.PLAIN, 50));
            }
        }

        //StayButton UI method
        public void setStayButtonDisable(){
            stay.setEnabled(false);
        }

        public void setStayButtonEnable(){
            stay.setEnabled(true);
        }
    
        //InnerClass - RollButton
        private class RollButton extends JButton{
            public RollButton(){
                super("ROLL");
                setFont(new Font(null, Font.PLAIN, 50));
            }
        }

        //RollButton UI method
        public void setRollButtonDisable(){
            roll.setEnabled(false);
        }

        public void setRollButtonEnable(){
            roll.setEnabled(true);
        }
    }
    
    //InnerClass - MoveButtonPanel
    class MoveButtonPanel extends JPanel{
        private DirectionBoard direction_board;
        private DirectionButton direction_button;
        private String[] dir;
        private int move_num;

        public MoveButtonPanel(){
            dir = new String[] {"","","","","",""};
            move_num = 1;
            
            GridBagConstraints[] gbc = new GridBagConstraints[3];
            setLayout(new GridBagLayout());
            for(int i = 0; i < gbc.length; i++){
                gbc[i] = new GridBagConstraints();
                gbc[i].weightx = 1.0;
                gbc[i].weighty = 1.0;
                gbc[i].fill = GridBagConstraints.BOTH;
            }

            direction_board = new DirectionBoard(dir);
            direction_button = new DirectionButton(dir, direction_board, move_num);
            MoveButton move = new MoveButton();

            move.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    if(checkValidDirection())
                        model.move(dir);
                }
            });

            setGBLayout(direction_board,0,0,4,1,gbc[0]);
            setGBLayout(direction_button,0,1,2,2,gbc[1]);
            setGBLayout(move,2,1,2,2,gbc[2]);
        }
    
        //LayoutManager
        private void setGBLayout(Component obj, int x, int y,int width, int height, GridBagConstraints c){
            c.gridx=x;
            c.gridy=y;
            c.gridwidth=width;
            c.gridheight=height;
            add(obj,c);
        }

        //Setter
        public void setMoveNum(int move_num){
            this.move_num = move_num;
            this.direction_button.setMove_num(move_num);
        }

        //Functional Method
        private boolean checkValidDirection(){
            int top = 0;
                for(int i = 0; i < this.dir.length; i++){
                    if(this.dir[i] != "")
                        top++;
                }
            
            if(this.move_num == top)
                return true;
            else
                return false;
        }

        //UI Method
        public void clear(){
            this.move_num = 0;
            for(int i = 0; i < 6; i++)  
                this.dir[i] = "";

            this.direction_button.clear();
            this.direction_board.setDir(this.dir);
            this.direction_board.redraw();
        }
    
        //InnerClass - DirectionBoard
        private class DirectionBoard extends JPanel implements Redrawalble{
            private String[] dir;

            public DirectionBoard(String[] dir){
                this.dir = dir;
            }

            //Setter(private)
            private void setDir(String[] dir){
                this.dir = dir;
            }

            @Override
            public void redraw(){
                removeAll();

                setBorder(BorderFactory.createEmptyBorder(0, 10 , 10 , 10));
                setLayout(new GridLayout(1,6));
    
                for(int i = 0; i < dir.length; i++){
                    JLabel j = new JLabel(this.dir[i]);
                    j.setHorizontalAlignment(JLabel.LEFT);
                    j.setFont(new Font(null, Font.BOLD, 30));
                    add(j);
                }

                revalidate();
		        repaint();
            }
        }
    

        //InnerClass - DirectionButton
        private class DirectionButton extends JPanel{
            private DirectionBoard direction_board;
            private String[] dir;
            private int top;
            private int move_num;

    
            public DirectionButton(String[] dir, DirectionBoard direction_board, int move_num){
                this.direction_board = direction_board;
                this.dir = dir;
                this.move_num = move_num;
                top = 0;

                JButton u = new JButton("U");
                JButton d = new JButton("D");
                JButton r = new JButton("R");
                JButton l = new JButton("L");
                JButton b = new JButton("<");
                
                setDirectionButton(u, "U");
                setDirectionButton(d, "D");
                setDirectionButton(r, "R");
                setDirectionButton(l, "L");
                setDeleteButton(b);

                GridBagConstraints[] gbc = new GridBagConstraints[5];
                setLayout(new GridBagLayout());
                for(int i = 0; i < gbc.length; i++){
                    gbc[i] = new GridBagConstraints();
                    gbc[i].weightx = 1.0;
                    gbc[i].weighty = 1.0;
                    gbc[i].fill = GridBagConstraints.BOTH;
                }
    
                setGBLayout(u,1,0,1,1,gbc[0]);
                setGBLayout(d,1,1,1,1,gbc[1]);
                setGBLayout(r,2,1,2,1,gbc[2]);
                setGBLayout(l,0,1,1,1,gbc[3]);
                setGBLayout(b,2,0,1,1,gbc[4]);
            }

            //LayoutManager            
            private void setGBLayout(Component obj, int x, int y,int width, int height, GridBagConstraints c){
                c.gridx=x;
                c.gridy=y;
                c.gridwidth=width;
                c.gridheight=height;
                add(obj,c);
            }

            //Setter(private)
            private void setMove_num(int move_num) {
                this.move_num = move_num;
            }

            //SetButton Methods
            private void setDirectionButton(JButton btn, String dir){
                btn.setFont(new Font(null, Font.PLAIN, 30));
                btn.addActionListener(new ActionListener(){ 
                    public void actionPerformed(ActionEvent e){
                        addDirection(dir);
                    }
                });
            }

            private void setDeleteButton(JButton btn){
                btn.setFont(new Font(null, Font.PLAIN, 30));
                btn.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        delDirection();
                    }
                });
            }

            //Button Funtional Methods
            private void addDirection(String s){
                
                if(this.top < this.move_num){
                    this.dir[top] = s;
                    this.top++;

                    this.direction_board.setDir(this.dir);
                    this.direction_board.redraw();

                }
                else
                    return;
            }

            private void delDirection(){
                
                if(this.top > 0){
                    this.dir[top - 1] = "";
                    this.top--;

                    this.direction_board.setDir(this.dir);
                    this.direction_board.redraw();
                }
                else
                    return;
            }
            
            //Button UI Method
            private void clear(){
                this. top = 0;

                for(int i = 0; i < 6; i++)  
                this.dir[i] = "";
            }
        }
    
        //InnerClass - MoveButton
        private class MoveButton extends JButton{
            public MoveButton(){
                super("Move");
                setFont(new Font(null, Font.PLAIN, 50));
            }
        }
    }
}