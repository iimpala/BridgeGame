package BoardGame;
import java.util.List;
import java.util.Vector;

interface Redrawalble{
    void redraw();
}

interface Observer {
    void update(Observable o, Object arg);
}

class Observable {
    private boolean changed = false;
    private Vector<Observer> obs;

    public Observable() {
        obs = new Vector<>();
    }

    public synchronized void addObserver(Observer o) {
        if (o == null)
            throw new NullPointerException();
        if (!obs.contains(o)) {
            obs.addElement(o);
        }
    }

    public synchronized void deleteObserver(Observer o) {
        obs.removeElement(o);
    }

    public void notifyObservers() {
        notifyObservers(null);
    }

    public void notifyObservers(Object arg) {

        Object[] arrLocal;

        synchronized (this) {
            if (!changed)
                return;
            arrLocal = obs.toArray();
            clearChanged();
        }

        for (int i = arrLocal.length-1; i>=0; i--)
            ((Observer)arrLocal[i]).update(this, arg);
    }

    protected synchronized void clearChanged() {
        changed = false;
    }
    
    protected synchronized void setChanged() {
        changed = true;
    }
}

class UpdatePacket{
    private int[][] map_data;
    private int diceNum;
    private Player player;
    private boolean Stayable;
    private boolean movable;
    private boolean playing;
    private List<Player> winners;

    public UpdatePacket(Player player){
        this.player = player;
        playing = true;
    }

    //Setter
    public void setDiceNum(int diceNum) {
        this.diceNum = diceNum;
    }

    public void setMap_data(int[][] map_data) {
        this.map_data = map_data;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public void setStayable(boolean Stayable) {
        this.Stayable = Stayable;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public void setWinners(List<Player> winners) {
        this.winners = winners;
    }


    //Getter
    public int getDiceNum() {
        return diceNum;
    }

    public int[][] getMap_data() {
        return map_data;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isStayable(){
        return Stayable;
    }

    public boolean isMovable(){
        return movable;
    }

    public boolean isPlaying(){
        return playing;
    }
    
    public List<Player> getWinners() {
        return winners;
    }

}