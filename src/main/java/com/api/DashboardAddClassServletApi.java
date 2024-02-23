package com.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.database.DatabaseConnection;
import com.google.gson.Gson;

/**
 * Servlet implementation class DashboardAddClassServletApi
 */
@WebServlet("/add-class")
public class DashboardAddClassServletApi extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DashboardAddClassServletApi() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String class_name = request.getParameter("class_name");
		String start_date = request.getParameter("start_date");
		String end_date = request.getParameter("end_date");
		String class_information = request.getParameter("class_information");
		String fees = request.getParameter("fees");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		response.setHeader("Content-Type", "application/json");
		PrintWriter out = response.getWriter();
		String sqlQuery = "insert into classes (class_name,start_date,end_date,class_information,fees) values (?,?,?,?,?)";
		String query = "INSERT INTO class_lecture(class_id, lecture_id) VALUES (?, ?)";
		try (Connection connection = DatabaseConnection.getConnection()) {
			// SQL query to insert a new class into the database
//            String query = "INSERT INTO classes (title, price, lectures_id) VALUES (?, ?, ?)";

			try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery,
					PreparedStatement.RETURN_GENERATED_KEYS)) {
				PreparedStatement preparedStatement2 = connection.prepareStatement(query);
				preparedStatement.setString(1, class_name);
				preparedStatement.setString(2, start_date);
				preparedStatement.setString(3, end_date);
				preparedStatement.setString(4, class_information);
				preparedStatement.setDouble(5, Double.parseDouble(fees));
				System.out.println("Lecture list 1 is" +  request.getParameter("lecture_ids")
				+ "next" + request.getParameter("lecture_ids[]") + "THEN" + request.getPathInfo());
				String lecturesIdsStr = request.getParameter("lecture_ids");
				List<String> lecturesIdsList = Arrays.asList(lecturesIdsStr.split(","));
				System.out.println("Lecture list is" + lecturesIdsList.toString() + request.getParameter("lecture_ids[]")
						+ "next" + request.getParameter("lecture_ids[]") + "THEN" + request.getPathInfo());
				int row = preparedStatement.executeUpdate();

				if (row > 0) {
					ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
					if (generatedKeys.next()) {
						int classId = generatedKeys.getInt(1);
						for (String lectureIdStr : lecturesIdsList) {
							int lecturesId = Integer.parseInt(lectureIdStr);
							preparedStatement2.setInt(1, classId);
							preparedStatement2.setInt(2, lecturesId);
							preparedStatement2.addBatch();
						}
						// Execute the batch insert
						int[] rowsInserted = preparedStatement2.executeBatch();

						// Check the number of rows inserted
						int totalRowsInserted = 0;
						for (int rows : rowsInserted) {
							totalRowsInserted += rows;
						}
						if (totalRowsInserted == lecturesIdsList.size()) {
							Map<String, Object> jsonResponse = new HashMap<>();
							jsonResponse.put("msg", "Created class successfully");
							jsonResponse.put("status", "success");
							Gson gson = new Gson();
							String employeeJson = gson.toJson(jsonResponse);
							// Write JSON response to output stream
							out.print(employeeJson);
						} else {
							out.print("{\"msg\": \"Failed to add class\"}");
						}
					}
				}
			}
		} catch (SQLException e) {
			throw new ServletException(e);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
