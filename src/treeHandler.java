/**
 * Database Class. which Searching using the tree data structure.
 * Which can return incorrect word in this method.
 * @author Daniel
 */

import java.util.Set;
import java.util.TreeSet;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
 
/**
 * @name: treeHandler 
 */
public interface treeHandler 
{
	/**
	 * Coverts an array of input files into array of trees
	 * @param inputFiles is an array of files selected by the user
	 * @return arrayOfTrees is an array of files in tree form
	 */
	public static ArrayList<Set<String>> convertMultipleFiles(File[] inputFiles)
	{
		ArrayList<Set<String>> arrayOfTrees= new ArrayList<Set<String>>();
		
		for(File newFile : inputFiles) 
		{
			arrayOfTrees.add(convertFileToTree(newFile));
		}
		
		return arrayOfTrees;
	}

	/**
	 * Put the word into the tree as and check which the string are letter or not.
	 * If is not letter cover by [\n\r]
	 * @param inputFile is an input text file
	 * @return newTree in an input file in tree form
	 */
	public static Set<String> convertFileToTree(File inputFile) 
	{
		Set<String> newTree = new TreeSet<String>();
		/**
		 * Reads text from a character-input stream, buffering characters so as to provide for the efficient reading of characters, arrays, and lines.
		 * The buffer size may be specified, or the default size may be used. The default is large enough for most purposes.
		 * In general, each read request made of a Reader causes a corresponding read request to be made of the underlying character or byte stream. 
		 * It is therefore advisable to wrap a BufferedReader around any Reader whose read() operations may be costly, such as FileReaders and InputStreamReaders.
		 */
		BufferedReader br = null;
	    String line = "";
	    String seperator = " ";

	    try 
	    {
			/**
	    	 * Convenience class for reading character files. The constructors of this class assume 
	    	 * that the default character encoding and the default byte-buffer size are appropriate. 
	    	 * To specify these values yourself, construct an InputStreamReader on a FileInputStream.
	    	 * @author Alex and Jinxin
	    	 */
	        br = new BufferedReader(new FileReader(inputFile));
	        while ((line = br.readLine()) != null) 
	        {   
	      	   String[] textLine = line.split(seperator);
	        	   
	       	   for(String word : textLine) 
	       	   {
	       		   word = word.replaceAll("[\n\r\t]+", "");
	       		   word = word.replaceAll("[^a-zA-Z]", "");
	       		   
	       		   if(!word.isEmpty()) 
	       		   {
	       			   newTree.add(word.toLowerCase());
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
	    
	    return newTree;
	}

	/**
	 * Check the word which are not in the dictionary, which means that the word are incorrect
	 * The method are checking and return the incorrect words. 
	 * @param inputTrees is an individual input file in tree form
	 * @param dictionaryTree is an individual dictionary file in tree form
	 * @return incorrectWords is a list of words in input file that are not in the dictionary file
	 */
	public static ArrayList<String> listIncorrectWords(Set<String> inputTrees, Set<String> dictionaryTree) 
	{
		ArrayList<String> incorrectWords = new ArrayList<String>();
	
		for(String word : inputTrees) 
		{
			if(wordIsUnique(word, dictionaryTree)) 
			{
				incorrectWords.add(word);
			}
		}
		
		return incorrectWords;
	}
	
	/**
	 * Checks if a word does not exist in a dictionary. This
	 * is basically a customized version of "contains".
	 * @param word is an individual word to check
	 * @param dictionaryTree is an individual dictionary file in tree form
	 * @return boolean telling whether the word is unique or not
	 */
	public static boolean wordIsUnique(String word, Set<String> dictionaryTree)
	{
		return !dictionaryTree.contains(word);
	}

	/**
	 * The methods in this class allow the JTree component to traverse
	 * the file system tree, and display the files and directories.
	 * Search through the dictionary for a word.
	 * @param inputTress is a list of input files in tree form
	 * @param dictionaryTrees is a list of dictionary files in tree form
	 * @return inputs is a structure containing all input missing words for each input-dictionary file combination
	 */
	public static ArrayList<ArrayList<ArrayList<String>>> incorrectWords(ArrayList<Set<String>> inputTrees, ArrayList<Set<String>> dictionaryTrees)
	{
		ArrayList<ArrayList<ArrayList<String>>> inputs = new ArrayList<ArrayList<ArrayList<String>>>();
		ArrayList<ArrayList<String>> dictionaries;
		ArrayList<String> incorrectWords;

		for(Set<String> inputTree : inputTrees)
		{
			dictionaries = new ArrayList<ArrayList<String>>();

			for(Set<String> dictionaryTree : dictionaryTrees)
			{
				incorrectWords = listIncorrectWords(inputTree, dictionaryTree);
				
				dictionaries.add(incorrectWords);
			}

			inputs.add(dictionaries);
		}

		return inputs;
	}
}


