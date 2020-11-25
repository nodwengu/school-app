package net.school.model;

import java.util.ArrayList;
import java.util.List;

public class Lesson {
   private Long id;
   private String lessonName;
   private String time;
   private Long subjectId;

   private List<Subject> subjects = new ArrayList<>();
   private List<TeacherSubject> teacherSubjects = new ArrayList<>();
   private List<Grade10learnerSubject> learnerSubjects = new ArrayList<>();
   private List<Lesson> lessons = new ArrayList<>();
   private List<Learner> learners = new ArrayList();
   private List<Teacher> teachers = new ArrayList<>();

   public Lesson(){}

   public Lesson(Long id, String lessonName, String time, Long subjectId) {
      this.id = id;
      this.lessonName = lessonName;
      this.time = time;
      this.subjectId = subjectId;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public void setLessonName(String lessonName) {
      this.lessonName = lessonName;
   }

   public void setTime(String time) {
      this.time = time;
   }

   public void setSubjectId(Long subjectId) {
      this.subjectId = subjectId;
   }

   public Long getId() {
      return id;
   }

   public String getLessonName() {
      return lessonName;
   }

   public String getTime() {
      return time;
   }

   public Long getSubjectId() {
      return subjectId;
   }

   public void addSubject(Subject subject) {
         subjects.add(subject);
   }

   public void setSubject(List<Subject> subjects) {
      this.subjects = subjects;
   }

   public List<Subject> getSubjects() {
      return subjects;
   }

   public void addTeacherSubject(TeacherSubject ts) {
      teacherSubjects.add(ts);
   }

   public void addLearnerSubject(Grade10learnerSubject ls) {
      learnerSubjects.add(ls);
   }


   public void setTeacherSubjects(List<TeacherSubject> teacherSubjects) {
      this.teacherSubjects = teacherSubjects;
   }

   public List<TeacherSubject> getTeacherSubjects() {
      return teacherSubjects;
   }

   public List<Grade10learnerSubject> getLearnerSubjects() {
      return learnerSubjects;
   }

   public void addLesson(Lesson lesson) {
      lessons.add(lesson);
   }

   public List<Lesson> getLessons() {
      return lessons;
   }

   public void addLearner(Learner learner) {
      learners.add(learner);
   }

   public List<Learner> getLearners() {
      return learners;
   }

   public void addTeacher(Teacher teacher) {
      if (!teachers.contains(teacher))
         teachers.add(teacher);
   }

   public List<Teacher> getTeachers() {
      return teachers;
   }

   @Override
   public String toString() {
      return "Lesson{" +
              "id=" + id +
              ", lessonName='" + lessonName + '\'' +
              ", time='" + time + '\'' +
              ", subjectId=" + subjectId +
              ", subjects=" + subjects +
              ", teacherSubjects=" + teacherSubjects +
              ", learnerSubjects=" + learnerSubjects +
              ", lessons=" + lessons +
              ", learners=" + learners +
              ", teachers=" + teachers +
              '}';
   }
}
