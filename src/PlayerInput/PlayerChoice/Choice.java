package PlayerInput.PlayerChoice;

/**
 * Created by bt on 29/12/2014.
 */
public class Choice {

    private boolean endGo = false;

    public void toggleEndGo() {
        this.endGo = !this.endGo;
    }

    public void setEndGo(boolean endGo) {
        this.endGo = endGo;
    }

    public boolean isEndGo() {
        return endGo;
    }
}
