import javax.swing.*;
import java.awt.*;

public class Main extends JFrame{

    public static final double FRICTION=0.995;//0.995
    public static final double GRAVITY=0.25;//0.25
    public static final double MAX_WIND=0.15;//0.15
    public static final int COLUMNS=25;//25
    public static final int ROWS =30;//30
    public static final int POINTS=ROWS*COLUMNS;
    public static final int LENGTH=20;//20
    public static final double STICK_STIFFNESS=0.8;//0.8
    public static final double DIAGONAL_STIFFNESS=0.001;//0.001
    public static final int DELAY = 2;
    public static final boolean ENLARGE_POINTS = false;

    public Main(){
        Drawer d=new Drawer();
        this.setTitle("Cloth");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setBackground(Color.BLACK);
        this.add(d);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        // write your code here
        Main m=new Main();
    }
}
