/*Ashwin R.
 * P5
 * 4-23-25
 * 
 * WattWorks.java
 * 
 * This file contains the JFrame and the holder panels for the Watt Works game
*/

//imports 

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Label;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.CardLayout;

import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;

import javax.swing.JFrame;	
import javax.swing.JPanel;

import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ButtonGroup;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class WattWorks //main class, contains JFrame
{
	public WattWorks()
	{
	}
	
	public static void main(String[] args)
	{
		WattWorks ww = new WattWorks();
		ww.makeFrame();
	}
	
	public void makeFrame()		//creates JFrame (1000x800), and adds FixedHolder Panel
	{
		System.out.print("stats");
		JFrame frame = new JFrame("Watt Works");
		frame.setSize(1000, 800);				
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE); 
		frame.setLocation(50,20);
		frame.setResizable(false);
		FixedHolder fh = new FixedHolder(); 		
		frame.getContentPane().add( fh );		
		frame.setVisible(true);	
	}
	
	
}




class FixedHolder extends JPanel implements ActionListener	//Holder Panel for home button
{
	private WattWorksHolder wwh;
	private JButton home;
	
	public FixedHolder()
	{
		setLayout(new BorderLayout(10,10));
		
		home = new JButton("home"); //button is created and added to south
		home.setVisible(false);	//first page does not need a home button
		home.addActionListener(this);
		add(home, BorderLayout.SOUTH);

		
		wwh = new WattWorksHolder(this);	//card layout holder panel is added to the center
		add(wwh, BorderLayout.CENTER);
	}
	
	public void actionPerformed(ActionEvent evt)	//when button is pressed, calls method to change cardLayout page to "home"
	{
		if(evt.getActionCommand().equals("home"))
			wwh.showHomePanel();
		else if(evt.getActionCommand().equals("return to puzzle"))
		{
			home.setText("home");
			wwh.returnToPuzzle();
		}
	}

	public void showHomeButton()	//this is triggered after user passes first page, when home button is actually needed
	{
		home.setVisible(true);
	}

	public void showPuzzleReturnButton()
	{
		home.setText("return to puzzle");
	}



	
	
}

class WattWorksHolder extends JPanel	//holder panel that contains cardLayout
{
	private CardLayout cards;
	private FixedHolder fh;
	private Instructions i;
	
	public WattWorksHolder(FixedHolder fhIn)
	{
		fh = fhIn;

		cards = new CardLayout();	//create cardLayout and set it as Layout
		setLayout(cards);
		
		User user = new User();		//create a new user instance
		User highScores = new User("highScores");	//create a user instance, which creates and/or reads from the highscores file.
		
		StartPanel sp = new StartPanel(this, cards, user);	//first page
		HomePanel hp = new HomePanel(this, cards, user);	//home page
		
		Puzzles p = new Puzzles(this, cards, user);		//create puzzles panel	
		i = new Instructions();	//create instruction book
		Stats s = new Stats(user, highScores);	//create stats page
		
		add(sp,"start");	//add each to cardLayout
		add(hp,"home");
		add(p, "puzzles");
		add(i, "instructions");
		add(s, "stats");
		
	}
	
	public void showHomePanel()	//method is called by FixedHolder 
	{
		cards.show(this, "home");
	}

	public void showHomeButton()	//invoked when user passes first page
	{
		fh.showHomeButton();
	}
	
	public void goToInstructions()
	{
		cards.show(this, "instructions");
		fh.showPuzzleReturnButton();
	}
	
	public void returnToPuzzle()
	{
		cards.show(this, "gamePanel");
		
	}
}

class StartPanel extends JPanel 
{
	private JTextField tfName;
	private WattWorksHolder wwh;
	private CardLayout cards;
	private User user;
	private String name;
	private JCheckBox jcb;
	private StartPanel sp;
	
	public StartPanel(WattWorksHolder wwhIn, CardLayout cardsIn, User userIn)	//first page, where user is asked to input name
	{
		wwh = wwhIn;
		cards = cardsIn;
		user = userIn;
		sp = this;
		
		JLabel welcome = new JLabel("Welcome to WattWorks");					//create and add a welcome label
		welcome.setFont(new Font("Serif", Font.BOLD, 24));
		welcome.setPreferredSize(new Dimension(800,25));
		welcome.setHorizontalAlignment(SwingConstants.CENTER);
		welcome.setForeground(new Color(255,255,255));
		welcome.setBackground(new Color(0,0,0,200));
		add(welcome);

		JTextArea message = new JTextArea("This game allows users to learn about electronic components by interacting with them.", 20,44);	//create a text area to show information about game
		message.setLineWrap(true);
		message.setWrapStyleWord(true);
		JScrollPane pane = new JScrollPane(message);
		add(pane);

		tfName = new JTextField("Enter your name:", 60);	//create text field where name can be entered and attatch ActionListener
		tfName.setHorizontalAlignment(SwingConstants.CENTER);
		tfName.setForeground(new Color(255,255,255));
		tfName.setBackground(new Color(0,0,0));
		//tfName.setOpaque(false);
		TextHandler th = new TextHandler();
		tfName.addActionListener(th);
		add(tfName);

		jcb = new JCheckBox("Proceed as guest");				//create check box to proceed to next page and add ActionListener
		jcb.setPreferredSize(new Dimension(800,20));	//if name has not been entered, say "proceed as guest"
		jcb.setHorizontalAlignment(SwingConstants.CENTER);
		jcb.setForeground(new Color(255,255,255));
		jcb.setOpaque(false);
		CheckHandler ch = new CheckHandler();
		jcb.addActionListener(ch);
		add(jcb);
    }
            
	class TextHandler implements ActionListener		//when user enters name
	{
		public void actionPerformed(ActionEvent evt)
		{
			name = evt.getActionCommand();		//set fv name to what was entered
			jcb.setText("Continue as " + name);	//update check box label
			sp.repaint();
		}
	}

	class CheckHandler implements ActionListener	//when check box is clicked 
	{
		public void actionPerformed(ActionEvent evt)
		{
			if(name != null)	//if user has entered a name, create a file for them
				user.setName(name);
			cards.show(wwh, "home");	//go to home page
			wwh.showHomeButton();	//make the home button visible
			sp.repaint();
		}
		
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(getImage("Background2.jpg",""), 0, 0,1000,800, this);
	}
	
	
	public Image getImage(String imgNameIn, String folderIn)
    {
		Image picture = null;
		String imgName = imgNameIn;
		 
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
		
}

class HomePanel extends JPanel		
{
	
	private WattWorksHolder wwh;
	private CardLayout cards;
	private JLabel welcome;
	private User user;
	
	public HomePanel(WattWorksHolder wwhIn, CardLayout cardsIn, User userIn)	//home page, user can select b/t puzzles, instructions, stats
	{
		wwh = wwhIn;
		cards = cardsIn;
		user = userIn;
		
		welcome = new JLabel("Welcome");							//create label
        welcome.setFont(new Font("Serif", Font.BOLD, 24));
        welcome.setPreferredSize(new Dimension(800,25));
        welcome.setHorizontalAlignment(SwingConstants.CENTER);
        welcome.setForeground(new Color(255,255,255));
        welcome.setBorder( new EmptyBorder(10,10,10,10));
        add(welcome);

        JTextArea message = new JTextArea("This game allows users to learn about electronic components by interacting with them.", 20,22);	//same textArea
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        JScrollPane pane = new JScrollPane(message);
        add(pane);

        ButtonGroup bg = new ButtonGroup();						//create button group and handler to deal with JRadioButtons 
        RadioButtonHandler rbh = new RadioButtonHandler();

        JRadioButton puzzles = new JRadioButton("puzzles");		//create and format the three buttons needed
        puzzles.setPreferredSize(new Dimension(800,25));
        puzzles.setHorizontalAlignment(SwingConstants.CENTER);
		puzzles.setForeground(new Color(255,255,255));
		puzzles.setOpaque(false);
        bg.add(puzzles);
        puzzles.addActionListener(rbh);
        add(puzzles);
        
        JRadioButton instructions = new JRadioButton("instructions");
        instructions.setPreferredSize(new Dimension(800,25));
        instructions.setHorizontalAlignment(SwingConstants.CENTER);
        instructions.setForeground(new Color(255,255,255));
		instructions.setOpaque(false);
        bg.add(instructions);
        instructions.addActionListener(rbh);
        add(instructions);
        
		JRadioButton stats = new JRadioButton("stats");
        stats.setPreferredSize(new Dimension(800,25));
        stats.setHorizontalAlignment(SwingConstants.CENTER);
        stats.setForeground(new Color(255,255,255));
		stats.setOpaque(false);
        bg.add(stats);
        stats.addActionListener(rbh);
        add(stats);
        
    }

    class RadioButtonHandler implements ActionListener		//when one of the buttons is selected, go to that page.
    {
        public void actionPerformed(ActionEvent evt)
        {
           
            if(evt.getActionCommand().equals("puzzles"))
                cards.show(wwh,"puzzles");
            else if(evt.getActionCommand().equals("instructions"))
				cards.show(wwh,"instructions");
			else if(evt.getActionCommand().equals("stats"))
				cards.show(wwh,"stats");
        }
    }
	
	public void paintComponent(Graphics g)		//this is for updating the title JLabel witht the user's name
	{
        super.paintComponent(g);
		if(user.getName() != null)
        	welcome.setText("Welcome " + user.getName());
        	
        g.drawImage(getImage("Background2.jpg",""), 0, 0,1000,800, this);
	}
	
	
	
	public Image getImage(String imgNameIn, String folderIn)
    {
		Image picture = null;
		String imgName = imgNameIn;
		 
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
}


 

