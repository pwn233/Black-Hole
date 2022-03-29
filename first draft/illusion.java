import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.awt.geom.*;
class illusion extends JPanel implements Runnable{
    //testing
    Color color = new Color(245,245,245);
    //color
    Color COBALT_BLUE = new Color(0, 71, 171);
    Color YELLOW = new Color(245, 189, 31);
    Color RED_BLOOD = new Color(138, 3, 3);
    //action
    double rotate = 0;
    double splitTime = 2500;
    double velocity = 500;
    double angle = -65;
    double velocityX, velocityY, velocityX1, velocityY1;
    double xc = 290; double yc = 280;
    public static void main(String[] argsS)  {
        illusion m = new illusion();
        JFrame f = new JFrame();
        f.add(m);
        f.setTitle("ILLUSION");
        f.setSize(600, 600);
        f.setBackground(Color.WHITE);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
        
        (new Thread(m)).start();
    }
    
    public void run() {
        double lastTime = System.currentTimeMillis();
        double currentTime, elapsedTime;
        while(true) {
            //control timeout
            currentTime = System.currentTimeMillis();
            elapsedTime = currentTime - lastTime;
            //update logic 
            rotate += 1 * (elapsedTime / 1000);
            //display
            repaint();
            //control time
            lastTime = currentTime;
        }
    }
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);    
        g2.fillRect(0, 0, 600, 600); 
        AffineTransform originalRotate = g2.getTransform();        //anchor = x + width/2, y +height/2
        for(int i = 0; i < 67; i++) {
            if(i%2 == 0) g2.setColor(Color.RED);
            else if(i%2 == 1) g2.setColor(Color.BLACK); 
            g2.rotate(rotate,  (int)xc-i,(int) yc-i);
            fillMidpointCircle(g2, (int)xc-i,(int) yc-i, 200-i*3);
        } 
        g2.setTransform(originalRotate);
        g2.setColor(Color.BLACK);   
        eye(g2, (int)xc, (int)yc,  120, 205);
        g2.setColor(Color.WHITE);   
        eye(g2, (int)xc, (int)yc,  117, 202);
    }
    public void eye(Graphics g, int xc, int yc, int a, int b) {
            for(; a <= b*2; a++) {
                midPointEllipse(g, (int)xc, (int)yc, a, b);
            }
        
    }
    public void midPointEllipse(Graphics g, int xc, int yc, int a, int b) {
        //region 1
        int x = 0;
        int y = b;
        int d = Math.round(b*b-a*a*b+a*a/4);
        while(b*b*x <= a*a*y) {
            plot(g,x+xc,y+yc,3);
            plot(g,x+xc,-y+yc,3);
            plot(g,-x+xc,y+yc,3);
            plot(g,-x+xc,-y+yc,3);

            x++;

            d = d + 2 * b * b * x + b * b;
            if(d >= 0) {
                y--;
                d = d - 2 * a * a * y;
            }
        }
        //region 2
        x = a;
        y = 0;
        d = Math.round(a*a-b*b*a+b*b/4);
        while(b*b*x >= a*a*y) {
            plot(g,x+xc,y+yc,3);
            plot(g,x+xc,-y+yc,3);
            plot(g,-x+xc,y+yc,3);
            plot(g,-x+xc,-y+yc,3);

            y++;

            d = d + 2 * a * a * y + a * a;
            if(d >= 0) {
                x--;
                d = d - 2 * b * b * x;
            }
        }
    }
    public void fillMidpointCircle(Graphics g, int xc, int yc, int r) {
        for(int i = 0; i <= r; i++) {
            midpointCircle(g, xc ,yc ,i);
        }
    }
    public void midpointCircle(Graphics g, int xc, int yc, int r) {
        int x = 0;
        int y = r;
        int d = 1-r;
        int dx = 2 * x;
        int dy = 2 * y;
        while(x <= y) {

            plot(g,x+xc,y+yc,3);
            plot(g,x+xc,-y+yc,3);
            plot(g,-x+xc,y+yc,3);
            plot(g,-x+xc,-y+yc,3);
            plot(g,y+xc,x+yc,3);
            plot(g,y+xc,-x+yc,3);
            plot(g,-y+xc,x+yc,3);
            plot(g,-y+xc,-x+yc,3);

            x++;
            dx += 2;
            d = d + dx + 1;
            if( d >= 0 ) {
                y--;
                dy -= 2;
                d = d - dy;
            }
        }
    }
    private void plot(Graphics g, int x, int y, int size) {
        g.fillRect(x, y, size, size);
    }
}