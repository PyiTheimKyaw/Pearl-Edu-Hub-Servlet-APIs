package com.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
		doGet(request, response);
	}
}
