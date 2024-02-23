package com.models;

public class EnrollmentModel {
	private int id;
	private StudentModel student;
	private int payment_type_id;
	private double amount;
	private String created_at;
	private String status;

	public EnrollmentModel(int id, StudentModel student, int payment_type_id, double amount, String created_at,
			String status) {
		super();
		this.id = id;
		this.student = student;
		this.payment_type_id = payment_type_id;
		this.amount = amount;
		this.created_at = created_at;
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public StudentModel getStudent() {
		return student;
	}

	public void setStudent(StudentModel student) {
		this.student = student;
	}

	public int getPayment_type_id() {
		return payment_type_id;
	}

	public void setPayment_type_id(int payment_type_id) {
		this.payment_type_id = payment_type_id;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
