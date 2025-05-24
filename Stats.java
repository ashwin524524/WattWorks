/*Ashwin R.
 * P5
 * 4-23-25
 * 
 * Stats.java
 * 
 * This file contains the Jpanel for displaying statistics as well as a user profile class
*/

import javax.swing.JPanel;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;

import java.util.Scanner;

import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JTextArea;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;

import java.time.LocalDateTime;
import java.time.LocalDate;


class Stats extends JPanel	
{
	private JLabel title;
	private JTextArea puzzle;
	private User user;
	private User highScore;
	private JPanel puzzles;
	 
	public Stats(User userIn, User highScoreIn)	//this is the JPanel where stats can be viewed
	{
		setLayout(new BorderLayout(10,10));
		
		user = userIn;
		highScore = highScoreIn;
		title = new JLabel("Stats for: ");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(new Font("Serif", Font.BOLD, 24));
		add(title, BorderLayout.NORTH);
		
		puzzles = new JPanel();
		puzzles.setLayout(new BorderLayout());
		
		JLabel completed = new JLabel("Puzzles Completed:");
		completed.setHorizontalAlignment(SwingConstants.CENTER);
		puzzles.add(completed, BorderLayout.NORTH);
		
		puzzle = new JTextArea("placeholder",20,50);
		puzzle.setFont(new Font("Serif", Font.PLAIN, 16));
		puzzle.setWrapStyleWord(true);
		puzzle.setLineWrap(true);
		puzzle.setMargin(new Insets(10, 10, 10, 10));
		puzzles.add(puzzle, BorderLayout.CENTER);
		
		add(puzzles, BorderLayout.CENTER);
		
		
	}
	
	public void paintComponent(Graphics g)
	{
		title.setText("Please restart program and enter name to view stats");
		
		if(!user.userIsNull())
			title.setText("Stats for: " + user.getName());
		
		System.out.println(user.getSolvedPuzzles());
		puzzle.setText(user.getSolvedPuzzles());
		System.out.println(puzzle.getText());
		puzzles.repaint();
		
	}
}

class User 	//this class is used to create user profiles. It reads from a user file and creates a new one if necessary
{
	private String name;
	private String recentPuzzle;
	private PrintWriter writer;
	private Scanner reader;
	
	private boolean isBlank = true;
	
	public User()	//blank constructor
	{
		
	}
	
	public User(String nameIn)	//overloaded constructor
	{
		name = nameIn;
		makeFile(nameIn);
		isBlank = false;
	}
	
	public void setName(String nameIn)	//used to set name and create file
	{
		name = nameIn;
		makeFile(nameIn);
		isBlank = false;
	}
	
	public boolean userIsNull()
	{	
		return isBlank;
	}
	
	public String getName()	//returns given user's name
	{
		return name;
	}
	
	public void recallUser(String nameIn)	//this method adds a scanner to the given file
	{
		String fileName = "users/" + nameIn;
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
	
	public void addPuzzle(String puzzleIn)
	{
		recentPuzzle = puzzleIn;
		makeFile();

		LocalDate date = LocalDate.now();
		
		writer.println("Solved " + puzzleIn + " on: " + date);
		writer.close();

		System.out.println("Current Date: " + date);
	}
	
	public String getSolvedPuzzles()
	{
		readFile();
		String all = new String("");
		reader.nextLine();
		
		while(reader.hasNextLine())
		{
			all = all + reader.nextLine() + " \n";
		}
		
		return all;
	}
	
	public String getMaxLevel()
	{
		return recentPuzzle;
	}
	
	public void makeFile()
	{
		makeFile(name);
	}
	
	public void readFile()
	{
		readFile(name, "users");
	}
	
	
    public void readFile(String fileNameIn, String folderIn)
	{
		reader = null;

		String fileName = folderIn + "/" + fileNameIn;
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
	
	
	public void makeFile(String nameIn)	//this file creates a FileWriter for the given name
	{
		String fileName = "users/" +  nameIn;
		File outFile = new File(fileName);

		boolean isNewFile= false;
		if(!outFile.exists())
			isNewFile =true;

		try 
		{
			writer = new PrintWriter(new FileWriter(outFile, true)); //create appending fileWriter.
		}
		catch(IOException e)
		{
			System.err.println("Cannot create " + fileName + " file to be written to.");
			System.exit(1);
		}

		if(isNewFile)
			formatFile();
		
		
		
		System.out.println("wrote to file");
		recallUser(nameIn);
		
	}

	public void formatFile()
	{
		writer.println("User: " + name);	//add the users name at top
		writer.close();
	}

	
}

