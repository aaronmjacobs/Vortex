import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.RenderingHints;
import java.lang.InterruptedException;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.util.ArrayList;
import java.lang.Math;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;

public class GamePanel extends JPanel
{
	//Constants
	private static final double MAXIMUM_VELOCITY = 25;
	private static final long ANIMATOR_SLEEP_TIME = (1000/60);
	private static final long PAINTER_SLEEP_TIME = (1000/60);
	
	private static final double DOOR_WIDTH = 29;
	private static final double DOOR_HEIGHT = 60;
	
	private boolean firstPaint, aPressed, dPressed, onGround, playerOnGround, jumping, playerJustPortaled, boxJustPortaled, lastShotBluePortal, raylineIntersection, raylineColorIsBlue, portalsActive, portal1Active, portal2Active, portalCollisionIssue, portalableIntersection, playerMovementIssue, boxHeld, boxToggle, buttonIntersection, levelHasButton, loadNextLevel, playerFacingRight, levelResetRequired, gameOver;
	private double playerX, playerY, boxX, boxY, heldBoxX, buttonX, buttonY, entranceDoorX, entranceDoorY, exitDoorX, exitDoorY, portalWidth, portalHeight, mouseX, mouseY, minimumDistance;
	private long animatorInitialTime, animatorFinalTime, animatorDeltaTime, animatorSleepTime, painterInitialTime, painterFinalTime, painterDeltaTime, painterSleepTime;
	private int portalRelativeLocation, portal1RelativeLocation, portal2RelativeLocation, currentLevelNumber;
	
	private Graphics2D canvas2D;
	
	private Rectangle2D floor, ceiling, leftWall, rightWall, outsidePlatform, player, thePhysicsObjectIntersection, closestIntersectedObject, box, button, entranceDoor, exitDoor, panel1, panel2, panel3, panel4, panel5, panel6, panel7, panel8;
	private Ellipse2D portal1, portal2;
	private Line2D rayline, drawnRayline;
	private Point2D testPoint;
	
	private BufferedImage playerTexture, playerTextureFlip, darkMarbleTexture, bluePortalTextureVert, bluePortalTextureHoriz, orangePortalTextureVert, orangePortalTextureHoriz, metalFloorLightTexture, metalFloorDarkTexture, backgroundTexture, metalVertLightTexture, metalVertDarkTexture, metalHorizLightTexture, metalHorizDarkTexture, marbleTileTexture, boxTexture, buttonTexture, buttonPressedTexture, doorTexture, doorOpenTexture;
	
	private Vec2 playerPos, playerVel, playerAcc, boxPos, boxVel, boxAcc, ray, testRay, drawnRay;
	private ArrayList allCollidableSurfaces, portalableSurfaces, nonPortalableSurfaces, drawnRaylineIntersections;
	
	private MouseInput myMouseInput;
	private Animator theAnimator;
	private Painter thePainter;
	private RenderingHints theRenderingHints;
	private KeyboardHandler theKeyboardHandler;
	
	private JOptionPane outputPane;
	
	public GamePanel()
	{
		super();
		this.setFocusable(true); //Allows for keyboard listener
		this.setBackground(Color.BLACK);
		this.setDoubleBuffered(true);
		
		firstPaint = true;
		
		playerX = 100.0;
		playerY = 500.0;
		boxX = -200.0;
		boxY = -50.0;
		
		playerPos = new Vec2(playerX, playerY);
		playerVel = new Vec2(0.0, 0.0);
		playerAcc = new Vec2(0.0, 0.2);
		boxPos = new Vec2(boxX, boxY);
		boxVel = new Vec2(0.0, 0.0);
		boxAcc = new Vec2(0.0, 0.2);
		ray = new Vec2(0.0, 0.0);
		testRay = new Vec2(0.0, 0.0);
		drawnRay = new Vec2(0.0, 0.0);
		
		theKeyboardHandler = new KeyboardHandler();
		addKeyListener(theKeyboardHandler);
		
		myMouseInput = new MouseInput();
	    addMouseListener(myMouseInput);
	    addMouseMotionListener(myMouseInput);
	    
	    outputPane = new JOptionPane();
	}
	
	private void initialize()
	{
		firstPaint = false;
		
		thePainter = new Painter();
		thePainter.setName("Painter Thread");
		theAnimator = new Animator();
		theAnimator.setName("Animator Thread");
		theRenderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		theRenderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		//The locations of the shapes
		floor = new Rectangle2D.Double(0, 657, 1008, 50);
		ceiling = new Rectangle2D.Double(0, 0, 1008, 50);
		leftWall = new Rectangle2D.Double(0, 0, 50, 657);
		rightWall = new Rectangle2D.Double(958, 0, 50, 657);
		outsidePlatform = new Rectangle2D.Double(-300, 0, 200, 50);
		button = new Rectangle2D.Double(buttonX, buttonY, 25, 25);
		portal1 = new Ellipse2D.Double(0, -50, portalWidth, portalHeight);
		portal2 = new Ellipse2D.Double(100, -50, portalWidth, portalHeight);
		player = new Rectangle2D.Double(playerX, playerY, 20, 40);
		box = new Rectangle2D.Double(boxX, boxY, 25, 25);
		entranceDoor = new Rectangle2D.Double(entranceDoorX, entranceDoorY, 29, 60);
		exitDoor = new Rectangle2D.Double(exitDoorX, exitDoorY, 29, 60);
		
		panel1 = new Rectangle2D.Double(-100, -100, 50, 50);
		panel2 = new Rectangle2D.Double(-100, -100, 50, 50);
		panel3 = new Rectangle2D.Double(-100, -100, 50, 50);
		panel4 = new Rectangle2D.Double(-100, -100, 50, 50);
		panel5 = new Rectangle2D.Double(-100, -100, 50, 50);
		panel6 = new Rectangle2D.Double(-100, -100, 50, 50);
		panel7 = new Rectangle2D.Double(-100, -100, 50, 50);
		panel8 = new Rectangle2D.Double(-100, -100, 50, 50);
		
		drawnRaylineIntersections = new ArrayList();
		allCollidableSurfaces = new ArrayList();
		portalableSurfaces = new ArrayList(); //Any surface not in this ArrayList will NOT be portalable
		nonPortalableSurfaces = new ArrayList();
		
		rayline = new Line2D.Double();
		drawnRayline = new Line2D.Double();
		testPoint = new Point2D.Double();
		
		try
		{                
        	playerTexture = ImageIO.read(new File("textures/character.png"));
        	playerTextureFlip = ImageIO.read(new File("textures/character_flip.png"));
        	buttonTexture = ImageIO.read(new File("textures/button.png"));
        	buttonPressedTexture = ImageIO.read(new File("textures/button_pressed.png"));
        	boxTexture = ImageIO.read(new File("textures/box_pink.jpg"));
        	doorTexture = ImageIO.read(new File("textures/door.png"));
        	doorOpenTexture = ImageIO.read(new File("textures/door_open.png"));
        	backgroundTexture = ImageIO.read(new File("textures/metal_background.jpg"));
        	
        	bluePortalTextureVert = ImageIO.read(new File("textures/portal_blue_vert.png"));
        	orangePortalTextureVert = ImageIO.read(new File("textures/portal_orange_vert.png"));
        	bluePortalTextureHoriz = ImageIO.read(new File("textures/portal_blue_horiz.png"));
        	orangePortalTextureHoriz = ImageIO.read(new File("textures/portal_orange_horiz.png"));
        	
        	metalFloorLightTexture = ImageIO.read(new File("textures/metal_floor.jpg"));
        	metalFloorDarkTexture = ImageIO.read(new File("textures/metal_floor_dark.jpg"));
        	metalVertLightTexture = ImageIO.read(new File("textures/metal_vert_light.jpg"));
        	metalVertDarkTexture = ImageIO.read(new File("textures/metal_vert_dark.jpg"));
        	metalHorizLightTexture = ImageIO.read(new File("textures/metal_horiz_light.jpg"));
        	metalHorizDarkTexture = ImageIO.read(new File("textures/metal_horiz_dark.jpg"));
        	
        	marbleTileTexture = ImageIO.read(new File("textures/metal_tile_light.jpg"));
        	darkMarbleTexture = ImageIO.read(new File("textures/metal_tile_dark.jpg"));
		}
		catch(IOException e)
		{
		}
		currentLevelNumber = 1;
		loadLevel(currentLevelNumber);
		
		theAnimator.start(); //Starts the animation/painting threads
		thePainter.start();
	}
	
	public void loadLevel(int theLevelNumber)
	{
		currentLevelNumber = theLevelNumber;
		
		resetVariables();
		
		if(theLevelNumber == 1)
		{
			//Button code
			levelHasButton = false;
			
			//Player code
			playerPos.set(100, 617);
			
			//Level structure code
			panel1.setFrame(50, 400, 400, 50);
			panel2.setFrame(400, 400, 50, 400);
			panel3.setFrame(200, 425, 50, 200);
			panel4.setFrame(200, 650, 50, 100);
			
			//Door code
			entranceDoorX = 100.0;
			entranceDoorY = 597.0;
			exitDoorX = 300.0;
			exitDoorY = 597.0;
			exitDoor.setFrame(exitDoorX, exitDoorY, DOOR_WIDTH, DOOR_HEIGHT);
			
			//Collision code
			nonPortalableSurfaces.add(panel1);
			portalableSurfaces.add(panel2);
			nonPortalableSurfaces.add(panel3);
			nonPortalableSurfaces.add(panel4);
			
			nonPortalableSurfaces.add(floor);
			portalableSurfaces.add(leftWall);
			nonPortalableSurfaces.add(ceiling);
			nonPortalableSurfaces.add(rightWall);
			
			allCollidableSurfaces.addAll(portalableSurfaces);
			allCollidableSurfaces.addAll(nonPortalableSurfaces);
		}
		if(theLevelNumber == 2)
		{
			levelHasButton = false;
			
			playerPos.set(100, 467);
			
			panel1.setFrame(50, 100, 400, 50);
			panel2.setFrame(450, 100, 50, 600);
			panel3.setFrame(350, 500,100, 200);
			panel4.setFrame(50, 500, 100, 200);
			
			entranceDoorX = 100.0;
			entranceDoorY = 447.0;
			exitDoorX = 400.0;
			exitDoorY = 440.0;
			exitDoor.setFrame(exitDoorX, exitDoorY, DOOR_WIDTH, DOOR_HEIGHT);
			
			nonPortalableSurfaces.add(panel1);
			nonPortalableSurfaces.add(panel2);
			nonPortalableSurfaces.add(panel3);
			nonPortalableSurfaces.add(panel4);
			
			portalableSurfaces.add(floor);
			portalableSurfaces.add(leftWall);
			nonPortalableSurfaces.add(ceiling);
			nonPortalableSurfaces.add(rightWall);
			
			allCollidableSurfaces.addAll(portalableSurfaces);
			allCollidableSurfaces.addAll(nonPortalableSurfaces);
		}
		if(theLevelNumber == 3)
		{
			levelHasButton = true;
			buttonX = 100.0;
			buttonY = 632.0;
			button.setFrame(buttonX, buttonY, 25, 25);
			
			boxX = 500.0;
			boxY = 450.0;
			boxPos.set(boxX, boxY);
			
			playerPos.set(100, 400);
			
			panel1.setFrame(50, 450, 150, 50);
			panel2.setFrame(400, 500, 150, 50);
			panel3.setFrame(50, 300, 500, 50);
			panel4.setFrame(550, 300, 50, 400);
			
			entranceDoorX = 100.0;
			entranceDoorY = 390.0;
			exitDoorX = 500.0;
			exitDoorY = 597.0;
			exitDoor.setFrame(exitDoorX, exitDoorY, DOOR_WIDTH, DOOR_HEIGHT);
			
			nonPortalableSurfaces.add(panel1);
			nonPortalableSurfaces.add(panel2);
			nonPortalableSurfaces.add(panel3);
			portalableSurfaces.add(panel4);
			
			nonPortalableSurfaces.add(floor);
			portalableSurfaces.add(leftWall);
			nonPortalableSurfaces.add(ceiling);
			nonPortalableSurfaces.add(rightWall);
			
			allCollidableSurfaces.addAll(portalableSurfaces);
			allCollidableSurfaces.addAll(nonPortalableSurfaces);
		}
		if(theLevelNumber == 4)
		{
			levelHasButton = true;
			buttonX = 400.0;
			buttonY = 632.0;
			button.setFrame(buttonX, buttonY, 25, 25);
			
			boxX = 250.0;
			boxY = 350.0;
			boxPos.set(boxX, boxY);
			
			playerPos.set(100, 617);
			
			panel1.setFrame(50, 400, 50, 50);
			panel2.setFrame(200, 400, 300, 50);
			panel3.setFrame(50, 200, 600, 50);
			panel4.setFrame(650, 200, 50, 500);
			panel5.setFrame(300, 200, 50, 350);
			panel6.setFrame(300, 580, 50, 100);
			panel7.setFrame(550, 400, 150, 50);
			
			entranceDoorX = 100.0;
			entranceDoorY = 597.0;
			exitDoorX = 600.0;
			exitDoorY = 340.0;
			exitDoor.setFrame(exitDoorX, exitDoorY, DOOR_WIDTH, DOOR_HEIGHT);
			
			nonPortalableSurfaces.add(panel1);
			nonPortalableSurfaces.add(panel2);
			portalableSurfaces.add(panel3);
			portalableSurfaces.add(panel4);
			nonPortalableSurfaces.add(panel5);
			nonPortalableSurfaces.add(panel6);
			nonPortalableSurfaces.add(panel7);
			
			nonPortalableSurfaces.add(floor);
			portalableSurfaces.add(leftWall);
			nonPortalableSurfaces.add(ceiling);
			nonPortalableSurfaces.add(rightWall);
			
			allCollidableSurfaces.addAll(portalableSurfaces);
			allCollidableSurfaces.addAll(nonPortalableSurfaces);
		}
		if(theLevelNumber == 5)
		{
			levelHasButton = true;
			buttonX = 800.0;
			buttonY = 425.0;
			button.setFrame(buttonX, buttonY, 25, 25);
			
			boxX = 150.0;
			boxY = 350.0;
			boxPos.set(boxX, boxY);
			
			playerPos.set(100, 617);
			
			panel1.setFrame(50, 400, 400, 50);
			panel2.setFrame(600, 450, 400, 50);
			panel3.setFrame(908, 150, 50, 300);
			panel4.setFrame(700, 150, 300, 50);
			panel5.setFrame(500, 50, 50, 200);
			
			entranceDoorX = 100.0;
			entranceDoorY = 597.0;
			exitDoorX = 850.0;
			exitDoorY = 90.0;
			exitDoor.setFrame(exitDoorX, exitDoorY, DOOR_WIDTH, DOOR_HEIGHT);
			
			nonPortalableSurfaces.add(panel1);
			nonPortalableSurfaces.add(panel2);
			portalableSurfaces.add(panel3);
			nonPortalableSurfaces.add(panel4);
			portalableSurfaces.add(panel5);
			
			portalableSurfaces.add(floor);
			portalableSurfaces.add(leftWall);
			nonPortalableSurfaces.add(ceiling);
			nonPortalableSurfaces.add(rightWall);
			
			allCollidableSurfaces.addAll(portalableSurfaces);
			allCollidableSurfaces.addAll(nonPortalableSurfaces);
		}
		if(theLevelNumber == 6)
		{
			levelHasButton = false;
			
			playerPos.set(100, 617);
			
			panel1.setFrame(50, 50, 300, 50);
			panel2.setFrame(600, 200, 500, 50);
			panel3.setFrame(350, 50, 50, 50);
			
			entranceDoorX = 100.0;
			entranceDoorY = 597.0;
			exitDoorX = 900.0;
			exitDoorY = 140.0;
			exitDoor.setFrame(exitDoorX, exitDoorY, DOOR_WIDTH, DOOR_HEIGHT);
			
			portalableSurfaces.add(panel1);
			nonPortalableSurfaces.add(panel2);
			nonPortalableSurfaces.add(panel3);
			
			portalableSurfaces.add(floor);
			portalableSurfaces.add(leftWall);
			nonPortalableSurfaces.add(ceiling);
			nonPortalableSurfaces.add(rightWall);
			
			allCollidableSurfaces.addAll(portalableSurfaces);
			allCollidableSurfaces.addAll(nonPortalableSurfaces);
		}
		if(theLevelNumber == 7)
		{
			levelHasButton = true;
			buttonX = 550.0;
			buttonY = 425.0;
			button.setFrame(buttonX, buttonY, 25, 25);
			
			boxX = 550.0;
			boxY = 275.0;
			boxPos.set(boxX, boxY);
			
			playerPos.set(100, 617);
			
			panel1.setFrame(50, 100, 300, 50);
			panel2.setFrame(350, 100, 250, 50);
			panel3.setFrame(600, 100, 50, 600);
			panel4.setFrame(250, 607, 350, 50);
			panel5.setFrame(450, 300, 150, 50);
			panel6.setFrame(400, 450, 200, 50);
			panel7.setFrame(200, 300, 50, 600);
			
			entranceDoorX = 100.0;
			entranceDoorY = 597.0;
			exitDoorX = 550.0;
			exitDoorY = 547.0;
			exitDoor.setFrame(exitDoorX, exitDoorY, DOOR_WIDTH, DOOR_HEIGHT);
			
			portalableSurfaces.add(panel1);
			nonPortalableSurfaces.add(panel2);
			nonPortalableSurfaces.add(panel3);
			portalableSurfaces.add(panel4);
			nonPortalableSurfaces.add(panel5);
			nonPortalableSurfaces.add(panel6);
			nonPortalableSurfaces.add(panel7);
			
			nonPortalableSurfaces.add(floor);
			portalableSurfaces.add(leftWall);
			nonPortalableSurfaces.add(ceiling);
			nonPortalableSurfaces.add(rightWall);
			
			allCollidableSurfaces.addAll(portalableSurfaces);
			allCollidableSurfaces.addAll(nonPortalableSurfaces);
		}
		if(theLevelNumber == 8)
		{
			levelHasButton = false;
			
			playerPos.set(100, 567);
			
			panel1.setFrame(50, 50, 50, 200);
			panel2.setFrame(200, 500, 100, 200);
			panel3.setFrame(400, 350, 100, 350);
			panel4.setFrame(650, 300, 100, 600);
			panel5.setFrame(700, 130, 150, 50);
			panel6.setFrame(908, 50, 50, 50);
			panel7.setFrame(700, 50, 50, 200);
			panel8.setFrame(50, 607, 150, 50);
			
			entranceDoorX = 100.0;
			entranceDoorY = 557.0;
			exitDoorX = 800.0;
			exitDoorY = 70.0;
			exitDoor.setFrame(exitDoorX, exitDoorY, DOOR_WIDTH, DOOR_HEIGHT);
			
			portalableSurfaces.add(panel1);
			nonPortalableSurfaces.add(panel2);
			nonPortalableSurfaces.add(panel3);
			nonPortalableSurfaces.add(panel4);
			nonPortalableSurfaces.add(panel5);
			portalableSurfaces.add(panel6);
			nonPortalableSurfaces.add(panel7);
			nonPortalableSurfaces.add(panel8);
			
			portalableSurfaces.add(floor);
			portalableSurfaces.add(leftWall);
			nonPortalableSurfaces.add(ceiling);
			nonPortalableSurfaces.add(rightWall);
			
			allCollidableSurfaces.addAll(portalableSurfaces);
			allCollidableSurfaces.addAll(nonPortalableSurfaces);
		}
		if(theLevelNumber == 9)
		{
			levelHasButton = true;
			buttonX = 525.0;
			buttonY = 632.0;
			button.setFrame(buttonX, buttonY, 25, 25);
			
			boxX = 125.0;
			boxY = 200.0;
			boxPos.set(boxX, boxY);
			
			playerPos.set(100, 617);
			
			panel1.setFrame(50, 250, 100, 50);
			panel2.setFrame(500, 250, 100, 50);
			panel3.setFrame(500, 50, 100, 50);
			panel4.setFrame(600, 50, 50, 650);
			panel5.setFrame(450, 50, 50, 50);
			
			entranceDoorX = 100.0;
			entranceDoorY = 597.0;
			exitDoorX = 550.0;
			exitDoorY = 190.0;
			exitDoor.setFrame(exitDoorX, exitDoorY, DOOR_WIDTH, DOOR_HEIGHT);
			
			nonPortalableSurfaces.add(panel1);
			nonPortalableSurfaces.add(panel2);
			portalableSurfaces.add(panel3);
			nonPortalableSurfaces.add(panel4);
			nonPortalableSurfaces.add(panel5);
			
			portalableSurfaces.add(floor);
			nonPortalableSurfaces.add(leftWall);
			nonPortalableSurfaces.add(ceiling);
			nonPortalableSurfaces.add(rightWall);
			
			allCollidableSurfaces.addAll(portalableSurfaces);
			allCollidableSurfaces.addAll(nonPortalableSurfaces);
		}
		if(theLevelNumber == 10)
		{
			levelHasButton = true;
			buttonX = 425.0;
			buttonY = 275.0;
			button.setFrame(buttonX, buttonY, 25, 25);
			
			boxX = 700.0;
			boxY = 550.0;
			boxPos.set(boxX, boxY);
			
			playerPos.set(100, 617);
			
			panel1.setFrame(500, 607, 550, 50);
			panel2.setFrame(500, 50, 550, 50);
			panel3.setFrame(450, 50, 50, 300);
			panel4.setFrame(350, 300, 100, 50);
			panel5.setFrame(300, 150, 50, 300);
			panel6.setFrame(800, 200, 250, 50);
			
			entranceDoorX = 100.0;
			entranceDoorY = 597.0;
			exitDoorX = 900.0;
			exitDoorY = 140.0;
			exitDoor.setFrame(exitDoorX, exitDoorY, DOOR_WIDTH, DOOR_HEIGHT);
			
			portalableSurfaces.add(panel1);
			portalableSurfaces.add(panel2);
			nonPortalableSurfaces.add(panel3);
			nonPortalableSurfaces.add(panel4);
			nonPortalableSurfaces.add(panel5);
			nonPortalableSurfaces.add(panel6);
			
			nonPortalableSurfaces.add(floor);
			portalableSurfaces.add(leftWall);
			nonPortalableSurfaces.add(ceiling);
			nonPortalableSurfaces.add(rightWall);
			
			allCollidableSurfaces.addAll(portalableSurfaces);
			allCollidableSurfaces.addAll(nonPortalableSurfaces);
		}
		if(theLevelNumber == 11)
		{
			levelHasButton = false;
			
			playerPos.set(100, 617);
			
			nonPortalableSurfaces.add(floor);
			nonPortalableSurfaces.add(leftWall);
			nonPortalableSurfaces.add(ceiling);
			nonPortalableSurfaces.add(rightWall);
			
			allCollidableSurfaces.addAll(portalableSurfaces);
			allCollidableSurfaces.addAll(nonPortalableSurfaces);
			if(!gameOver)
			{
				gameOver = true;
				outputPane.showMessageDialog(this, "Congratulations!", "You win!", JOptionPane.INFORMATION_MESSAGE);
				this.loadLevel(1);
			}
		}
	}
	
	private void resetVariables()
	{
		allCollidableSurfaces.clear();
		portalableSurfaces.clear();
		nonPortalableSurfaces.clear();
		loadNextLevel = false;
		
		playerX = 100.0;
		playerY = 500.0;
		boxX = -200.0;
		boxY = -50.0;
		buttonX = 0.0;
		buttonY = -200;
		entranceDoorX = -100;
		entranceDoorY = -100;
		exitDoorX = -100;
		exitDoorY = -100;
		portalWidth = 20.0;
		portalHeight = 40.0;
		mouseX = 0.0;
		mouseY = 0.0;
		heldBoxX = 20.0;
		
		portal1.setFrame(0, -50, portalWidth, portalHeight);
		portal2.setFrame(100, -50, portalWidth, portalHeight);
		panel1.setFrame(-100, -100, 50, 50);
		panel2.setFrame(-100, -100, 50, 50);
		panel3.setFrame(-100, -100, 50, 50);
		panel4.setFrame(-100, -100, 50, 50);
		panel5.setFrame(-100, -100, 50, 50);
		panel6.setFrame(-100, -100, 50, 50);
		panel7.setFrame(-100, -100, 50, 50);
		panel8.setFrame(-100, -100, 50, 50);
		button.setFrame(buttonX, buttonY, 25, 25);
		box.setFrame(boxX, boxY, 25, 25);
		entranceDoor.setFrame(entranceDoorX, entranceDoorY, 29, 60);
		exitDoor.setFrame(exitDoorX, exitDoorY, 29, 60);
		
		playerPos.set(100, 617);
		playerVel.set(0.0, 0.0);
		playerAcc.set(0.0, 0.0);
		
		boxPos.set(boxX, boxY);
		boxVel.set(0.0, 0.0);
		boxAcc.set(0.0, 0.0);
		
		portalRelativeLocation = 0;
		portal1RelativeLocation = 0;
		portal2RelativeLocation = 0;
		
		aPressed = false;
		dPressed = false;
		onGround = false;
		playerOnGround = false;
		jumping = false;
		playerJustPortaled = false;
		boxJustPortaled = false;
		lastShotBluePortal = true;
		raylineIntersection = false;
		portalsActive = false;
		portal1Active = false;
		portal2Active = false;
		portalableIntersection = false;
		portalCollisionIssue = false;
		raylineColorIsBlue = false;
		playerMovementIssue = false;
		boxHeld = false;
		boxToggle = true;
		buttonIntersection = false;
		playerFacingRight = true;
		levelResetRequired = false;
		gameOver = false;
	}
	
	public int getCurrentLevelNumber()
	{
		return this.currentLevelNumber;
	}
	
	public void paint(Graphics canvas)
	{
		super.paint(canvas);
		
		canvas2D = (Graphics2D)canvas;
		
		if(firstPaint) //Only ran once
		{
			this.initialize();
		}
		
		canvas2D.setRenderingHints(theRenderingHints);
		
		canvas2D.drawImage(backgroundTexture, 0, 0, this); //Background image
		
		canvas2D.drawImage(doorTexture, (int)entranceDoorX, (int)entranceDoorY, this);
		if(buttonIntersection)
		{
			canvas2D.drawImage(doorOpenTexture, (int)exitDoorX, (int)exitDoorY, this);
			canvas2D.drawImage(buttonPressedTexture, (int)buttonX, (int)buttonY, this);
		}
		else
		{
			canvas2D.drawImage(doorTexture, (int)exitDoorX, (int)exitDoorY, this);
			canvas2D.drawImage(buttonTexture, (int)buttonX, (int)buttonY, this);
		}
		
		if(portalableIntersection && raylineColorIsBlue)
		{
			canvas2D.setColor(Color.BLUE);
		}
		else if(portalableIntersection && !raylineColorIsBlue)
		{
			canvas2D.setColor(Color.ORANGE);
		}
		else
		{
			canvas2D.setColor(Color.GRAY);
		}
		canvas2D.draw(drawnRayline); //Used to show where a portal will be placed
		
		for(int i = 0; i < portalableSurfaces.size(); i++) //Portalable surfaces
		{
			double theWidth = ((Rectangle2D)portalableSurfaces.get(i)).getWidth();
			double theHeight = ((Rectangle2D)portalableSurfaces.get(i)).getHeight();
			int timesToDrawX = (int)(theWidth / 50);
			int timesToDrawY = (int)(theHeight / 50);
			
			for(int x = 0; x < timesToDrawX; x++)
			{
				for(int y = 0; y < timesToDrawY; y++)
				{
					canvas2D.drawImage(marbleTileTexture, (int)((Rectangle2D)portalableSurfaces.get(i)).getX() + 50 * x, (int)((Rectangle2D)portalableSurfaces.get(i)).getY() + 50 * y, this);
				}
			}
		}
		
		for(int i = 0; i < nonPortalableSurfaces.size(); i++) //Non-portalable surfacse
		{
			double theWidth = ((Rectangle2D)nonPortalableSurfaces.get(i)).getWidth();
			double theHeight = ((Rectangle2D)nonPortalableSurfaces.get(i)).getHeight();
			int timesToDrawX = (int)(theWidth / 50);
			int timesToDrawY = (int)(theHeight / 50);
			
			for(int x = 0; x < timesToDrawX; x++)
			{
				for(int y = 0; y < timesToDrawY; y++)
				{
					canvas2D.drawImage(darkMarbleTexture, (int)((Rectangle2D)nonPortalableSurfaces.get(i)).getX() + 50 * x, (int)((Rectangle2D)nonPortalableSurfaces.get(i)).getY() + 50 * y, this);
				}
			}
		}
		
		if(portalableSurfaces.contains(leftWall))
		{
			canvas2D.drawImage(metalVertLightTexture, (int)leftWall.getX(), (int)leftWall.getY(), this);
			canvas2D.drawImage(metalVertLightTexture, (int)leftWall.getX(), (int)leftWall.getY() + 400, this);
		}
		else
		{
			canvas2D.drawImage(metalVertDarkTexture, (int)leftWall.getX(), (int)leftWall.getY(), this);
			canvas2D.drawImage(metalVertDarkTexture, (int)leftWall.getX(), (int)leftWall.getY() + 400, this);
		}
		
		if(portalableSurfaces.contains(rightWall))
		{
			canvas2D.drawImage(metalVertLightTexture, (int)rightWall.getX(), (int)rightWall.getY(), this);
			canvas2D.drawImage(metalVertLightTexture, (int)rightWall.getX(), (int)rightWall.getY() + 400, this);
		}
		else
		{
			canvas2D.drawImage(metalVertDarkTexture, (int)rightWall.getX(), (int)rightWall.getY(), this);
			canvas2D.drawImage(metalVertDarkTexture, (int)rightWall.getX(), (int)rightWall.getY() + 400, this);
		}
		
		if(portalableSurfaces.contains(ceiling))
		{
			canvas2D.drawImage(metalHorizLightTexture, (int)ceiling.getX(), (int)ceiling.getY(), this);
			canvas2D.drawImage(metalHorizLightTexture, (int)ceiling.getX() + 400, (int)ceiling.getY(), this);
			canvas2D.drawImage(metalHorizLightTexture, (int)ceiling.getX() + 800, (int)ceiling.getY(), this);
		}
		else
		{
			canvas2D.drawImage(metalHorizDarkTexture, (int)ceiling.getX(), (int)ceiling.getY(), this);
			canvas2D.drawImage(metalHorizDarkTexture, (int)ceiling.getX() + 400, (int)ceiling.getY(), this);
			canvas2D.drawImage(metalHorizDarkTexture, (int)ceiling.getX() + 800, (int)ceiling.getY(), this);
		}
		
		if(portalableSurfaces.contains(floor))
		{
			canvas2D.drawImage(metalFloorLightTexture, (int)floor.getX(), (int)floor.getY(), this);
			canvas2D.drawImage(metalFloorLightTexture, (int)floor.getX() + 400, (int)floor.getY(), this);
			canvas2D.drawImage(metalFloorLightTexture, (int)floor.getX() + 800, (int)floor.getY(), this);
		}
		else
		{
			canvas2D.drawImage(metalFloorDarkTexture, (int)floor.getX(), (int)floor.getY(), this);
			canvas2D.drawImage(metalFloorDarkTexture, (int)floor.getX() + 400, (int)floor.getY(), this);
			canvas2D.drawImage(metalFloorDarkTexture, (int)floor.getX() + 800, (int)floor.getY(), this);
		}
		
		
		if(portal1.getWidth() > portal1.getHeight())
		{
			canvas2D.drawImage(bluePortalTextureHoriz, (int)portal1.getX(), (int)portal1.getY(), this);
		}
		else
		{
			canvas2D.drawImage(bluePortalTextureVert, (int)portal1.getX(), (int)portal1.getY(), this);
		}
		
		if(portal2.getWidth() > portal2.getHeight())
		{
			canvas2D.drawImage(orangePortalTextureHoriz, (int)portal2.getX(), (int)portal2.getY(), this);
		}
		else
		{
			canvas2D.drawImage(orangePortalTextureVert, (int)portal2.getX(), (int)portal2.getY(), this);
		}
		
		canvas2D.drawImage(boxTexture, (int)boxPos.getX(), (int)boxPos.getY(), this);
		
		if(playerFacingRight)
		{
			canvas2D.drawImage(playerTexture, (int)playerPos.getX(), (int)playerPos.getY(), this);
		}
		else
		{
			canvas2D.drawImage(playerTextureFlip, (int)playerPos.getX(), (int)playerPos.getY(), this);
		}
		
		if(loadNextLevel && buttonIntersection)
		{
			this.loadLevel(++currentLevelNumber);
		}
		
		if(levelResetRequired)
		{
			this.loadLevel(currentLevelNumber);
		}
	}
	
	private class Animator extends Thread
	{
		public void run()
		{
			while(true) //While the game is running
			{
				animatorInitialTime = System.currentTimeMillis();
				
				if(!levelHasButton)
				{
					buttonIntersection = true;
				}
				
				this.checkMaxVelocity(playerVel); //Check against the maximum velocity
				this.checkBounds(playerPos); //Makes sure the player is inside the visible area
				this.checkCollisions(player, playerPos, playerVel, playerAcc); //Check to see if the player is colliding with a surface
				this.addFriction(playerVel, playerAcc); //Adds friction
				this.sumAccVelPos(playerPos, playerVel, playerAcc); //Add up the player's acceleration to their velocity, and their velocity to their position
				
				if(!boxHeld)
				{
					this.checkMaxVelocity(boxVel);
					if(levelHasButton)
					{
						this.checkBounds(boxPos);
					}
					this.checkCollisions(box, boxPos, boxVel, boxAcc);
					this.addFriction(boxVel, boxAcc);
					this.sumAccVelPos(boxPos, boxVel, boxAcc);
				}
				else
				{
					boxPos.set((playerPos.getX() + heldBoxX), (playerPos.getY() + 10));
					boxVel.set(playerVel);
					boxAcc.set(playerAcc);
					this.checkCollisions(box, boxPos, boxVel, boxAcc);
					playerPos.set(boxPos.getX() - heldBoxX, boxPos.getY() - 10);
				}
				
				this.setNewPosition(); //Move the player to the new location
				
				this.setDrawnRay(); //Draw the ray indicating where a portal will go
				
				animatorFinalTime = System.currentTimeMillis();
				
				this.doNothing(); //Sleep for the appropriate amount of time
			}
		}
		
		private void checkMaxVelocity(Vec2 thePhysicsObjectVel) //Check against the maximum velocity
		{
			if(thePhysicsObjectVel.getX() > MAXIMUM_VELOCITY)
			{
				thePhysicsObjectVel.set(MAXIMUM_VELOCITY, thePhysicsObjectVel.getY());
			}
			else if(thePhysicsObjectVel.getX() < -MAXIMUM_VELOCITY)
			{
				thePhysicsObjectVel.set(-MAXIMUM_VELOCITY, thePhysicsObjectVel.getY());
			}
			
			if(thePhysicsObjectVel.getY() > MAXIMUM_VELOCITY)
			{
				thePhysicsObjectVel.set(thePhysicsObjectVel.getX(), MAXIMUM_VELOCITY);
			}
			else if(thePhysicsObjectVel.getY() < -MAXIMUM_VELOCITY)
			{
				thePhysicsObjectVel.set(thePhysicsObjectVel.getX(), -MAXIMUM_VELOCITY);
			}
		}
		
		private void checkBounds(Vec2 thePhysicsObjectPos) //Resets the level if the player/box exit the visible area
		{
			if(thePhysicsObjectPos.getX() < 0.0
			|| thePhysicsObjectPos.getX() > 1008.0
			|| thePhysicsObjectPos.getY() < 0.0
			|| thePhysicsObjectPos.getY() > 707.0)
			{
				levelResetRequired = true;
			}
		}
		
		private void checkCollisions(Rectangle2D thePhysicsObject, Vec2 thePhysicsObjectPos, Vec2 thePhysicsObjectVel, Vec2 thePhysicsObjectAcc) //Handles collisions
		{
			onGround = false;
			if(thePhysicsObject == player)
			{
				playerOnGround = false;
			}
			
			for(int i = 0; i < allCollidableSurfaces.size(); i++) //For each normal surface
			{
				if(thePhysicsObject.intersects((Rectangle2D)allCollidableSurfaces.get(i))) //If the player is touching a surface
				{
					if(portalsActive && (thePhysicsObject.intersects(portal1.getFrame()) || thePhysicsObject.intersects(portal2.getFrame()))) //If the player is touching an active portal
					{
						break;
					}
					thePhysicsObjectIntersection = thePhysicsObject.createIntersection((Rectangle2D)allCollidableSurfaces.get(i)); //Create an object representing the intersection
					
					if(thePhysicsObjectIntersection.getHeight() > 5) //Makes sure a majority of the player is up against the wall
					{
						if(thePhysicsObjectIntersection.getCenterX() > thePhysicsObject.getCenterX()) //If the surface is to the right of the player
						{
							if(thePhysicsObjectIntersection.getWidth() > 0) //If they are not just touching the edge
							{
								thePhysicsObjectPos.set(thePhysicsObjectPos.getX() - thePhysicsObjectIntersection.getWidth(), thePhysicsObjectPos.getY()); //Move them out of the object
								
								//Stop them from moving
								thePhysicsObjectVel.set(0.0, thePhysicsObjectVel.getY());
								thePhysicsObjectAcc.set(0.0, thePhysicsObjectVel.getY());
							}
						}
						else if(thePhysicsObjectIntersection.getCenterX() < thePhysicsObject.getCenterX()) //If the surface is to the left of the player
						{
							if(thePhysicsObjectIntersection.getWidth() > 0) //If they are not just touching the edge
							{
								thePhysicsObjectPos.set(thePhysicsObjectPos.getX() + thePhysicsObjectIntersection.getWidth(), thePhysicsObjectPos.getY()); //Move them out of the object
								
								thePhysicsObjectVel.set(0.0, thePhysicsObjectVel.getY());
								thePhysicsObjectAcc.set(0.0, thePhysicsObjectVel.getY());
							}
						}
					}
					
					if(thePhysicsObjectIntersection.getWidth() > 5) //Makes sure a majority of the player is up against the floor/ceiling
					{
						if(thePhysicsObjectIntersection.getCenterY() < thePhysicsObject.getCenterY()) //If the surface is above the player
						{
							if(thePhysicsObjectIntersection.getHeight() > 0) //If they are not just touching the edge
							{
								thePhysicsObjectPos.set(thePhysicsObjectPos.getX(), thePhysicsObjectPos.getY() + thePhysicsObjectIntersection.getHeight()); //Move them out of the object
								
								//Stop them from moving
								thePhysicsObjectVel.set(thePhysicsObjectVel.getX(), 0.0);
								thePhysicsObjectAcc.set(thePhysicsObjectAcc.getX(), 0.0);
							}
						}
						else if(thePhysicsObjectIntersection.getCenterY() > thePhysicsObject.getCenterY()) //If the surface is below the player
						{
							onGround = true;
							if(thePhysicsObject == player)
							{
								playerOnGround = true;
							}
							
							if(thePhysicsObjectIntersection.getHeight() > 0) //If they are not just touching the surface of the floor
							{
								thePhysicsObjectPos.set(thePhysicsObjectPos.getX(), thePhysicsObjectPos.getY() - thePhysicsObjectIntersection.getHeight() + 1); //Move them to the surface
								
								if(!jumping && (thePhysicsObject == player))
								{
									//Stop them from moving
									thePhysicsObjectVel.set(thePhysicsObjectVel.getX(), 0.0);
									thePhysicsObjectAcc.set(thePhysicsObjectAcc.getX(), 0.0);
								}
								else if(thePhysicsObject != player)
								{
									//Stop them from moving
									thePhysicsObjectVel.set(thePhysicsObjectVel.getX(), 0.0);
									thePhysicsObjectAcc.set(thePhysicsObjectAcc.getX(), 0.0);
								}
							}
						}
					}
				}
				
				if(drawnRayline.intersects((Rectangle2D)allCollidableSurfaces.get(i))) //If the drawnRayline intersects a surface
				{
					drawnRaylineIntersections.add((Rectangle2D)allCollidableSurfaces.get(i)); //Contains all surfaces that the drawnRayline intersects
				}
				
				if(!onGround) //Apply gravity if not on the ground
				{
					thePhysicsObjectAcc.set(thePhysicsObjectAcc.getX(), 0.2);
				}
			}
			
			if(levelHasButton)
			{
				if(player.intersects(button) || box.intersects(button))
				{
					buttonIntersection = true;
				}
				else
				{
					buttonIntersection = false;
				}	
			}
			
			if(drawnRaylineIntersections.size() > 0) //If the drawnRayline intersects any surfaces
			{
				double distance;
				
				//Find which surface is the closest (not perfect)
				minimumDistance = Math.sqrt(Math.pow(((Rectangle2D)drawnRaylineIntersections.get(0)).getCenterX() - player.getCenterX(), 2) + Math.pow(((Rectangle2D)drawnRaylineIntersections.get(0)).getCenterY() - player.getCenterY(), 2));
				for(int i = 0; i < drawnRaylineIntersections.size(); i++)
				{
					distance = Math.sqrt(Math.pow(((Rectangle2D)drawnRaylineIntersections.get(i)).getCenterX() - player.getCenterX(), 2) + Math.pow(((Rectangle2D)drawnRaylineIntersections.get(i)).getCenterY() - player.getCenterY(), 2));
					if(distance <= minimumDistance)
					{
						closestIntersectedObject = (Rectangle2D)drawnRaylineIntersections.get(i);
						minimumDistance = Math.sqrt(Math.pow((closestIntersectedObject.getCenterX() - player.getCenterX()), 2) + Math.pow((closestIntersectedObject).getCenterY() - player.getCenterY(), 2));
					}
				}
				
				if(portalableSurfaces.contains(closestIntersectedObject)) //if the closest object is portalable
				{
					portalableIntersection = true;
				}
				else
				{
					portalableIntersection = false;
				}
			}
			drawnRaylineIntersections.clear();
			
			if(thePhysicsObject == player)
			{
				if(!playerJustPortaled) //For portals
				{
					if(thePhysicsObject.intersects(portal1.getFrame()) && portalsActive)
					{
						thePhysicsObjectPos.set(portal2.getX(), portal2.getY());
						setUpPortalPhysics(portal1RelativeLocation, portal2RelativeLocation, thePhysicsObjectPos, thePhysicsObjectVel, thePhysicsObjectAcc);
						playerJustPortaled = true;
					}
					else if(thePhysicsObject.intersects(portal2.getFrame()) && portalsActive)
					{
						thePhysicsObjectPos.set(portal1.getX(), portal1.getY());
						setUpPortalPhysics(portal2RelativeLocation, portal1RelativeLocation, thePhysicsObjectPos, thePhysicsObjectVel, thePhysicsObjectAcc);
						playerJustPortaled = true;
					}
				}
				
				else if(!thePhysicsObject.intersects(portal1.getFrame()) && !thePhysicsObject.intersects(portal2.getFrame())) //Once the player is no longer touching a portal, they are able to use a portal again
				{
					playerJustPortaled = false;
				}
				
				if(player.intersects(exitDoor))
				{
					loadNextLevel = true;
				}
				else
				{
					loadNextLevel = false;
				}
			}
			
			if(thePhysicsObject == box)
			{
				if(!boxHeld)
				{
					if(!boxJustPortaled) //For portals
					{
						if(thePhysicsObject.intersects(portal1.getFrame()) && portalsActive)
						{
							thePhysicsObjectPos.set(portal2.getX(), portal2.getY());
							setUpPortalPhysics(portal1RelativeLocation, portal2RelativeLocation, thePhysicsObjectPos, thePhysicsObjectVel, thePhysicsObjectAcc);
							boxJustPortaled = true;
						}
						else if(thePhysicsObject.intersects(portal2.getFrame()) && portalsActive)
						{
							thePhysicsObjectPos.set(portal1.getX(), portal1.getY());
							setUpPortalPhysics(portal2RelativeLocation, portal1RelativeLocation, thePhysicsObjectPos, thePhysicsObjectVel, thePhysicsObjectAcc);
							boxJustPortaled = true;
						}
					}
					
					else if(!thePhysicsObject.intersects(portal1.getFrame()) && !thePhysicsObject.intersects(portal2.getFrame())) //Once the player is no longer touching a portal, they are able to use a portal again
					{
						boxJustPortaled = false;
					}
				}
			}
		}
		
		private void setUpPortalPhysics(int firstPortalRelativeLocation, int secondPortalRelativeLocation, Vec2 thePhysicsObjectPos, Vec2 thePhysicsObjectVel, Vec2 thePhysicsObjectAcc) //Sets up the portal physics (dependent upon the relative placement of the portals: above, below etc.)
		{
			switch(firstPortalRelativeLocation) //Where the portal is going to be drawn relative to the object it is being drawn on
			{
				case Rectangle2D.OUT_TOP:
					if(thePhysicsObjectVel.getY() < 0) //If the object attempts to enter the portal the "wrong way"
					{
						thePhysicsObjectVel.set(thePhysicsObjectVel.getX(), 0.0); //Stop their motion
					}
					switch(secondPortalRelativeLocation)
					{
						case Rectangle2D.OUT_TOP:
							thePhysicsObjectVel.set(0.0, -thePhysicsObjectVel.getY());
							if(thePhysicsObjectPos == playerPos)
							{
								thePhysicsObjectPos.set(thePhysicsObjectPos.add(10.0, -40.0));//Has to compensate for gravity
							}
							if(thePhysicsObjectPos == boxPos)
							{
								thePhysicsObjectPos.set(thePhysicsObjectPos.add(10.0, -25.0));//Has to compensate for gravity
							}
							
							if(thePhysicsObjectVel.getY() >= -0.5) //In case the player steps sideways into a floor portal
							{
								thePhysicsObjectVel.set(thePhysicsObjectVel.add(0.0, -2.0));
							}
							break;
						case Rectangle2D.OUT_BOTTOM:
							thePhysicsObjectVel.set(0.0, thePhysicsObjectVel.getY());
							thePhysicsObjectPos.set(thePhysicsObjectPos.add(0.0, 10.0));
							break;
						case Rectangle2D.OUT_LEFT:
							thePhysicsObjectVel.set(-thePhysicsObjectVel.getY(), 0.0);
							thePhysicsObjectPos.set(thePhysicsObjectPos.add(-10.0, 0.0));
							if(thePhysicsObjectVel.getX() == 0.0)
							{
								thePhysicsObjectPos.set(thePhysicsObjectPos.add(-10.0, 0.0));
							}
							break;
						case Rectangle2D.OUT_RIGHT:
							thePhysicsObjectVel.set(thePhysicsObjectVel.getY(), 0.0);
							thePhysicsObjectPos.set(thePhysicsObjectPos.add(10.0, 0.0));
							if(thePhysicsObjectVel.getX() == 0.0)
							{
								thePhysicsObjectPos.set(thePhysicsObjectPos.add(10.0, 0.0));
							}
							break;
						default:
							//System.out.println("OUT_TOP Default");
							break;
					}
					break;
					
				case Rectangle2D.OUT_BOTTOM:
					if(thePhysicsObjectVel.getY() > 0) //If the object attempts to enter the portal the "wrong way"
					{
						thePhysicsObjectVel.set(thePhysicsObjectVel.getX(), 0.0); //Stop their motion
					}
					switch(secondPortalRelativeLocation)
					{
						case Rectangle2D.OUT_TOP:
							thePhysicsObjectVel.set(0.0, thePhysicsObjectVel.getY());
							thePhysicsObjectPos.set(thePhysicsObjectPos.add(10.0, -40.0));
							if(thePhysicsObjectVel.getY() >= 0.0)
							{
								thePhysicsObjectPos.set(thePhysicsObjectPos.add(10.0, -20.0));
							}
							break;
						case Rectangle2D.OUT_BOTTOM:
							thePhysicsObjectVel.set(0.0, -thePhysicsObjectVel.getY());
							thePhysicsObjectPos.set(thePhysicsObjectPos.add(10.0, 10.0));
							break;
						case Rectangle2D.OUT_LEFT:
							thePhysicsObjectVel.set(thePhysicsObjectVel.getY(), 0.0);
							thePhysicsObjectPos.set(thePhysicsObjectPos.add(-10.0, 0.0));
							break;
						case Rectangle2D.OUT_RIGHT:
							thePhysicsObjectVel.set(-thePhysicsObjectVel.getY(), 0.0);
							thePhysicsObjectPos.set(thePhysicsObjectPos.add(10.0, 0.0));
							break;
						default:
							//System.out.println("OUT_BOTTOM Default");
							break;
					}
					break;
					
				case Rectangle2D.OUT_LEFT:
					if(thePhysicsObjectVel.getX() < 0) //If the object attempts to enter the portal the "wrong way"
					{
						thePhysicsObjectVel.set(0.0, thePhysicsObjectVel.getY()); //Stop their motion
					}
					switch(secondPortalRelativeLocation)
					{
						case Rectangle2D.OUT_TOP:
							thePhysicsObjectVel.set(0.0, -thePhysicsObjectVel.getX());
							thePhysicsObjectPos.set(thePhysicsObjectPos.add(10.0, -60.00)); //Takes care of collision issues
							break;
						case Rectangle2D.OUT_BOTTOM:
							thePhysicsObjectVel.set(0.0, thePhysicsObjectVel.getX());
							thePhysicsObjectPos.set(thePhysicsObjectPos.add(10.0, 10.0));
							break;
						case Rectangle2D.OUT_LEFT:
							thePhysicsObjectVel.set(-thePhysicsObjectVel.getX(), 0.0);
							thePhysicsObjectPos.set(thePhysicsObjectPos.add(-10.0, 0.0));
							break;
						case Rectangle2D.OUT_RIGHT:
							thePhysicsObjectVel.set(thePhysicsObjectVel.getX(), 0.0);
							thePhysicsObjectPos.set(thePhysicsObjectPos.add(10.0, 0.0));
							if(thePhysicsObjectVel.getX() <= 0.0)
							{
								thePhysicsObjectPos.set(thePhysicsObjectPos.add(10.0, 0.0));
							}
							break;
						default:
							//System.out.println("OUT_LEFT Default");
							break;
					}
					break;
					
				case Rectangle2D.OUT_RIGHT:
					if(thePhysicsObjectVel.getX() > 0) //If the object attempts to enter the portal the "wrong way"
					{
						thePhysicsObjectVel.set(0.0, thePhysicsObjectVel.getY()); //Stop their motion
					}
					switch(secondPortalRelativeLocation)
					{
						case Rectangle2D.OUT_TOP:
							thePhysicsObjectVel.set(0.0, thePhysicsObjectVel.getX());
							thePhysicsObjectPos.set(thePhysicsObjectPos.add(10.0, -60.0)); //Takes care of collision issues
							break;
						case Rectangle2D.OUT_BOTTOM:
							thePhysicsObjectVel.set(0.0, -thePhysicsObjectVel.getX());
							thePhysicsObjectPos.set(thePhysicsObjectPos.add(10.0, 10.0));
							break;
						case Rectangle2D.OUT_LEFT:
							thePhysicsObjectVel.set(thePhysicsObjectVel.getX(), 0.0);
							thePhysicsObjectPos.set(thePhysicsObjectPos.add(-10.0, 0.0));
							if(thePhysicsObjectVel.getX() >= 0.0)
							{
								thePhysicsObjectPos.set(thePhysicsObjectPos.add(-10.0, 0.0));
							}
							break;
						case Rectangle2D.OUT_RIGHT:
							thePhysicsObjectVel.set(-thePhysicsObjectVel.getX(), 0.0);
							thePhysicsObjectPos.set(thePhysicsObjectPos.add(10.0, 0.0));
							break;
						default:
							//System.out.println("OUT_RIGHT Default");
							break;
					}
					break;
					
				default:
					//System.out.println("portal1RelativeLocation Default");
					break;
			}
		}
		
		private void addFriction(Vec2 thePhysicsObjectVel, Vec2 thePhysicsObjectAcc) //Applies friction (if on the ground)
		{
			if((thePhysicsObjectVel == playerVel) && (aPressed || dPressed))
			{
				playerMovementIssue = true;
			}
			else
			{
				playerMovementIssue = false;
			}
			
			if(onGround && !playerMovementIssue) //If on the ground, and not trying to move left, and not trying to move right
			{
				if(thePhysicsObjectVel.getX() > 0.0) //If the player is moving to the right
				{
					if(thePhysicsObjectVel.getX() < 1.0) //If they are moving very slowly
					{
						//Stop them from moving
						thePhysicsObjectVel.set(0.0, thePhysicsObjectVel.getY());
						thePhysicsObjectAcc.set(0.0, thePhysicsObjectAcc.getY());
					}
					else
					{
						thePhysicsObjectAcc.set(thePhysicsObjectAcc.add(-0.10, 0.0));
					}
				}
				
				if(thePhysicsObjectVel.getX() < 0.0) //If the player is moving to the left
				{
					if(thePhysicsObjectVel.getX() > -1.0) //If they are moving very slowly
					{
						//Stop them from moving
						thePhysicsObjectVel.set(0.0, thePhysicsObjectVel.getY());
						thePhysicsObjectAcc.set(0.0, thePhysicsObjectAcc.getY());
					}
					else
					{
						thePhysicsObjectAcc.set(thePhysicsObjectAcc.add(0.10, 0.0));
					}
				}
			}
			else if(!onGround || dPressed || aPressed) //If in the air, or trying to move right, or trying to move left
			{
				thePhysicsObjectAcc.set(0.0, thePhysicsObjectAcc.getY()); //Get rid of any friction acceleration
			}
		}
		
		private void sumAccVelPos(Vec2 thePhysicsObjectPos, Vec2 thePhysicsObjectVel, Vec2 thePhysicsObjectAcc)
		{
			//Calculate the new velocity/position for the player
			thePhysicsObjectVel.set(thePhysicsObjectVel.add(thePhysicsObjectAcc));
			thePhysicsObjectPos.set(thePhysicsObjectPos.add(thePhysicsObjectVel));
				
			playerX = playerPos.getX();
			playerY = playerPos.getY();
			
			boxX = boxPos.getX();
			boxY = boxPos.getY();
			
			if(playerVel.getY() >= 0.0)
			{
				jumping = false;
			}
		}
		
		private void setNewPosition() //Move the player to the new location
		{
			player.setRect(playerX, playerY, 20, 40);
			box.setRect(boxX, boxY, 25, 25);
		}
		
		private void setDrawnRay()
		{
			drawnRay.set((mouseX - player.getCenterX()), (mouseY - player.getCenterY()));
			drawnRay = drawnRay.normalize();
			drawnRay = drawnRay.multiply(1200);
			drawnRayline.setLine(player.getCenterX(), player.getCenterY(), drawnRay.getX() + player.getCenterX(), drawnRay.getY() + player.getCenterY());
		}
		
		private void doNothing()
		{
			animatorDeltaTime = (animatorFinalTime - animatorInitialTime);
			animatorSleepTime = (ANIMATOR_SLEEP_TIME - animatorDeltaTime);
			
			if(animatorSleepTime < 0) //If the animations took longer than ANIMATOR_SLEEP_TIME to run
			{
				animatorSleepTime = 0;
			}
			
			try
			{
				Thread.sleep(animatorSleepTime);
			}
			catch(InterruptedException e)
			{
				System.exit(0);
			}
		}
	}
	
	private class Painter extends Thread
	{
		public void run()
		{
			while(true)
			{
				painterInitialTime = System.currentTimeMillis();
				repaint();
				painterFinalTime = System.currentTimeMillis();
				painterDeltaTime = (painterFinalTime - painterInitialTime);
				painterSleepTime = (PAINTER_SLEEP_TIME - painterDeltaTime);
				
				if(painterSleepTime < 0)
				{
					painterSleepTime = 0;
				}
				
				try
				{
					Thread.sleep(painterSleepTime); //Approx. 1/60th of a second
				}
				catch(InterruptedException e)
				{
					System.exit(0);
				}	
			}
		}
	}
	
	private class KeyboardHandler extends KeyAdapter
	{
		public void keyPressed(KeyEvent e)
		{
			int theKey = e.getKeyCode();
			double boxDistance;
			
			if((theKey == KeyEvent.VK_A) && (!aPressed) && (!playerJustPortaled)) //!justPortaled prevents the user from going backwards, potentially through the level
			{
				aPressed = true;
				if(playerVel.getX() > -3.0) //If not moving to the left at walking velocity
				{
					playerVel.set(playerVel.add(-3.0 - playerVel.getX(), 0.0));
					heldBoxX = -20.0;
					playerFacingRight = false;
				}
				
			}
			if((theKey == KeyEvent.VK_D) && (!dPressed) && (!playerJustPortaled))
			{
				dPressed = true;
				if(playerVel.getX() < 3.0) //If not moving to the right at walking velocity
				{
					playerVel.set(playerVel.add(3.0 - playerVel.getX(), 0.0));
					heldBoxX = 20.0;
					playerFacingRight = true;
				}
			}
			if((theKey == KeyEvent.VK_S) && (!playerJustPortaled))
			{
				playerVel.set(0.0, playerVel.getY());
			}
			if(theKey == KeyEvent.VK_SPACE)
			{
				if(playerOnGround  && (playerVel.getY() > -2))
				{
					playerVel.set(playerVel.add(0.0, -6.0)); //-6.0
					jumping = true;
				}
			}
			if(theKey == KeyEvent.VK_E)
			{
				boxDistance = Math.sqrt(Math.pow(player.getCenterX() - box.getCenterX(), 2) + Math.pow(player.getCenterY() - box.getCenterY(), 2));
				if(boxDistance < 30)
				{
					if(boxToggle)
					{
						if(boxHeld)
						{
							boxHeld = false;
							boxToggle = false;
						}
						else
						{
							boxHeld = true;
							boxToggle = false;
						}
					}
				}
			}
		}
		
		public void keyReleased(KeyEvent e)
		{
			int theKey = e.getKeyCode();
			
			if(theKey == KeyEvent.VK_A)
			{
				aPressed = false;
			}
			if(theKey == KeyEvent.VK_D)
			{
				dPressed = false;
			}
			if(theKey == KeyEvent.VK_E)
			{
				boxToggle = true;
			}
		}
	}
	
	private class MouseInput extends MouseInputAdapter
	{
		public void mouseMoved(MouseEvent e)
		{
			mouseX = e.getX();
			mouseY = e.getY();
		}
		
		public void mouseDragged(MouseEvent e)
		{
			mouseX = e.getX();
			mouseY = e.getY();
		}
		
		public void mousePressed(MouseEvent e)
		{
			int surfaceNumber = 0;
			double distanceBetweenPortals;
			
			ray.set((e.getX() - player.getCenterX()), (e.getY() - player.getCenterY()));
			ray = ray.normalize();
			
			for(int i = 0; i < 1200; i++)
			{
				testRay = ray.multiply(i); //Each run of the for loop, the testRay gets 1 pixel longer
				rayline.setLine(player.getCenterX(), player.getCenterY(), testRay.getX() + player.getCenterX(), testRay.getY() + player.getCenterY()); //Draw a line from the player to the end of the testRay
				
				for(surfaceNumber = 0; surfaceNumber < allCollidableSurfaces.size(); surfaceNumber++) //For each normal surface
				{
					if(rayline.intersects((Rectangle2D)allCollidableSurfaces.get(surfaceNumber))) //If the line intersects a surface
					{
						//Break out of the loops
						raylineIntersection = true;
						testRay = ray.multiply(i - 1); //Get the ray just before the intersection
						testPoint.setLocation(testRay.getX() + player.getCenterX(), testRay.getY() + player.getCenterY()); //Get the point just before the intersection (used to find the relative portal location)
						break;
					}	
				}
				
				if(raylineIntersection) //Break out of the loop if an intersection is made
				{
					break;
				}
			}
			
			if(portalableSurfaces.contains((Rectangle2D)allCollidableSurfaces.get(surfaceNumber))) //If the first surface hit is portalable
			{
				portalRelativeLocation = ((Rectangle2D)allCollidableSurfaces.get(surfaceNumber)).outcode(testPoint);
			
				if((e.getButton() == 1) && raylineIntersection)
				{
					lastShotBluePortal = true;
				}
				else if((e.getButton() == 3) && raylineIntersection)
				{
					lastShotBluePortal = false;
				}
				
				if(raylineIntersection && lastShotBluePortal) //Prevents from portals being too close to eachother (70 pixels)
				{
					distanceBetweenPortals = Math.sqrt(Math.pow(testPoint.getX() - portal2.getCenterX(), 2) + Math.pow(testPoint.getY() - portal2.getCenterY(), 2));
					
					if(distanceBetweenPortals < 50)
					{
						portalCollisionIssue = true;
					}
					else
					{
						portalCollisionIssue = false;
					}
				}
				else if(raylineIntersection && !lastShotBluePortal)
				{
					distanceBetweenPortals = Math.sqrt(Math.pow(testPoint.getX() - portal1.getCenterX(), 2) + Math.pow(testPoint.getY() - portal1.getCenterY(), 2));
					
					if(distanceBetweenPortals < 70)
					{
						portalCollisionIssue = true;
					}
					else
					{
						portalCollisionIssue = false;
					}
				}
				
				if(!portalCollisionIssue)
				{
					findPortalRelativeLocations();
				}
				
				if(lastShotBluePortal && raylineIntersection && !portalCollisionIssue) //Shooting the blue portal
				{
					portal1.setFrame(rayline.getX2() - (portalWidth / 2), rayline.getY2() - (portalHeight / 2), portalWidth, portalHeight);
					raylineColorIsBlue = true;
					portal1Active = true;
				}
				else if(!lastShotBluePortal && raylineIntersection && !portalCollisionIssue) //Shooting the orange portal
				{
					portal2.setFrame(rayline.getX2() - (portalWidth / 2), rayline.getY2() - (portalHeight / 2), portalWidth, portalHeight);
					raylineColorIsBlue = false;
					portal2Active = true;
				}
				
				if(portal1Active && portal2Active)
				{
					portalsActive = true;
				}
			}
			
			raylineIntersection = false;
		}
		
		private void findPortalRelativeLocations()
		{
			switch(portalRelativeLocation) //Where the portal is going to be drawn relative to the object it is being drawn on
			{
				case Rectangle2D.OUT_TOP:
					portalWidth = 40.0;
					portalHeight = 20.0;
					if(lastShotBluePortal && raylineIntersection)
					{
						portal1RelativeLocation = Rectangle2D.OUT_TOP;
					}
					else if(!lastShotBluePortal && raylineIntersection)
					{
						portal2RelativeLocation = Rectangle2D.OUT_TOP;
					}
					break;
				case Rectangle2D.OUT_BOTTOM:
					portalWidth = 40.0;
					portalHeight = 20.0;
					if(lastShotBluePortal)
					{
						portal1RelativeLocation = Rectangle2D.OUT_BOTTOM;
					}
					else if(!lastShotBluePortal && raylineIntersection)
					{
						portal2RelativeLocation = Rectangle2D.OUT_BOTTOM;
					}
					break;
				case Rectangle2D.OUT_LEFT:
					portalWidth = 20.0;
					portalHeight = 40.0;
					if(lastShotBluePortal)
					{
						portal1RelativeLocation = Rectangle2D.OUT_LEFT;
					}
					else if(!lastShotBluePortal && raylineIntersection)
					{
						portal2RelativeLocation = Rectangle2D.OUT_LEFT;
					}
					break;
				case Rectangle2D.OUT_RIGHT:
					portalWidth = 20.0;
					portalHeight = 40.0;
					if(lastShotBluePortal)
					{
						portal1RelativeLocation = Rectangle2D.OUT_RIGHT;
					}
					else if(!lastShotBluePortal && raylineIntersection)
					{
						portal2RelativeLocation = Rectangle2D.OUT_RIGHT;
					}
					break;
				default:
					//System.out.println("portalRelativeLocation Default");
					break;
			}
		}
	}
}