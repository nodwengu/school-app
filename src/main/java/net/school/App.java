package net.school;

import com.google.common.collect.Multimap;
import net.school.impl.*;
import net.school.model.*;
import org.jdbi.v3.core.Jdbi;
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

//         TeacherDaoImpl teacherDao = new TeacherDaoImpl(jdbi);
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


         get("/day/:id/grade/:id", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            List<Grade> grades = gradeDao.getAll();
            Grade grade = gradeDao.getById(Long.parseLong(req.params("id")));
            Day day = dayDao.getById(Long.parseLong(req.params("id")));

            List<Learner> learners = learnerDao.getAll();
            List<Lesson> lessons = lessonDao.getAll();

            map.put("grades", grades);
            map.put("grade", grade);
            map.put("day", day);
            map.put("learners", learners);
            map.put("lessons", lessons);

            return new ModelAndView(map, "grade.handlebars");
         }, new HandlebarsTemplateEngine());

         post("/day/:id/grade/:id", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Learner learner = new Learner();
            String firstName = req.queryParams("firstName").toLowerCase();
            String lastName = req.queryParams("lastName").toLowerCase();
            String email = req.queryParams("email").toLowerCase();
            Long gradeId = Long.parseLong(req.params("id"));

            String name = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
            String surname = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);

            learner.setFirstName(name);
            learner.setLastName(surname);
            learner.setEmail(email);
            learner.setGradeId(gradeId);

            learnerDao.registerLearner(learner);

            res.redirect("/day/" + req.params("id") + "/grade/" + req.params("id"));

            return new ModelAndView(map, "grade.handlebars");
         }, new HandlebarsTemplateEngine());


         post("/grade/:grade/lesson/add", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Lesson lesson = new Lesson();
            String lessonName = req.queryParams("lessonName").toLowerCase();
            String time = req.queryParams("time");
            Long subjectId = Long.parseLong(req.queryParams("subjectId"));

            String name = lessonName.substring(0, 1).toUpperCase() + lessonName.substring(1);
            System.out.println("name " + name);
            System.out.println("WE GOOD FROM HERE...");

            lesson.setLessonName(name);
            lesson.setSubjectId(subjectId);
            lesson.setTime(time);

            System.out.println("subjectId " + lesson.getSubjectId());
            System.out.println("lessonName " + lesson.getLessonName());
            System.out.println("time " + lesson.getTime());

            lessonDao.addLesson(lesson);

            res.redirect("/grade/" + req.params("grade"));

            return new ModelAndView(map, req.params("grade") + ".handlebars");
         }, new HandlebarsTemplateEngine());

         get("/learner/:id", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long learnerId = Long.parseLong(req.params("id"));
            List<Subject> subjects = subjectDao.getAll();
            //List<Lesson> learnerLessons = learnerDao.getAllLessons(learnerId);
            //List<Learner> learnerSubjects = learnerDao.getAllSubjects(learnerId);
            Learner learner = learnerDao.getById(learnerId);

            map.put("subjects", subjects);
            //map.put("learnerLessons", learnerLessons);
            //map.put("learnerSubjects", learnerSubjects);
            map.put("learner", learner);

            return new ModelAndView(map, "learner.handlebars");
         }, new HandlebarsTemplateEngine());

//         get("/learner/:id", (req, res) -> {
//            Map<String, Object> map = new HashMap<>();
//            Long learnerId = Long.parseLong(req.params("id"));
//
//            List<Lesson> learnerLessons = grade10LearnerDao.getAllLessons(learnerId);
//            for (Lesson l: learnerLessons) {
//               System.out.println(l);
//            }
//
//            Grade10Learner learner = grade10LearnerDao.getById(learnerId);
//            List<Grade10Learner> learnerSubjects = grade10LearnerDao.getLearnerSubjects(learnerId);
//            Grade10Learner grade10learner = grade10LearnerDao.getById(learnerId);
//
//            map.put("learnerLessons", learnerLessons);
//            map.put("learnerSubjects", learnerSubjects);
//            map.put("grade10learner", grade10learner);
//            map.put("learner", learner);
//
//            return new ModelAndView(map, "learner.handlebars");
//         }, new HandlebarsTemplateEngine());


//         post("/learner/:id", (req, res) -> {
//            Map<String, Object> map = new HashMap<>();
//            Long learnerId = Long.parseLong(req.params("id"));
//
//            for (String subject: req.queryParams())
//               grade10LearnerDao.selectSubject(learnerId, subjectDao.getId(subject));
//
//            res.redirect("/learner/" + req.params("id"));
//
//            return new ModelAndView(map, "teacher.handlebars");
//         }, new HandlebarsTemplateEngine());


//         get("/teacher/:id", (req, res) -> {
//            Map<String, Object> map = new HashMap<>();
//            List<Subject> subjects = subjectDao.getAll();
//            int teacherId = Integer.parseInt(req.params(":id"));
//            List<Lesson> teacherLessons = teacherDao.getTeacherLessons(teacherId);
//            Teacher teacher = teacherDao.getById(teacherId);
//
//            map.put("subjects", subjects);
//            map.put("teacherId", teacherId);
//            map.put("teacher_lessons", teacherLessons);
//            map.put("teacher", teacher);
//
//            return new ModelAndView(map, "teacher.handlebars");
//         }, new HandlebarsTemplateEngine());

//         get("/teacher/:teacherId/lesson/:lessonId", (req, res) -> {
//            Map<String, Object> map = new HashMap<>();
//            Long teacherId = Long.parseLong(req.params("teacherId"));
//            Long lessonId = Long.parseLong(req.params("lessonId"));
//            List<Learner> learnerList = learnerDao.getLearnersFor(lessonId);
//            Lesson lesson = lessonDao.getById(lessonId);
//            Multimap<Lesson, Subject> subjectForLesson = subjectDao.getSubjectForLesson(lessonId);
//            String subjectName = null;
//
//            for (Map.Entry<Lesson, Subject> item: subjectForLesson.entries())
//               subjectName = item.getValue().getSubject_name();
//
//            map.put("teacherId", teacherId);
//            map.put("lessonId", lessonId);
//            map.put("learnerList", learnerList);
//            map.put("lesson", lesson);
//            map.put("subjectName", subjectName);
//
//            return new ModelAndView(map, "teachLesson.handlebars");
//         }, new HandlebarsTemplateEngine());
//
//         get("/teacher/:teacherId/lesson/:lessonId/teach", (req, res) -> {
//            Map<String, Object> map = new HashMap<>();
//            Long teacherId = Long.parseLong(req.params("teacherId"));
//            Long lessonId = Long.parseLong(req.params("lessonId"));
//
//            System.out.println("TEACHING A LESSON...");
//
//            map.put("lessonId", lessonId);
//
//            res.redirect("/teacher/" + teacherId + "/lesson/"  + lessonId);
//
//            return new ModelAndView(map, "attendLesson.handlebars");
//         }, new HandlebarsTemplateEngine());
//
//
//         get("/learner/:learnerId/lesson/:lessonId", (req, res) -> {
//            Map<String, Object> map = new HashMap<>();
//            Long learnerId = Long.parseLong(req.params("learnerId"));
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
//
//         get("/learner/:learnerId/lesson/:lessonId/attend", (req, res) -> {
//            Map<String, Object> map = new HashMap<>();
//            Long learnerId = Long.parseLong(req.params("learnerId"));
//            Long lessonId = Long.parseLong(req.params("lessonId"));
//
//           learnerDao.attendLesson(learnerId, lessonId);
//           System.out.println("ATTENDING A LESSON...");
//
//            map.put("learnerId", learnerId);
//            map.put("lessonId", lessonId);
//
//            res.redirect("/learner/" + learnerId + "/lesson/"  + lessonId);
//
//            return new ModelAndView(map, "attendLesson.handlebars");
//         }, new HandlebarsTemplateEngine());


      }
      catch (SQLException ex) {
         ex.printStackTrace();
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }

}
