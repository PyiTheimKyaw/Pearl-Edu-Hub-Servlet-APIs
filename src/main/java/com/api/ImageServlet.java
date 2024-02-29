package com.api;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.database.DatabaseConnection;

@WebServlet("/image")
public class ImageServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
//	private DataSource dataSource;

    @Override
    public void init() throws ServletException {
        super.init();
//        dataSource = (DataSource) getServletContext().getAttribute("dataSource"); // Assuming you have configured a DataSource in your servlet context
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int imageId = Integer.parseInt(request.getParameter("id")); // Assuming you pass the image ID as a parameter

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT image_data FROM images WHERE id = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, imageId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        response.setContentType("image/jpeg"); // Set the appropriate content type
                        try (OutputStream out = response.getOutputStream()) {
                            out.write(resultSet.getBytes("image_data"));
                        }
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    }
                }
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("Database error: " + e.getMessage());
        } catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }
}
