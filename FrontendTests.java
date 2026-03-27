import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.util.*;

public class FrontendTests {
	/**
	 * This test runs the submit command, show fastest times command and quit command. 
         */
	@Test
	public void roleTest1() {
		// Input that contains the commands that we are testing
		String input = "submit multiple records.csv\n" +
				"show fastest times\n" +
				"quit\n";
		// Initialize needed placeholders and objects
		TextUITester tester = new TextUITester(input);
		Tree_Placeholder tree = new Tree_Placeholder();
		Backend_Placeholder backend = new Backend_Placeholder(tree);
		Scanner scnr = new Scanner(System.in);
		Frontend frontend = new Frontend(scnr, backend);
		// Run the loop so we can get the commands
		frontend.runCommandLoop();

		String output = tester.checkOutput();
		// Assertion that output contains voidR1fter, which was hardcoded into the backend placeholder
		Assertions.assertTrue(output.contains("voidR1fter"),
		"Output should contain 'voidR1fter' (hardcoded in backend) after submitting a file.");
	}
	
	/**
	 * This test tests the submit with seven arguments, collectables with 2
	 * arguments.
	 */
	@Test
	public void roleTest2() {
		// Input
		String input = "submit PlayerOne NORTH_AMERICA 1000 10 5 10:00:00\n" + 
				"collectables 0 to 60000\n" +
				"location NORTH_AMERICA\n" +
				"show fastest times \n" + 
				"quit\n" ;
		// Initialize placeholders and objects
		TextUITester tester = new TextUITester(input);
		Tree_Placeholder tree = new Tree_Placeholder();
		Backend_Placeholder backend = new Backend_Placeholder(tree);
		Scanner scnr = new Scanner(System.in);
		Frontend frontend = new Frontend(scnr, backend);
		// Run the command loop
		frontend.runCommandLoop();

		String output = tester.checkOutput();
		// ne0nVandal is hardoded into the backend placeholder
		Assertions.assertTrue(output.contains("ne0nVandal"),
		"Output should contain 'ne0nVandal' (hardcoded in backend) after single submit.");
		// Shouldn't contain an error because are collectables and location are valid
		Assertions.assertFalse(output.contains("Error"),
		"Output should not contain 'Error' for valid collectables/location commands. ");
	}
	/**
	 * This test tests the the submit multiple records, show, and quit
	 */
	@Test
	public void roleTest3() {
		// Input
		String input = "submit multiple records.csv\n" +
				"show 60003\n" + 
				"quit\n";
		// Placeholders and objects
		TextUITester tester = new TextUITester(input);
		Tree_Placeholder tree = new Tree_Placeholder();
        	Backend_Placeholder backend = new Backend_Placeholder(tree);
	        Scanner scnr = new Scanner(System.in);
        	Frontend frontend = new Frontend(scnr, backend);
		// Run the command loop
	        frontend.runCommandLoop();

        	String output = tester.checkOutput();
		// speedRoyalty is from the tree placeholder
		Assertions.assertTrue(output.contains("speedRoyalty"), 
            "Output should contain 'speedRoyalty' (from Tree_Placeholder) when range is set high enough.");
		// Hardcoded into backend placeholder
		Assertions.assertTrue(output.contains("voidR1fter"),
            "Output should also contain 'voidR1fter' from the file load.");
	}
}
