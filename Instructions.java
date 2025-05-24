/*Ashwin R.
 * P5
 * 4-23-25
 * 
 * Instructions.java
 * 
 * This file contains the Jpanel that displays other instructional panels
*/

//import

import javax.swing.JPanel;

import java.util.Scanner;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.Graphics;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;

import javax.imageio.ImageIO;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

class Instructions extends JPanel //holds the instruction booklet and each of its pages
{
	private CardLayout cards;	//cardLayout variables that are needed to attatch to prev & next buttons
	private Instructions instructions;
	private Scanner reader;		//reads instructionText file

	public Instructions()
	{
		cards = new CardLayout();	//set variables
		instructions = this;
		setLayout(cards);

		readFile("instructionText", "instructions");	//set up the Scanner
		
		for(int i = 1; i <= getNumPages(); i++)	//get the number of pages listed in the file, and make that many panels
		{
			add(makePanel(i));	//make a each panel
		}
	}
	
	
	public JPanel makePanel(int pageIn)	//makes each page of the instruction booklet.
	{
		JPanel page = new JPanel();	//main Jpanel
		page.setLayout( new GridLayout(1,2,10,10));	//set the formatting of the page
		page.setBorder( new EmptyBorder(10,10,10,10));
		
		JPanel west = new JPanel();	//create and format the left panel
		page.setBorder( new EmptyBorder(10,10,10,10));
		
		String[] info = getText(pageIn); //read the section of the file that corresponds with page number, get the information in a string array

		Image img = getImage(info[2], "instructions");	//create an image object
		RightPanel center = new RightPanel(img, info[3]);	//create the right panel and give the image object and formula
		
		west.setLayout(new BorderLayout(10,10));
		
		JLabel title = new JLabel(info[0]);	//create the title of the page
		title.setFont(new Font("Serif", Font.BOLD, 24));
		title.setHorizontalAlignment(SwingConstants.CENTER);
		west.add(title, BorderLayout.NORTH);
		
		
		JTextArea jta = new JTextArea(info[1]);	//create the main text box and format it
		jta.setFont(new Font("Serif", Font.PLAIN, 16));
		jta.setWrapStyleWord(true);
		jta.setLineWrap(true);
		jta.setMargin(new Insets(10, 10, 10, 10));
		jta.setEditable(false);
		west.add(jta, BorderLayout.CENTER);
		
		ButtonPanel bp = new ButtonPanel();	//add the button panel, which cycles through the pages
		west.add(bp, BorderLayout.SOUTH);
		
		page.add(west);
		page.add(center);

		center.repaint();
		
		return page;
	}

	public String[] getText(int pageIn)	//file parsing method
	{
		String[] info = new String[]{"","","",""};
		String line = "";
		String word = "";
		
		int page = pageIn;
		
		
		do
		{
			if(reader.hasNext())
				word = reader.next();
		}while(!(word.equals("page"  + pageIn)));	//go to page marker
			
		
		do
		{
			line = reader.nextLine();	//read the title, img name, formula
		}while((line.isBlank()));
		
		info[0] = line;
		
		do
		{
			line = reader.nextLine();
		}while((line.isBlank()));
		
		info[2] = line;
		
		do
		{
			line = reader.nextLine();
		}while((line.isBlank()));
		
		info[3] = line;
		
		line = reader.nextLine();
		
		do  		//read the rest of the text until the next page marker
		{
			if(line.isBlank())
				line = "\n\n";
			info[1] = info[1] + line;
			line = reader.nextLine();
		}while(!(line.equals("page" + (pageIn+1))));
		
		readFile();
		
		System.out.println("formula" + info[3]);

		return info;

	}
	
	public int getNumPages()
	{
		int count = 0;
		String word;
		while(reader.hasNext())
		{
			word = reader.next();
			if(word.equals("page" + (count+1)))
				count++;
		}
		count--;
		readFile();
		return count;
	}
	
	public void readFile()
	{
		readFile("instructionText", "instructions");
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

	class RightPanel extends JPanel	//right panel	
	{
		private Image image;
		private JTextArea formula;

		public RightPanel(Image imageIn, String formulaStringIn)
		{
			image = imageIn;
			setLayout(new BorderLayout());
			formula = new JTextArea(formulaStringIn.trim(), 20,6);	//create the formula box
			formula.setFont(new Font("Serif", Font.BOLD, 16));
			formula.setWrapStyleWord(true);
			
			add(formula, BorderLayout.SOUTH);
			
		}

		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			g.drawImage(image, 50, 50, 400,300,this);		//paint the image in the correct location
		}
	}
	
	class ButtonPanel extends JPanel implements ActionListener
	{
		public ButtonPanel()	//create button panel and attatch actionlisteners, which cycle through the pages
		{
			setLayout(new GridLayout(1,2,15,15));
			setBorder( new EmptyBorder(5,5,5,5));
			
			JButton prev = new JButton("prev.");
			JButton next = new JButton("next");
			
			prev.setBorder(BorderFactory.createRaisedBevelBorder());
			next.setBorder(BorderFactory.createRaisedBevelBorder());
			
			prev.addActionListener(this);
			next.addActionListener(this);

			prev.setPreferredSize(new Dimension(100,55));
			next.setPreferredSize(new Dimension(100,55));
			
			add(prev);
			add(next);
			
		}
		
		public void actionPerformed(ActionEvent evt)
		{
			if(evt.getActionCommand().equals("prev."))
				cards.previous(instructions);
			else if(evt.getActionCommand().equals("next"))
				cards.next(instructions);

		}
		
	}
	
	
	public Image getImage(String imgNameIn, String folderIn) //image getter method.
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


