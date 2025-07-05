package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {
	// Create the scanner for user input
	private Scanner scanner = new Scanner(System.in);
	// Create the project service
	private ProjectService projectService = new ProjectService();
	// Create the current project
	private Project curProject;

	// Store list of menu options

	// @formatter:off
			private List<String> operations = List.of(
					"1) Add a project",
					"2) List projects",
					"3) Select a project"
					
					);
			// @formatter:on

	// Main method
	public static void main(String[] args) {
		// Create a new Projects application
		new ProjectsApp().proccessUserSelections();
	}

	// Process and display the user selections in console
	private void proccessUserSelections() {
		// Variable is initially false
		boolean done = false;

		// Loop until the variable is true
		while (!done) {

			// Test for errors in method with try/catch
			try {
				// Get the user selection which is an integer
				int selection = getUserSelection();

				// Process the user selections
				switch (selection) {
				// Exit the menu if selection is -1
				case -1:
					done = exitMenu();
					break;
				// Create a project if selection is 1
				case 1:
					createProject();
					break;
				// List projects if selection is 2	
				case 2:
					listProjects();
					break;
				// Select a project if selection is 3
				case 3:
					selectProject();
					break;
					
				// By default, print string user must make a selection
				default:
					System.out.println("\n" + selection + " is not a valid selection. Try again.");
				}
			}

			/*
			 * If error occurs, define block of code and print error message
			 */
			catch (Exception e) {
				System.out.println("\nError: " + e + " Try again.");
			}
		}

	}
	// Create the selectProject method
	private void selectProject() {
		// Method to list projects
		listProjects();
		// Prompt user to enter a project ID
		Integer projectId = getIntInput("Enter a project ID to select a project");
		// Un-select the current project
		curProject = null;
		// Throws an exception if an invalid project ID is entered
		curProject = projectService.fetchProjectById(projectId);
		
	}
	// Create the listProjects method
	private void listProjects() {
		// Utilize a collection of List for the projects and fetch the list
		List<Project> projects = projectService.fetchAllProjects();
		// Print out List header to console
		System.out.println("\nProjects:");
		// Print out a colon after the printed project name(s)
		projects.forEach(project -> System.out
				.println("  " + project.getProjectId() + ": " + project.getProjectName()));

	}

	// Gather the project details from the user
	private void createProject() {
		// Prompt for user to enter project name as a string
		String projectName = getStringInput("Enter the project name");
		// Prompt for user to enter estimated hours as a BigDecimal
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		// Prompt for user to enter actual hours as a BigDecimal
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		// Prompt for user to enter project difficulty as a Integer
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		// Prompt for user to enter project as a String
		String notes = getStringInput("Enter the project notes");

		// Create Project variable
		Project project = new Project();

		// Call the appropriate setters
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);

		/*
		 * Call the addProject method on the projectService object and pass in Project
		 * object
		 */
		Project dbProject = projectService.addProject(project);
		// Print the success message to the console
		System.out.println("You have successfully created project: " + dbProject);
	}

	// Method to get user BigDecimal input
	private BigDecimal getDecimalInput(String prompt) {
		// Print the prompt
		String input = getStringInput(prompt);

		// Test if the value is null and if so, return null
		if (Objects.isNull(input)) {
			return null;
		}
		// Test that the value can be converted to integer
		try {
			return new BigDecimal(input).setScale(2);
		}
		// If conversion not possible, throw exception
		catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number. Try again.");
		}

	}

	// Exit the menu and print message confirmation
	private boolean exitMenu() {
		System.out.println("Exiting the menu.");
		return true;
	}

	// Call printOperations method and accept user input as integer
	private int getUserSelection() {
		// Call the printOperations method
		printOperations();

		// Get user input and pass the string literal
		Integer input = getIntInput("Enter a menu selection");

		/*
		 * Check to see if user value is null, otherwise return the input
		 */
		return Objects.isNull(input) ? -1 : input;
	}

	// Create method that accepts user input and convert to integer
	private Integer getIntInput(String prompt) {
		// Print
		String input = getStringInput(prompt);

		// Test if the value is null and if so, return null
		if (Objects.isNull(input)) {
			return null;
		}
		// Test that the value can be converted to integer
		try {
			return Integer.valueOf(input);
		}
		// If conversion not possible, throw exception
		catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number. Try again.");
		}
	}

	// Print the prompt and get user input
	private String getStringInput(String prompt) {
		// Print colon and space before user input
		System.out.print(prompt + ": ");
		// Scan entire line of input and go to next line
		String input = scanner.nextLine();
		// If user input is blank, return null
		return input.isBlank() ? null : input.trim();
	}

	// Print operations
	private void printOperations() {
		// Print string literal for available selections
		System.out.println("\nThese are the available selections. Press the Enter key to quit:");

		/*
		 * Print all available menu selections using Lambda, and indent each line
		 */
		operations.forEach(line -> System.out.println(" " + line));
		// If the current project is null, print a message to console
		if(Objects.isNull(curProject)) {
			System.out.println("\nYou are not working with a project.");
		} else {
			// Otherwise print a message for project user is working with
			System.out.println("\nYou are working with project: " + curProject);
		}
	}

}
