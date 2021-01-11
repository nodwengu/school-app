package net.school.model;

import java.util.ArrayList;
import java.util.List;

public class Lesson {
   private Long id;
   private String lessonName;
   private String time;
   private Long subjectId;
   private Long gradeId;
   private Long dayId;

   private List<Subject> subjects = new ArrayList<>();
   private List<TeacherSubject> teacherSubjects = new ArrayList<>();
   private List<LearnerSubject> learnerSubjects = new ArrayList<>();
   private List<Lesson> lessons = new ArrayList<>();
   private List<Learner> learners = new ArrayList();
   private List<Teacher> teachers = new ArrayList<>();
   private List<Grade> grades = new ArrayList<>();
   private List<Day> days = new ArrayList<>();

   public Lesson(){}

   public Lesson(Long id, String lessonName, String time, Long subjectId, Long gradeId, Long dayId) {
      this.id = id;
      this.lessonName = lessonName;
      this.time = time;
      this.subjectId = subjectId;
      this.gradeId = gradeId;
      this.dayId = dayId;
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

   public void setGradeId(Long gradeId) {
      this.gradeId = gradeId;
   }

   public void setDayId(Long dayId) {
      this.dayId = dayId;
   }

   public Long getGradeId() {
      return gradeId;
   }

   public Long getDayId() {
      return dayId;
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
      if (subjects.isEmpty())
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

   public void addLearnerSubject(LearnerSubject ls) {
      learnerSubjects.add(ls);
   }


   public void setTeacherSubjects(List<TeacherSubject> teacherSubjects) {
      this.teacherSubjects = teacherSubjects;
   }

   public List<TeacherSubject> getTeacherSubjects() {
      return teacherSubjects;
   }

   public List<LearnerSubject> getLearnerSubjects() {
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
      teachers.add(teacher);
   }

   public List<Teacher> getTeachers() {
      return teachers;
   }

   public void addGrade(Grade grade) {
      grades.add(grade);
   }

   public List<Grade> getGrades() {
      return grades;
   }

   public void addDay(Day day) {
      if (days.isEmpty())
         days.add(day);
   }

   public List<Day> getDays() {
      return days;
   }

   @Override
   public String toString() {
      return "Lesson{" +
              "id=" + id +
              ", lessonName='" + lessonName + '\'' +
              ", time='" + time + '\'' +
              ", subjectId=" + subjectId +
              ", gradeId=" + gradeId +
              ", dayId=" + dayId +
              ", subjects=" + subjects +
              ", teacherSubjects=" + teacherSubjects +
              ", learnerSubjects=" + learnerSubjects +
//              ", lessons=" + lessons +
              ", learners=" + learners +
              ", teachers=" + teachers +
              ", days=" + days +
              ", grades=" + grades +
              '}';
   }

}
