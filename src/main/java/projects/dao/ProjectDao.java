package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;

// Create the method using the JDBC to perform CRUD operations on the project tables
public class ProjectDao extends DaoBase {
	// Create constants for each table
	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";

	// Method to do a transaction with the database
	public Project insertProject(Project project) {
		// formatter:off
		String sql = "" + "INSERT INTO " + PROJECT_TABLE + " "
				+ "(project_name, estimated_hours, actual_hours, difficulty, notes) " + "VALUES " + "(?, ?, ?, ?, ?)";

		// Prepare to transact with the database and check for errors
		// @formatter:on
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			// Set the project parameters
			// Surround with a try/catch
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				// After parameters are set, do the transaction
				stmt.executeUpdate();
				// Commit data to the table
				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				commitTransaction(conn);
				// Return the data
				project.setProjectId(projectId);
				return project;
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}
	// Method to fetch project list via MySQL
	public List<Project> fetchAllProjects() {
	    String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";
	    // Surround connection with try/catch for any errors
	    try(Connection conn = DbConnection.getConnection()) {
	      startTransaction(conn);
	      // Prepare statement and execute the query, surround with try/catch
	      try(PreparedStatement stmt = conn.prepareStatement(sql)) {
	        try(ResultSet rs = stmt.executeQuery()) {
	          List<Project> projects = new LinkedList<>();
	          // Loop through the projects
	          while(rs.next()) {
	            projects.add(extract(rs, Project.class));
	          }
	          // Return projects
	          return projects;
	        }
	      }
	      catch(Exception e) {
	        rollbackTransaction(conn);
	        throw new DbException(e);
	      }
	    }
	    catch(SQLException e) {
	      throw new DbException(e);
	    }
	  }
	  // Method to fetch projects by ID via MySQL
	  public Optional<Project> fetchProjectById(Integer projectId) {
	    String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
	    // Connect to DB and surround with try/catch for errors
	    try(Connection conn = DbConnection.getConnection()) {
	      startTransaction(conn);
	      // Prepare statement and execute the query, surround with try/catch
	      try {
	    	// Make the project initially null
	        Project project = null;
	     // Prepare statement and execute the query, surround with try/catch
	        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
	        	setParameter(stmt, 1, projectId, Integer.class);
	        	// Get the table and execute query, surround with try/catch
	        	try(ResultSet rs = stmt.executeQuery()) {
	        		if(rs.next()) {
	        			project = extract(rs, Project.class);
	        		}
	        	}
	        }
	        // Add 3 method calls for project attributes
	        if(Objects.nonNull(project)) {
	        	project.getMaterials().addAll(fetchMaterialsForProjects(conn, projectId));
	        	project.getSteps().addAll(fetchStepsForProject(conn, projectId));
	        	project.getCategories().addAll(fetchCategoriesForProject(conn, projectId));
	        }
	        // Commit the transaction
	        commitTransaction(conn);
	        return Optional.ofNullable(project);
	        
	      }
	      // Roll back the transaction if there are any errors
	      catch(Exception e) {
	    	  rollbackTransaction(conn);
	    	  throw new DbException(e);
	      }
	    }
	    catch(SQLException e) {
	    	throw new DbException(e);
	    }
	  }
	// Method to fetch projects by category via MySQL
	private List<Category> fetchCategoriesForProject(Connection conn,
			Integer projectId) throws SQLException {
		// @formatter:off
	    String sql = ""
	    	// When fetching categories, join with project_category table
	        + "SELECT c.* FROM " + CATEGORY_TABLE + " c "
	        + "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) "
	        + "WHERE project_id = ?";
	    // @formatter:on
	    // Prepare statement and execute the query, surround with try/catch
	    try(PreparedStatement stmt = conn.prepareStatement(sql)) {
	        setParameter(stmt, 1, projectId, Integer.class);
	     // Get the table and execute query, surround with try/catch
	        try(ResultSet rs = stmt.executeQuery()) {
	          // Get the list of categories
	          List<Category> categories = new LinkedList<>();
	          // Loop through categories 
	          while(rs.next()) {
	            categories.add(extract(rs, Category.class));
	          }
	          // Return the categories
	          return categories;
	        }
	      }
	      catch(SQLException e) {
	        throw new DbException(e);
	      }
	}
	  // Method to fetch project steps via MySQL
	  private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException {
		    String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";
		    // Prepare statement and execute the query, surround with try/catch
		    try(PreparedStatement stmt = conn.prepareStatement(sql)) {
		      setParameter(stmt, 1, projectId, Integer.class);
			  // Get the table and execute query, surround with try/catch
		      try(ResultSet rs = stmt.executeQuery()) {
		        List<Step> steps = new LinkedList<>();
		        // Loop through steps 
		        while(rs.next()) {
		          steps.add(extract(rs, Step.class));
		        }
		        // Return steps
		        return steps;
		      }
		    }
		  }
	// Method to fetch materials steps via MySQL
	private List<Material> fetchMaterialsForProjects(Connection conn, Integer projectId) 
		throws SQLException {
	  String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";
	  // Prepare statement and execute the query, surround with try/catch
	  try(PreparedStatement stmt = conn.prepareStatement(sql)) {
		  setParameter(stmt, 1, projectId, Integer.class);
		// Get the table and execute query, surround with try/catch
		  try(ResultSet rs = stmt.executeQuery()) {
			  List<Material> materials = new LinkedList<>();
			  // Loop through steps 
			  while(rs.next()) {
				  materials.add(extract(rs, Material.class));
			  }
			  // Return materials
			  return materials;
		  }
	  }
	}
	  
	  
}
