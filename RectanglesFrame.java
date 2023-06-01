import javax.swing.*;
import java.awt.*;

public class RectanglesFrame extends JFrame {

    private int maxBallSize = 100;
    private int ballSize = 100;
    private int direction = 1;

    public RectanglesFrame() {
        this.setTitle("Connected Rectangles");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 700);
        this.getContentPane().setBackground(new Color(0x000000));
        this.setVisible(true);
        Timer timer = new Timer(50, e -> {
            if (direction == 1){
                ballSize -= 10;
                repaint();
                if (ballSize <= 10) {
                    direction = -1;
                }
            } else {
                ballSize += 10;
                repaint();
                if (ballSize >= maxBallSize) {
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

            int ballX = (width - ballSize) / 2;
            int ballY = (height - ballSize) / 2;

            // Draw the ball
            g.fillOval(ballX, ballY, ballSize, ballSize);

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

            g.drawLine((width - innerX) / 2, (height - innerY) / 2, (width - outerX) / 2, (height - outerY) / 2);
            g.drawLine((width - innerX) / 2, (height - innerY) / 2 + innerY, (width - outerX) / 2, (height - outerY) / 2 + outerY);
            g.drawLine((width - innerX) / 2 + innerX, (height - innerY) / 2, (width - outerX) / 2 + outerX, (height - outerY) / 2);
            g.drawLine((width - innerX) / 2 + innerX, (height - innerY) / 2 + innerY, (width - outerX) / 2 + outerX, (height - outerY) / 2 + outerY);
        }
    }
}
