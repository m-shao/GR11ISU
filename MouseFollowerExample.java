import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MouseFollowerExample extends JPanel {
    private int mouseX;
    private int mouseY;

    public MouseFollowerExample() {
        setPreferredSize(new Dimension(400, 400));
        setBackground(Color.WHITE);

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.fillOval(mouseX - 10, mouseY - 10, 20, 20);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Mouse Follower Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        MouseFollowerExample panel = new MouseFollowerExample();
        frame.getContentPane().add(panel);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
