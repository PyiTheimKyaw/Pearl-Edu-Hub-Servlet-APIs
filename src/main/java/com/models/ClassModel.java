package com.models;

import java.util.List;

public class ClassModel {
	private int id;
	private String class_name;
	private String classInfo;
	private String startDate;
	private String endDate;
	private double fees;
	private List<LectureModel> lectures;
	private List<StudentModel> students;
	private List<EnrollmentModel> enrollments;

	// Constructor
	public ClassModel(int id, String class_name, List<LectureModel> lectures, String classInfo, String startDate,
			String endDate, double fees, List<StudentModel> students,List<EnrollmentModel> enrollments) {
		this.id = id;
		this.class_name = class_name;
		this.lectures = lectures;
		this.classInfo = classInfo;
		this.startDate = startDate;
		this.endDate = endDate;
		this.fees = fees;
		this.students = students;
		this.enrollments = enrollments;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	public String getClassInfo() {
		return classInfo;
	}

	public void setClassInfo(String classInfo) {
		this.classInfo = classInfo;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public double getFees() {
		return fees;
	}

	public void setFees(double fees) {
		this.fees = fees;
	}

	public List<LectureModel> getLectures() {
		return lectures;
	}

	public void setLectures(List<LectureModel> lectures) {
		this.lectures = lectures;
	}

	public List<StudentModel> getStudents() {
		return students;
	}

	public void setStudents(List<StudentModel> students) {
		this.students = students;
	}

	public List<EnrollmentModel> getEnrollments() {
		return enrollments;
	}

	public void setEnrollments(List<EnrollmentModel> enrollments) {
		this.enrollments = enrollments;
	}

	

}
