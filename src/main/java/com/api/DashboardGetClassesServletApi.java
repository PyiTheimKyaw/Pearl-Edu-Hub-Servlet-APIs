package com.api;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.sql.*;

import com.database.DatabaseConnection;
import com.google.gson.Gson;
import com.models.ClassModel;
import com.models.LectureModel;

@WebServlet("/get-classes")
public class DashboardGetClassesServletApi extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// SQL query to retrieve class information along with lecture information
		String sqlQuery = "SELECT c.class_id, c.class_name,c.class_information,c.start_date,c.end_date,c.fees, l.id ,l.name "
				+ "FROM classes c " + "INNER JOIN class_lecture cl ON c.class_id = cl.class_id "
				+ "INNER JOIN lectures l ON cl.lecture_id = l.id";

		// Set content type and character encoding
//		response.setCharacterEncoding("UTF-8");
//		response.setHeader("Access-Control-Allow-Origin", "https://pyitheimkyaw.github.io/");
////		response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
//		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
////		response.setHeader("Access-Control-Max-Age", "86400");
//		response.setHeader("Content-Type", "application/json");
		
		// Allow specific HTTP methods
//		response.setHeader("Access-Control-Allow-Origin", "https://pyitheimkyaw.github.io/");
//		response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
//
//        // Allow specific headers
//		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
//
//        // Allow credentials (if needed)
//		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		response.setHeader("Content-Type", "application/json");

		PrintWriter out = response.getWriter();

		try {
			Connection connection = DatabaseConnection.getConnection();
			// Create prepared statement with the SQL query
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			// Execute the query and get the result set
			ResultSet resultSet = statement.executeQuery();
			// Map to store class info with lectures
			// Map to store class info with lectures
			Map<Integer, ClassModel> classMap = new HashMap<>();

			// Iterate through the result set
			while (resultSet.next()) {
				// Retrieve class and lecture information
				int classId = resultSet.getInt("class_id");
				String className = resultSet.getString("class_name");
				String classInfo = resultSet.getString("class_information");
				String startDate = resultSet.getString("start_date");
				String endDate = resultSet.getString("end_date");
				double fees = resultSet.getDouble("fees");

				// If the class is not yet in the map, add it
				if (!classMap.containsKey(classId)) {
					classMap.put(classId, new ClassModel(classId, className, new ArrayList<>(), classInfo, startDate,
							endDate, fees, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
				}

				// Add lecture info to the class
				LectureModel lecture = new LectureModel(resultSet.getInt("id"), resultSet.getString("name"));
				classMap.get(classId).getLectures().add(lecture);
			}

			// Construct JSON response

			// Construct JSON response
			Map<String, Object> jsonResponse = new HashMap<>();
			jsonResponse.put("classes", new ArrayList<>(classMap.values()));
			jsonResponse.put("status", "success");
			Gson gson = new Gson();
			String employeeJson = gson.toJson(jsonResponse);
			// Write JSON response to output stream
			out.print(employeeJson);
//			out.print("{ \"msg\": \"Hello from Java Servlet!\",\"admin\":\"Hello from Java Servlet!\"" + " }");
		} catch (SQLException e) {
			// Handle database errors
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			out.print("{\"error\": \"Database error\"}");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
