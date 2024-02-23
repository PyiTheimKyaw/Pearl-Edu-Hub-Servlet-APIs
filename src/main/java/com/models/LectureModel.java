package com.models;

public class LectureModel {
    private int id;
    private String lecture_name;

    // Constructor
    public LectureModel(int id, String lecture_name) {
        this.id = id;
        this.lecture_name = lecture_name;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLecture_name() {
        return lecture_name;
    }

    public void setLecture_name(String lecture_name) {
        this.lecture_name = lecture_name;
    }
}

