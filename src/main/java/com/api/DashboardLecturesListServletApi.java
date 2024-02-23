package com.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.database.DatabaseConnection;
import com.google.gson.Gson;
import com.models.LectureModel;

/**
 * Servlet implementation class DashboardLecturesListServletApi
 */
@WebServlet("/get-lectures")
public class DashboardLecturesListServletApi extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DashboardLecturesListServletApi() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		response.setHeader("Content-Type", "application/json");
		PrintWriter out = response.getWriter();
		try {
			Connection connection = DatabaseConnection.getConnection();
			// Create prepared statement with the SQL query
			PreparedStatement statement = connection.prepareStatement("select * from lectures");
			// Execute the query and get the result set
			ResultSet resultSet = statement.executeQuery();
			// Map to store class info with lectures
			// Map to store class info with lectures
			Map<Integer, LectureModel> lectureMap = new HashMap<>();
			while (resultSet.next()) {
				// Retrieve class and lecture information
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");

				// If the class is not yet in the map, add it
				if (!lectureMap.containsKey(id)) {
					lectureMap.put(id,new LectureModel(id,name));
				}

			
			}
			Map<String, Object> jsonResponse = new HashMap<>();
			jsonResponse.put("lectures", new ArrayList<>(lectureMap.values()));
			jsonResponse.put("status", "success");
			Gson gson = new Gson();
			String employeeJson = gson.toJson(jsonResponse);
			// Write JSON response to output stream
			out.print(employeeJson);
		} catch(Exception e) {}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
