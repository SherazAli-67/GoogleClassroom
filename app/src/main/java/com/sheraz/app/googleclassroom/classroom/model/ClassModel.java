package com.sheraz.app.googleclassroom.classroom.model;

public class ClassModel {
    String class_name;
    String subject_teacher;
    String section;
    String user_id;
    String classCode;
    String img;

    public ClassModel() {
    }

    public ClassModel(String class_name, String subject_teacher, String section, String user_id, String classCode, String img) {
        this.class_name = class_name;
        this.subject_teacher = subject_teacher;
        this.section = section;
        this.user_id = user_id;
        this.classCode = classCode;
        this.img = img;
    }

    public String getClass_name() {
        return class_name;
    }

    public String getSubject_teacher() {
        return subject_teacher;
    }

    public String getSection() {
        return section;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getClassCode() {
        return classCode;
    }

    public String getImg() {
        return img;
    }
}
