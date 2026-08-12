import javax.swing.*;
import java.awt.*;

public class Drawer extends JPanel {

    static int WIDTH;
    static int HEIGHT;
    static final double FRICTION=0.99;//0.99
    static final double GRAVITY=0.25;//0.25
    static final double MAX_WIND=0.15;//0.15
    static final int COLUMNS=25;//5
    static final int ROWS =30;//6
    static final int POINTS=ROWS*COLUMNS;
    static final int LENGTH=20;//100
    static final double STICK_STIFFNESS=0.5;//0.8
    static final double DIAGONAL_STIFFNESS=0.002;//0.001
    static final int DELAY = 5;
    static final boolean ENLARGE_POINTS = false;

    static Point[] points=new Point[POINTS];
    static Stick[] sticks=new Stick[(ROWS-1)*COLUMNS+POINTS-ROWS];
    static Stick[] diagonals=new Stick[(COLUMNS-1)*(ROWS-1)*2];
    static int COUNT = 0;

    public static void init(){
        //setting points
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                points[row * COLUMNS + col] = new Point(WIDTH / 2.0 + (col - COLUMNS/2) * LENGTH, LENGTH * row + 50, false);
            }
        }

        //setting sticks
        int j = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                if (column + 1 < COLUMNS) {
                    sticks[j++] = new Stick(points[row * COLUMNS + column], points[row * COLUMNS + column + 1], true,STICK_STIFFNESS);
                }
                if (row + 1 < ROWS) {
                    sticks[j++] = new Stick(points[row * COLUMNS + column], points[row * COLUMNS + column + COLUMNS], true,STICK_STIFFNESS);
                }
            }
        }

        //setting diagonlas
        j = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS -1; column++) {
                if (row + 1 < ROWS) {
                    diagonals[j++] = new Stick(points[row * COLUMNS + column], points[(row+1) * COLUMNS + column + 1], true,DIAGONAL_STIFFNESS);
                    diagonals[j++] = new Stick(points[(row+1) * COLUMNS + column], points[row * COLUMNS + column + 1], true,DIAGONAL_STIFFNESS);
                }
            }
        }

        //declaring fixed points and disabled sticks
        points[0].fixed(true);
        points[COLUMNS -1].fixed(true);
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
        if(ENLARGE_POINTS) for (Point point : points) g.drawArc((int)point.x-5,(int)point.y-5,10,10,0,360);
        for (Stick stick : sticks)if(stick.enabled) g.drawLine((int)stick.startX, (int)stick.startY, (int)stick.endX, (int)stick.endY);

        //Changing math variables and moving points
        double WIND = wind(COUNT);
        for (Point point : points){
            if(COUNT>0)point.vy=point.y-point.oldY;
            if(COUNT>0)point.vx=point.x-point.oldX;

            point.oldX=point.x;
            point.oldY=point.y;

            if(!point.fixed)point.vx+=WIND;
            if(!point.fixed)point.vy+=GRAVITY;
            if(!point.fixed)point.vy*=FRICTION;
            if(!point.fixed)point.vx*=FRICTION;
            point.y+=point.vy;
            point.x+=point.vx;
        }
        for(int i=0;i<100;i++) {
            for (Stick stick : sticks) {
                if (stick.enabled) stick.sticks();
            }
        }
        for (Stick stick : sticks) if (stick.enabled) stick.editStick();

        for(int i=0;i<100;i++) {
            for (Stick stick : diagonals) {
                if (stick.enabled) stick.sticks();
            }
        }
        for (Stick stick : diagonals) if (stick.enabled) stick.editStick();

        //Delay
        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        COUNT++;
        repaint();
    }

    public static double wind(int COUNT){
        double base = MAX_WIND*(COUNT%200)/200;
        if(!((COUNT/200)%4>=3)) base = 0;
        return base + ((1-2*Math.random())*(COUNT%200)/2000);
    }
}
