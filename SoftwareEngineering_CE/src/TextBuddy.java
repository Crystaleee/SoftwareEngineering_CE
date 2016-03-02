/********************************************
 * This class is to manipulate text in a file. A user can add or delete items in the file.
 * The command format is given by the example interaction below:
 * 
 * 		Welcome to TextBuddy. new.txt is ready for use.
 * 		[command] add (some texts): add texts into the file
 * 		[command] delete (a number): delete an item from the file
 * 		[command] display: display all the items in the file
 *	 	[command] clear: clear all the texts from the file
 *		[command] sort: sort all the lines in the file
 *		[command] search: search for a word 
 *		[command] exit: exit the program
 * 		Command: display
 *		new.txt is empty.
 * 		Command: add the first item
 * 		Added to new.txt: "the first item"
 * 		Command: add the second item
 * 		Added to new.txt: "the second item"
 * 		Command: display
 * 		1. the first item
 * 		2. the second item
 * 		Command: delete 1
 * 		Deleted from new.txt: "the first item"
 * 		Command: display
 * 		2. the second item
 * 		Command: clear
 * 		Command: All content deleted from new.txt
 * 		Command: display
 * 		new.txt is empty.
 * 		Command: exit
 * 
 * @author  Zhu Bingjing
 * @date 2016/1/26 2:07:58 p.m 
 * @assumptions about program behavior:
 * 		1) The file will be saved to the disk after each user operation.
 * 		2) If the file folder/path doesn't exit, print out ERR_MESSAGE_INVALID_FILE.
 * 		3) If the file doesn't exist, a new file with the same file name will be created.
 * 		4) If the parameter given when running the program contains more or less than one 
 * 			character string, ERR_MESSAGE_ARGUMENTS will be printed.
 * 		5) There are altogether 5 legal ERR_MESSAGE_INVALID_ARGUMENTS: add (some texts), delete (an index), display, clear, exit.
 * 			Any other wrong commands will lead to ERR_MESSAGE_INVALID_COMMAND.
 * 		6) Delete command will delete the item with certain index which can be retrieved from display.
 * 		7) Add command will attach the texts with the integer after the existing biggest one.
 * 		8) If the index to be deleted is not found, ERR_MESSAGE_INDEX_NOT_FOUND will be
 * 			 printed.		
 * ******************************************* */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class TextBuddy {
	// collection of command type
	enum CommandType {
		ADD, DELETE, CLEAR, DISPLAY, SORT, EXIT, SEARCH, INVALID
	};

	// err message
	private static final String ERR_MESSAGE_INVALID_FILE = "The File cannnot be opened.";
	private static final String ERR_MESSAGE_INVALID_ARGUMENTS = "Invalid arguments!";
	private static final String ERR_MESSAGE_INVALID_COMMAND = "Invalid commands!";
	private static final String ERR_MESSAGE_INDEX_NOT_FOUND = "Index not found.";

	// feedback message
	private static final String MESSAGE_INPUT = "Command: ";
	private static final String MESSAGE_SEARCH_DONE = "Search done!";
	private static final String MESSAGE_CLEAR_DONE = "All content deleted from "
			+ "%s";
	private static final String MESSAGE_DELETE_DONE = "Deleted from " + "%s"
			+ ": \"" + "%s" + "\"";
	private static final String MESSAGE_FILE_EMPTY = "%s" + " is empty.";
	private static final String MESSAGE_ADD_DONE = "Added to " + "%s" + ": \""
			+ "%s" + "\"";
	private static final String MESSAGE_WELCOME = "Welcome to TextBuddy. "
			+ "%s" + " is ready for use." + "\n"
			+ "[command] add (some texts): add texts into the file" + "\n"
			+ "[command] delete (a number): delete an item from the file"
			+ "\n" + "[command] display: display all the items in the file"
			+ "\n" + "[command] clear: clear all the texts from the file"
			+ "\n" + "[command] exit: exit the program";

	// This is the file name of the file to be manipulated
	static String fileName = new String();

	// This List is used to store all the items locally
	private static List<String> texts = new ArrayList<String>();
	// This List is used to store the current list that user is viewing
	private static List<String> viewList = new ArrayList<String>();

	// This is the legal length of input arguments when running the program
	private static final int PARAM_SIZE = 1;

	// These are the locations at which various parameters will appear in a
	// command
	private static final int PARAM_POSITION_DELETE_INDEX = 1;

	// These are used to indicate that command after "delete" is not an integer
	// and item not found
	private static final int NOT_INTEGER = -1;
	private static final int ITEM_NOT_FOUND = -1;

	// These are the specific commands of all types
	private static ArrayList<String> addCommands = new ArrayList<String>(
			Arrays.asList("add", "+"));
	private static ArrayList<String> deleteCommands = new ArrayList<String>(
			Arrays.asList("delete", "del", "-"));
	private static ArrayList<String> displayCommands = new ArrayList<String>(
			Arrays.asList("display", "show"));
	private static ArrayList<String> clearCommands = new ArrayList<String>(
			Arrays.asList("clear", "clr"));
	private static ArrayList<String> exitCommands = new ArrayList<String>(
			Arrays.asList("exit", "quit", "bye"));
	private static ArrayList<String> sortCommands = new ArrayList<String>(
			Arrays.asList("sort", "queue"));
	private static ArrayList<String> searchCommands = new ArrayList<String>(
			Arrays.asList("search", "find"));

	/*
	 * Declared for the whole class to facilitate automated testingusing the I/O
	 * redirection technique
	 */
	static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) throws IOException {
		checkArgSize(args);
		readLegalFile(args);
		printMessage();
		//excuteUntilExit();
	}

	/**
	 * Read the commands and excute until exit
	 * 
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	private static void excuteUntilExit() throws IOException {
		while (true) {
			promtInput();
			String input = getCommand();
			String feedback = doCommand(input);
			showToUser(feedback);
		}
	}

	private static void showToUser(String feedback) {
		System.out.println(feedback);
	}

	/**
	 * get user command from scanner
	 * 
	 * @return
	 */
	private static String getCommand() {
		String input = scanner.nextLine();
		return input;
	}

	/**
	 * promt message to let user enter command
	 */
	private static void promtInput() {
		System.out.print(MESSAGE_INPUT);
	}

	/**
	 * decide what kind of command it is
	 */
	private static CommandType switchCommandType(String command) {
		if (addCommands.contains(command)) {
			return CommandType.ADD;
		} else if (deleteCommands.contains(command)) {
			return CommandType.DELETE;
		} else if (displayCommands.contains(command)) {
			return CommandType.DISPLAY;
		} else if (clearCommands.contains(command)) {
			return CommandType.CLEAR;
		} else if (exitCommands.contains(command)) {
			return CommandType.EXIT;
		} else if (sortCommands.contains(command)) {
			return CommandType.SORT;
		} else if (searchCommands.contains(command)) {
			return CommandType.SEARCH;
		} else
			return CommandType.INVALID;
	}

	/**
	 * switch and do the commands
	 * 
	 * @param input
	 * @throws IOException
	 */
	static String doCommand(String input) throws IOException {
		String feedback;
		String[] commands = input.split(" ");
		CommandType commandType = switchCommandType(commands[0]);
		switch (commandType) {
		case ADD:
			feedback = addText(input);
			break;
		case DELETE:
			feedback = deleteText(commands);
			break;
		case DISPLAY:
			feedback = displayText(input);
			break;
		case CLEAR:
			feedback = clearText(input);
			break;
		case SORT:
			feedback = sortText(input);
			break;
		case SEARCH:
			feedback = searchText(input);
			break;
		case EXIT:
			System.exit(0);
		default:
			feedback = (ERR_MESSAGE_INVALID_COMMAND);
		}
		return feedback;
	}

	private static boolean isOneWordCommand(String input) {
		if (input.split(" ").length == 1) {
			return true;
		} else
			return false;
	}

	/**
	 * search for a word in the file and return the lines containing that word.
	 * 
	 * @return
	 * 
	 * @throws IOException
	 *             If an I/O error occurs during writeFile()
	 */
	static String searchText(String input) {
		String keyword = getItemContent(input);
		List<String> result = searchKeyword(keyword);
		viewList = result;
		displayList();
		return MESSAGE_SEARCH_DONE;
	}

	private static void displayList() {
		for (int i = 0; i < viewList.size(); i++) {
			System.out.println((i + 1) + ". " + viewList.get(i));
		}
	}

	/**
	 * @param keyword
	 * @return
	 */
	private static List<String> searchKeyword(String keyword) {
		List<String> searchResult = new ArrayList<String>();
		for (String item : texts) {
			if (item.contains(keyword)) {
				searchResult.add(item);
			}
		}
		return searchResult;
	}

	/**
	 * sort lines alphabetically
	 * 
	 * @param input
	 * @return
	 * 
	 * @throws IOException
	 *             If an I/O error occurs during writeFile()
	 */
	static String sortText(String input) throws IOException {
		if (input != null && !isOneWordCommand(input)) {
			return ERR_MESSAGE_INVALID_COMMAND;
		}
		Collections.sort(texts);
		writeFile();
		return displayText(null);
	}

	/**
	 * Clear all the items in List<String> texts and the file
	 * 
	 * @param input
	 * @return
	 * 
	 * @throws IOException
	 *             If an I/O error occurs during writeFile()
	 */
	static String clearText(String input) throws IOException {
		if (input != null && !isOneWordCommand(input)) {
			return ERR_MESSAGE_INVALID_COMMAND;
		}
		texts.clear();
		writeFile();
		return (String.format(MESSAGE_CLEAR_DONE, fileName));
	}

	/**
	 * Delete item with certain index and write the file
	 * 
	 * @param commands
	 *            The command to delete an item
	 * @return
	 * @throws IOException
	 *             If an I/O error occurs during isFoundandDeleted
	 */
	static String deleteText(String[] commands) throws IOException {
		int index = -1;
		index = testInteger(commands[PARAM_POSITION_DELETE_INDEX], index);
		if (index == NOT_INTEGER) {
			return (ERR_MESSAGE_INVALID_COMMAND);
		}

		if (checkIndex(index) == ITEM_NOT_FOUND) {
			return (ERR_MESSAGE_INDEX_NOT_FOUND);
		}

		index = index - 1;
		String deleteItemContent = viewList.get(index);
		texts.remove(deleteItemContent);
		writeFile();
		return (String.format(MESSAGE_DELETE_DONE, fileName, deleteItemContent));

	}

	/**
	 * @param commands
	 * @param index
	 * @return
	 */
	private static int testInteger(String commandIndex, int index) {
		try {
			index = Integer.parseInt(commandIndex);
		} catch (Exception e) {
			return NOT_INTEGER;
		}
		return index;
	}

	/**
	 * return the found index, otherwise -1
	 * 
	 * @param index
	 * @return
	 */
	private static int checkIndex(int index) {
		if (index <= texts.size()) {
			return index;
		}
		return ITEM_NOT_FOUND;
	}

	/**
	 * Display all the items in List<String> texts
	 * 
	 * @param input
	 * @return
	 */
	static String displayText(String input) {
		if (input != null && !isOneWordCommand(input)) {
			return ERR_MESSAGE_INVALID_COMMAND;
		}
		// change the list to be viewed to texts
		viewList = texts;
		if (viewList.isEmpty()) {
			return (String.format(MESSAGE_FILE_EMPTY, fileName));
		} else {
			displayList();
		}
		return "";
	}

	/**
	 * Add item in the List<String> texts and write all the items into the file
	 * 
	 * @param input
	 *            user's commands to add texts
	 * @throws IOException
	 *             If an I/O error occurs during writeFile()
	 */
	static String addText(String input) throws IOException {
		String addItemContent = getItemContent(input);
		texts.add(addItemContent);
		writeFile();
		return (String.format(MESSAGE_ADD_DONE, fileName, addItemContent));
	}

	/**
	 * @param input
	 * @return
	 */
	private static String getItemContent(String input) {
		int position = input.indexOf(" ");
		String ItemContent = input.substring(position + 1);
		return ItemContent;
	}

	/**
	 * Write all the items in List<String> texts into the file
	 * 
	 * @throws IOException
	 *             If an I/O error occurs during operations of bw and fos
	 */
	private static void writeFile() throws IOException {
		FileOutputStream fos = new FileOutputStream(fileName);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		for (String text : texts) {
			bw.write(text);
			bw.newLine();
		}
		bw.flush();
		fos.close();
		bw.close();
	}

	/**
	 * Read the file, store all the items in List<String> texts, store the last
	 * index in lastIndex
	 * 
	 * @throws IOException
	 *             If an I/O error occurs during readLine()
	 */
	static int readFile() {
		BufferedReader br;
		int lineNum = 0;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					fileName)));
			String lineStr = br.readLine();
			while (lineStr != null) {
				lineNum++;
				//texts.add(lineStr);
				lineStr = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			System.out.println(ERR_MESSAGE_INVALID_FILE);
			System.exit(0);
		}
		return lineNum;
	}

	/**
	 * Print usage and welcome messages
	 */
	private static void printMessage() {
		System.out.println(String.format(MESSAGE_WELCOME, fileName));
	}

	/**
	 * Judge arguments and read legal file
	 * 
	 * @param args
	 *            the parameter (file) that user input
	 */
	private static void readLegalFile(String[] args) {
		checkFileValidity(args);
		readFile();
	}

	/**
	 * check the file exists or can be created, otherwise exit
	 * 
	 * @param args
	 */
	private static void checkFileValidity(String[] args) {
		File file = new File(args[0]);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fileName = args[0];
		} catch (IOException e) {
			System.out.println(ERR_MESSAGE_INVALID_FILE);
			System.exit(0);
		}
	}

	/**
	 * check if the input argument size is legal, exit if not
	 * 
	 * @param args
	 */
	private static void checkArgSize(String[] args) {
		if (args.length != PARAM_SIZE) {
			System.out.println(ERR_MESSAGE_INVALID_ARGUMENTS);
			System.exit(0);
		}
	}

}
