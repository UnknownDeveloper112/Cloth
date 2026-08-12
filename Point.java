public class Point {

    double x;
    double y;
    double oldX;
    double oldY;
    double vx;
    double vy;
    boolean fixed;

    public Point(double x,double y,boolean fixed){
        this.x=x;
        this.y=y;
        this.fixed=fixed;
    }

    public void fixed(boolean fix){
        if(fix) {
            this.vx = 0;
            this.vy = 0;
        }
        this.fixed=fix;
    }

}
