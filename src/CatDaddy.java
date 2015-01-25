import java.awt.*;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;

import static java.awt.event.KeyEvent.*;

public class CatDaddy extends TimerTask {

    public static int left_mapping = VK_LEFT;
    public static int right_mapping = VK_RIGHT;
    public static int up_mapping = VK_UP;
    public static int down_mapping = VK_DOWN;

    public static Point centerPoint;
    public static Robot robot;

    public static HashMap<Integer, Integer> keyQueueMap;

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
        keyQueueMap = new HashMap<Integer, Integer>();
        keyQueueMap.put(left_mapping, 0);
        keyQueueMap.put(right_mapping, 0);
        keyQueueMap.put(up_mapping, 0);
        keyQueueMap.put(down_mapping, 0);

        // runs "run()" nonstop
        // quit = cmd + fn + f2 while focused on IDE
        TimerTask timerTask = new CatDaddy();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 1000/60); // 60 fps
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
            System.out.println("dx: " + dx + " \tdy: " + dy);

            addKeys(dx, dy);
        }

        releaseKeys();
        pressKeys();

        // set mouse to center pos
        if(Math.random()<0.25) moveMouse(centerPoint);
    }

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
        if(dx < -1) {
            int curr = keyQueueMap.get(left_mapping);
            keyQueueMap.put(left_mapping, curr + dx);
        }
        if(dx > 1) {
            int curr = keyQueueMap.get(right_mapping);
            keyQueueMap.put(right_mapping, curr + dx);
        }
        if(dy < -1) {
            int curr = keyQueueMap.get(up_mapping);
            keyQueueMap.put(up_mapping, curr + dy);
        }
        if(dy > 1) {
            int curr = keyQueueMap.get(down_mapping);
            keyQueueMap.put(down_mapping, curr + dy);
        }
    }

    public static void releaseKeys() {
        int l = keyQueueMap.get(left_mapping);
        int r = keyQueueMap.get(right_mapping);
        int u = keyQueueMap.get(up_mapping);
        int d = keyQueueMap.get(down_mapping);

        if(l >= -20) robot.keyRelease(left_mapping);
        if(r <=  20) robot.keyRelease(right_mapping);
        if(u >= -20) robot.keyRelease(up_mapping);
        if(d <=  20) robot.keyRelease(down_mapping);

        double change = 0.5;

        if(l != 0) keyQueueMap.put(left_mapping, (int)(l*change));
        if(r != 0) keyQueueMap.put(right_mapping, (int)(r*change));
        if(u != 0) keyQueueMap.put(up_mapping, (int)(u*change));
        if(d != 0) keyQueueMap.put(down_mapping, (int)(d*change));

    }

    public static void pressKeys() {

        int l = keyQueueMap.get(left_mapping);
        int r = keyQueueMap.get(right_mapping);
        int u = keyQueueMap.get(up_mapping);
        int d = keyQueueMap.get(down_mapping);

        int leftrightWeight = l + r;
        int updownWeight = u + d;

        if(leftrightWeight != 0) System.out.println(leftrightWeight);

        if(leftrightWeight > 1) robot.keyPress(right_mapping);
        else if(leftrightWeight <-1) robot.keyPress(left_mapping);
        if(updownWeight    > 1) robot.keyPress(down_mapping);
        else if(updownWeight    <-1) robot.keyPress(up_mapping);

    }

}