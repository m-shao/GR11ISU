import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.*;
import java.awt.*;

class lineDrawer {
    static void thickRect(int thickness, int x, int y, int width, int height, Graphics g){
        for (int i = 1; i <= thickness; i++) {
            g.drawRect(x - i, y - i, width + i * 2, height + i * 2);
        }
    }

    static void drawPlayer(int x, int y, int width, int height, Graphics g){
        int trueX = x - 9;
        int trueY = y - 30;

        g.setColor(Color.RED);
        lineDrawer.thickRect(5, (int)(trueX - width/2), (int)(trueY - height/2), width, height, g);
        g.drawLine(trueX, trueY - height/2, trueX, trueY + height/2);
        g.drawLine(trueX - width/2, trueY, trueX + width/2, trueY);
    }
}

public class RectanglesFrame extends JFrame {

    private int screenWidth = 1000;
    private int screenHeight = 700;

    private double scaleDecrease = 1.5;
    
    private double ballAnimationSize = 0.025;
    private int animationDelay = 16;
    private int ballSize = 100;
    private int direction = 1;
    
    private double posZ = 1;
    private double ballPosX = 0.2;
    private double ballPosY = 0.2;

    private int ballTrueX = (int)((screenWidth-ballSize)* ballPosX);
    private int ballTrueY = (int)((screenHeight-ballSize)* ballPosY);
    
    private int directionX = 1;
    private int directionY = 1;

    private int mouseX;
    private int mouseY;

    public RectanglesFrame() {
        this.setTitle("Connected Rectangles");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(screenWidth, screenHeight);
        this.getContentPane().setBackground(new Color(0x000000));
        this.setVisible(true);
        Timer timer = new Timer(animationDelay, e -> {


            // if (directionX == 1) {
            //     ballPosX -= ballAnimationSize;
            //     if (ballPosX <= 0) directionX = -1;
            // } else {
            //     ballPosX += ballAnimationSize;
            //     if (ballPosX >= 1) directionX = 1;
            // }

            // if (directionY == 1){
            //     ballPosY -= ballAnimationSize;
            //     if (ballPosY <= 0) directionY = -1;
            // } else {
            //     ballPosY += ballAnimationSize;
            //     if (ballPosY >= 1) directionY = 1;
            // }

            if (direction == 1){
                posZ -= ballAnimationSize * posZ;
                repaint();
                if (posZ < 1/Math.pow(scaleDecrease, 4)) {
                    direction = -1;
                }
            } else {
                posZ += ballAnimationSize * posZ;
                repaint();
                if (posZ >= 1) {
                    direction = 1;
                    
                }
            }
        });
        timer.start();
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                repaint();
            }
        });

        RectanglesComponent component = new RectanglesComponent();
        this.add(component);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RectanglesFrame();
        });
    }

    private class RectanglesComponent extends JComponent {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(new Color(0x32CD32));
            int width = getWidth();
            int height = getHeight();
            int rectWidth = (int) (width / scaleDecrease);
            int rectHeight = (int) (height / scaleDecrease);
            
            int outerX = rectWidth;
            int outerY = rectHeight;

            int lineWidth = 16;
            while (rectWidth >= 100) {
                lineDrawer.thickRect(lineWidth, (width - rectWidth) / 2, (height - rectHeight) / 2, rectWidth, rectHeight, g);
                lineWidth /= 2;
                rectWidth = (int) (rectWidth / scaleDecrease);
                rectHeight = (int) (rectHeight / scaleDecrease);
            }

            int innerX = (int) (rectWidth * 1.5);
            int innerY = (int) (rectHeight * 1.5);
            
            int[] cornerInner0 = {(width - innerX) / 2, (height - innerY) / 2};
            int[] cornerInner1 = {(width - innerX) / 2, (height - innerY) / 2 + innerY};
            int[] cornerInner2 = {(width - innerX) / 2 + innerX, (height - innerY) / 2};
            int[] cornerInner3 = {(width - innerX) / 2 + innerX, (height - innerY) / 2 + innerY};
            
            int[] cornerOuter0 = {(width - outerX) / 2, (height - outerY) / 2};
            int[] cornerOuter1 = {(width - outerX) / 2, (height - outerY) / 2 + outerY};
            int[] cornerOuter2 = {(width - outerX) / 2 + outerX, (height - outerY) / 2};
            int[] cornerOuter3 = {(width - outerX) / 2 + outerX, (height - outerY) / 2 + outerY};


            int activeRecWidth = (int)((width/scaleDecrease) * posZ);
            int activeRecHeight = (int)((height/scaleDecrease) * posZ);

            int activeRecX = (int)(width - (width/scaleDecrease) * posZ)/2;
            int activeRecY = (int)(height - (height/scaleDecrease) * posZ)/2;

            // g.setColor(new Color(0xff0000));
            g.drawRect(activeRecX, activeRecY, activeRecWidth, activeRecHeight);
            // g.setColor(new Color(0x32CD32));

            g.drawLine(cornerInner0[0], cornerInner0[1], cornerOuter0[0], cornerOuter0[1]);
            g.drawLine(cornerInner1[0], cornerInner1[1], cornerOuter1[0], cornerOuter1[1]);
            g.drawLine(cornerInner2[0], cornerInner2[1], cornerOuter2[0], cornerOuter2[1]);
            g.drawLine(cornerInner3[0], cornerInner3[1], cornerOuter3[0], cornerOuter3[1]);


            ballTrueX = (int)(activeRecX + ((activeRecWidth - (ballSize * posZ)) * ballPosX));
            ballTrueY = (int)(activeRecY + ((activeRecHeight - (ballSize * posZ)) * ballPosY));

            g.setColor(new Color(0x09c7ed));
            g.fillOval(ballTrueX, ballTrueY, (int)(ballSize * posZ), (int)(ballSize * posZ));
            g.setColor(new Color(0x32CD32));

            g.setColor(Color.RED);
            int playerWidth = (int)(screenWidth/scaleDecrease/5);
            int playerHeight = (int)(screenHeight/scaleDecrease/4.5);

            lineDrawer.drawPlayer(mouseX, mouseY, playerWidth, playerHeight, g);
            
        }
    }
}
