import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

class styleHelper {
	public static void stylePrimaryButton(JButton b) {
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(8, 12, 8, 12));
        b.setBackground(new Color(45, 125, 245));
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void styleGhostButton(JButton b) {
        b.setFocusPainted(false);
        b.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 223, 228), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        b.setBackground(Color.WHITE);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void stylePrimaryButtonStatic(JButton b) {
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 14, 10, 14));
        b.setBackground(new Color(45, 125, 245));
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void styleGhostButtonStatic(JButton b) {
        b.setFocusPainted(false);
        b.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 223, 228), 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
        b.setBackground(Color.WHITE);
        b.setOpaque(true);
    }
	
	public static void setNiceLookAndFeel() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) { }

        // Lite snyggare standard-font (om Nimbus inte tar det helt)
        UIManager.put("defaultFont", new Font("SansSerif", Font.PLAIN, 13));
    }
}