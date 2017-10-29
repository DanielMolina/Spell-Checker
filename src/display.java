/**
 * This file contains the display, Empty, SpellCheckMenuBar, SpellCheckPanel, and SpellCheckFrame classes which make up the GUI display
 * @author Daniel
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.Color;
import java.awt.event.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.Utilities;

/**
 * @name: display
 * Contains the runDisplay method which runs the GUI
 */
public class display 
{
	JFrame scFrame;

	public static ArrayList<Integer> numWordsIn; //number of words in input file for stats
	public static ArrayList<Integer> numReplaced;	//number of words replaced for stats
	public static ArrayList<Integer> numAdded;	//number of words added for stats
	public static ArrayList<Integer> numLinesIn;	//number of lines in inputfile for stats
	public static ArrayList<Integer> numIgnored;	//number of words ignored for stats

	public void runDisplay()
	{
		scFrame = new SpellCheckFrame();
		scFrame.setVisible(true);
	}
}

/**
 * @name Empty
 * Used to check if display is in use 
 */
class Empty
{
	boolean status = true;
}

/**
 * @name SpellCheckMenuBar
 * Implements the menu bar section of the GUI
 */
class SpellCheckMenuBar extends JMenuBar
{
	
	JMenu file; 
	JMenu help;
	JMenu stats;
	JMenuItem newFile; // in file
	JMenuItem fileClose;// in file
	JMenuItem howToUse; // in help
	JMenuItem printStats; //in stats
	
	int end; //used to assign dialog box option at fileClose

	/**
	 * Constructor
	 * Sets up the menu bar
	 */
	public SpellCheckMenuBar()
	{
		// menu bar set up
		file = new JMenu("File");
		help = new JMenu("Help");
		newFile = new JMenuItem("New File(s)");
		fileClose = new JMenuItem("Close");
		howToUse = new JMenuItem("How to use Spell Checker");
		stats = new JMenu("Statistics");
		printStats = new JMenuItem("Print Statistics");
	
		//ad items to menubar
		file.add(newFile);
		file.add(fileClose);
		help.add(howToUse);
		stats.add(printStats);
		add(file);
		add(help);
		add(stats);

		// menu bar listeners
		/*
		* closes program
		*/
		fileClose.addActionListener
		(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{	
					//System exits only if user responds with yes
					end = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?");

					if (end == JOptionPane.YES_OPTION) 
					{
						System.exit(0);		
					}
				}
			}
		);

		/** 
		* opens text file with instructions on how to use the program
		*/
		howToUse.addActionListener
		(
			new ActionListener()
			{
				JFrame helpFrame; 
				JTextField helpText;
				FileReader reader;
				JScrollPane scroller;

				public void actionPerformed(ActionEvent event) 
				{
					// Creates the frame for the help instructions
					helpFrame = new JFrame(); 
					helpText = new JTextField();
					scroller = new JScrollPane(helpText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
					
				    helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				    helpFrame.setTitle("Help Instructions");
				    helpFrame.add(helpText, BorderLayout.CENTER);
				    helpFrame.getContentPane().add(scroller, BorderLayout.CENTER);
				       
				    /**
				     * Reads the help.txt file with the instructions 
				     */
				    reader = null;

				    try 
				    {
				      reader = new FileReader("help.txt");
				      helpText.read(reader, "help.txt");
				      helpText.setEditable(false);
				    }
				    /**
				     * @exception IOException signals that the help file was not found
				     */ 
				    catch (IOException exception) 
				    {
				      System.err.println("Help file not found");
				      exception.printStackTrace();
				    } 
				    finally 
				    {
				      if (reader != null) 
				      {
				        try 
				        {
				          reader.close();
				        } 
				        /**
				     	 * @exception IOException signals that there was an error close the help file
				     	 */ 
				        catch (IOException exception) 
				        {
				          System.out.println("Error closing file");
				          exception.printStackTrace();
				        }
				      }
				    }
				    
				    // displays help.txt on the helpframe
				    helpFrame.setLocation(10, 270);
				    helpFrame.setSize(700, 400);
				    helpFrame.setVisible(true); 
				}	
			}
		);
	}
}

/**
 * @name SpellCheckWord
 * Each word of the input file displayed in the text area has the the SpellCheck attributes
 */
class SpellCheckWord
{
	String word;
	int startLocation;
	File inputFile;
	File dictionaryFile;
	boolean missingWord; //true if not in dictionary
	boolean ignored;	//true when Ignore has been clicked, used in stats implementation
	boolean replaced;	//true when Replace has been clicked, used in stats implementation
	boolean added;	//true when Added has been clicked, used in stats implementation
	int inputFileIndex;
	int dictionaryFileIndex;

	/*
	 * Constructor 
	 */
	public SpellCheckWord(String scWord, int start, File input, File dictionary)
	{
		word = scWord;
		startLocation = start;
		inputFile = input;
		dictionaryFile = dictionary;
		missingWord = false;
		ignored = false;
		replaced = false;
		added = false;
		inputFileIndex = 0;
		dictionaryFileIndex = 0;
	}
	
	/*
	 * Constructor
	 */
	public SpellCheckWord(String scWord, int start)
	{
		word = scWord;
		startLocation = start;
		missingWord = false;
		ignored = false;
		replaced = false;
		added = false;
	}
}

/**
 * @name SpellCheckPopupMenu
 * allows users to add, ignore, or replace words
 */
class SpellCheckPopupMenu extends JPopupMenu
{
	JMenuItem add;
	JMenuItem ignore;
	JMenuItem replace;

	public SpellCheckPopupMenu(SpellCheckWord selectedWord, MouseEvent event, SpellCheckTextArea text)
	{
		add = new JMenuItem("Add");
		ignore = new JMenuItem("Ignore");
		replace = new JMenuItem("Replace");

		//  if the word is in the missing list, the pop-up menu becomes available
		if(selectedWord.missingWord)
		{
			add(add);
			add(ignore);
			add(replace);
			show(event.getComponent(), event.getX(), event.getY());	
		}
		else
		{  //if word is in dictionary, then only the replace option is available
			add(replace);
			show(event.getComponent(), event.getX(), event.getY());
		}

		//when add is clicked, the current word is added to the dictionary
		add.addActionListener
		(
			new ActionListener()
			{
				BufferedWriter out;

				public void actionPerformed(ActionEvent event)
				{
					/** 
					 * opens file and appends the words chosen to be added to the dictionary
					 * disables both add and ignore buttons after first click
					 */
					Set<String> currentDictionaryTree = treeHandler.convertFileToTree(selectedWord.dictionaryFile);
					
					try 
					{
						if(treeHandler.wordIsUnique(selectedWord.word, currentDictionaryTree)) 
						{
							selectedWord.added = true;
							out = new BufferedWriter(new FileWriter(selectedWord.dictionaryFile, true)); 
							out.newLine();
							out.write(selectedWord.word);
							out.close();

							// Statistics updating after word is successfully added
							int loc = (selectedWord.inputFileIndex+1)*(selectedWord.dictionaryFileIndex+1);
							display.numAdded.set(loc, Integer.valueOf(display.numAdded.get(loc) + 1));
						}
						
						text.unhighlight(selectedWord);
					}
					/**
				     * @exception IOException signals that there was an error in writing user added words to the corresponding dictionary
				     */ 
					catch (IOException exception) 
					{
						exception.printStackTrace();
					}
				}
			}
		);

		//word is not added to the dictionary
		ignore.addActionListener
		(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					text.unhighlight(selectedWord);
					selectedWord.ignored = true;
					// Statistics updating after word is successfully ignored
					int loc = (selectedWord.inputFileIndex+1)*(selectedWord.dictionaryFileIndex+1);
					display.numIgnored.set(loc, Integer.valueOf(display.numIgnored.get(loc) + 1));
				}
			}
		);

		//user may replace current word with a new word.
		replace.addActionListener
		(
			new ActionListener()
			{
				JFrame replaceWindow;
				JTextField newWordField;
				JLabel replaceMessage;
				JButton replaceButton;
				JButton cancelButton;
				JPanel top;
				JPanel bottom;

				public void actionPerformed(ActionEvent event)
				{
					replaceWindow = new JFrame("Replace");
					newWordField = new JTextField(20);
					replaceMessage = new JLabel("New word:");
					replaceButton = new JButton("Replace");
					cancelButton = new JButton("Cancel");
					top = new JPanel();
					bottom = new JPanel();

					top.add(replaceMessage);
					top.add(newWordField);
					bottom.add(replaceButton);
					bottom.add(cancelButton);
					replaceWindow.add(top, BorderLayout.NORTH);
					replaceWindow.add(bottom, BorderLayout.SOUTH);
					replaceWindow.pack();
					replaceWindow.setLocationRelativeTo(null);
					replaceWindow.setVisible(true);

					cancelButton.addActionListener
					(
						new ActionListener()
						{
							public void actionPerformed(ActionEvent event)
							{
								replaceWindow.dispatchEvent(new WindowEvent(replaceWindow, WindowEvent.WINDOW_CLOSING));
							}
						}
					);

					replaceButton.addActionListener
					(
						new ActionListener()
						{
							String inputText;
							String newWord;
							String oldWord;
							String currentLine;
							String[] textLine;
							String seperator = " ";
							int difference;
							File newInputFile;
							BufferedWriter bw;
							BufferedReader br;

							public void actionPerformed(ActionEvent event)
							{
								replaceWindow.dispatchEvent(new WindowEvent(replaceWindow, WindowEvent.WINDOW_CLOSING));

								if(!(newWordField.getText().equals("")))
								{
									newWord = newWordField.getText();
									newWord = newWord.replaceAll("[\n\r\t]+", "");
							       	newWord = newWord.replaceAll("[^a-zA-Z]", "");

									for(SpellCheckWord pair : text.contentPairs)
									{
										if(pair.startLocation == selectedWord.startLocation)
										{
											if(selectedWord.word.length() == newWord.length())
											{
												text.replaceRange(newWord, pair.startLocation, (pair.startLocation + pair.word.length()));
												oldWord = selectedWord.word;
												pair.word = newWord;
											}
											else
											{
												difference = newWord.length() - selectedWord.word.length();
												text.replaceRange(newWord, pair.startLocation, (pair.startLocation + pair.word.length()));
												text.shiftContentPairs(pair.startLocation, difference);
												oldWord = selectedWord.word;
												pair.word = newWord;
											}
										}
									}
								}

								try
								{
									// copy correct input file contents to a temporary files

									newInputFile = new File("temp.txt");
									bw = new BufferedWriter(new FileWriter(newInputFile, false));
									br = new BufferedReader(new FileReader(selectedWord.inputFile));

									while((currentLine = br.readLine()) != null)
									{
										textLine = currentLine.split(seperator);

										for(String word : textLine) 
							       	    {
							       			word = word.replaceAll("[\n\r\t]+", "");
							       			word = word.replaceAll("[^a-zA-Z]", "");

							       			if(word.equals(oldWord))
							       			{
							       				bw.write(newWord);
							       				bw.newLine();
							       				// Statistics updating after word is successfully added
												int loc = (selectedWord.inputFileIndex+1)*(selectedWord.dictionaryFileIndex+1);
												display.numReplaced.set(loc, Integer.valueOf(display.numReplaced.get(loc) + 1));
							       			}
							       			else if(!word.isEmpty())
							       			{
							       				bw.write(word);
							       				bw.newLine();
							       			}
							       		}
									}
									
								}
								/**
								 * @exception FileNotFoundException this exception will be thrown by the FileInputStream, FileOutputStream, and RandomAccessFile constructors when a file with the specified pathname does not exist. 
								 * It will also be thrown by these constructors if the file does exist but for some reason is inaccessible, for example when an attempt is made to open a read-only file for writing.
							     */
							    catch(FileNotFoundException exception) 
								{
									exception.printStackTrace();
								}
								/**
							     * @exception IOException signals that there was an error in writing to the new input file
							     */
								catch(IOException exception) 
								{
									exception.printStackTrace();
								}
								finally
								{
									try
									{
										br.close();
										bw.close();

										try
										{
											// copy contents on corrected input file to actual input file
											bw = new BufferedWriter(new FileWriter(selectedWord.inputFile, false));
											br = new BufferedReader(new FileReader(newInputFile));

											while((currentLine = br.readLine()) != null)
											{
												textLine = currentLine.split(seperator);

												for(String word : textLine) 
												{
													word = word.replaceAll("[\n\r\t]+", "");
											       	word = word.replaceAll("[^a-zA-Z]", "");

											       	if(!word.isEmpty())
											       	{
											       		bw.write(word);
											       		bw.newLine();
											       	}
												}
											}
										}
										catch(FileNotFoundException exception) 
										{
											exception.printStackTrace();
										}
										catch(IOException exception)
										{
											exception.printStackTrace();
										}
										finally
										{
											try
											{
												br.close();
												bw.close();
											}
											catch(IOException exception) 
											{
												exception.printStackTrace();
											}
										}
									}
									catch(IOException exception) 
									{
										exception.printStackTrace();
									}	
								}	
							}
						}
					);
				}
			}
		);
	}
}

/**
 * @name SpellCheckTextArea
 * Displays input text into text area and uses the SpellCheckPopupMenu and SpellCheckWord to 
 * make the add, ignore, and replace functionality to the user.
 */
class SpellCheckTextArea extends JTextArea
{
	ArrayList<SpellCheckWord> contentPairs;

	public SpellCheckTextArea()
	{
		contentPairs = new ArrayList<SpellCheckWord>();
	
		addMouseListener
		(
			new MouseListener()
			{
				int offset;
				int start;
				int end;
				int index;
				String selectedWord;
				SpellCheckPopupMenu clickMenu;
				SpellCheckWord word;

				public void mouseClicked(MouseEvent event)
				{
					if(SwingUtilities.isRightMouseButton(event))
					{
						offset = viewToModel(event.getPoint());

						try
						{
							start = Utilities.getWordStart(SpellCheckTextArea.this, offset);
							end = Utilities.getWordEnd(SpellCheckTextArea.this, offset);
							selectedWord = getText(start, end - start);

							if(selectedWord != "" && selectedWord != "\n")
							{
								index = searchContentPairs(selectedWord, start);
								try
								{
									word = contentPairs.get(index);
									clickMenu = new SpellCheckPopupMenu(word, event, SpellCheckTextArea.this);
								
								}
								catch(ArrayIndexOutOfBoundsException exception)
								{}
							}
						}
						catch(BadLocationException exception)
						{}
					}
				}

				public void mousePressed(MouseEvent event)
				{}
                public void mouseEntered(MouseEvent event)
                {}
                public void mouseExited(MouseEvent event)
                {}
                public void mouseReleased(MouseEvent event)
                {}
			}
		);
	}

	int searchContentPairs(String word, int wordStart)
	{
		int index = 0;

		for(SpellCheckWord pair : contentPairs)
		{
			if(pair.word.equals(word) && pair.startLocation == wordStart)
			{
				return index;
			}

			index = index + 1;
		}

		return -1;
	}

	void shiftContentPairs(int start, int shift)
	{
		for(SpellCheckWord pair : contentPairs)
		{
			if(pair.startLocation > start)
			{
				pair.startLocation = pair.startLocation + shift;
			}
		}
	}

	//appends current word to text area. 
	void extend(SpellCheckWord scWord)
	{
		if(!scWord.word.equals("\n\n"))
			super.append(scWord.word + " ");
		else
			super.append(scWord.word);

		contentPairs.add(scWord);
	}

	void highlight(SpellCheckWord word)
	{
		try
		{
			Highlighter highlighter = getHighlighter();
			HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.yellow);
			int position = 0;
			String text = getText();

			while((position = text.indexOf(word.word, position)) >= 0)
			{
				if(position == word.startLocation)
					highlighter.addHighlight(position, position + word.word.length(), painter);   

	            position += word.word.length();
			}
		}
		catch(BadLocationException exception)
		{}
	}

	/*
	 * Unhighlights words that have be added, ignored, or replaced by user
	 */
	void unhighlight(SpellCheckWord word)
	{
		Highlighter highlighter = getHighlighter();
		Highlighter.Highlight[] highlights = highlighter.getHighlights();

    	for (int i = 0; i < highlights.length; i++) 
    	{
      		if(highlights[i].getStartOffset() == word.startLocation) 
      		{
              highlighter.removeHighlight(highlights[i]);
              word.missingWord = false;
      		}
   		}
	}

	/*
	 * Used to add the name of current input file an dictionary file so that the user can 
	 * know with which pair they are working
	 */
	public void extend(String inputFileName) 
	{
		super.append(inputFileName);	
	}
}

/**
 * @name SpellCheckFrame
 * Main SpellChecker Frame.
 */
class SpellCheckFrame extends JFrame 
{
	SpellCheckMenuBar scMenuBar;
	int end; //used to assign dialog box option at the click of exit
	Empty empty = new Empty(); //used to reset the panel after at least one file has been processed.

	/**
	 * Constructor
	 */
	public SpellCheckFrame() 
	{
		// frame parameters
		setTitle("MO42 Spell Checker");
		setSize(500,500); // default size is 0,0
		setLocationRelativeTo(null); // default is 0,0 (top left corner)
		
		// window listeners
		addWindowListener
		(
			new WindowAdapter() 
			{
			  	public void windowClosing(WindowEvent event) 
			  	{
			  		// only exits if the user clicks yes, nothing otherwise
			  		end = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?");

					if (end == JOptionPane.YES_OPTION) 
					{	
			  			dispose();
						System.exit(0);
					}
					else
						setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			  	} 
			}
		);
	
		// menu bar
		scMenuBar = new SpellCheckMenuBar();

		// new file listener
		scMenuBar.newFile.addActionListener
		(
			new ActionListener()
			{
				// input and dictionary file variables
				JFileChooser newFileChooser;
				JFileChooser dictionaryFileChooser;
				FileNameExtensionFilter filter; // to filter .txt files only
				File inputFiles[];
				File dictionaryFiles[]; 
				
				// display variables
				JScrollPane scroller;
				Container contentPane;
				SpellCheckTextArea inputText;
				SpellCheckWord tuple3;
				BufferedReader br = null;
	    		String line = "";
	   	 		String seperator = " ";
				int index;
				int innerIndex;
				int loc;

				// data structure variables
				ArrayList<Set<String>> inputTrees;  //holds tree form of input file
				ArrayList<Set<String>> dictionaryTrees; // holds tree form of dictionary file
				ArrayList<ArrayList<ArrayList<String>>> missingWords; // holds the misspelled words
				ArrayList<ArrayList<String>> setsOfWords; // holds different sets on misspelled words according to input file and dictionary
				ArrayList<String> words; // holds words in setsOfWords
				String word; // holds on single word
				
				// used during iteration
				int newSelectionResult;
				int dictionarySelectionResult;
				int numberOfInputs;
				int numberOfDictionaries;
				int numberOfWords;
				int inFileNumber;
				int dictionaryNumber;
				
				/**
				 * process that allows user to select input file(s) and dictionary file(s) and displays misspelled words
				 */
				public void actionPerformed(ActionEvent event)
				{
					// if an input file is already loaded, clear panel before adding new words
					if(!empty.status)
					{
						// new session only if user responds with yes
						end = JOptionPane.showConfirmDialog(null, "Are you sure you want to start a new spell check session?");

						if (end == JOptionPane.YES_OPTION) 
						{							
							getContentPane().removeAll();
							revalidate();
							empty.status = false;

							// file chooser for input file and dictionaries selection set up
							newFileChooser = new JFileChooser();
							filter = new FileNameExtensionFilter(".txt files", "txt");
							newFileChooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
							newFileChooser.setApproveButtonText("Load Input");
							newFileChooser.setDialogTitle("Load Input File(s)");
							newFileChooser.setFileFilter(filter);
							newFileChooser.setMultiSelectionEnabled(true);
							newSelectionResult = newFileChooser.showOpenDialog(scMenuBar.newFile);
						}
						else
						{
							newSelectionResult = JFileChooser.CANCEL_OPTION;
							dictionarySelectionResult = JFileChooser.CANCEL_OPTION;
						}
					}
					else
					{
						empty.status = false;

						// file chooser for input file and dictionaries selection set up
						newFileChooser = new JFileChooser();
						filter = new FileNameExtensionFilter(".txt files", "txt");
						newFileChooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
						newFileChooser.setApproveButtonText("Load Input");
						newFileChooser.setDialogTitle("Load Input File(s)");
						newFileChooser.setFileFilter(filter);
						newFileChooser.setMultiSelectionEnabled(true);
						newSelectionResult = newFileChooser.showOpenDialog(scMenuBar.newFile);
					}
					
					if(newSelectionResult == JFileChooser.APPROVE_OPTION)
					{
						inputFiles = newFileChooser.getSelectedFiles();
					
						inputTrees = new ArrayList<Set<String>>(); // **** took away TreeSet
						inputTrees = treeHandler.convertMultipleFiles(inputFiles);
						
						dictionaryFileChooser = new JFileChooser();
						dictionaryFileChooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
						dictionaryFileChooser.setApproveButtonText("Load Dictionary");
						dictionaryFileChooser.setDialogTitle("Load Dictionary File(s)");
						dictionaryFileChooser.setFileFilter(filter);
						dictionaryFileChooser.setMultiSelectionEnabled(true);
						dictionarySelectionResult = dictionaryFileChooser.showOpenDialog(null);

						// only change view if user inputs both an input and dictionary(ies)
						if(dictionarySelectionResult == JFileChooser.APPROVE_OPTION)
						{
							dictionaryFiles = dictionaryFileChooser.getSelectedFiles();
							dictionaryTrees = new ArrayList<Set<String>>(); // **** took away TreeSet
							dictionaryTrees = treeHandler.convertMultipleFiles(dictionaryFiles);
							contentPane = getContentPane();

							inputText = new SpellCheckTextArea();
							inputText.setLineWrap(true);
							inputText.setWrapStyleWord(true);
							
							display.numWordsIn = new ArrayList<Integer>((inputFiles.length*dictionaryFiles.length + 1)); //size of numDictionaries*numInputFiles + 1 for the 0 index
							display.numReplaced = new ArrayList<Integer>((inputFiles.length*dictionaryFiles.length + 1));
							display.numAdded = new ArrayList<Integer>((inputFiles.length*dictionaryFiles.length + 1));
							display.numLinesIn = new ArrayList<Integer>((inputFiles.length*dictionaryFiles.length + 1));
							display.numIgnored = new ArrayList<Integer>((inputFiles.length*dictionaryFiles.length + 1));
										
							/*
							 * get number of lines in input file
							 */
							for(index = 0; index < inputFiles.length; index++)
							{
								for(innerIndex = 0; innerIndex < dictionaryFiles.length; innerIndex++)
								{
									loc = (index+1)*(innerIndex+1);
									
									//zero location in list is added and then ignored
									display.numLinesIn.add(0);
									
									//initialize all statistics variables for each inputFile/DictionaryFile pair to 0;
									display.numLinesIn.add(loc, Integer.valueOf(0)); 
									
									try 
								    {
								        br = new BufferedReader(new FileReader(inputFiles[index]));
			        
					       			   	while ((line = br.readLine()) != null) 
								        {   
					       			   		display.numLinesIn.set(loc, Integer.valueOf(display.numLinesIn.get(loc) + 1)); //increment lines read
								       	}
								    }
									/**
									 * @exception FileNotFoundException this exception will be thrown by the FileInputStream, FileOutputStream, and RandomAccessFile constructors when a file with the specified pathname does not exist. 
									 * It will also be thrown by these constructors if the file does exist but for some reason is inaccessible, for example when an attempt is made to open a read-only file for writing.
								     */
								    catch (FileNotFoundException exception) 
									{
										exception.printStackTrace();
									}
									/** 
									 * @exception IOException signals that an I/O exception of some sort has occurred. This class is the general class of exceptions produced by failed or interrupted I/O operations.
									 */   
								    catch (IOException exception) 
									{
										exception.printStackTrace();
									}
								    finally 
								    {
								        if (br != null) 
								        {
								            try 
								            {
								                br.close();
							                } 
								            catch (IOException exception) 
											{
												exception.printStackTrace();
											}
								        }
								    }

								}
							}

							/*
							 * Reads file and displays ready for user input of add, replace, ignore
							 */
							for(index = 0; index < inputFiles.length; index++)
							{
								for(innerIndex = 0; innerIndex < dictionaryFiles.length; innerIndex++)
								{
									loc = (index+1)*(innerIndex+1);
									
									//all zero locations in list are added and then ignored
									display.numWordsIn.add(0);
									display.numReplaced.add(0);
									display.numAdded.add(0);
									display.numIgnored.add(0);

									//initialize all statistics variables for each inputFile/DictionaryFile pair to 0
									display.numWordsIn.add(loc, Integer.valueOf(0));
									display.numReplaced.add(loc, Integer.valueOf(0));
									display.numAdded.add(loc, Integer.valueOf(0));
									display.numIgnored.add(loc, Integer.valueOf(0));
									
									/**
								     * Convenience for reading character files.
								     */
									try 
								    {
								        br = new BufferedReader(new FileReader(inputFiles[index]));
								        
								        //displays current input file and dictionary name at top of text
					       			   	inputText.extend("Input file: " + inputFiles[index].getName() + "\tDictionary File: " + dictionaryFiles[innerIndex].getName() + "\n\n");

					       			   	while ((line = br.readLine()) != null) 
								        {   
								      		String[] textLine = line.split(seperator);
								        	   
								       		for(String word : textLine) 
								       		{
								       		   word = word.replaceAll("[\n\r\t]+", "");
								       		   word = word.replaceAll("[^a-zA-Z]", "");
								       		   
								       		   if(!word.isEmpty()) 
								       		   {
								       				display.numWordsIn.set(loc, Integer.valueOf(display.numWordsIn.get(loc) + 1)); //increment the number of words in the input file
								       				tuple3 = new SpellCheckWord(word.toLowerCase(), inputText.getText().length(), inputFiles[index], dictionaryFiles[innerIndex]);
								  					tuple3.inputFileIndex = index;
								  					tuple3.dictionaryFileIndex = innerIndex;

								       				inputText.extend(tuple3);
								       		   }
								       		}	
								        }
								    } 
									/**
									 * @exception FileNotFoundException this exception will be thrown by the FileInputStream, FileOutputStream, and RandomAccessFile constructors when a file with the specified pathname does not exist. 
									 * It will also be thrown by these constructors if the file does exist but for some reason is inaccessible, for example when an attempt is made to open a read-only file for writing.
								     */
								    catch (FileNotFoundException exception) 
									{
										exception.printStackTrace();
									}
									/** 
									 * @exception IOException signals that an I/O exception of some sort has occurred. This class is the general class of exceptions produced by failed or interrupted I/O operations.
									 */   
								    catch (IOException exception) 
									{
										exception.printStackTrace();
									}
								    finally 
								    {
								        if (br != null) 
								        {
								            try 
								            {
								                br.close();
							                } 
								            catch (IOException exception) 
											{
												exception.printStackTrace();
											}
								        }
								    }

								    inputText.extend(new SpellCheckWord("\n\n", inputText.getText().length()));
								}
							}
							
							/**
							 * Calls the treeHandler functions and displays the misspelled words.
							 * Outer list's indices represent different input files. 
							 * Middle list's indices represent different dictionary files that each input file will be compared to.
							 * Inner list's indices represent the words in an input file that are not in the corresponding dictionary.
							 */
							missingWords = new ArrayList<ArrayList<ArrayList<String>>>();
							missingWords = treeHandler.incorrectWords(inputTrees, dictionaryTrees);
							numberOfInputs = 0; 
							numberOfDictionaries = 0; 
							numberOfWords = 0; 
							inFileNumber = 0;
							dictionaryNumber = 0;
							numberOfInputs = missingWords.size();

							for(Iterator<ArrayList<ArrayList<String>>> iterator = missingWords.iterator(); iterator.hasNext();) 
							{
								setsOfWords = iterator.next();
								numberOfDictionaries += setsOfWords.size();
							
								for(Iterator<ArrayList<String>> iterator2 = setsOfWords.iterator(); iterator2.hasNext();)
								{
									// use label to separate differences from different dictionary file
									words = iterator2.next();
									numberOfWords += words.size(); 
									
									if (!words.isEmpty())
									{
										for(Iterator<String> iterator3 = words.iterator(); iterator3.hasNext();)
										{
											word = iterator3.next();
						
											for(SpellCheckWord pair : inputText.contentPairs)
											{
												if(pair.word.equals(word) && !pair.missingWord && pair.inputFile.equals(inputFiles[inFileNumber]) && pair.dictionaryFile.equals(dictionaryFiles[dictionaryNumber]))
												{
													pair.missingWord = true;
													inputText.highlight(pair);
												}
											}
										}
									}
									
									dictionaryNumber += 1;
								}

								inFileNumber += 1;
								dictionaryNumber = 0;
							}

							/*
							 * prints statistics for each pair of input/dictionary file to txt file
							 */
							 scMenuBar.printStats.addActionListener
							(
								new ActionListener()
								{
									public void actionPerformed(ActionEvent event)
									{
										try
										{
											// Create new file
											for(index = 0; index < inputFiles.length; index++)
											{
												for(innerIndex = 0; innerIndex < dictionaryFiles.length; innerIndex++)
												{	
													loc = (index+1)*(innerIndex+1);
													String path = System.getProperty("user.home") + "/Desktop/Stats-for-" + inputFiles[index].getName() + "-" + dictionaryFiles[innerIndex].getName() + ".txt";
													File writer = new File(path);
													            
													// file does not exist, create
													if (!writer.exists()) 
													{
													  	writer.createNewFile();
													}
						
													FileWriter fw = new FileWriter(writer.getAbsoluteFile());
													BufferedWriter bw = new BufferedWriter(fw);
						
													// Write in file
													bw.write(" Words in input file\t: " + display.numWordsIn.get(loc) + "\n Lines in input file\t: " + display.numLinesIn.get(loc) + "\n Number of words replaced:\t" + display.numReplaced.get(loc) + "\n Number of words added:\t" + display.numAdded.get(loc) + "\n Number of words ignored:\t" + display.numIgnored.get(loc));
													    
													// Close connection
													bw.close();
												}
											}
										}
										catch(Exception e)
										{
											System.out.println(e);
										}
									}
								}
							);
							
							inFileNumber = 0;
							inputText.setEditable(false);
							scroller = new JScrollPane(inputText);
							contentPane.add(scroller, BorderLayout.CENTER);
														
							// refresh frame
							//pack();					
							revalidate();	
						}
					}
				}
			}
		);

		setJMenuBar(scMenuBar);
 	}
}

