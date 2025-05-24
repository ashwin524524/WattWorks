/*Ashwin R.
 * P5
 * 5-7-25
 * 
 * GamePanel.java
 * 
 * This file is where the user plays the game
*/

import javax.swing.JPanel;	//imports
import javax.swing.JSlider;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSlider;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

import java.awt.Graphics;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Time;
import java.util.Scanner;

import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;

import javax.swing.Timer;

import javax.imageio.ImageIO;

import java.util.Scanner;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

class GamePanel extends JPanel	//panel where user plays the game
{
	private int x;	//where the mouseDragged posistions are stored
	private int y;
	
	private int destX;	//where the user needs to drag the component to
	private int destY;
	
	private String typeSelected = "";	//stores the component and value chosen
	private String singleSelected = "";
	
	private String componentTypeNeeded = "";	//what the user needs to choose
	private String componentValueNeeded = "";
	
	private ComponentPanel cpp;	//3 of the 4 panels that are added to the main borderlayout
	private GamePanel gp;
	private PuzzlePanel pzp;
	
	private int clockMinutes;	//handles the main puzzle timer
	private int clockSeconds;
	private Timer clock;
	
	private JLabel title;

	private boolean solved;		//booleans that help with drawing and freezing puzzle when needed.
	private boolean freezePuzzle;
	private boolean answerSolved;
	private boolean puzzleAdded;
	
	private String puzzleSelected;
	
	private Scanner reader;	//scans the main puzzle file

	private String[] information;
	
	private User user;
	private WattWorksHolder wwh;
		
	public GamePanel(String puzzleSelectedIn, User userIn, WattWorksHolder wwhIn)
	{
		puzzleSelected = puzzleSelectedIn;
		user = userIn;
		wwh = wwhIn;
		
		setLayout(new BorderLayout(10,10));		//format panel
		setBorder( new EmptyBorder(10,10,10,10));
		
		gp = this;
		readFile(puzzleSelected, "puzzles/puzzleInformation");	//read the main file
		parseMainPuzzleFile();
		
		ToolPanel tp = new ToolPanel();	//create and add the 4 panels to border layout
		QuestionPanel qp = new QuestionPanel();
		cpp = new ComponentPanel();
		pzp = new PuzzlePanel();
		
		title = new JLabel(puzzleSelected);	//create Title 
		title.setFont(new Font("serif", Font.BOLD, 30));
		title.setHorizontalAlignment(SwingConstants.CENTER);
		add(title, BorderLayout.NORTH);
		
		TimeHandler th = new TimeHandler();	//create timer and set delay = 1s
		
		clock = new Timer(1000, th);
		clock.start();	//start timer when page is created
		
		add(tp, BorderLayout.WEST);	//add the panels
		add(qp, BorderLayout.SOUTH);
		add(cpp, BorderLayout.EAST);
		add(pzp, BorderLayout.CENTER);
	}
	
	public void paintComponent(Graphics g)	//updates the timer through the JLabel
	{
		super.paintComponent(g);
		title.setText("");
		title.setText(String.format("%s  %02d:%02d",puzzleSelected, clockMinutes,clockSeconds));

		if(solved && !freezePuzzle)
		{
			title.setForeground(new Color(0,255,0));
			clock.stop();
			freezePuzzle = true;
			
		}
		if(answerSolved && !puzzleAdded)
		{
			user.addPuzzle(puzzleSelected);	//adds the puzzle to the users file
			puzzleAdded = true;
		}

	}
	
	class TimeHandler implements ActionListener		//actionListener for timer
	{
		public void actionPerformed(ActionEvent evt)
		{
			clockSeconds++;
			if(clockSeconds==60)
			{
				clockSeconds = 0;
				clockMinutes++;
			}
			repaint();
		}
	}
	
	
	class ToolPanel extends JPanel	//where the instruction book button is
	{
		private Image multimeter;
		private Image oscilloscope;
		
		public ToolPanel()
		{
			setLayout(new BorderLayout());
			
			multimeter = getImage("multimeter.jpg", "tools");
			oscilloscope = getImage("oscilloscope.jpg", "tools");
			
			JLabel label = new JLabel("Tool Panel");
			label.setFont(new Font("Serif", Font.PLAIN, 18));
			add(label, BorderLayout.NORTH);
			
			
			JButton instructions = new JButton("go to instruction book");	//create button
			InstructionHandler ih = new InstructionHandler();
			instructions.addActionListener(ih);
			
			add(instructions, BorderLayout.SOUTH);
			
		}
		
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			g.drawImage(multimeter, 0,20,100,100,this);	//draw multimeter and oscilloscope
			g.drawImage(oscilloscope, 0,160,100,100,this);
		}
		
		class InstructionHandler implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				wwh.goToInstructions();	//when user presses button, change page to the instruction book
			}
		}
		
	}
	
	class QuestionPanel extends JPanel	//where the user is prompted with a question and answers it
	{
		
		private JTextField answers;
		public QuestionPanel()
		{
			setLayout(new GridLayout(1,2));	//format panel
			setBorder( new EmptyBorder(10,10,10,10));
			
			JTextArea questions = new JTextArea(information[3],10,5);	//create and format the question text box.
			questions.setFont(new Font("Serif", Font.PLAIN, 16));
			questions.setWrapStyleWord(true);
			questions.setLineWrap(true);
			questions.setEditable(false);
			questions.setMargin(new Insets(10, 20, 10, 20));
			add(questions);

			answers = new JTextField("Put answer here");	//set up answers field
			TextHandler th = new TextHandler();
			
			answers.setFont(new Font("Serif", Font.PLAIN, 16));
			answers.addActionListener(th);
			add(answers);
			
			
			for(int i = 0; i < information.length; i++)
			{
				System.out.println(information[i]);
				
			}

		}
		
		class TextHandler implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				String command = evt.getActionCommand();
				
				if(command.equalsIgnoreCase(information[4].trim()))	//checks whether answer is correct or wrong, proceeds accordingly
				{
					answerSolved = true;
					answers.setForeground(Color.GREEN);
					answers.setText("correct!");
				}
				else
				{
					answers.setForeground(Color.RED);
					answers.setText("Wrong, please try again");
				}
				
					
			}
		}
		
	}
	
	class ComponentPanel extends JPanel implements ActionListener //where the user can select components that they need.
	{

		private SelectorPanel sp = null;
		private JRadioButton[] buttons = null;

		public ComponentPanel()
		{
			setLayout(new BorderLayout());	//format panel
			setBorder( new EmptyBorder(5,5,5,5));
			
			add(makeComponentMenu(), BorderLayout.NORTH);	//make the component menu 

			buttons = new JRadioButton[8];
			sp = new SelectorPanel();

			sp.setVisible(false);
			add(sp, BorderLayout.CENTER);
		}
		
		

		public JMenuBar makeComponentMenu()	//where user can select from resistor, diode, etc.
		{
			JMenuBar bar = new JMenuBar();
			bar.setPreferredSize(new Dimension(200, 100));
			JMenu select = new JMenu("select component");
		
			JMenuItem resistor = new JMenuItem("resistors");
			JMenuItem capacitor = new JMenuItem("capacitors");
			JMenuItem misc = new JMenuItem("misc.");
			JMenuItem inductor = new JMenuItem("inductors");
			
			resistor.addActionListener(this);
			capacitor.addActionListener(this);	
			misc.addActionListener(this);
			inductor.addActionListener(this);

			
			select.add( resistor );
			select.add( capacitor );
			select.add( misc );
			select.add( inductor );

			bar.add(select);
		
			return bar;
		}

		

		public void actionPerformed( ActionEvent evt ) //when the user selects a component type
		{
			String command = evt.getActionCommand();
			
			typeSelected = command; 
			sp.updatePanel(command);	//update the selector panel, which contains the radio buttons, setting their text accordingly
			//this.add(sp);	//
			sp.setVisible(true); 
		}


		
		class SelectorPanel extends JPanel implements ActionListener
		{
			public SelectorPanel()
			{
					
				ButtonGroup bg = new ButtonGroup();
				
				setLayout(new GridLayout(1,2));
				
				JPanel holder = new JPanel();
				holder.setLayout(new GridLayout(8,1));
				
				ImagePanel ip = new ImagePanel();
				
				add(ip);
				add(holder);
				
				for(int i = 0; i < 8; i++)	//use for loop to set up radio buttons, turn them invisible for now.
				{
					JRadioButton radio = new JRadioButton("");
					radio.addActionListener(this);
					radio.setPreferredSize(new Dimension(100,150));
					bg.add(radio);
					buttons[i] = radio;
					holder.add(radio);
					
				}
			}

			public void updatePanel(String commandIn)	//gets in what was selected from menu and updates radio buttons
			{
				String[] names = null;

				if(commandIn.equals("resistors"))
					names = new String[]{"1k", "5k", "10k","20k","100k","200k","500k"};
				else if (commandIn.equals("capacitors"))
					names = new String[]{"10pf", "100pf", "1nf", "100nf", "10f"};
				else if (commandIn.equals("misc."))
					names = new String[]{"diode", "transistor"};
				else if (commandIn.equals("inductors"))
					names = new String[]{"1mH", "10mH", "1nH", "10nH", "1H"};

		
				for(int i = 0; i<8; i++)
				{
					buttons[i].setVisible(false);	//set all invisible
				}
				
				for(int j = 0; j < names.length; j++)
				{
					buttons[j].setText(names[j]);	//set the name
					buttons[j].setVisible(true);	//turn those with names visible
				}
			}
			
			public void actionPerformed(ActionEvent evt)	//when the user selects a specific value, set the variable.
			{
				String command = evt.getActionCommand();
				
				singleSelected = command;

				pzp.repaint();
				
			}
			
			
			class ImagePanel extends JPanel	//paints the images for the selector panel
			{
				public ImagePanel()
				{
					
				}
				
				public void paintComponent(Graphics g)
				{
					if(typeSelected.equals("resistors"))
						for(int i = 0; i < 7; i++)
						{	
							g.drawImage(getImage("resistor.jpg", "puzzles/components"),10,10+50*i,80,24, this);
						}
					else if(typeSelected.equals("capacitors"))
						for(int i = 0; i < 5; i++)
						{	
							g.drawImage(getImage("capacitor.png", "puzzles/components"),10,10+60*i,this);
						}
					else if(typeSelected.equals("misc."))
					{
						g.drawImage(getImage("diode.png", "puzzles/components"),10,10,100,30,this);
						g.drawImage(getImage("transistor.png", "puzzles/components"),10,70,100,30,this);
					}
					else if(typeSelected.equals("inductors"))
						for(int i = 0; i < 5; i++)
						{	
							g.drawImage(getImage("inductor.png", "puzzles/components"),10,10+60*i,80,40,this);
						}
				}
			}
		}

	}
	class PuzzlePanel extends JPanel implements MouseMotionListener	//the center panel, where user solves puzzle
	{
		private JLabel objective; 
		private Scanner reader2;
		private String[] toBeDrawn;
		private boolean currentAnimation;
		private Timer timer;

		public PuzzlePanel()
		{
			setLayout(new BorderLayout());

			objective = new JLabel();		//set objective label and add to south
			objective.setHorizontalAlignment(SwingConstants.CENTER);
			objective.setFont(new Font("Serif", Font.BOLD, 16));
			add(objective, BorderLayout.SOUTH);

			readFile("draw"+puzzleSelected, "puzzles/puzzleImages"); //read the draw File, which contains info about drawing the puzzle needed
			int numLines = getNumLines();	//get the number of instructions in the file.

			readFile("draw"+puzzleSelected, "puzzles/puzzleImages"); //reread file
			parseFile(numLines);	//parse the instructions and add them to the toBeDrawn array
			
			addMouseMotionListener(this);

			TimeHandler2 th2 = new TimeHandler2();	//timer that deals with animation of arrows.
			timer = new Timer(500, th2);
			timer.start();
			currentAnimation = false;
			
		}

		class TimeHandler2 implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				if(answerSolved)	//stop the animation if user has completed everything
					timer.stop();
				currentAnimation = !currentAnimation;
				pzp.repaint();
			}
		}

		public void paintComponent(Graphics g)	//where the puzzle image is drawn
		{
			super.paintComponent(g);
			
			objective.setText("Objective: " +  information[2]);	//updates the objective

			for(int i = 0; i<toBeDrawn.length; i++)	//cycles through the drawing instructions
			{
				String[] commands = toBeDrawn[i].split(","); //split each line into values using String.split()

				if(commands[0].equals("DW"))	//draw a wire
				{
					g.fillRect(Integer.parseInt(commands[1].trim()),Integer.parseInt(commands[2].trim()),Integer.parseInt(commands[3].trim()),Integer.parseInt(commands[4].trim()));	//draws the wire

					if(solved)	//if puzzle has been solved, start the arrow animation
					{
						int offset = 0;
						if(currentAnimation)	//flips the offset to make arrows move.
							offset = 12;
						else
							offset = 0;
						
						
						if(commands[5].trim().equals("right"))		//these if loops paint the arrows on top of the given wires in the given direction. The values come from the draw file
						{

							for(int arrowX = Integer.parseInt(commands[1].trim()); arrowX < Integer.parseInt(commands[3].trim())+Integer.parseInt(commands[1].trim()); arrowX+=50)
							{

								g.drawImage(getImage("rightArrow.png", "puzzles/components"), arrowX+offset, Integer.parseInt(commands[2].trim())-12, this);
							}
						}

						if(commands[5].trim().equals("left"))
						{

							for(int arrowX = Integer.parseInt(commands[1].trim()); arrowX < Integer.parseInt(commands[3].trim())+Integer.parseInt(commands[1].trim()); arrowX+=50)
							{

								g.drawImage(getImage("leftArrow.png", "puzzles/components"), arrowX+offset, Integer.parseInt(commands[2].trim())-12, this);
							}
						}

						
						if(commands[5].trim().equals("up"))
						{

							for(int arrowY = Integer.parseInt(commands[2].trim()); arrowY < Integer.parseInt(commands[4].trim())+Integer.parseInt(commands[2].trim()); arrowY+=50)
							{

								g.drawImage(getImage("upArrow.png", "puzzles/components"), Integer.parseInt(commands[1].trim())-12, arrowY+offset, this);
							}
						}

						if(commands[5].trim().equals("down"))
						{

							for(int arrowY = Integer.parseInt(commands[2].trim()); arrowY < Integer.parseInt(commands[4].trim())+Integer.parseInt(commands[2].trim()); arrowY+=50)
							{

								g.drawImage(getImage("downArrow.png", "puzzles/components"), Integer.parseInt(commands[1].trim())-12, arrowY+offset, this);
							}
						}

					}
				}
				else if(commands[0].trim().equals("TXT"))
				{
					g.drawString(commands[1], Integer.parseInt(commands[2].trim()),  Integer.parseInt(commands[3].trim()));
				}
				else
				{
					if(commands.length == 5)
						g.drawImage(getImage(commands[0], "puzzles/components"),Integer.parseInt(commands[1].trim()),Integer.parseInt(commands[2].trim()),Integer.parseInt(commands[3].trim()),Integer.parseInt(commands[4].trim()), this);
					else if(commands.length == 3)
						g.drawImage(getImage(commands[0], "puzzles/components"),Integer.parseInt(commands[1].trim()),Integer.parseInt(commands[2].trim()),this);
				}
					
			}
			
			
			if(typeSelected.equals("resistors"))	//draws the component that is dragged and dropped
				g.drawImage(getImage("resistor.jpg", "puzzles/components"),x,y,80,24,this);
				
			else if(typeSelected.equals("capacitors"))
				g.drawImage(getImage("capacitor.png", "puzzles/components"),x,y,this);
	
			else if(typeSelected.equals("misc.")&&singleSelected.equals("diode"))
	
				g.drawImage(getImage("diode.png", "puzzles/components"),x,y,100,30,this);

			else if(typeSelected.equals("misc.")&&singleSelected.equals("transistor"))
	
				g.drawImage(getImage("transistor.png", "puzzles/components"),x,y,100,30,this);

			else if(typeSelected.equals("inductors"))

				g.drawImage(getImage("inductor.png", "puzzles/components"),x,y,80,40,this);
		}

		public void mouseDragged(MouseEvent evt)	//gets the mouse position when dragged.
		{
			if(!solved)
			{
			x = evt.getX();
			y = evt.getY();
			}
		

			if((destX-20 < x && destX+20 >x) && (destY-20 <y && destY+20 >y) && (singleSelected.equals(componentValueNeeded)) && (typeSelected.equals(componentTypeNeeded)))
			{
				solved = true;
				gp.repaint();
			}
			repaint();
		}
		
		public void mouseMoved(MouseEvent evt){}

		public void readFile(String fileNameIn, String folderIn)	
		{

			System.out.println("reading the draw file");
			reader2 = null;

			String fileName = folderIn + "/" + fileNameIn + ".txt";
			File readFile = new File(fileName);
			
			try
			{
				reader2 = new Scanner(readFile);
			}
			catch(FileNotFoundException e)
			{
				System.err.println("Couldn't find " + fileName + " file.");
				System.exit(1);
			}


		}

		public int getNumLines()
		{
			int lines = 0;

			while(reader2.hasNextLine())
			{
				reader2.nextLine();
				lines++;

			}
			System.out.println("numLines: " + lines);
			return lines;
		}

		public void parseFile(int numLines)
		{
			toBeDrawn = new String[numLines];

			for(int i = 0; i < toBeDrawn.length; i++)
			{
				toBeDrawn[i] = reader2.nextLine();
				System.out.print("parsing lines: " + toBeDrawn[i]);

			}
		}
	}
	
	public Image getImage(String imgNameIn, String folderIn)
    {
		Image picture = null;
		String imgName = folderIn + "/" + imgNameIn;
		 
        try
        {
            picture = ImageIO.read(new File(imgName));

		}
		
        catch(IOException e)
        {
            System.err.println("Could not find image at " + imgName);
        }
        return picture;
    }
    
    public void readFile(String fileNameIn, String folderIn)
	{
		reader = null;

		String fileName = folderIn + "/" + fileNameIn + ".txt";
		File readFile = new File(fileName);
		
		try
		{
			reader = new Scanner(readFile);
		}
		catch(FileNotFoundException e)
		{
			System.err.println("Couldn't find " + fileName + " file.");
			System.exit(1);
		}


	}
	public void parseMainPuzzleFile()
		{
			information = new String[6];
			String line = "";
			String word = "";
			
			for(int i = 0; i < information.length; i++)
				information[i] = "";
			
			do
			{
				if(reader.hasNext())
					word = reader.next();
			}while(!(word.equals("Puzzle:")));
			
			
			System.out.println(word);
			word = reader.next();
			
			do
			{
				if(reader.hasNext())
				{
					information[0] = information[0] + word + " ";
					word = reader.next();
				}
			}while(!(word.equals("Image:")));
			
			word = reader.next();
			
			do
			{
				if(reader.hasNext())
				{
					information[1] = information[1] + word + " ";
					word = reader.next();
				}
			}while(!(word.equals("Objective:")));
			
			word = reader.next();
			
			do
			{
				if(reader.hasNext())
				{
					information[2] = information[2] + word + " ";
					word = reader.next();
				}
			}while(!(word.equals("Question:")));
			
			word = reader.next();
			
			do
			{
				if(reader.hasNext())
				{
					information[3] = information[3]  + word + " ";
					word = reader.next();
				}
			}while(!(word.equals("Answer:")));
			
			word = reader.next();
			
			do
			{
				if(reader.hasNext())
				{
					information[4] = information[4]  + word + " ";
					word = reader.next();
				}
			}while(!(word.equals("DragLocation:")));
			

			destX = reader.nextInt();
			destY = reader.nextInt();
			System.out.println("dest" + destX + " " + destY);
			
			do
			{
				if(reader.hasNext())
				{
					word = reader.next();	
				}
			}while(!(word.equals("Component:")));
			
			do
			{
				if(reader.hasNext())
				{
					word = reader.next();
					componentTypeNeeded = word;
					
					word = reader.next();
					componentValueNeeded = word;	
				}
			}while(reader.hasNext());
			
			for(int i = 0; i < information.length; i++)
				information[i].trim();
				
			System.out.println("dest" + destX + " " + destY);
			System.out.println("type" + componentTypeNeeded + "  " + componentValueNeeded);

		}
}		
	
