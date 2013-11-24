// Made by Aaron Jacobs

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;

public class Vortex
{
	public static void main(String[] args)
	{
		MainWindow myMainWindow = new MainWindow(); //Create the window
		JMenuBar bar = new JMenuBar(); //Menu bar
		JMenu fileMenu = new JMenu("File");
		JMenu levelMenu = new JMenu("Level");
		JMenu levelSelectionMenu = new JMenu("Choose level");
		JMenu helpMenu = new JMenu("Help");
		
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(myMainWindow);
		fileMenu.add(exit);
		
		JMenuItem reset = new JMenuItem("Reset level");
		reset.addActionListener(myMainWindow);
		levelMenu.add(reset);
		levelMenu.add(levelSelectionMenu);
		
		JMenuItem level1 = new JMenuItem("Level 1");
		level1.addActionListener(myMainWindow);
		levelSelectionMenu.add(level1);
		
		JMenuItem level2 = new JMenuItem("Level 2");
		level2.addActionListener(myMainWindow);
		levelSelectionMenu.add(level2);
		
		JMenuItem level3 = new JMenuItem("Level 3");
		level3.addActionListener(myMainWindow);
		levelSelectionMenu.add(level3);
		
		JMenuItem level4 = new JMenuItem("Level 4");
		level4.addActionListener(myMainWindow);
		levelSelectionMenu.add(level4);
		
		JMenuItem level5 = new JMenuItem("Level 5");
		level5.addActionListener(myMainWindow);
		levelSelectionMenu.add(level5);
		
		JMenuItem level6 = new JMenuItem("Level 6");
		level6.addActionListener(myMainWindow);
		levelSelectionMenu.add(level6);
		
		JMenuItem level7 = new JMenuItem("Level 7");
		level7.addActionListener(myMainWindow);
		levelSelectionMenu.add(level7);
		
		JMenuItem level8 = new JMenuItem("Level 8");
		level8.addActionListener(myMainWindow);
		levelSelectionMenu.add(level8);
		
		JMenuItem level9 = new JMenuItem("Level 9");
		level9.addActionListener(myMainWindow);
		levelSelectionMenu.add(level9);
		
		JMenuItem level10 = new JMenuItem("Level 10");
		level10.addActionListener(myMainWindow);
		levelSelectionMenu.add(level10);
		
		JMenuItem howToPlay = new JMenuItem("How to Play");
		howToPlay.addActionListener(myMainWindow);
		helpMenu.add(howToPlay);
		
		JMenuItem hint = new JMenuItem("Display hint");
		hint.addActionListener(myMainWindow);
		helpMenu.add(hint);
		
		bar.add(fileMenu);
		bar.add(levelMenu);
		bar.add(helpMenu);
		myMainWindow.setJMenuBar(bar);
		
		myMainWindow.setVisible(true); //Make the window visible
	}
}