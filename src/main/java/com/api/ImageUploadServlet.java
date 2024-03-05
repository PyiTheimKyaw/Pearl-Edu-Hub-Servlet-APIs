package com.api;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.database.DatabaseConnection;

@WebServlet("/upload")
@MultipartConfig
public class ImageUploadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try (Connection conn = DatabaseConnection.getConnection()) {
            Part filePart = request.getPart("image");
            InputStream inputStream = filePart.getInputStream();
            String sql = "INSERT INTO images (image_data,file_name) VALUES (?,?)";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setBlob(1, inputStream);
                statement.setString(2, request.getParameter("file_name"));
                statement.executeUpdate();
                response.getWriter().print("Image uploaded successfully");
            } catch (SQLException e) {
                response.getWriter().print("Failed to upload image: " + e.getMessage());
            }
        } catch (SQLException e) {
            response.getWriter().print("Database connection error: " + e.getMessage());
        } catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }
}


