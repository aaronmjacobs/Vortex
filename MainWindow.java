import javax.swing.JFrame;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

public class MainWindow extends JFrame implements ActionListener
{
	public static final int WIDTH = 1014;
	public static final int HEIGHT = 758;
	
	private int theCurrentLevelNumber;
	
	private GamePanel theGamePanel = new GamePanel();
	private HowToPlayWindow myHowToPlayWindow = new HowToPlayWindow();
	private JOptionPane hintPane = new JOptionPane();
	
	public MainWindow()
	{
		super();
		this.setSize(WIDTH, HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setTitle("Vortex");
		this.getContentPane().setBackground(Color.BLACK);
		this.add(theGamePanel);
		this.setLocationRelativeTo(null);
	}
	
	public void actionPerformed(ActionEvent e) //Handles the JMenuItems
	{
		String buttonString = e.getActionCommand();
		
		if(buttonString.equals("Exit"))
		{
			System.exit(0);
		} else if(buttonString.equals("Reset level"))
		{
			theGamePanel.loadLevel(theGamePanel.getCurrentLevelNumber());
		} else if(buttonString.equals("Level 1"))
		{
			theGamePanel.loadLevel(1);
		} else if(buttonString.equals("Level 2"))
		{
			theGamePanel.loadLevel(2);
		} else if(buttonString.equals("Level 3"))
		{
			theGamePanel.loadLevel(3);
		} else if(buttonString.equals("Level 4"))
		{
			theGamePanel.loadLevel(4);
		} else if(buttonString.equals("Level 5"))
		{
			theGamePanel.loadLevel(5);
		} else if(buttonString.equals("Level 6"))
		{
			theGamePanel.loadLevel(6);
		} else if(buttonString.equals("Level 7"))
		{
			theGamePanel.loadLevel(7);
		} else if(buttonString.equals("Level 8"))
		{
			theGamePanel.loadLevel(8);
		} else if(buttonString.equals("Level 9"))
		{
			theGamePanel.loadLevel(9);
		} else if(buttonString.equals("Level 10"))
		{
			theGamePanel.loadLevel(10);
		} else if(buttonString.equals("How to Play"))
		{
			myHowToPlayWindow.setVisible(true);
		} else if(buttonString.equals("Display hint"))
		{
			theCurrentLevelNumber = theGamePanel.getCurrentLevelNumber();
			
			switch(theCurrentLevelNumber)
			{
				case 1:
					hintPane.showMessageDialog(this, "You can't fit through the hole in the wall, but you CAN see through it...", "Hint", JOptionPane.INFORMATION_MESSAGE);
					break;
				case 2:
					hintPane.showMessageDialog(this, "The gap is too far to jump, if only there was some way for you to gain more momentum...", "Hint", JOptionPane.INFORMATION_MESSAGE);
					break;
				case 3:
					hintPane.showMessageDialog(this, "You can't go through the door unless it's open, but in order for it to be open the button has to be pressed down.\nYou can't be in two places at once...", "Hint", JOptionPane.INFORMATION_MESSAGE);
					break;
				case 4:
					hintPane.showMessageDialog(this, "Did you know that the box can go through vortexes?", "Hint", JOptionPane.INFORMATION_MESSAGE);
					break;
				case 5:
					hintPane.showMessageDialog(this, "How could you get enough speed to make it to the door?", "Hint", JOptionPane.INFORMATION_MESSAGE);
					break;
				case 6:
					hintPane.showMessageDialog(this, "I wonder what would happen if you placed one vortex directly above the other...", "Hint", JOptionPane.INFORMATION_MESSAGE);
					break;
				case 7:
					hintPane.showMessageDialog(this, "Success is only a hop, skip, and a fling away!", "Hint", JOptionPane.INFORMATION_MESSAGE);
					break;
				case 8:
					hintPane.showMessageDialog(this, "Each pillar is taller than the last, how could you use this to your advantage?\nAlso, the S key is your friend!", "Hint", JOptionPane.INFORMATION_MESSAGE);
					break;
				case 9:
					hintPane.showMessageDialog(this, "Momentum is conserved when moving through vortexes; I wonder what would happen if both were placed on the ground?", "Hint", JOptionPane.INFORMATION_MESSAGE);
					break;
				case 10:
					hintPane.showMessageDialog(this, "Did you know that the box can move through vortexes without you having to hold it?", "Hint", JOptionPane.INFORMATION_MESSAGE);
					break;
				default:
					hintPane.showMessageDialog(this, "What level are you on anyways?", "Hint", JOptionPane.INFORMATION_MESSAGE);
					break;
			}
		}
	}
}