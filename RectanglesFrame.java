import javax.swing.*;
import java.awt.*;

public class RectanglesFrame extends JFrame {

    public RectanglesFrame() {
        this.setTitle("Connected Rectangles");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 700);
        this.getContentPane().setBackground(new Color(0x000000));
        this.setVisible(true);
    }

    public void paint(Graphics g) {
        super.paint(g);
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

        int innerX = (int)(rectWidth * 1.5);
        int innerY = (int)(rectHeight * 1.5); 

        g.drawLine((width - innerX) / 2, (height - innerY) / 2, (width - outerX) / 2, (height - outerY) / 2);
        g.drawLine((width - innerX) / 2, (height - innerY) / 2 + innerY, (width - outerX) / 2, (height - outerY) / 2 + outerY);
        g.drawLine((width - innerX) / 2 + innerX, (height - innerY) / 2, (width - outerX) / 2 + outerX, (height - outerY) / 2);
        g.drawLine((width - innerX) / 2 + innerX, (height - innerY) / 2 + innerY, (width - outerX) / 2 + outerX, (height - outerY) / 2 + outerY);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RectanglesFrame();
        });
    }
}
