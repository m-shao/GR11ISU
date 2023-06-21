package isu;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.*;
import java.awt.*;
import java.io.*;

class lineDrawer {
    //method for drawing rectangles at desired line width
    static void thickRect(int thickness, int x, int y, int width, int height, Graphics g){
        //draw increasingly smaller rectangles to make it appear thicker
        for (int i = 1; i <= thickness; i++) {
            g.drawRect(x - i, y - i, width + i * 2, height + i * 2);
        }
    }

    //method for drawing the cursor(of user/ai)
    static void drawCursor(int x, int y, int width, int height, Graphics g, int thickness){
        lineDrawer.thickRect(thickness, (int)(x - width/2), (int)(y - height/2), width, height, g);
        g.drawLine(x, y - height/2, x, y + height/2);
        g.drawLine(x - width/2, y, x + width/2, y);
    }
}

//class for checking collisions
class colisionCheck{
    //check if ball is on the paddle
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

    //check if the ball hits the walls
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

    //init variables
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
    int aiSpeed = 5;
    
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
    
    private int score = 0;
    private int highScore;
    
    //read/write to file
    public void editHighscoreFile(String type){
        //try
        try{
            if (type == "r"){
                //get the file
                FileReader reader = new FileReader("highscore.txt");
                //get the file reader
                BufferedReader buffer = new BufferedReader(reader);
                highScore = Integer.parseInt(buffer.readLine());
                buffer.close();
                reader.close();
            } else if (type == "w"){
                FileWriter writer = new FileWriter("highscore.txt");
                writer.write(String.valueOf(score));
                writer.close();
            }
            
        } catch (IOException e) {
            //if we don't find the file, tell the user
            System.out.println("FILE NOT FOUND");
        }
    }

    //main method
    public Isu() {
        //init frame varibles
        this.setTitle("Connected Rectangles");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(screenWidth, screenHeight);
        this.getContentPane().setBackground(new Color(0x000000));
        this.setVisible(true);
        
        //start frame counter
        timer = new Timer(animationDelay, e -> {
            //flip direction of ball if it hits a boundary
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
            
            //every 5 counts, speed the ball up
            if (ballAnimationCycle == 5){
                ballAnimationSize += ballAnimationSpeedIncrease;
                ballAnimationCycle = 0;
            }

            //change the ball's direction on frame change
            ballPosX += hitAngleX * 0.02;
            ballPosY += hitAngleY * 0.02;

            //if the ball is moving towards the ai
            if (direction == 1){
                //move ball
                posZ -= ballAnimationSize * posZ;
                //rerender
                repaint();
                //once the ball hits the opponents side
                if (posZ < 1/Math.pow(scaleDecrease, 4)) {
                    //change direction
                    direction = -1;
                    ballAnimationCycle++;
                    //check if the ai hit it
                    hitPosition = colisionCheck.onCursor(aiX - aiWidth/2, aiY - aiHeight/2, aiWidth, aiHeight, ballx, bally, (int)(ballSize * posZ));
                    //change the angle that ball the moving at
                    if (hitPosition.length > 1){
                        hitAngleX = hitPosition[0] / (cursorWidth / 2) * -1;
                        hitAngleY = hitPosition[1] / (cursorHeight / 2) * -1;
                    //if ai misses, end game
                    } else {
                        System.out.println("gameover");
                        timer.stop();
                    }
                }
            //do same thing for player as we did for ai
            } else {
                posZ += ballAnimationSize * posZ;
                repaint();
                if (posZ >= 1) {
                    direction = 1;
                    hitPosition = colisionCheck.onCursor(mouseX, mouseY, cursorWidth, cursorHeight, ballx, bally, ballSize);
                    if (hitPosition.length > 1){
                        hitAngleX = hitPosition[0] / (cursorWidth / 2);
                        hitAngleY = hitPosition[1] / (cursorHeight / 2);
                        score++;
                        
                    } else {
                        if (score > highScore){
                            editHighscoreFile("w");
                        }
                        timer.stop(); 
                    }
                    
                }
            }
        });
        
        //start the game
        timer.start();
        //read the highscore from the file
        editHighscoreFile("r");
        
        //listen for mouse events
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX() - 9;
                mouseY = e.getY() - 30;
            }
            
        });
        
        //make frame
        RectanglesComponent component = new RectanglesComponent();
        this.add(component);
    }

    private class RectanglesComponent extends JComponent {
        @Override
        protected void paintComponent(Graphics g) {
            //init graphics
            super.paintComponent(g);

            //init variables
            g.setColor(new Color(0x32CD32));
            int width = getWidth();
            int height = getHeight();
            int rectWidth = (int) (width / scaleDecrease);
            int rectHeight = (int) (height / scaleDecrease);
            
            int outerX = rectWidth;
            int outerY = rectHeight;

            int lineWidth = 16;
            //draw the tunnel
            while (rectWidth >= 100) {
                lineDrawer.thickRect(lineWidth, (width - rectWidth) / 2, (height - rectHeight) / 2, rectWidth, rectHeight, g);
                lineWidth /= 2;
                rectWidth = (int) (rectWidth / scaleDecrease);
                rectHeight = (int) (rectHeight / scaleDecrease);
            }
            
            //grab the varibles to do with the boundries of the tunnel
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

            
            //get position of "ball tracking" rectangle
            int activeRecWidth = (int)((width/scaleDecrease) * posZ);
            int activeRecHeight = (int)((height/scaleDecrease) * posZ);

            int activeRecX = (int)(width - (width/scaleDecrease) * posZ)/2;
            int activeRecY = (int)(height - (height/scaleDecrease) * posZ)/2;
            
            //draw ball tracking rectangle
            g.drawRect(activeRecX, activeRecY, activeRecWidth, activeRecHeight);

            g.drawLine(cornerInner0[0], cornerInner0[1], cornerOuter0[0], cornerOuter0[1]);
            g.drawLine(cornerInner1[0], cornerInner1[1], cornerOuter1[0], cornerOuter1[1]);
            g.drawLine(cornerInner2[0], cornerInner2[1], cornerOuter2[0], cornerOuter2[1]);
            g.drawLine(cornerInner3[0], cornerInner3[1], cornerOuter3[0], cornerOuter3[1]);


            //get ball's position
            ballx = (int)(activeRecX + ((activeRecWidth - (ballSize * posZ)) * ballPosX));
            bally = (int)(activeRecY + ((activeRecHeight - (ballSize * posZ)) * ballPosY));

            //draw ai
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
            
            //draw ai
            lineDrawer.drawCursor(aiX - aiWidth/2, aiY - aiHeight/2, aiWidth, aiHeight, g, 2);

            //draw ball
            g.setColor(new Color(0x09c7ed));
            g.fillOval(ballx, bally, (int)(ballSize * posZ), (int)(ballSize * posZ));
            g.setColor(new Color(0x32CD32));

            //draw scoreline
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Score: " + score, 10,50);
            g.drawString("High Score: " + highScore, 10, screenHeight - 50);
            
            //draw player
            g.setColor(Color.RED);
            lineDrawer.drawCursor(mouseX, mouseY, cursorWidth, cursorHeight, g, 5);
            
        }
    }


     public static void main(String[] args) {
         //start program
        SwingUtilities.invokeLater(() -> {
            new Isu();
        });
    }

}
