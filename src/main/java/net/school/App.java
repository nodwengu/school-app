package net.school;

import com.google.common.collect.Multimap;
import net.school.exceptions.InputRequiredException;
import net.school.impl.*;
import net.school.model.*;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.JoinRow;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static spark.Spark.*;

public class App {
   private static Jdbi jdbi;

   static int getHerokuAssignedPort() {
      ProcessBuilder processBuilder = new ProcessBuilder();
      if (processBuilder.environment().get("PORT") != null) {
         return parseInt(processBuilder.environment().get("PORT"));
      }
      return 4567;
   }

   static Jdbi getJdbiDatabaseConnection(String defaultJdbcUrl) throws URISyntaxException, SQLException {
      ProcessBuilder processBuilder = new ProcessBuilder();
      String database_url = processBuilder.environment().get("DATABASE_URL");
      if (database_url != null) {

         URI uri = new URI(database_url);
         String[] hostParts = uri.getUserInfo().split(":");
         String username = hostParts[0];
         String password = hostParts[1];
         String host = uri.getHost();

         int port = uri.getPort();

         String path = uri.getPath();
         String url = String.format("jdbc:postgresql://%s:%s%s", host, port, path);

         return Jdbi.create(url, username, password);
      }

      return Jdbi.create(defaultJdbcUrl);
   }


   public static void main(String[] args) {
      try {
         staticFiles.location("/public");
         port(getHerokuAssignedPort());

         jdbi = getJdbiDatabaseConnection("jdbc:postgresql://localhost/school?user=thando&password=thando123");

         TeacherDaoImpl teacherDao = new TeacherDaoImpl(jdbi);
         SubjectDaoImpl subjectDao = new SubjectDaoImpl(jdbi);
         LessonDaoImpl lessonDao = new LessonDaoImpl(jdbi);
         LearnerDaoImpl learnerDao = new LearnerDaoImpl(jdbi);
         DayDaoImpl dayDao = new DayDaoImpl(jdbi);
         GradeDaoImpl gradeDao = new GradeDaoImpl(jdbi);

         get("/", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            List<Day> days = dayDao.getAll();

            map.put("days", days);

            return new ModelAndView(map, "index.handlebars");
         }, new HandlebarsTemplateEngine());

         get("/day/:id", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            List<Grade> grades = gradeDao.getAll();
            Day day = dayDao.getById(Long.parseLong(req.params("id")));

            map.put("grades", grades);
            map.put("day", day);

            return new ModelAndView(map, "day.handlebars");
         }, new HandlebarsTemplateEngine());


         get("/day/:dayId/grade/:gradeId", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long gradeId = Long.parseLong(req.params("gradeId"));
            Long dayId = Long.parseLong(req.params("dayId"));
            List<Grade> grades = gradeDao.getAll();
            List<Day> days = dayDao.getAll();
            Grade grade = gradeDao.getById(Long.parseLong(req.params("gradeId")));
            Day day = dayDao.getById(Long.parseLong(req.params("dayId")));
            //Learner learner = learnerDao.getById()
            List<Learner> learners = learnerDao.getAll();
            List<Lesson> lessons = lessonDao.getAll();
            List<Subject> subjects = subjectDao.getAll();
            List<Teacher> teachers = teacherDao.getAll();
            List<Learner> learnersForGrade = learnerDao.getLearnersForGrade(gradeId);
            List<Lesson> lessonsByGradeAndDay = lessonDao.getLessonsByGradeAndDay(gradeId, dayId);

            map.put("grades", grades);
            map.put("grade", grade);
            map.put("days", days);
            map.put("day", day);
            //map.put("learners", learners);
            map.put("lessons", lessons);
            map.put("subjects", subjects);
            map.put("teachers", teachers);
            map.put("learnersForGrade", learnersForGrade);
            map.put("lessonsByGradeAndDay", lessonsByGradeAndDay);


            return new ModelAndView(map, "grade.handlebars");
         }, new HandlebarsTemplateEngine());

         post("/day/:dayId/grade/:gradeId/learner/add", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Learner learner = new Learner();
            Long gradeId = Long.parseLong(req.params("gradeId"));
            Long dayId = Long.parseLong(req.params("dayId"));

            String firstName = req.queryParams("firstName").toLowerCase();
            String lastName = req.queryParams("lastName").toLowerCase();
            String email = req.queryParams("email").toLowerCase();
            String gradeName = req.queryParams("grade");

            String inputErrorMsg = "";
            String showStatus = "none";

            try {
               if (firstName.length() == 0 || firstName.length() == 0 || firstName.length() == 0 || firstName.length() == 0) {
                  throw new InputRequiredException("DISPLAY INPUT REQUIRED MSG FOR THE ADMIN...");
               } else {
                  String name = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
                  String surname = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
                  learner.setFirstName(name);
                  learner.setLastName(surname);
                  learner.setEmail(email);
                  learner.setGradeId(gradeDao.getIdByName(gradeName));
                  learnerDao.registerLearner(learner);
               }

            } catch (InputRequiredException ex) {
               inputErrorMsg = ex.getMessage();
               showStatus = "block";
               System.out.println(ex.getMessage());
               ex.printStackTrace();
            }
            System.out.println(inputErrorMsg);
            System.out.println(showStatus);
            map.put("inputErrorMsg", inputErrorMsg);

            res.redirect("/day/" + dayId + "/grade/" + gradeId);

            return new ModelAndView(map, "grade.handlebars");
         }, new HandlebarsTemplateEngine());




         get("/day/:dayId/grade/:gradeId/learner/:learnerId/delete", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long gradeId = Long.parseLong(req.params("gradeId"));
            Long dayId = Long.parseLong(req.params("dayId"));
            Long learnerId = Long.parseLong(req.params("learnerId"));

            learnerDao.delete(learnerId);

            res.redirect("/day/" + dayId + "/grade/" + gradeId);

            return new ModelAndView(map, "grade.handlebars");
         }, new HandlebarsTemplateEngine());

         get("/day/:dayId/grade/:gradeId/teacher/:teacherId/delete", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long gradeId = Long.parseLong(req.params("gradeId"));
            Long dayId = Long.parseLong(req.params("dayId"));
            Long teacherId = Long.parseLong(req.params("teacherId"));

            teacherDao.delete(teacherId);

            res.redirect("/day/" + dayId + "/grade/" + gradeId);

            return new ModelAndView(map, "grade.handlebars");
         }, new HandlebarsTemplateEngine());

         get("/day/:dayId/grade/:gradeId/lesson/:lessonId/delete", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long gradeId = Long.parseLong(req.params("gradeId"));
            Long dayId = Long.parseLong(req.params("dayId"));
            Long lessonId = Long.parseLong(req.params("lessonId"));

            lessonDao.delete(lessonId);

            res.redirect("/day/" + dayId + "/grade/" + gradeId);

            return new ModelAndView(map, "grade.handlebars");
         }, new HandlebarsTemplateEngine());

         get("/day/:dayId/grade/:gradeId/subject/:subjectId/delete", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long gradeId = Long.parseLong(req.params("gradeId"));
            Long dayId = Long.parseLong(req.params("dayId"));
            Long subjectId = Long.parseLong(req.params("subjectId"));

            subjectDao.delete(subjectId);

            res.redirect("/day/" + dayId + "/grade/" + gradeId);

            return new ModelAndView(map, "grade.handlebars");
         }, new HandlebarsTemplateEngine());


         //UPDATING SECTION
         get("/day/:dayId/grade/:gradeId/subject/:subjectId/update", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long gradeId = Long.parseLong(req.params("gradeId"));
            Long dayId = Long.parseLong(req.params("dayId"));
            Long subjectId = Long.parseLong(req.params("subjectId"));

            subjectDao.delete(subjectId);

            res.redirect("/day/" + dayId + "/grade/" + gradeId);

            return new ModelAndView(map, "grade.handlebars");
         }, new HandlebarsTemplateEngine());


         post("/day/:dayId/grade/:gradeId/learner/:learnerId/update", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long gradeId = Long.parseLong(req.params("gradeId"));
            Long dayId = Long.parseLong(req.params("dayId"));
            Long learnerId = Long.parseLong(req.params("learnerId"));

            System.out.println("UPDATING THE LEARNER");

//            subjectDao.delete(subjectId);

            res.redirect("/day/" + dayId + "/grade/" + gradeId);

            return new ModelAndView(map, "grade.handlebars");
         }, new HandlebarsTemplateEngine());









         post("/day/:id/grade/:id/teacher/add", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Teacher teacher = new Teacher();
            String firstName = req.queryParams("firstName").toLowerCase();
            String lastName = req.queryParams("lastName").toLowerCase();
            String email = req.queryParams("email").toLowerCase();

            try {
               if (firstName.length() == 0 || lastName.length() == 0 || email.length() == 0) {
                  throw new InputRequiredException("DISPLAY INPUT REQUIRED MSG FOR THE ADMIN...");
               } else {
                  String name = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
                  String surname = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);

                  teacher.setFirstName(name);
                  teacher.setLastName(surname);
                  teacher.setEmail(email);
                  teacherDao.addTeacher(teacher);

               }

            } catch (InputRequiredException e) {
               System.out.println(e.getMessage());
               e.printStackTrace();
            }

            res.redirect("/day/" + req.params("id") + "/grade/" + req.params("id"));

            return new ModelAndView(map, "grade.handlebars");
         }, new HandlebarsTemplateEngine());


         post("/day/:dayId/grade/:gradeId/lesson/add", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long dayId = Long.parseLong(req.params("dayId"));
            Long gradeId = Long.parseLong(req.params("gradeId"));
            Lesson lesson = new Lesson();

            String lessonName = req.queryParams("lessonName").toLowerCase();
            String time = req.queryParams("time");
            String subjectName = req.queryParams("subject");
            String gradeName = req.queryParams("grade");
            String dayName = req.queryParams("day");

            try {
               if (time.length() == 0 || lessonName.length() == 0) {
                  throw new InputRequiredException("DISPLAY INPUT REQUIRED MSG FOR THE ADMIN...");
               } else {
                  String name = lessonName.substring(0, 1).toUpperCase() + lessonName.substring(1);

                  lesson.setLessonName(name);
                  lesson.setTime(time);
                  lesson.setSubjectId(subjectDao.getId(subjectName));
                  lesson.setGradeId(gradeDao.getIdByName(gradeName));
                  lesson.setDayId(dayDao.getIdByName(dayName));

                  lessonDao.addLesson(lesson);
               }
            } catch (InputRequiredException e) {
               System.out.println(e.getMessage());
               e.printStackTrace();
            }

            res.redirect("/day/" + dayId + "/grade/" + gradeId);

            return new ModelAndView(map, "grade.handlebars");
         }, new HandlebarsTemplateEngine());


         post("/day/:dayId/grade/:gradeId/subject/add", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long dayId = Long.parseLong(req.params("dayId"));
            Long gradeId = Long.parseLong(req.params("gradeId"));
            Subject subject = new Subject();
            String subjectName = req.queryParams("subjectName").toLowerCase();
            try {
               if (subjectName.length() == 0) {
                  throw new InputRequiredException("INPUT IS REQUIRED MSG SHOULD BE DISPLAYED FOR THE ADMINISTRATOR...");
               }
               String name = subjectName.substring(0, 1).toUpperCase() + subjectName.substring(1);
               subject.setSubjectName(name);
               subjectDao.addSubject(subject);

            } catch (InputRequiredException ex) {
               System.out.println(ex.getMessage());
               ex.printStackTrace();
            }


            res.redirect("/day/" + dayId + "/grade/" + gradeId);
            return new ModelAndView(map, "grade.handlebars");
         }, new HandlebarsTemplateEngine());

         get("/day/:dayId/learner/:id", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long learnerId = Long.parseLong(req.params("id"));
            List<Subject> subjects = subjectDao.getAll();
            List<Lesson> learnerLessons = learnerDao.getLessons(learnerId);
            List<Learner> learnerSubjects = learnerDao.getSubjects(learnerId);
            Learner learner = learnerDao.getById(learnerId);
            List<Day> days = dayDao.getAll();

            map.put("subjects", subjects);
            map.put("learnerLessons", learnerLessons);
            map.put("learnerSubjects", learnerSubjects);
            map.put("learner", learner);
            map.put("days", days);

            return new ModelAndView(map, "learner.handlebars");
         }, new HandlebarsTemplateEngine());

         get("/day/:dayId/learner/:learnerId/lesson", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long dayId = Long.parseLong(req.params("dayId"));
            Long learnerId = Long.parseLong(req.params("learnerId"));
            List<Subject> subjects = subjectDao.getAll();
            //List<Lesson> learnerLessons = learnerDao.getAllLessons(learnerId);
            List<Learner> learnerSubjects = learnerDao.getSubjects(learnerId);
            Learner learner = learnerDao.getById(learnerId);
            List<Day> days = dayDao.getAll();
            Day day = dayDao.getById(Long.parseLong(req.params("dayId")));


            List<Lesson> lessonsByDay = learnerDao.getLessonsForDay(learnerId, dayId);

            map.put("subjects", subjects);
            //map.put("learnerLessons", learnerLessons);
            map.put("lessonsByDay", lessonsByDay);
            map.put("learnerSubjects", learnerSubjects);
            map.put("learner", learner);
            map.put("days", days);
            map.put("day", day);



//            List<Learner> learners = learnerDao.getAll();
//            List<Lesson> lessons = lessonDao.getAll();
//            List<Teacher> teachers = teacherDao.getAll();
//
//
//
//
//            map.put("lessons", lessons);
//            map.put("teachers", teachers);



            //res.redirect("day/" + req.params("dayId") + "/learner/" + req.params("id"));

            return new ModelAndView(map, "learnerDay.handlebars");
         }, new HandlebarsTemplateEngine());


         post("/day/:dayId/learner/:learnerId/lesson", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long learnerId = Long.parseLong(req.params("learnerId"));
            Long dayId = Long.parseLong(req.params("dayId"));

            for (String subject: req.queryParams())
               learnerDao.selectSubject(learnerId, subjectDao.getId(subject));

            res.redirect("/day/" + dayId + "/learner/" + learnerId + "/lesson");

            return new ModelAndView(map, "learnerDay.handlebars");
         }, new HandlebarsTemplateEngine());


         get("/day/:dayId/learner/:learnerId/subject/:subjectId/delete", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long learnerId = Long.parseLong(req.params("learnerId"));
            Long subjectId = Long.parseLong(req.params("subjectId"));
            Long dayId = Long.parseLong(req.params("dayId"));

            learnerDao.removeLearnerSubject(subjectId);

            res.redirect("/day/" + dayId + "/learner/" + learnerId + "/lesson");

            return new ModelAndView(map, "learnerDay.handlebars");
         }, new HandlebarsTemplateEngine());


         get("/teacher/:id", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            List<Subject> subjectList = subjectDao.getAll();
            Long teacherId = Long.parseLong(req.params("id"));
            List<Lesson> teacherLessons = teacherDao.getTeacherLessons(teacherId);
            Teacher teacher = teacherDao.getById(teacherId);
            List<Teacher> teacherSubjects = teacherDao.getAllSubjects(teacherId);

            map.put("subjectList", subjectList);
            map.put("teacherId", teacherId);
            map.put("teacher_lessons", teacherLessons);
            map.put("teacher", teacher);
            map.put("teacherSubjects", teacherSubjects);

            return new ModelAndView(map, "teacher.handlebars");
         }, new HandlebarsTemplateEngine());

         get("/teacher/:teacherId/subject/:subjectId/delete", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long teacherId = Long.parseLong(req.params("teacherId"));
            Long subjectId = Long.parseLong(req.params("subjectId"));

            teacherDao.removeTeacherSubject(subjectId);

            res.redirect("/teacher/" + teacherId);

            return new ModelAndView(map, "teacher.handlebars");
         }, new HandlebarsTemplateEngine());

         post("/teacher/:id", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long teacherId = Long.parseLong(req.params("id"));

            for (String subject: req.queryParams())
               teacherDao.selectSubject(teacherId, subjectDao.getId(subject));

            res.redirect("/teacher/"  + req.params("id"));
            return new ModelAndView(map, "teacher.handlebars");
         }, new HandlebarsTemplateEngine());


         get("/teacher/:teacherId/lesson/:lessonId", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long teacherId = Long.parseLong(req.params("teacherId"));
            Long lessonId = Long.parseLong(req.params("lessonId"));
            List<Learner> learnerList = learnerDao.getLearnersAttending(lessonId);
            Lesson lesson = lessonDao.getById(lessonId);
            Multimap<Lesson, Subject> subjectForLesson = subjectDao.getSubjectForLesson(lessonId);
            String subjectName = null;

            for (Map.Entry<Lesson, Subject> item: subjectForLesson.entries())
               subjectName = item.getValue().getSubjectName();

            map.put("teacherId", teacherId);
            map.put("lessonId", lessonId);
            map.put("learnerList", learnerList);
            map.put("lesson", lesson);
            map.put("subjectName", subjectName);

            return new ModelAndView(map, "teachLesson.handlebars");
         }, new HandlebarsTemplateEngine());

         get("/teacher/:teacherId/lesson/:lessonId/teach", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long teacherId = Long.parseLong(req.params("teacherId"));
            Long lessonId = Long.parseLong(req.params("lessonId"));
            List<Learner> attendingLearners = learnerDao.getLearnersAttending(lessonId);
            Teacher teacher = teacherDao.getById(teacherId);

            // ALL LEARNERS GET 3 TOKENS
            for (Learner learner: attendingLearners) {
               learner.increaseTokens();
               learnerDao.update(3L, learner);
            }

            // TEACHER SHOULD ALSO GET 5 TOKENS FOR TEACHING
            teacher.increaseTokens();
            teacherDao.update(teacherId, teacher);



            map.put("lessonId", lessonId);

            res.redirect("/teacher/" + teacherId + "/lesson/"  + lessonId);

            return new ModelAndView(map, "teachLesson.handlebars");
         }, new HandlebarsTemplateEngine());


//         get("/learner/:learnerId/lesson/:lessonId", (req, res) -> {
//            Map<String, Object> map = new HashMap<>();
//            Long learnerId = Long.parseLong(req.params("learnerId"));
//            System.out.println("GOOD FROM HERE...");
//            Long lessonId = Long.parseLong(req.params("lessonId"));
//            List<Learner> learnerList = learnerDao.getLearnersFor(lessonId);
//            Lesson lesson = lessonDao.getById(lessonId);
//            Multimap<Lesson, Subject> subjectForLesson = subjectDao.getSubjectForLesson(lessonId);
//            String subjectName = null;
//
//            for (Map.Entry<Lesson, Subject> item: subjectForLesson.entries())
//               subjectName = item.getValue().getSubject_name();
//
//            map.put("learnerId", learnerId);
//            map.put("lessonId", lessonId);
//            map.put("learnerList", learnerList);
//            map.put("lesson", lesson);
//            map.put("subjectName", subjectName);
//
//            return new ModelAndView(map, "attendLesson.handlebars");
//         }, new HandlebarsTemplateEngine());
////
         get("/learner/:learnerId/lesson/:lessonId/attend", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long learnerId = Long.parseLong(req.params("learnerId"));
            Long lessonId = Long.parseLong(req.params("lessonId"));

           learnerDao.attendLesson(learnerId, lessonId);
           System.out.println("ATTENDING A LESSON...");

            map.put("learnerId", learnerId);
            map.put("lessonId", lessonId);

            ///day/1/learner/3/lesson
            res.redirect("/day/" + 1 + "/learner/" + learnerId + "/lesson" );

            return new ModelAndView(map, "attendLesson.handlebars");
         }, new HandlebarsTemplateEngine());

         get("/subject/:id/update", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long subjectId = Long.parseLong(req.params("id"));
            Subject subject = subjectDao.getById(subjectId);

            //subjectDao.update(subjectId, subject);

           // res.redirect("/day/" + dayId + "/grade/" + gradeId);
            return new ModelAndView(map, "grade.handlebars");
         }, new HandlebarsTemplateEngine());


      }
      catch (SQLException ex) {
         ex.printStackTrace();
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }

}
