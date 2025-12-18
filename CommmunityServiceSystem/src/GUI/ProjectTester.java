
package GUI;


import javax.swing.SwingUtilities;

public class ProjectTester {
public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new WelcomeFrame().setVisible(true);
        });
    }
}
