package projects.service;

import java.util.List;
import java.util.NoSuchElementException;

import projects.dao.ProjectDao;
import projects.entity.Project;

public class ProjectService {
	// Create the Data Access Object
	private ProjectDao projectDao = new ProjectDao();

	// Create the add method to add projects
	public Project addProject(Project project) {

		// Return the value from the DAO
		return projectDao.insertProject(project);
	}
	// Returns a list of project records
	public List<Project> fetchAllProjects() {
		return projectDao.fetchAllProjects();
	}
	// Call the DAO to get all all project details
	public Project fetchProjectById(Integer projectId) {
		return projectDao.fetchProjectById(projectId).orElseThrow(
				() -> new NoSuchElementException(
						"Project with project ID=" + projectId
							+ " does not exist."));
	}
}
