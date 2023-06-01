


import javax.swing.*;
import java.awt.*;

public class RectanglesFrame extends JFrame {

    private int maxBallSize = 100;
    private int minBallSize = 30;
    private double ballAnimationSize = 0.01;
    private int animationDelay = 16;
    private int ballSize = 100;
    private int direction = 1;
    
    private double size = 1;
    private double ballPosX = 0.5;
    private double ballPosY = 0.5;

    public RectanglesFrame() {
        this.setTitle("Connected Rectangles");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 700);
        this.getContentPane().setBackground(new Color(0x000000));
        this.setVisible(true);
        Timer timer = new Timer(animationDelay, e -> {
            if (direction == 1){
                size -= ballAnimationSize;
                repaint();
                if ((int)(ballSize * size) <= minBallSize) {
                    direction = -1;
                }
            } else {
                size += ballAnimationSize;
                repaint();
                if ((int)(ballSize * size) >= maxBallSize) {
                    direction = 1;
                }
            }
        });
        timer.start();

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
            double scaleDecrease = 1.5;
            int rectWidth = (int) (width / scaleDecrease);
            int rectHeight = (int) (height / scaleDecrease);
            
            int outerX = rectWidth;
            int outerY = rectHeight;

            int lineWidth = 16;
            while (rectWidth >= 100) {
                for (int i = 1; i <= lineWidth; i++) {
                    g.drawRect((width - rectWidth) / 2 + i, (height - rectHeight) / 2 + i, rectWidth - i * 2, rectHeight - i * 2);
                }
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
            

            g.drawLine(cornerInner0[0], cornerInner0[1], cornerOuter0[0], cornerOuter0[1]);
            g.drawLine(cornerInner1[0], cornerInner1[1], cornerOuter1[0], cornerOuter1[1]);
            g.drawLine(cornerInner2[0], cornerInner2[1], cornerOuter2[0], cornerOuter2[1]);
            g.drawLine(cornerInner3[0], cornerInner3[1], cornerOuter3[0], cornerOuter3[1]);
            
            
            int ballX = ((int)(width*(1 -size)) - (int)(ballSize * size)) / 2;
            int ballY = ((int)(height*(1 -size)) - (int)(ballSize * size)) / 2;

            g.fillOval(ballX, ballY, (int)(ballSize * size), (int)(ballSize * size));
        }
    }
}
