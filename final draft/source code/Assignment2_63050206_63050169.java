//อนุวัตร ตรีเจริญรัตน์ 63050206
//ภูธเนศ ประสิทธิ์สิน 63050169

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.*;
import java.util.*;

class Assignment2_63050206_63050169 extends JPanel implements Runnable{
    //testing
    Color color = new Color(245,245,245);
    //color
    Color neonGreen = new Color(57, 255, 20);
    Color MidnightBlue = new Color(25, 25, 112);
    Color DeepSkyBlue = new Color(0, 191, 255);
    Color SkyBlue = new Color(56, 56, 127);
    Color moonColor = new Color(255, 250, 250);
    Color Yellow = new Color(255, 140, 0);
    Color DimGrey = new Color(105, 105, 105);
    Color SteelBlue1 = new Color(99, 184, 255);
    Color OrangeRed = new Color(255, 69, 0);
    Color grey31 = new Color(79, 79, 79);
    // Color A = new Color(4, 0, 50);
    Color A = new Color(17, 30, 108);
    Color B = new Color(4, 0, 26);
    //action
    double rotate = 0;
    double splitTime = 2500;
    double velocity = 500; // 500
    double xc = 290; double yc = 280;

    // planets
    ArrayList<Planet> planets = new ArrayList<>();
    double planetCreateSpeed = 0.5;
    int maxPlanetCount = 25;

    double planetCreateTimer = 0;

    // stars
    int starCount = 250;
    Point2D.Float[] stars ;

    public static void main(String[] args)  {
        Assignment2_63050206_63050169 m = new Assignment2_63050206_63050169();
        JFrame f = new JFrame();
        f.add(m);
        f.setTitle("Assignment2_63050206_63050169");
        f.setSize(600, 600);
        f.setBackground(Color.WHITE);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);

        (new Thread(m)).start();
    }

    public void run() {
        Random random = new Random();
        stars = new Point2D.Float[starCount];
        for (int i=0; i<starCount; i++) stars[i] = new Point2D.Float(random.nextInt(800) - 100, random.nextInt(800) - 100);
        double lastTime = System.currentTimeMillis();
        double startTime = System.currentTimeMillis();
        double currentTime, elapsedTime;
        while(true) {
            //control timeout
            currentTime = System.currentTimeMillis();
            elapsedTime = currentTime - lastTime;
            //update logic
                double elapsedSec = (elapsedTime / 1000);
                rotate += 0.3 * elapsedSec;
                planetCreateTimer +=  elapsedSec / planetCreateSpeed;
                for (int i=0; i<planets.size(); i++) {
                    planets.get(i).update(elapsedSec);
                    if (planets.get(i).reachedTarget()) {
                        planets.remove(i);
                        i--;
                    }
                }
                if(currentTime - startTime > 3000) {
                    while (planetCreateTimer >= 1) {
                        if (planets.size() < maxPlanetCount) {
                            double randomAngle = random.nextDouble() * Math.PI * 2;
                            double randX = Math.cos(randomAngle) * 400 + 300;
                            double randY = Math.sin(randomAngle) * 400 + 300;
                            double randSize = random.nextDouble() * 25 + 25; // between 25 - 50
                            final float hue = random.nextFloat();
                            // Saturation between 0.1 and 0.3
                            final float saturation = (random.nextInt(2000) + 1000) / 10000f;
                            final float luminance = 0.9f;
                            Color randColor = Color.getHSBColor(hue, saturation, luminance);
    
                            planets.add(new Planet(
                                    randX, randY,   // begin position
                                    xc, yc,       // end position (center of blackhole)
                                    3,            // speed
                                    randSize,
                                    randColor
                            ));
                        }
                        planetCreateTimer -= 1;
                    }
                }
                //display
                repaint();
                //control time
                lastTime = currentTime;
        }
    }
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        AffineTransform oT = g2.getTransform();
        //background first layer
        g2.setColor(B);
        g2.fillRect(0, 0, 600, 600);

        // stars
        AffineTransform starT = new AffineTransform(oT);
        starT.rotate(rotate * 0.5, 300, 300);

        g2.setTransform(starT);
        galaxyTrash(g2);
        g2.setTransform(oT);

        //blackhole shadow
        Paint oPaint = g2.getPaint();

        float[] fractions = new float[]{ 0.5f, 1.0f };
        Color[] colors = new Color[]{Color.BLACK, new Color(0, 0, 0, 0)};
        Paint shadowPaint = new RadialGradientPaint(300, 300, 300, fractions, colors);

        g2.setPaint(shadowPaint);
        //background second layer ( + shadow )
        g2.fillRect(0, 0, 600, 600);

        g2.setPaint(oPaint);

        //Blackhole
        for(int i = 0; i < 100; i++) {
            if(i%2 == 0) g2.setColor(Color.BLACK);
            else g2.setColor(A);
            g2.rotate(rotate,  (int)xc-i,(int) yc-i);
            fillMidpointCircle(g2, (int)xc-i,(int) yc-i, 200-i*3);
        }

        g2.setTransform(oT);
        //try
        moon(g2);
    }

    public void galaxyTrash(Graphics g){
        g.setColor(Color.WHITE);
        if (stars == null) return;
        for(int i=0;i<starCount;i++) midpointCircle(g, (int)stars[i].x, (int)stars[i].y, 1);
    }

    public void moon (Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        for (int i=0; i<planets.size(); i++) {
            Planet planet = planets.get(i);
            g2.setColor(planet.color);
            fillMidpointCircle(g2, planet.getX(), planet.getY(), planet.getSize());
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

    // EDIT BEGIN
    public void fillMidpointCircle(Graphics g, int xc, int yc, int r) {
        fillCircle(g, xc, yc, r);
        /*
        for(int i = 0; i <= r; i++) {
            midpointCircle(g, xc ,yc ,i);
        }
         */

    }

    private void fillCircle(Graphics g, int midX, int midY, int radius) {
        int x = 0;
        int y = radius;
        int dx = 0; // dx = 2 * x;
        int dy = 2 * y;
        int d = 1 - radius;

        while (x <= y) {

            int x2 = x + x, y2 = y + y;

            g.fillRect(midX - x, midY + y, x2, 1); // [midX - x -> midX + x] [midY + y]
            g.fillRect(midX - x, midY - y, x2, 1); // [midX - x -> midX + x] [midY - y]
            g.fillRect(midX - y, midY + x, y2, 1); // [midY - y -> midY + y] [midX + x]
            g.fillRect(midX - y, midY - x, y2, 1); // [midY - y -> midY + y] [midX - x]

            x++;
            dx += 2;
            d += dx + 1;

            if (d >= 0) {
                // only draw strips when y value is changed to prevent overlaps (single y multiple x)
                y--;
                dy -= 2;
                d -= dy;
            }
        }
    }

    // END
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
    private void randPlot(Graphics g) {
        int t,t2;
        for(int i = 0; i<10 ;i++) {
            Random r = new Random();
            color = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
            g.setColor(color);
            t  = 108+r.nextInt(460);
            t2 = 108+r.nextInt(460);
            plot(g, t, t2, 7);
        }
    }
    private void slope(Graphics g) {
        for(int i = 0; i<600 ;i++) {
            Random r = new Random();
            color = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
            g.setColor(color);
            plot(g, i,i, 7);
        }
    }
    private void naiveLine(Graphics g, int x1, int y1, int x2, int y2) {
        double dx,dy,m,b;
        dx = x2 - x1;
        dy = y2 - y1;
        m = dy/dx;
        b = y1 - (m*x1); //in case we use 1, both x and y must use 1,
        for (int x = x1; x <= x2; x++) {
            int y = (int)Math.round((m*x) + b);
            plot(g, x ,y ,1);
        }
    }
    public void ddaLine(Graphics g, int x1, int y1, int x2, int y2) {
        double x,y,dx,dy,m;
        dx = x2 - x1;
        dy = y2 - y1;
        m = dy/dx;
        // case : bottom right
        if ( m <= 1 && m >= 0 ) {
            y = Math.min(y1, y2);
            for( x = Math.min(x1,x2); x<= Math.max(x1,x2); x++) {
                y += m;
                plot(g, (int)x, (int)y , 3);
            }
            // case : bottom left
        } else if ( m >= -1 && m < 0 ) {
            y = Math.min(y1, y2);
            for( x = Math.max(x1,x2); x<= Math.min(x1,x2); x--) {
                y -= m;
                plot(g, (int)x, (int)y , 3);
            }
            // case : top left
        } else if ( m > 1 ) {
            x = Math.min(x1, x2);
            for( y = Math.min(y1,y2); y<= Math.max(y1,y2); y++) {
                x += 1/m;
                plot(g, (int)x, (int)y, 3);
            }
            // case : top right
        } else {
            x = Math.min(x1, x2);
            for( y = Math.max(y1,y2); y<= Math.min(y1,y2); y++) {
                x -= 1/m;
                plot(g, (int)x, (int)y, 3);
            }
        }
    }
    public void brasenhamLine(Graphics g, int x1, int y1, int x2, int y2) {
        float D,dx,dy;
        dx = Math.abs(x2 - x1);
        dy = Math.abs(y2 - y1);
        int sx,sy;
        sx = ( x1 < x2 ) ? 1  : -1;
        sy = ( y1 < y2 ) ? 1  : -1;
        boolean isSwap = false;
        if( dy > dx) {
            float z = dx;
            dx = dy;
            dy = z;
            isSwap = true;
        }
        D = 2 * dy - dx;
        float y = y1;
        float x = x1;
        for(int i = 1; i <= dx; i++) {
            plot(g, (int)Math.round(x), (int)Math.round(y), 3);
            if(D >= 0) {
                if(isSwap) x += sx;
                else  y += sy;
                D -= 2 * dx;
            }
            if(isSwap) y += sy;
            else x += sx;
            D += 2 * dy;
        }
    }
    public void bezierCurve(Graphics g, int x1, int y1, int x2, int y2, int x3 ,int y3, int x4, int y4) {
        for(int i = 0; i <= 1000; i++) {
            double t = i/1000.0;
            int x = (int)((Math.pow((1 - t), 3) * x1) + (3 * t * Math.pow(1 - t, 2) * x2) + (3 * Math.pow(t, 2) * (1-t) * x3) + Math.pow(t, 3) * x4);
            int y = (int)((Math.pow((1 - t), 3) * y1) + (3 * t * Math.pow(1 - t, 2) * y2) + (3 * Math.pow(t, 2) * (1-t) * y3) + Math.pow(t, 3) * y4);
            plot(g,x,y,3);
        }
    }
    public BufferedImage floodFill(BufferedImage m, int x,int y, Color targetColor, Color replacementColor) {
        Queue<Point> q = new LinkedList<Point>();
        Graphics2D g2 = m.createGraphics();

        g2.setColor(replacementColor);

        plot(g2, x, y, 1);
        q.add(new Point(x,y));

        while(!q.isEmpty()) {
            Point p = q.poll();
            //check x (0,600) and y (0,600) in case we go out of frame
            //south
            if(p.y < 600 && new Color (m.getRGB(p.x, p.y + 1)).equals(targetColor)) {
                g2.setColor(replacementColor);
                plot(g2, p.x, p.y + 1, 1);
                q.add(new Point(p.x, p.y + 1));
            }
            //north
            if(p.y > 0 && new Color (m.getRGB(p.x, p.y - 1)).equals(targetColor)) {
                g2.setColor(replacementColor);
                plot(g2, p.x, p.y - 1, 1);
                q.add(new Point(p.x, p.y - 1));
            }
            //east
            if(p.x < 600 && new Color (m.getRGB(p.x + 1, p.y)).equals(targetColor)) {
                g2.setColor(replacementColor);
                plot(g2, p.x+ 1, p.y, 1);
                q.add(new Point(p.x + 1,p.y));
            }
            //west
            if(p.x > 0 && new Color (m.getRGB(p.x - 1, p.y)).equals(targetColor)) {
                g2.setColor(replacementColor);
                plot(g2, p.x - 1, p.y, 1);
                q.add(new Point(p.x - 1,p.y));
            }
        }
        return m;
    }
    public void triPoly(Graphics g, int[] xPoly, int[] yPoly) {
        // for making triangle inside polygon, we decide to draw a line which start from
        // first point toward other point by not drawing to the nearest left and right angle
        // and after we draw toward at every each angle inside the polygon we will get 5 triangles for first position
        // in case we want more different way to build, we just change start point to next point in the Polygon
        // by the way like this, we will get 5 triangles by 7 ways( due to how many Polygon Angle are).
        for(int i = 0; i < xPoly.length; i++) {
            Random r = new Random();
            color = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
            g.setColor(color);
            for(int j = 2; j < xPoly.length; j++) g.drawLine(xPoly[i], yPoly[i], xPoly[j], yPoly[j]);
        }
        g.setColor(Color.BLACK);
        Polygon poly = new Polygon(xPoly, yPoly, 7);
        g.drawPolygon(poly);
    }

    static class Planet {
        public double startX;
        public double startY;
        public double targetX;
        public double targetY;
        private double duration;
        public Color color;

        public double startSize;
        private double progress;

        public Planet(double x, double y, double targetX, double targetY, double duration, double size, Color color) {
            this.startX = x;
            this.startY = y;
            this.targetX = targetX;
            this.targetY = targetY;
            this.duration = duration;
            this.startSize = size;
            this.color = color;
            progress = 0;
        }

        public void update(double elapsedSec) {
            if (!reachedTarget()) {
                progress = Math.min(1, progress + elapsedSec / duration);
            }
        }

        public int getX()       { return (int)Math.round(lerp(startX, targetX, progress));  }
        public int getY()       { return (int)Math.round(lerp(startY, targetY, progress));  }
        public int getSize()    { return (int)Math.round(lerp(startSize, 0, progress));     }

        private double lerp(double a, double b, double t) { return a + t * (b - a); }

        public boolean reachedTarget() {
            return progress >= 1;
        }


    }
}
