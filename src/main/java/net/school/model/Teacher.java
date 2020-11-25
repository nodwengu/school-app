package net.school.model;

public class Teacher {
   private int id;
   private String first_name;
   private String last_name;
   private String email;
   private int tokens;
//   private List<TeacherSubject> teacherSubjects = new ArrayList<>();
//   private List<Subject> subjects = new ArrayList<>();
//   private List<Lesson> lessons = new ArrayList<>();

   public Teacher() {}

   public Teacher(int id, String first_name, String last_name, String email, int tokens) {
      this.id = id;
      this.first_name = first_name;
      this.last_name = last_name;
      this.email = email;
      this.tokens = tokens;
   }

   public void setId(int id) {
      this.id = id;
   }

   public void setFirstName(String first_name) {
      this.first_name = first_name;
   }

   public void setLastName(String last_name) {
      this.last_name = last_name;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public void setTokens(int tokens) {
      this.tokens = tokens;
   }

   public int getId() {
      return id;
   }

   public String getFirstName() {
      return first_name;
   }

   public String getLastName() {
      return last_name;
   }

   public String getEmail() {
      return email;
   }

   public int getTokens() {
      return tokens;
   }

//   public void addTeacherSubject(TeacherSubject ts) {
//      teacherSubjects.add(ts);
//   }
//
//   public void setTeacherSubjects(List<TeacherSubject> teacherSubjects) {
//      this.teacherSubjects = teacherSubjects;
//   }
//
//   public List<TeacherSubject> getTeacherSubjects() {
//      return teacherSubjects;
//   }
//
//   public void addSubject(Subject subject) {
//      subjects.add(subject);
//   }
//
//   public List<Subject> getSubjects() {
//      return subjects;
//   }
//
//   public void addLesson(Lesson lesson) {
//      lessons.add(lesson);
//   }
//
//   public List<Lesson> getLessons() {
//      return lessons;
//   }

   @Override
   public String toString() {
      return "Teacher{" + "id='" + getId() + '\'' +
              ", firstName='" + getFirstName() + '\'' +
              ", lastName='" + getLastName() + '\'' +
              ", Tokens='" + getTokens() + '\'' +
              '}';
   }

//   public static void main(String[] args) {
//      System.out.println(new Teacher(1, "Thomas", "Khumalo", "thomas@gmail.com", 20));
//   }
}
