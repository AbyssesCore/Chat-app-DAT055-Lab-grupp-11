import javax.swing.JTextArea;
import javax.swing.FocusManager;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Font;


// Source - https://stackoverflow.com/a/13033751
// Posted by Itchy Nekotorych, modified by community. See post 'Timeline' for change history
// Retrieved 2026-03-06, License - CC BY-SA 3.0

public class TextFieldWithPrompt extends JTextArea{
	
	private String placeholder;
	
	private int x, y;
	
	TextFieldWithPrompt(String placeholder) {
		this.placeholder = placeholder;
		x = 0;
		y = 0;
	}
	
	TextFieldWithPrompt(String placeholder, int x, int y) {
		this.placeholder = placeholder;
		this.x = x;
		this.y = y;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if(getText().isEmpty() && ! (FocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == this)){
			Graphics2D g2 = (Graphics2D)g.create();
			g2.setBackground(Color.gray);
			g2.setFont(getFont());
			g2.drawString(placeholder, 13, 22); //figure out x, y from font's FontMetrics and size of component.
			g2.dispose();
		}
	}
}