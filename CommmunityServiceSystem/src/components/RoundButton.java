package components;


import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JButton;

public class RoundButton extends JButton {
private static final int ARC_SIZE = 30;

    private Color buttonColor;
    private Color hoverColor = new Color(0, 120, 140);

    public RoundButton(String text, Color color) {
        super(text);
        this.buttonColor = color;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bg = buttonColor; 

        if (getModel().isRollover()) {
            bg = hoverColor;
        }
        if (getModel().isPressed()) {
            bg = buttonColor.darker();
        }
        
        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, ARC_SIZE, ARC_SIZE));

        g2.setColor(Color.WHITE);
        
     
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(getText(), x, y);

        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
    }
}