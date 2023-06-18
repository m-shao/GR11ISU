package isu;

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

    static void drawCursor(int x, int y, int width, int height, Graphics g, int thickness){
        lineDrawer.thickRect(thickness, (int)(x - width/2), (int)(y - height/2), width, height, g);
        g.drawLine(x, y - height/2, x, y + height/2);
        g.drawLine(x - width/2, y, x + width/2, y);
    }
}

class colisionCheck{
    static double[] onCursor(int cursorX, int cursorY, int cursorWidth, int cursorHeight, int ballX, int ballY, int ballSize){
        ballX += ballSize/2;
        ballY += ballSize/2;
        
        cursorX -= cursorWidth/2;
        cursorY -= cursorHeight/2;

        double closestX = Math.max(cursorX, Math.min(ballX, cursorX + cursorWidth));
        double closestY = Math.max(cursorY, Math.min(ballY, cursorY + cursorHeight));
        double distance = Math.sqrt(Math.pow(ballX - closestX, 2) + Math.pow(ballY - closestY, 2));

        boolean collided = distance <= ballSize/2;

        double [] distanceArr = new double[2];
        if (collided){
            cursorX += cursorWidth/2;
            cursorY += cursorHeight/2;
            distanceArr[0] = ballX - cursorX;
            distanceArr[1] = ballY - cursorY;
        } else {
            distanceArr = new double[0];
        }
        return distanceArr;
    }

    static int[] checkBoudries(int ballX, int ballY, int ballSize, int leftBound, int rightBound, int topBound, int bottomBound, int cursorWidth, int cursorHeight){
        int[] hitPosition = {ballX, ballY};
        if (ballX < leftBound + cursorWidth/2){
            hitPosition[0] = leftBound + cursorWidth/2;
        } else if (ballX > rightBound - cursorWidth/2){
            hitPosition[0] = rightBound - cursorWidth/2;
        }
        if (ballY < bottomBound + cursorHeight/2){
            hitPosition[1] = bottomBound + cursorHeight/2;
        } else if (ballY > topBound - cursorHeight/2){
            hitPosition[1] = topBound - cursorHeight/2;
        }
        return hitPosition;
    }
}

public class Isu extends JFrame {

    private boolean gameOver = false;

    private int screenWidth = 1000;
    private int screenHeight = 700;

    private double scaleDecrease = 1.5;
    
    private double ballAnimationSize = 0.035;
    private double ballAnimationSpeedIncrease = 0.01;
    private int ballAnimationCycle = 0;
    private int animationDelay = 16;
    private int ballSize = 100;
    private int direction = 1;
    
    private double posZ = 1;
    private double ballPosX = 0.5;
    private double ballPosY = 0.5;

    private int ballx = (int)((screenWidth-ballSize)* ballPosX);
    private int bally = (int)((screenHeight-ballSize)* ballPosY);
    
    private int aiTargetX;
    private int aiTargetY;
    int aiX = screenWidth/2;
    int aiY = screenHeight/2;
    int aiSpeed = 3;
    
    private Timer timer;

    private int mouseX;
    private int mouseY;
    private int cursorWidth = (int)(screenWidth/scaleDecrease/6);
    private int cursorHeight = (int)(screenHeight/scaleDecrease/5.5);
    private int aiWidth = (int)((screenWidth/scaleDecrease/6)/Math.pow(scaleDecrease, 4));
    private int aiHeight = (int)((screenHeight/scaleDecrease/5.5)/Math.pow(scaleDecrease, 4));

    private double[] hitPosition = new double[0];
    private double hitAngleX = 0;
    private double hitAngleY = 0;

    public Isu() {
        this.setTitle("Connected Rectangles");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(screenWidth, screenHeight);
        this.getContentPane().setBackground(new Color(0x000000));
        this.setVisible(true);
        
        
        timer = new Timer(animationDelay, e -> {
            if (ballPosX > 1) {
                ballPosX = 1;
                hitAngleX *= -1;
            } else if (ballPosX < 0) {
                ballPosX = 0;
                hitAngleX *= -1;
            }
            if (ballPosY > 1) {
                ballPosY = 1;
                hitAngleY *= -1;
            } else if (ballPosY < 0) {
                ballPosY = 0;
                hitAngleY *= -1;
            }

            if (ballAnimationCycle == 5){
                ballAnimationSize += ballAnimationSpeedIncrease;
                ballAnimationCycle = 0;
            }

            ballPosX += hitAngleX * 0.02;
            ballPosY += hitAngleY * 0.02;

            if (direction == 1){
                posZ -= ballAnimationSize * posZ;
                repaint();
                if (posZ < 1/Math.pow(scaleDecrease, 4)) {
                    direction = -1;
                    ballAnimationCycle++;
                    hitPosition = colisionCheck.onCursor(aiX - aiWidth/2, aiY - aiHeight/2, aiWidth, aiHeight, ballx, bally, (int)(ballSize * posZ));
                    if (hitPosition.length > 1){
                        hitAngleX = hitPosition[0] / (cursorWidth / 2) * -1;
                        hitAngleY = hitPosition[1] / (cursorHeight / 2) * -1;
                        
                    } else {
                        System.out.println("gameover");
                        timer.stop();
                    }
                }
            } else {
                posZ += ballAnimationSize * posZ;
                repaint();
                if (posZ >= 1) {
                    direction = 1;
                    hitPosition = colisionCheck.onCursor(mouseX, mouseY, cursorWidth, cursorHeight, ballx, bally, ballSize);
                    if (hitPosition.length > 1){
                        hitAngleX = hitPosition[0] / (cursorWidth / 2);
                        hitAngleY = hitPosition[1] / (cursorHeight / 2);
                        
                    } else {
                        timer.stop(); 
                    }
                    
                }
            }
        });
        
        
        timer.start();
        
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX() - 9;
                mouseY = e.getY() - 30;
            }
            
        });

        RectanglesComponent component = new RectanglesComponent();
        this.add(component);
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

            int innerX = (int) (rectWidth * scaleDecrease);
            int innerY = (int) (rectHeight * scaleDecrease);
            
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


            ballx = (int)(activeRecX + ((activeRecWidth - (ballSize * posZ)) * ballPosX));
            bally = (int)(activeRecY + ((activeRecHeight - (ballSize * posZ)) * ballPosY));

            int smallWidth = cornerInner3[0] - cornerInner0[0];
            int smallHeight = cornerInner3[1] - cornerInner0[1];

            g.setColor(Color.BLUE);
            int [] aiCoords = colisionCheck.checkBoudries((int)(cornerInner0[0] + (smallWidth * ballPosX)), (int)(cornerInner0[1] + (smallHeight * ballPosY)), ballSize, cornerInner0[0], cornerInner3[0], cornerInner3[1], cornerInner0[1], aiWidth, aiHeight);
            int aiTargetX = aiCoords[0];
            int aiTargetY = aiCoords[1];
            
            if (aiX - aiTargetX < -5 || aiX - aiTargetX > 5){
                aiX = aiX + ((aiX - aiTargetX) / Math.abs(aiX - aiTargetX) * -1) * aiSpeed;
            }
            if (aiY - aiTargetY < -5 ||  aiY - aiTargetY > 5){
                aiY = aiY + ((aiY - aiTargetY) / Math.abs(aiY - aiTargetY) * -1) * aiSpeed;
            }
            
//            
//            System.out.println((aiX - aiTargetX) / Math.abs(aiX - aiTargetX) * -1);
//            
            lineDrawer.drawCursor(aiX - aiWidth/2, aiY - aiHeight/2, aiWidth, aiHeight, g, 2);

            
            g.setColor(new Color(0x09c7ed));
            g.fillOval(ballx, bally, (int)(ballSize * posZ), (int)(ballSize * posZ));
            g.setColor(new Color(0x32CD32));

            g.setColor(Color.ORANGE);
            
            g.fillRect(screenWidth/2 - screenWidth/8, screenHeight/2 - screenHeight/16, screenWidth/4, screenHeight/8);
            
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Start", screenWidth/2 - 50,screenHeight/2 + 10);
            g.setColor(new Color(0x32CD32));
            
            
            g.setColor(Color.RED);
            lineDrawer.drawCursor(mouseX, mouseY, cursorWidth, cursorHeight, g, 5);
            
        }
    }


     public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Isu();
        });
    }

}
