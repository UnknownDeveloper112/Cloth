import javax.swing.*;
import java.awt.*;

public class Main extends JFrame{

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
