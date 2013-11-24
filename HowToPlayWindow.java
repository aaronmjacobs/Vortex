import javax.swing.JFrame;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;

public class HowToPlayWindow extends JFrame
{
	public static final int WIDTH = 508;
	public static final int HEIGHT = 730;
	
	public HowToPlayWindow()
	{
		super();
		this.setSize(WIDTH, HEIGHT);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setResizable(false);
		this.setTitle("How to Play");
		this.setLocationRelativeTo(null);
		
		ImagePanel theImagePanel = new ImagePanel();
		this.add(theImagePanel);
	}
	
	private class ImagePanel extends JPanel
	{
		private BufferedImage howToPlayTexture;
		
	    public ImagePanel()
	    {
	        super();

	        try
			{
				howToPlayTexture = ImageIO.read(new File("textures/howtoplay.jpg"));
			}
			catch(IOException e)
			{
				
			}
	    }
	    public void paint(Graphics canvas)
	    {
	        super.paint(canvas);        
	        canvas.drawImage(howToPlayTexture, 0, 0, null);
	    }
	}
}