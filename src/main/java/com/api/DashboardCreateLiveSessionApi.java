package com.api;

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.database.DatabaseConnection;
import com.google.gson.Gson;

/**
 * Servlet implementation class DashboardCreateLiveSessionApi
 */
@WebServlet("/create-live-session")
public class DashboardCreateLiveSessionApi extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public DashboardCreateLiveSessionApi() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		response.setHeader("Content-Type", "application/json");
		PrintWriter out = response.getWriter();
		Map<String, Object> jsonRes = new HashMap<>();
		Map<String, Object> jsonRes2 = new HashMap<>();
		Map<String, String[]> parameters = request.getParameterMap();
		Gson gson = new Gson();
		boolean anyParamNull = false;
		for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
			String[] paramValues = entry.getValue();
			if (paramValues == null || paramValues.length == 0 || paramValues[0].isEmpty()) {
				anyParamNull = true;
				jsonRes.put(entry.getKey(), "You need to fill " + entry.getKey());

			}
		}

		if (anyParamNull) {
			// Respond with an error message
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			jsonRes2.put("errors", jsonRes);
			String liveSessionJson = gson.toJson(jsonRes2);
			// Write JSON response to output stream
			out.print(liveSessionJson);
		} else {
			String sqlQuery = "insert into live_session (start_time,end_time,date,meet_url,lecture_ids,created_at,class_id,live_title) values (?,?,?,?,?,?,?,?)";
			try (Connection connection = DatabaseConnection.getConnection()) {
				Thread.sleep(2000);
				try (PreparedStatement preStmt = connection.prepareStatement(sqlQuery)) {
					// Set parameters for insertion
					preStmt.setString(1, request.getParameter("start_time"));
					preStmt.setString(2, request.getParameter("end_time"));
					preStmt.setString(3, request.getParameter("date"));
					preStmt.setString(4, request.getParameter("meet_url"));
					preStmt.setString(5, request.getParameter("lecture_ids"));
					preStmt.setString(6, new Timestamp(System.currentTimeMillis()).toString());
					preStmt.setInt(7, Integer.parseInt(request.getParameter("class_id")));
					preStmt.setString(8, request.getParameter("live_title"));
					int row = preStmt.executeUpdate();
					if (row > 0) {
						Map<String, Object> jsonResponse = new HashMap<>();
						jsonResponse.put("msg", "Created live session successfully");
						jsonResponse.put("status", "success");
						Gson gson1 = new Gson();
						String liveSessionJson = gson1.toJson(jsonResponse);
						// Write JSON response to output stream
						out.print(liveSessionJson);
					} else {
						out.print("{\"msg\": \"Failed to create live session\"}");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
