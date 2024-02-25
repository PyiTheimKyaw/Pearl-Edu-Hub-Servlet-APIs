package com.models;

public class LiveSessionModel {
	private int id;
	private String date;
	private String start_time;
	private String end_time;
	private String lecture_ids;
	private String meet_url;
	private int class_id;
	private String created_at;

	public LiveSessionModel(int id, String date, String start_time, String end_time, String lecture_ids,
			String meet_url, int class_id, String created_at) {
		super();
		this.id = id;
		this.date = date;
		this.start_time = start_time;
		this.end_time = end_time;
		this.lecture_ids = lecture_ids;
		this.meet_url = meet_url;
		this.class_id = class_id;
		this.created_at = created_at;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getLecture_ids() {
		return lecture_ids;
	}

	public void setLecture_ids(String lecture_ids) {
		this.lecture_ids = lecture_ids;
	}

	public String getMeet_url() {
		return meet_url;
	}

	public void setMeet_url(String meet_url) {
		this.meet_url = meet_url;
	}

	public int getClass_id() {
		return class_id;
	}

	public void setClass_id(int class_id) {
		this.class_id = class_id;
	}

}
