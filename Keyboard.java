

import java.util.LinkedList;
import java.util.List;

public class Keyboard {
    private List<Keycap> keycaps;

    public Keyboard() {
        this.keycaps = new LinkedList<Keycap>();
    }

    public void addKeycap(Keycap keycap) {
        this.keycaps.add(keycap);
    }

    public boolean isNeighbour(String keycap1, String keycap2){
        for (Keycap keycap : keycaps) {
            if (keycap.getSymbol().equals(keycap1)) {
                List<Keycap> neighbours = keycap.getNeighbouringKeycaps();
                for (Keycap neighbour : neighbours) {
                    if (neighbour.getSymbol().equals(keycap2)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void printKeyboard() {
        for (Keycap keycap : keycaps) {
            for (int j = 0; j < keycap.getNeighbouringKeycaps().size(); j++) {
                System.out.print(keycap.getSymbol() + ": \n");
            }
        }
    }
}
