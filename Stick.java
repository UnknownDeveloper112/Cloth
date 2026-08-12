public class Stick {

    public double startX;
    public double startY;
    public double endX;
    public double endY;
    private final Point p1;
    private final Point p2;
    private final double length;
    public boolean enabled;
    public double stiffness;

    public Stick(Point p1,Point p2,boolean enabled,double stiffness){
        startX=p1.x;
        startY=p1.y;
        endX=p2.x;
        endY=p2.y;
        this.p1=p1;
        this.p2=p2;
        this.enabled=enabled;
        length=Math.sqrt(Math.pow(startX-endX,2)+Math.pow(startY-endY,2));
        this.stiffness = stiffness;
    }

    public void editStick(){
        startX=(int)p1.x;
        startY=(int)p1.y;
        endX=(int)p2.x;
        endY=(int)p2.y;
    }

    public void sticks() {
        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;
        double pseudoLength = Math.sqrt(dx * dx + dy * dy);
        double diff = (pseudoLength - length) / pseudoLength;
        double correctionX = dx * diff * stiffness * 0.5;
        double correctionY = dy * diff * stiffness * 0.5;
        if (!p1.fixed && !p2.fixed) {
            p1.x += correctionX;
            p1.y += correctionY;
            p2.x -= correctionX;
            p2.y -= correctionY;
        } else if (p1.fixed && !p2.fixed) {
            p2.x -= correctionX * 2;
            p2.y -= correctionY * 2;
        } else if (!p1.fixed) {
            p1.x += correctionX * 2;
            p1.y += correctionY * 2;
        }
    }

}
