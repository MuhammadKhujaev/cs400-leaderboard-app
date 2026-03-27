import java.util.Scanner;
import java.util.*;
import java.io.*;

public class Frontend implements FrontendInterface {
private Scanner in;
private BackendInterface backend;
    public Frontend(Scanner in, BackendInterface backend) {
	this.in = in;
	this.backend = backend;
    }
    /**
     * Displays instructions for the syntax of user commands.  And then 
     * repeatedly gives the user an opportunity to issue new commands until
     * they enter "quit".  Uses the evaluateSingleCommand method below to
     * parse and run each command entered by the user.  If the backend ever
     * throws any exceptions, they should be caught here and reported to the
     * user.  The user should then continue to be able to issue subsequent
     * commands until they enter "quit".  This method must use the scanner
     * passed into the constructor to read commands input by the user.
     */
    @Override
    public void runCommandLoop() {
	showCommandInstructions();
	while (true) {
		if (!in.hasNextLine())
			return;
		String command = in.nextLine().trim();
		if (command.equalsIgnoreCase("quit"))
			return;
		try {
			processSingleCommand(command);
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
    }

    /**
     * Displays instructions for the user to understand the syntax of commands
     * that they are able to enter.  This should be displayed once from the
     * command loop, before the first user command is read in, and then later
     * in response to the user entering the command: help.
     *
     * The lowercase words in the following examples are keywords that the
     * user must match exactly in their commands, while the upper case words
     * are placeholders for arguments that the user can specify.  The following
     * are examples of valid command syntax that your frontend should be able
     * to handle correctly.
     *
     * submit NAME CONTINENT SCORE DAMAGE_TAKEN COLLECTABLES COMPLETION_TIME
     * submit multiple FILEPATH
     * collectables MAX
     * collectables MIN to MAX
     * location CONTINENT
     * show MAX_COUNT
     * show fastest times
     * help
     * quit
     */
    @Override
    public void showCommandInstructions() {
	System.out.println("User command instructions");
	System.out.println("-------------------------");
	System.out.println("lowercase words are keywords that specify the command");
	System.out.println("UPPERCASE words specify the arguments that go with the command");
	System.out.println();
	System.out.println("Available Commands");
	System.out.println("------------------");
	System.out.println("submit NAME CONTINENT SCORE DAMAGE_TAKEN COLLECTABLES COMPLETION_TIME");
	System.out.println(" - The backend will add a new record with the arguments");
	System.out.println("submit multiple FILEPATH");
	System.out.println(" - The backend will load the data from the specific FILEPATH");
	System.out.println("collectables MAX");
	System.out.println(" - Updates the backend's range of records to return");
	System.out.println("collectables MIN to MAX");
	System.out.println(" - Updates the backend's range of records to return a range between the MIN and MAX");
	System.out.println("location CONTINENT");
	System.out.println(" - Updates the backend's filter criteria show it just shows the records in CONTINENT");
	System.out.println("show MAX_COUNT");
	System.out.println(" - displays a list of records with currently set threshold and filters where MAX_COUNT limits the number of record names displayed to the first MAX_COUNT in the list returned from the backend");
	System.out.println("show fastest times");
	System.out.println("- displays the results returned from the backend's getTopTen method");
	System.out.println("help");
	System.out.println("- Displays the command instructions that you are seeing right now");
	System.out.println("quit");
	System.out.println("- ends this program");
    }

    /**
     * This method takes a command entered by the user as input. It parses
     * that command to determine what kind of command it is, and then makes
     * use of the backend (which was passed to the constructor) to update the
     * state of that backend.  When a show or help command is issued, this
     * method prints the appropriate results to standard out.  When a command 
     * does not follow the syntax rules described above, this method should 
     * print out an error message that describes at least one defect in the 
     * syntax of the provided command argument.
     * 
     * Some notes on the expected behavior of the different commands:
     *     submit : results in backend adding a new record with the specific NAME, CONTINENT,
     *          SCORE, DAMAGE_TAKEN, COLLECTABLES, COMPLETION_TIME
     *          COMPLETION_TIME is of the format "hhh:mm:ss"
     *     submit multiple: results in backend loading data from specified path
     *     collectables: updates backend's range of records to return
     *                 should not result in any records being displayed
     *     location: updates backend's filter criteria
     *                   should not result in any records being displayed
     *     show: displays list of records with currently set thresholds and filters
     *           MAX_COUNT: argument limits the number of record names displayed
     *           to the first MAX_COUNT in the list returned from backend
     *           fastest times: argument displays results returned from th     *           backend's getTopTen method
     *     help: displays command instructions
     *     quit: ends this program (handled by runCommandLoop method above)
     *           (do NOT use System.exit(), as this will interfere with tests)
     */
    @Override
    public void processSingleCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            return;
        }
        String[] parts = command.trim().split("\\s+");
        try {
            switch (parts[0]) {
                case "submit":
                    if (parts.length >= 2 && parts[1].equals("multiple")) {
                        if (parts.length < 3) {
                            System.out.println("Error: Third argument in submit multiple must be a filepath");
                        } else {
                            try {
                                backend.readData(parts[2]);
                            } catch (IOException e) {
                                System.out.println("Error loading file: " + e.getMessage());
                            }
                        }
                    }
                    else {
                        if (parts.length < 7) {
                            System.out.println("Error: Invalid number of arguments for submit");
                        } else {
                            try {
                                String name = parts[1];
                                GameRecord.Continent continent = GameRecord.Continent.valueOf(parts[2].toUpperCase());
                                int score = Integer.parseInt(parts[3]);
                                int damage_taken = Integer.parseInt(parts[4]);
                                int collectables = Integer.parseInt(parts[5]);
                                String time = parts[6];

                                backend.addRecord(new GameRecord(name, continent, score, damage_taken, collectables, time));
                            } catch (NumberFormatException e) {
                                System.out.println("Error: Score, Damage, or Collectables must be valid integers");
                            } catch (IllegalArgumentException e) {
                                System.out.println("Error: Invalid Continent");
                            }
                        }
                    }
                    break;

                case "collectables":
                    try {
                        if (parts.length == 4 && parts[2].equals("to")) {
                            int min = Integer.parseInt(parts[1]);
                            int max = Integer.parseInt(parts[3]);
                            backend.getAndSetRange(min, max);
                        }
                        else if (parts.length == 2) {
                            int max = Integer.parseInt(parts[1]);
                            backend.getAndSetRange(0, max);
                        } else {
                            System.out.println("Error: Usage is 'collectables MAX' or 'collectables MIN to MAX'");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Arguments for collectables must be Integers");
                    }
                    break;

                case "location":
                    if (parts.length < 2) {
                        System.out.println("Error: Location requires a continent argument");
                    } else {
                        try {
                            GameRecord.Continent continent = GameRecord.Continent.valueOf(parts[1].toUpperCase());
                            backend.applyAndSetFilter(continent);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error: First argument must be a valid GameRecord.Continent");
                        }
                    }
                    break;

                case "show":
                    if (parts.length >= 2) {
                        if (parts.length >= 3 && parts[1].equals("fastest") && parts[2].equals("times")) {
                            java.util.List<String> display = backend.getTopTen();
                            for (int i = 0; i < display.size(); i++) {
                                System.out.println((i + 1) + " " + display.get(i));
                            }
                        } else {
                            try {
                                int maxCount = Integer.parseInt(parts[1]);
                                java.util.List<String> display = backend.getAndSetRange(0, maxCount); 
                                int limit = Math.min(display.size(), maxCount);
                                for (int i = 0; i < limit; i++) {
                                    System.out.println((i + 1) + " " + display.get(i));
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Error: MAX_COUNT must be of type Integer");
                            }
                        }
                    } else {
                        System.out.println("Error: Usage is 'show <count>' or 'show fastest times'");
                    }
                    break;

                case "help":
			showCommandInstructions();
                    break;

                default:
                    System.out.println("Error: Unknown command");
            }
        } catch (Exception e) {
            System.out.println("Error processing command: " + e.getMessage());
        }
    }
}
