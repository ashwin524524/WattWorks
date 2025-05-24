/*Ashwin R.
 * P5
 * 4-23-25
 * 
 * Puzzles.java
 * 
 * This file displays and generates the puzzles
*/

import javax.swing.JPanel;
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
import java.awt.Label;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;

import java.util.Scanner;

import javax.swing.Timer;

import javax.imageio.ImageIO;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

class Puzzles extends JPanel
{
    private JSlider level;
    private int difficulty;
    private int puzzleSelected;
    private JMenuBar puzzles;
    private Puzzles self;
    
    private WattWorksHolder wwh;
    private CardLayout cards;
    
    private Scanner reader;
    
    private JMenuItem[] menuArray;
    
    private User user;
    
    private boolean locked;
    private Timer lockTimer;

    public Puzzles(WattWorksHolder wwhIn, CardLayout cardsIn, User userIn)
    {
		wwh = wwhIn;
		cards = cardsIn;
		user = userIn;
		
		readFile();
		
		setBorder(new EmptyBorder(50,50,50,50));
		
		self = this;
		
		LockHandler lh = new LockHandler();
		lockTimer = new Timer(1000, lh);
		
		SliderPanel sp = new SliderPanel();
		
		add(sp);
		 
	    puzzles = makePuzzleMenu();
	    puzzles.setVisible(false);
	    add(puzzles);

    }
    
    class LockHandler implements ActionListener
    {
		public void actionPerformed(ActionEvent evt)
		{
			locked = false;
			lockTimer.stop();
		}
	}
    
    public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			
			System.out.println(locked);
			if(locked)
				g.drawImage(getImage("lockIcon.png", "icons"), 350, 250, this);		
		}
	
	class SliderPanel extends JPanel
	{
		
		
		public SliderPanel()
		{
			JLabel label = new JLabel("first select difficulty level");
			add(label);
			
			level = new JSlider(JSlider.HORIZONTAL,1,5,1);
			level.setMajorTickSpacing(1);	// create tick marks on slider every 5 units
			level.setPaintTicks(true);
			level.setLabelTable(level.createStandardLabels(1)); // create labels on tick marks
			level.setPaintLabels(true);
			level.setSnapToTicks(true);
			
			SliderHandler handler = new SliderHandler();
			level.addChangeListener(handler);
		
			add(level);
			repaint();
		}
		
		
		
		class SliderHandler implements ChangeListener
		{
			public void stateChanged(ChangeEvent evt)
			{
				difficulty = level.getValue();
				
				if(difficulty > 2)
				{
					lockTimer.start();
					locked = true;
					level.setValue(2);
				}
				
					
				System.out.println(difficulty + "2");
				updateMenu();
				puzzles.setVisible(true);
				repaint();
				self.repaint();
			}	
		}
	}

    public JMenuBar makePuzzleMenu()
    {
        JMenuBar bar = new JMenuBar();
        bar.setPreferredSize(new Dimension(200, 100));
        JMenu puzzle = new JMenu("then select puzzle");
        
        menuArray = new JMenuItem[10];
        
        PuzzleMenuHandler pmh = new PuzzleMenuHandler();
        
        String[] names = getPuzzleNames(1);
		int size = Integer.parseInt(names[19]);
        
        for(int i = 0; i < 7; i++)
        {
			JMenuItem item = new JMenuItem(""+i);
			item.addActionListener(pmh);
			item.setVisible(false);
			puzzle.add(item);
			menuArray[i] = item;
		}
		bar.add(puzzle);
        return bar;
    }
    
    public void updateMenu()
    {
		String[] names = getPuzzleNames(difficulty);
		int size = Integer.parseInt(names[19]);

		for(int i = 0; i < 7; i++)
		{
			menuArray[i].setVisible(false);
		}
		
		for(int j = 0; j < size; j++)
		{
			menuArray[j].setText(names[j]);
			menuArray[j].setVisible(true);
		}
	}
	
	public String[] getPuzzleNames(int levelIn)
	{
		String[] names = new String[20];
		int size  = 0;
		int level = levelIn;
		
		String word = null;
		String line = null;
		
		do
		{
			if(reader.hasNext())
				word = reader.next();

		}while(!(word.equals("level"  + level)));
		
		line = reader.nextLine();
		
		do
		{
			if(!line.isBlank())
			{
				names[size] = line;
				size++;
			}

			line = reader.nextLine();
		}while(!(line.equals("level" + (level+1))));
		
		readFile();
		names[19] = ""+size;
		
		return names;

	}
	public void readFile()
	{
		readFile("puzzlesList", "puzzles");
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


    class PuzzleMenuHandler implements ActionListener 
    {
        public void actionPerformed( ActionEvent evt ) 
        {
            String command = evt.getActionCommand();
            
            GamePanel gp = new GamePanel(command, user, wwh);
            wwh.add(gp, "gamePanel");  
			cards.show(wwh, "gamePanel");
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
            System.exit(2);
        }
        return picture;
    }
}



	



