package com.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.database.DatabaseConnection;
import com.google.gson.Gson;
import com.models.*;

@WebServlet("/get-class")
public class DashboardClassDetailServletApi extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public DashboardClassDetailServletApi() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Content-Type", "application/json");

		PrintWriter out = response.getWriter();

		try {
			Connection connection = DatabaseConnection.getConnection();
			String sqlQuery = "SELECT c.class_id, c.class_name, c.class_information, c.start_date, c.end_date, c.fees, "
					+ "l.id AS lecture_id, l.name AS lecture_name, " + "stu.stu_id, stu.stu_name, stu.stu_email, "
					+ "tr.id AS transaction_id, tr.payment_type_id, tr.created_at, tr.amount, "
					+ "live.live_title, live.id AS live_id, live.date AS live_start_date, live.start_time,live.end_time,live.lecture_ids AS live_lecture_ids,live.meet_url,live.class_id AS live_class_id,live.created_at as live_created_at "
					+ "FROM classes c " + "INNER JOIN class_lecture cl ON c.class_id = cl.class_id "
					+ "LEFT JOIN transactions tr ON c.class_id = tr.class_id "
					+ "LEFT JOIN student stu ON tr.student_id = stu.stu_id "
					+ "LEFT JOIN lectures l ON cl.lecture_id = l.id "
					+ "LEFT JOIN live_session live ON c.class_id = live.class_id " + "WHERE c.class_id = ?";

			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setInt(1, Integer.parseInt(request.getParameter("class_id")));
			ResultSet resultSet = statement.executeQuery();

			Map<Integer, ClassModel> classMap = new HashMap<>();

			while (resultSet.next()) {
				int classId = resultSet.getInt("class_id");
				if (!classMap.containsKey(classId)) {
					String className = resultSet.getString("class_name");
					String classInfo = resultSet.getString("class_information");
					String startDate = resultSet.getString("start_date");
					String endDate = resultSet.getString("end_date");
					double fees = resultSet.getDouble("fees");
					List<LectureModel> lectures = new ArrayList<>();
					List<StudentModel> students = new ArrayList<>();
					List<EnrollmentModel> enrollments = new ArrayList<>();
					List<LiveSessionModel> liveSessions = new ArrayList<>();
					classMap.put(classId, new ClassModel(classId, className, lectures, classInfo, startDate, endDate,
							fees, students, enrollments, liveSessions));
				}

				int lectureId = resultSet.getInt("lecture_id");
				if (!isDuplicate(classMap.get(classId).getLectures(), lectureId)) {
					LectureModel lecture = new LectureModel(lectureId, resultSet.getString("lecture_name"));
					classMap.get(classId).getLectures().add(lecture);
				}

				if (!isDuplicate(classMap.get(classId).getEnrollments(), resultSet.getInt("transaction_id"))) {
					EnrollmentModel student = new EnrollmentModel(resultSet.getInt("transaction_id"),
							new StudentModel(resultSet.getInt("stu_id"), resultSet.getString("stu_name"),
									resultSet.getString("stu_email"), null, null),
							resultSet.getInt("payment_type_id"), resultSet.getDouble("amount"),
							resultSet.getString("created_at"), null);

					classMap.get(classId).getEnrollments().add(student);
				}

				if (!isDuplicate(classMap.get(classId).getLiveSessions(), resultSet.getInt("live_id"))) {
					LiveSessionModel liveSessin = new LiveSessionModel(resultSet.getString("live_title"),
							resultSet.getInt("live_id"), resultSet.getString("live_start_date"),
							resultSet.getString("start_time"), resultSet.getString("end_time"),
							resultSet.getString("live_lecture_ids"), resultSet.getString("meet_url"),
							resultSet.getInt("live_class_id"), resultSet.getString("live_created_at"));
					classMap.get(classId).getLiveSessions().add(liveSessin);
				}

			}

			// Close resources
			resultSet.close();
			statement.close();
			connection.close();

			Map<String, Object> jsonResponse = new HashMap<>();
			jsonResponse.put("status", "success");
			jsonResponse.put("class_detail", classMap.isEmpty() ? null : classMap.values().iterator().next());

			Gson gson = new Gson();
			String json = gson.toJson(jsonResponse);
			out.print(json);

		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			out.print("{\"error\": \"Database error\"}");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean isDuplicate(List<? extends Object> list, int id) {
		if (id == 0) {
			return true; // Skip adding if ID is 0
		}
		for (Object obj : list) {
			if (obj instanceof LectureModel && ((LectureModel) obj).getId() == id) {
				return true;
			} else if (obj instanceof EnrollmentModel && ((EnrollmentModel) obj).getId() == id) {
				return true;
			} else if (obj instanceof LiveSessionModel && ((LiveSessionModel) obj).getId() == id) {
				return true;
			}
		}
		return false;
	}

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
		String sqlQuery = "update classes set class_name =? ,start_date =?,end_date =?,class_information =? ,fees =? where class_id=?";
		String deleteLectureFromClassQuery = "delete from class_lecture where class_id = "
				+ request.getParameter("class_id");
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
				preparedStatement.setInt(6, Integer.parseInt(request.getParameter("class_id")));
				System.out.println("Lecture list 1 is" + request.getParameter("lecture_ids") + "next"
						+ request.getParameter("lecture_ids[]") + "THEN" + request.getPathInfo());
				String lecturesIdsStr = request.getParameter("lecture_ids");
				List<String> lecturesIdsList = Arrays.asList(lecturesIdsStr.split(","));
				System.out
						.println("Lecture list is" + lecturesIdsList.toString() + request.getParameter("lecture_ids[]")
								+ "next" + request.getParameter("lecture_ids[]") + "THEN" + request.getPathInfo());
				int deleteLectureRow = connection.prepareStatement(deleteLectureFromClassQuery).executeUpdate();
				int row = preparedStatement.executeUpdate();

				if (row > 0) {
//					ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
//					if (generatedKeys.next()) {
//						int classId = generatedKeys.getInt(1);
					for (String lectureIdStr : lecturesIdsList) {
						int lecturesId = Integer.parseInt(lectureIdStr);
						preparedStatement2.setInt(1, Integer.parseInt(request.getParameter("class_id")));
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
						jsonResponse.put("msg", "Updated class successfully");
						jsonResponse.put("status", "success");
						Gson gson = new Gson();
						String employeeJson = gson.toJson(jsonResponse);
						// Write JSON response to output stream
						out.print(employeeJson);
					} else {
						out.print("{\"msg\": \"Failed to update class\"}");
					}
//					}
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
