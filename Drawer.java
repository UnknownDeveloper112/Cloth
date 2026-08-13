import javax.swing.*;
import java.awt.*;

public class Drawer extends JPanel {

    public static int WIDTH;
    public static int HEIGHT;
    static Point[] points=new Point[Main.POINTS];
    static Stick[] sticks=new Stick[(Main.ROWS-1)* Main.COLUMNS+ Main.POINTS- Main.ROWS];
    static Stick[] diagonals=new Stick[(Main.COLUMNS-1)*(Main.ROWS-1)*2];
    static int COUNT = 0;
    static double frameRate;
    static double time = 0;

    public static void init(){
        //setting points
        for (int row = 0; row < Main.ROWS; row++) {
            for (int col = 0; col < Main.COLUMNS; col++) {
                points[row * Main.COLUMNS + col] = new Point(WIDTH / 2.0 + (col - Main.COLUMNS/2) * Main.LENGTH, Main.LENGTH * row + 50, false);
            }
        }

        //setting sticks
        int j = 0;
        for (int row = 0; row < Main.ROWS; row++) {
            for (int column = 0; column < Main.COLUMNS; column++) {
                if (column + 1 < Main.COLUMNS) {
                    sticks[j++] = new Stick(points[row * Main.COLUMNS + column], points[row * Main.COLUMNS + column + 1], true, Main.STICK_STIFFNESS);
                }
                if (row + 1 < Main.ROWS) {
                    sticks[j++] = new Stick(points[row * Main.COLUMNS + column], points[row * Main.COLUMNS + column + Main.COLUMNS], true, Main.STICK_STIFFNESS);
                }
            }
        }

        //setting diagonlas
        j = 0;
        for (int row = 0; row < Main.ROWS; row++) {
            for (int column = 0; column < Main.COLUMNS -1; column++) {
                if (row + 1 < Main.ROWS) {
                    diagonals[j++] = new Stick(points[row * Main.COLUMNS + column], points[(row+1) * Main.COLUMNS + column + 1], true, Main.DIAGONAL_STIFFNESS);
                    diagonals[j++] = new Stick(points[(row+1) * Main.COLUMNS + column], points[row * Main.COLUMNS + column + 1], true, Main.DIAGONAL_STIFFNESS);
                }
            }
        }

        //declaring fixed points and disabled sticks
        points[0].fixed(true);
        points[Main.COLUMNS -1].fixed(true);
    }

    public void paintComponent(Graphics g){
        //set HEIGHT and WIDTH
        WIDTH=getWidth();
        HEIGHT=getHeight();

        //initialisation (1 time only)
        if(COUNT==0) init();

        //Erase and repaint points and sticks
        g.setColor(Color.BLACK);
        g.fillRect(0,0,WIDTH,HEIGHT);
        g.setColor(Color.CYAN);
        if(Main.ENLARGE_POINTS) for (Point point : points) g.drawArc((int)point.x-5,(int)point.y-5,10,10,0,360);
        for (Stick stick : sticks)if(stick.enabled) g.drawLine((int)stick.startX, (int)stick.startY, (int)stick.endX, (int)stick.endY);

        //Changing math variables and moving points
        double WIND = wind(COUNT);
        for (Point point : points){
            if(COUNT>0)point.vy=point.y-point.oldY;
            if(COUNT>0)point.vx=point.x-point.oldX;

            point.oldX=point.x;
            point.oldY=point.y;

            if(!point.fixed)point.vx+=WIND;
            if(!point.fixed)point.vy+= Main.GRAVITY;
            if(!point.fixed)point.vy*= Main.FRICTION;
            if(!point.fixed)point.vx*= Main.FRICTION;
            point.y+=point.vy;
            point.x+=point.vx;
        }
        for(int i=0;i<10;i++) {
            for (Stick stick : sticks) {
                if (stick.enabled) stick.sticks();
            }
        }
        for (Stick stick : sticks) if (stick.enabled) stick.editStick();

        for(int i=0;i<10;i++) {
            for (Stick stick : diagonals) {
                if (stick.enabled) stick.sticks();
            }
        }
        for (Stick stick : diagonals) if (stick.enabled) stick.editStick();

        frameRate=Math.round(10000000000D/(System.nanoTime()-time))/10.0;
        time=System.nanoTime();
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial",Font.PLAIN,25));
        g.drawString(Double.toString(frameRate),10,HEIGHT-10);

        //Delay
        try {
            Thread.sleep(Main.DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        COUNT++;
        repaint();
    }

    public static double wind(int COUNT){
        double base = Main.MAX_WIND*(COUNT%200)/200;
        if(!((COUNT/200)%5>=4)) return 0;
        return base + ((1-2*Math.random())*(COUNT%200)/2000);
    }
}
