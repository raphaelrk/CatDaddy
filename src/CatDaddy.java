














import java.awt.*;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;

import static java.awt.event.KeyEvent.*;

public class CatDaddy extends TimerTask {

    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    public static int left_wheel_forward_direction = UP;
    public static int right_wheel_forward_direction = RIGHT;

    public static double left_wheel_weighting = 1.0;
    public static double right_wheel_weighting = 0.5;

    public static int left_mapping = VK_LEFT;
    public static int right_mapping = VK_RIGHT;
    public static int up_mapping = VK_UP;
    public static int down_mapping = VK_DOWN;
    public static Point centerPoint;

    public static HashMap<Integer, Double> keyQueueMap;
    public static void main(String[] args) {
        // set center point
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        centerPoint = new Point();
        centerPoint.x = width/2;
        centerPoint.y = height/2;

        try {
            robot = new Robot();
            robot.setAutoDelay(1); // 1 ms delay on click
            System.out.println("Successfully created robot");
        } catch(Exception e) {
            System.out.println("ABORT! Error in creating robot");
            e.printStackTrace();
        }

        //keyPressQueue = new ArrayList<Integer>();
        keyQueueMap = new HashMap<Integer, Double>();
        keyQueueMap.put(left_mapping, 0.0);
        keyQueueMap.put(right_mapping, 0.0);
        keyQueueMap.put(up_mapping, 0.0);
        keyQueueMap.put(down_mapping, 0.0);

        // runs "run()" nonstop
        // quit = cmd + fn + f2 while focused on IDE
        TimerTask timerTask = new CatDaddy();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 1000/100); // 1000/fps
        while(true){}
    }

    // performs mouse-scanning task
    public void run() {
        // mouse position
        Point p = MouseInfo.getPointerInfo().getLocation();

        // if mouse pos differs from center pos
        if(p.x != centerPoint.x && p.y != centerPoint.y) {
            // display diffs
            // dx > 0 =  moved right
            // dy > 0 = moved down
            int dx = p.x - centerPoint.x;
            int dy = p.y - centerPoint.y;
            //System.out.println("dx: " + dx + " \tdy: " + dy);

            addKeys(dx, dy);
        }

        releaseKeys();
        pressKeys();

        // set mouse to center pos
        if(Math.random()<0.25) moveMouse(centerPoint);
    }

    public static Robot robot;

    // mouse cursor-moving command with multi-monitor support
    // used to move mouse cursor to screen center
    public static void moveMouse(Point p) {
        GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();

        // Search the devices for the one that draws the specified point.
        for (GraphicsDevice device: gs) {
            GraphicsConfiguration[] configurations =
                    device.getConfigurations();
            for (GraphicsConfiguration config: configurations) {
                Rectangle bounds = config.getBounds();
                if(bounds.contains(p)) {
                     // Set point to screen coordinates.
                     Point b = bounds.getLocation();
                     Point s = new Point(p.x - b.x, p.y - b.y);

                     try {
                         Robot r = new Robot(device);
                         r.mouseMove(s.x, s.y);
                     } catch (AWTException e) {
                         e.printStackTrace();
                     }

                     return;
                 }
             }
         }
         // Couldn't move to the point, it may be off screen.
         return;
     }

     public static void addKeys(int dx, int dy) {

         int left_wheel_speed = 0;
         int right_wheel_speed = 0;

         if(left_wheel_forward_direction == UP) left_wheel_speed = -dy;
         else if(left_wheel_forward_direction == DOWN) left_wheel_speed = dy;
         else if(left_wheel_forward_direction == LEFT) left_wheel_speed = -dx;
         else if(left_wheel_forward_direction == RIGHT) left_wheel_speed = dx;

         if(right_wheel_forward_direction == UP) right_wheel_speed = -dy;
         else if(right_wheel_forward_direction == DOWN) right_wheel_speed = dy;
         else if(right_wheel_forward_direction == LEFT) right_wheel_speed = -dx;
         else if(right_wheel_forward_direction == RIGHT) right_wheel_speed = dx;

         left_wheel_speed *= left_wheel_weighting;
         right_wheel_speed *= right_wheel_weighting;

         // vectors! moving the wheels forward adds a vector going forward and towards the middle
         //          moving the wheels backward adds a vector going backward and away from the middle

         int sum_dx = 0;
         int sum_dy = 0; // dy > 0 = down

        if(right_wheel_speed > 1 && left_wheel_speed > 1) { // forward
             sum_dy += -Math.min(right_wheel_speed, left_wheel_speed); // mouse up
             sum_dx += 0.4 * (right_wheel_speed - left_wheel_speed); // turn right/left
         }
         if(right_wheel_speed < -1 && left_wheel_speed < -1) { // back
             sum_dy += -Math.max(right_wheel_speed, left_wheel_speed); // mouse down
             sum_dx += 0.4 * (right_wheel_speed - left_wheel_speed);
         }
         if(right_wheel_speed > 1 && left_wheel_speed < -1) { // turn left
             sum_dx += -Math.min(right_wheel_speed, -left_wheel_speed); // mouse left
             sum_dy += Math.abs(left_wheel_speed) - Math.abs(right_wheel_speed);

         }
         if(right_wheel_speed < -1 && left_wheel_speed > 1) { // turn right
              sum_dx += Math.min(-right_wheel_speed, left_wheel_speed); // mouse right
              sum_dy += Math.abs(right_wheel_speed) - Math.abs(left_wheel_speed);
          }

         if(Math.abs(sum_dy) > 10) sum_dx *= 0.25;

         System.out.print("lwheel: " + left_wheel_speed + "   \trightwheel: " + right_wheel_speed);
        sum_dy += Math.abs(right_wheel_speed) - Math.abs(left_wheel_speed);
        System.out.println("   \tdx: " + sum_dx + " \tdy: " + sum_dy);

        if(sum_dx < -1) {
            double curr = keyQueueMap.get(left_mapping);
            keyQueueMap.put(left_mapping, curr + sum_dx);
        }
        if(sum_dx > 1) {
            double curr = keyQueueMap.get(right_mapping);
            keyQueueMap.put(right_mapping, curr + sum_dx);
        }
        if(sum_dy < -1) {
            double curr = keyQueueMap.get(up_mapping);
            keyQueueMap.put(up_mapping, curr + sum_dy);
        }
        if(sum_dy > 1) {
            double curr = keyQueueMap.get(down_mapping);
            keyQueueMap.put(down_mapping, curr + sum_dy);
        }
    }

    public static void releaseKeys() {
        double l = keyQueueMap.get(left_mapping);
        double r = keyQueueMap.get(right_mapping);
        double u = keyQueueMap.get(up_mapping);
        double d = keyQueueMap.get(down_mapping);

        if(l >= -20) robot.keyRelease(left_mapping);
        if(r <=  20) robot.keyRelease(right_mapping);
        if(u >= -20) robot.keyRelease(up_mapping);
        if(d <=  20) robot.keyRelease(down_mapping);

        if(Math.abs(l) < 20 && Math.abs(r) < 20 &&
                Math.abs(u) < 20 && Math.abs(d) < 20) robot.keyRelease(VK_SPACE);

        double change = 0.5;

        if(l != 0) keyQueueMap.put(left_mapping, l*change);
        if(r != 0) keyQueueMap.put(right_mapping, r*change);
        if(u != 0) keyQueueMap.put(up_mapping, u*change);
        if(d != 0) keyQueueMap.put(down_mapping, d*change);

    }

    public static void pressKeys() {

        double l = keyQueueMap.get(left_mapping);
        double r = keyQueueMap.get(right_mapping);
        double u = keyQueueMap.get(up_mapping);

        double leftrightWeight = l + r;
        double d = keyQueueMap.get(down_mapping);
        double updownWeight = u + d;

        // if(leftrightWeight != 0) System.out.println(leftrightWeight);

        if(leftrightWeight > 1) robot.keyPress(right_mapping);
        else if(leftrightWeight <-1) robot.keyPress(left_mapping);
        if(updownWeight    > 1) robot.keyPress(down_mapping);
        else if(updownWeight    <-1) robot.keyPress(up_mapping);

        if(Math.abs(leftrightWeight) > 1 || Math.abs(updownWeight) > 1) robot.keyPress(VK_SPACE);

    }

}