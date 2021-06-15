package net.school;

import com.google.common.collect.Multimap;
import net.school.dao.*;
import net.school.exceptions.InputRequiredException;
import net.school.exceptions.MinSubjectsException;
import net.school.exceptions.OutOfTokensException;
import net.school.impl.*;
import net.school.model.*;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.JoinRow;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static java.lang.Integer.parseInt;
import static spark.Spark.*;

public class App {
   private static Jdbi jdbi;

   private static String inputErrorMsg = "";
   private static String showStatus = "none";

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

         TeacherDao teacherDao = new TeacherDaoImpl(jdbi);
         SubjectDao subjectDao = new SubjectDaoImpl(jdbi);
         LessonDao lessonDao = new LessonDaoImpl(jdbi);
         LearnerDao learnerDao = new LearnerDaoImpl(jdbi);
         DayDao dayDao = new DayDaoImpl(jdbi);
         GradeDao gradeDao = new GradeDaoImpl(jdbi);
         NotesDao notesDao = new NotesDaoImpl(jdbi);
         ProductDao productDao = new ProductDaoImpl(jdbi);

         //BASE GET ROUTES

         get("/", (req, res) -> {
            Map<String, Object> map = new HashMap<>();

            res.redirect("/school/grade/" + 1);

            return new ModelAndView(map, "grade.handlebars");
         }, new HandlebarsTemplateEngine());


         get("/school/grade/:gradeId", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long gradeId = Long.parseLong(req.params("gradeId"));
            LocalDate nowDate = LocalDate.now();
            String currentDay = nowDate.getDayOfWeek().toString().toLowerCase();
            currentDay = currentDay.substring(0, 1).toUpperCase() + currentDay.substring(1);
            Long dayId = 1L;

            if (!(currentDay.equals("Saturday") || currentDay.equals("Sunday")))
               dayId = dayDao.getIdByName(currentDay);

            List<Grade> grades = gradeDao.getAll();
            List<Day> days = dayDao.getAll();
            Grade grade = gradeDao.getById(Long.parseLong(req.params("gradeId")));
            Day day = dayDao.getById(dayId);
            List<Learner> learners = learnerDao.getAll();
            List<Lesson> lessons = lessonDao.getAll();
            List<Subject> subjects = subjectDao.getAll();
            List<Teacher> teachers = teacherDao.getAll();
            List<Learner> learnersForGrade = learnerDao.getLearnersForGrade(gradeId);

            //HOW DO WILL GET DAY_ID IF I REMOVE THE ONE ON THE PARAMS
            List<Lesson> lessonsByGradeAndDay = lessonDao.getLessonsByGradeAndDay(gradeId, dayId);

            map.put("grades", grades);
            map.put("grade", grade);
            map.put("days", days);
            map.put("day", day);
            map.put("currentDay", currentDay);
            map.put("lessons", lessons);
            map.put("subjects", subjects);
            map.put("teachers", teachers);
            map.put("learnersForGrade", learnersForGrade);
            map.put("lessonsByGradeAndDay", lessonsByGradeAndDay);
            map.put("inputErrorMsg", inputErrorMsg);
            map.put("showStatus", showStatus);

            return new ModelAndView(map, "grade.handlebars");
         }, new HandlebarsTemplateEngine());




         // LESSON ADD & DELETE
         post("/school/grade/:gradeId/lesson/add", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            //Long dayId = Long.parseLong(req.params("dayId"));
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

            res.redirect("/school/grade/" + gradeId);

            return new ModelAndView(map, "grade.handlebars");
         }, new HandlebarsTemplateEngine());

         get("/school/grade/:gradeId/lesson/:lessonId/delete", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long gradeId = Long.parseLong(req.params("gradeId"));
            //Long dayId = Long.parseLong(req.params("dayId"));
            Long lessonId = Long.parseLong(req.params("lessonId"));

            lessonDao.delete(lessonId);

            res.redirect("/school/grade/" + gradeId);

            return new ModelAndView(map, "grade.handlebars");
         }, new HandlebarsTemplateEngine());




         // LEARNER ADD, DELETE & UPDATE
         post("/school/grade/:gradeId/learner/add", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Learner learner = new Learner();
            Long gradeId = Long.parseLong(req.params("gradeId"));
            //Long dayId = Long.parseLong(req.params("dayId"));

            String firstName = req.queryParams("firstName").toLowerCase();
            String lastName = req.queryParams("lastName").toLowerCase();
            String email = req.queryParams("email").toLowerCase();
            String gradeName = req.queryParams("grade");

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

            map.put("inputErrorMsg", inputErrorMsg);
            map.put("showStatus", showStatus);

            res.redirect("/school/grade/" + gradeId);

            return new ModelAndView(map, "grade.handlebars");
         }, new HandlebarsTemplateEngine());


         get("/school/grade/:gradeId/learner/:learnerId/delete", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long gradeId = Long.parseLong(req.params("gradeId"));
            //Long dayId = Long.parseLong(req.params("dayId"));
            Long learnerId = Long.parseLong(req.params("learnerId"));

            learnerDao.delete(learnerId);

            res.redirect("/school/grade/" + gradeId);

            return new ModelAndView(map, "grade.handlebars");
         }, new HandlebarsTemplateEngine());

         //         get("/school/grade/:gradeId/learner/:learnerId/update", (req, res) -> {
//            Map<String, Object> map = new HashMap<>();
//            Long gradeId = Long.parseLong(req.params("gradeId"));
//            //Long dayId = Long.parseLong(req.params("dayId"));
//            //Long learnerId = Long.parseLong(req.params("learnerId"));
//
//            System.out.println("UPDATING THE LEARNER INFORMATION");
//
//            res.redirect("/school/grade/" + gradeId);
//
//            return new ModelAndView(map, "grade.handlebars");
//         }, new HandlebarsTemplateEngine());

         post("/school/grade/:gradeId/learner/:learnerId/update", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long gradeId = Long.parseLong(req.params("gradeId"));
            Long dayId = Long.parseLong(req.params("dayId"));
            Long learnerId = Long.parseLong(req.params("learnerId"));

            System.out.println("UPDATING THE LEARNER INFORMATION");

            res.redirect("/school/grade/" + gradeId);

            return new ModelAndView(map, "grade.handlebars");
         }, new HandlebarsTemplateEngine());





         // TEACHER ADD & DELETE
         post("/school/grade/:id/teacher/add", (req, res) -> {
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

            res.redirect("/school/grade/" + req.params("id"));

            return new ModelAndView(map, "grade.handlebars");
         }, new HandlebarsTemplateEngine());

         get("/school/grade/:gradeId/teacher/:teacherId/delete", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long gradeId = Long.parseLong(req.params("gradeId"));
            //Long dayId = Long.parseLong(req.params("dayId"));
            Long teacherId = Long.parseLong(req.params("teacherId"));

            teacherDao.delete(teacherId);

            res.redirect("/school/grade/" + gradeId);

            return new ModelAndView(map, "grade.handlebars");
         }, new HandlebarsTemplateEngine());




         // SUBJECT ADD, DELETE & UPDATE
         post("/school/grade/:gradeId/subject/add", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            //Long dayId = Long.parseLong(req.params("dayId"));
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

            res.redirect("/school/grade/" + gradeId);
            return new ModelAndView(map, "grade.handlebars");
         }, new HandlebarsTemplateEngine());

         get("/school/grade/:gradeId/subject/:subjectId/delete", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long gradeId = Long.parseLong(req.params("gradeId"));
            //Long dayId = Long.parseLong(req.params("dayId"));
            Long subjectId = Long.parseLong(req.params("subjectId"));

            subjectDao.delete(subjectId);

            res.redirect("/school/grade/" + gradeId);

            return new ModelAndView(map, "grade.handlebars");
         }, new HandlebarsTemplateEngine());

         post("/school/grade/:gradeId/subject/:subjectId/update", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long subjectId = Long.parseLong(req.params("subjectId"));
            System.out.println(subjectId);
            Long gradeId = Long.parseLong(req.params("gradeId"));
//            Subject subject = subjectDao.getById(subjectId);
//            String newName = req.queryParams("newName");
//
//            subject.setSubjectName(newName);
//            subjectDao.update(subject);

            res.redirect("/school/grade/" + gradeId);

            return new ModelAndView(map, "grade.handlebars");
         }, new HandlebarsTemplateEngine());




         // LEARNER ROUTES

         get("/learner/:learnerId/day/:dayId/lessons", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long learnerId = Long.parseLong(req.params("learnerId"));
            Long dayId = Long.parseLong(req.params("dayId"));

            List<Subject> subjects = subjectDao.getAll();
            List<Learner> learnerSubjects = learnerDao.getSubjects(learnerId);
            Learner learner = learnerDao.getById(learnerId);

            List<Lesson> lessons = learnerDao.getLessons(learnerId);
            List<Lesson> lessonsByDay = learnerDao.getLessonsByDay(learnerId, dayId);
            List<Day> days = dayDao.getAll();
            Day day = dayDao.getById(dayId);
            List<Lesson> LessonsByGradeAndDay = lessonDao.getUserLessonsByGradeAndDay(learnerId, learner.getGradeId(), dayId);

            List<Notes> notes = notesDao.getLearnerNotes(learnerId);
            List<Learner> classmates = learnerDao.getClassmates(learner.getGradeId());
            List<Subject> newSubjects = new ArrayList<>();

            System.out.println("TESTING SUBJECTS: " + subjects);

            for (Subject s: subjects) {
               for (Learner ls: learnerSubjects) {
                  for (Subject sub: ls.getSubjects()) {
                     if (s.getSubjectName().equals(sub.getSubjectName())) {
                        s.setSelected("checked");
                     }
                  }
               }
               newSubjects.add(s);
            }

            System.out.println("NEW SUBJECTS: " + newSubjects);

            map.put("subjects", subjects);
            map.put("newSubjects", newSubjects);
            map.put("lessons", lessons);
            //map.put("lessonsByDay", lessonsByDay);
            map.put("learnerSubjects", learnerSubjects);
            map.put("learner", learner);
            map.put("notes", notes);
            map.put("classmates", classmates);
            map.put("days", days);
            map.put("day", day);
            map.put("dayId", dayId);
            map.put("LessonsByGradeAndDay", LessonsByGradeAndDay);
            map.put("inputErrorMsg", inputErrorMsg);
            map.put("showStatus", showStatus);

            return new ModelAndView(map, "learner.handlebars");
         }, new HandlebarsTemplateEngine());


         post("/learner/:learnerId/day/:dayId/lessons", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long learnerId = Long.parseLong(req.params("learnerId"));
            Long dayId = Long.parseLong(req.params("dayId"));
            showStatus = "none";

            try {
               if (req.queryParams().size() < 1)
                  throw new InputRequiredException("Choose at least one subject");

               for (String subject: req.queryParams())
                  learnerDao.selectSubject(learnerId, subjectDao.getId(subject));

            } catch (InputRequiredException ex) {
               System.out.println(ex.getMessage());
               inputErrorMsg = ex.getMessage();
               showStatus = "block";
            }

            res.redirect("/learner/" + learnerId + "/day/" + dayId + "/lessons");

            return new ModelAndView(map, "learner.handlebars");
         }, new HandlebarsTemplateEngine());


         get("/learner/:learnerId/day/:dayId/subject/:subjectId/delete", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long learnerId = Long.parseLong(req.params("learnerId"));
            Long subjectId = Long.parseLong(req.params("subjectId"));
            Long dayId = Long.parseLong(req.params("dayId"));

            learnerDao.removeLearnerSubject(subjectId);

            res.redirect("/learner/" + learnerId + "/day/" + dayId + "/lessons");

            return new ModelAndView(map, "learner.handlebars");
         }, new HandlebarsTemplateEngine());

         get("/learner/:learnerId/lesson/:lessonId/attend", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long learnerId = Long.parseLong(req.params("learnerId"));
            Long lessonId = Long.parseLong(req.params("lessonId"));
            Lesson lesson = lessonDao.getById(lessonId);
            List<Subject> subjectList = learnerDao.allSubjects(learnerId);

            try {
               if (subjectList.size() < 3) {
                  throw new MinSubjectsException("Need to be registered for 3 or more subjects");
               }

              learnerDao.attendLesson(learnerId, lessonId);;
            }
            catch (MinSubjectsException ex) {
               System.out.println(ex.getMessage());
               inputErrorMsg = ex.getMessage();
               showStatus = "block";
            }

            map.put("learnerId", learnerId);
            map.put("lessonId", lessonId);

            res.redirect("/learner/" + learnerId + "/day/" + lesson.getDayId() + "/lessons" );

            return new ModelAndView(map, "attendLesson.handlebars");
         }, new HandlebarsTemplateEngine());


         get("/learner/:learnerId/day/:dayId/classmate/:classmateId/notes", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long learnerId = Long.parseLong(req.params("learnerId"));
            Long classmateId = Long.parseLong(req.params("classmateId"));
            Long dayId = Long.parseLong(req.params("dayId"));
            Learner classmate = learnerDao.getById(classmateId);
            Learner learner = learnerDao.getById(learnerId);
            Day day = dayDao.getById(dayId);

            List<Notes> classmateNotes = notesDao.getLearnerNotes(classmateId);

            map.put("classmateNotes", classmateNotes);
            map.put("classmate", classmate);
            map.put("learner", learner);
            map.put("day", day);

            //res.redirect("/learner/" + learnerId + "/classmate/" + classmateId + "/notes");

            return new ModelAndView(map, "buyNotes.handlebars");
         }, new HandlebarsTemplateEngine());


         get("/learner/:learnerId/day/:dayId/notes/:notesId/buy", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long learnerId = Long.parseLong(req.params("learnerId"));
            Long notesId = Long.parseLong(req.params("notesId"));
            Long dayId = Long.parseLong(req.params("dayId"));
            Notes notes = notesDao.getById(notesId);

            // NEED TO FOCUS ON THIS
            Lesson lesson = lessonDao.getById(notes.getLessonId());      //lesson has subject_id
            Subject subject = subjectDao.getById(lesson.getSubjectId());
            List<Subject> subjects = learnerDao.allSubjects(learnerId);

            boolean isUserRegistered = false;

            Subject subject1 = new Subject(subjects.get(1).getId(), subjects.get(1).getSubjectName());
            Subject subject2 = new Subject(subject.getId(), subject.getSubjectName());
            System.out.println(subject);
            System.out.println(subject1.equals(subject2));

            System.out.println(subjects.get(1));
//            for (Subject item: subjects) {
//               System.out.println(item);
//               if (subject.equals(item)) {
//                  isUserRegistered = true;
//                  System.out.println("Prints nothing");
//               }
//            }

            System.out.println("isUserRegistered: " + isUserRegistered);

            if (isUserRegistered) {
               System.out.println("THIS USER IS REGISTERED....");
            } else {
               System.out.println("THIS USER IS NOT REGISTERED....");
            }

//            System.out.println("Subject:  " + subject);
//            System.out.println("Subjects:  " + subjects);
//            System.out.println("CONTAINS: " + subjects.contains(subject));

            List<Notes>  learnerNotes = notesDao.getLearnerNotes(learnerId);

            // ARE YOU REGISTERED FOR THE SUBJECT
            // DEDUCT 5 TOKENS IF NOT REGISTERED FOR THE SUBJECT
            // GET SUBJECT BY NOTE ID

            System.out.println("PREPARING TO BUY NOTES...");
            System.out.println("/learner/:learnerId/notes/:notesId/buy");
            Learner learner = learnerDao.getById(learnerId);
            //Long classmateId = Long.parseLong(req.params("classmateId"));


            //notesDao.addLearnerNotes(learnerId, notesId);
            //notesDao.updateSource(learnerId, notesId, "bought");

//            // TIME TO SUBTRACT 2 TOKENS FROM THE CURRENT LEARNER
            //learner.decreaseTokens(2);
            //learnerDao.update(learnerId, learner);

            map.put("learner", learner);

            res.redirect("/learner/" + learnerId + "/day/" + dayId + "/lessons");
            return new ModelAndView(map, "buyNotes.handlebars");
         }, new HandlebarsTemplateEngine());


         get("/learner/:learnerId/day/:dayId/notes/:notesId/delete", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long learnerId = Long.parseLong(req.params("learnerId"));
            Long notesId = Long.parseLong(req.params("notesId"));
            Long dayId = Long.parseLong(req.params("dayId"));

            notesDao.deleteLearnerNotes(learnerId, notesId);

            res.redirect("/learner/" + learnerId + "/day/" + dayId + "/lessons");

            return new ModelAndView(map, "buyNotes.handlebars");
         }, new HandlebarsTemplateEngine());

         get("/learner/:learnerId/day/:dayId/cafeteria", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long learnerId = Long.parseLong(req.params("learnerId"));
            Long dayId = Long.parseLong(req.params("dayId"));
            Day day = dayDao.getById(dayId);

            String error = inputErrorMsg;

            Learner learner = learnerDao.getById(learnerId);
            List<Product> products = productDao.getAll();
            List<Product> buyerHistoryItems = productDao.getBuyerHistory(learnerId);

            map.put("learner", learner);
            map.put("day", day);
            map.put("products", products);
            map.put("buyerHistoryItems", buyerHistoryItems);
            map.put("inputError", inputErrorMsg);
            map.put("showStatus", showStatus);
            map.put("error", inputErrorMsg);

            System.out.println("inputErrorMsg in CAFETERIA: " + inputErrorMsg);
            System.out.println("showStatus in CAFETERIA: " + showStatus);

            return new ModelAndView(map, "cafeteria.handlebars");
         }, new HandlebarsTemplateEngine());

         get("/learner/:learnerId/day/:dayId/product/:productId/buy", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long learnerId = Long.parseLong(req.params("learnerId"));
            Long productId = Long.parseLong(req.params("productId"));
            Long dayId = Long.parseLong(req.params("dayId"));

            Learner learner = learnerDao.getById(learnerId);
            Product product = productDao.getById(productId);

            try {
               if (learner.getTokens() < product.getCost()) {
                  throw new OutOfTokensException("DO NOT HAVE ENOUGH TOKENS");
               }

               learner.decreaseTokens(product.getCost());
               learnerDao.update(learner.getId(), learner);

               // Add product to sold items
               productDao.addToSales(learner.getId(), learner.getFirstName(), learner.getLastName(), product.getId(), dayId);
               productDao.addToBuyerHistory(learnerId, productId, dayId);
            }
            catch (OutOfTokensException ex) {
               System.out.println(ex.getMessage());
               inputErrorMsg = ex.getMessage();
               showStatus = "block";
            }

            res.redirect("/learner/" + learnerId + "/day/" + dayId + "/cafeteria");

            return new ModelAndView(map, "cafeteria.handlebars");
         }, new HandlebarsTemplateEngine());









         // TEACHER ROUTES

         get("/teacher/:id/day/:dayId/lessons", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            List<Subject> subjectList = subjectDao.getAll();
            Long teacherId = Long.parseLong(req.params("id"));
            Long dayId = Long.parseLong(req.params("dayId"));
            showStatus = "none";

            Day day = dayDao.getById(dayId);

            List<Lesson> teacherLessons = teacherDao.getTeacherLessons(teacherId);
                 List<Lesson> lessonsByDay = teacherDao.getLessonsByDay(teacherId, dayId);
            //List<Lesson> lessonsByDay = lessonDao.getUserLessonsByGradeAndDay(teacherId, gradeId, dayId);
            Teacher teacher = teacherDao.getById(teacherId);
            List<Teacher> teacherSubjects = teacherDao.getAllSubjects(teacherId);
            List<Grade> grades = gradeDao.getAll();
            List<Day> days = dayDao.getAll();

            map.put("subjectList", subjectList);
            map.put("teacherId", teacherId);
            map.put("lessonsByDay", lessonsByDay);
            map.put("teacher", teacher);
            map.put("teacherSubjects", teacherSubjects);
            map.put("grades", grades);
            map.put("day", day);
            map.put("days", days);
            map.put("showStatus", showStatus);
            map.put("inputErrorMsg", inputErrorMsg);

            return new ModelAndView(map, "teacher.handlebars");
         }, new HandlebarsTemplateEngine());

         post("/teacher/:id/day/:dayId/subject/add", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long teacherId = Long.parseLong(req.params("id"));
            String gradeName = req.queryParams("grade");
            Long dayId = Long.parseLong(req.params("dayId"));
            Day day = dayDao.getById(dayId);
            showStatus = "none";

            try {
               if ( req.queryParams().size() <= 1 || req.queryParams("grade").equals("select grade") ) {
                  throw new InputRequiredException("SELECT ONE OR MORE SUBJECTS");
               } else {
                  Long gradeId = gradeDao.getIdByName(gradeName);

                  for (String item: req.queryParams()) {
                     if (item.equals("grade")){
                        continue;
                     }
                     teacherDao.selectSubject(teacherId, subjectDao.getId(item), gradeId);
                  }
               }
            }
            catch (InputRequiredException ex) {
               System.out.println("Error: " + ex.getMessage());
               //ex.printStackTrace();
               inputErrorMsg = ex.getMessage();
               showStatus = "block";
            }

            res.redirect("/teacher/"  + req.params("id") + "/day/" + dayId + "/lessons");
            return new ModelAndView(map, "teacher.handlebars");
         }, new HandlebarsTemplateEngine());

         get("/teacher/:teacherId/day/:dayId/subject/:subjectId/delete", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long teacherId = Long.parseLong(req.params("teacherId"));
            Long subjectId = Long.parseLong(req.params("subjectId"));
            Long dayId = Long.parseLong(req.params("dayId"));

            teacherDao.removeTeacherSubject(subjectId);

            res.redirect("/teacher/" + teacherId + "/day/" + dayId + "/lessons");

            return new ModelAndView(map, "teacher.handlebars");
         }, new HandlebarsTemplateEngine());


         get("/teacher/:teacherId/day/:dayId/lesson/:lessonId", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long teacherId = Long.parseLong(req.params("teacherId"));
            Long lessonId = Long.parseLong(req.params("lessonId"));
            Long dayId = Long.parseLong(req.params("dayId"));
            Notes notes = null;

            Day day = dayDao.getById(dayId);

            List<Learner> learnerList = learnerDao.getLearnersAttending(lessonId);
            Lesson lesson = lessonDao.getById(lessonId);
            Multimap<Lesson, Subject> subjectForLesson = subjectDao.getSubjectForLesson(lessonId);
            String subjectName = null;

            List<Notes> notesByLesson = notesDao.getAllByLessonId(lessonId);
            String notesStatus = "Not Available";

            if (notesByLesson.size() > 0) {
               Long notesId = notesDao.getIdByLessonId(lessonId);
               notes = notesDao.getById(notesId);

               notesStatus = "Available";
            }

            int noOfLearners = learnerList.size();

            for (Map.Entry<Lesson, Subject> item: subjectForLesson.entries())
               subjectName = item.getValue().getSubjectName();

            map.put("teacherId", teacherId);
            map.put("lessonId", lessonId);
            map.put("learnerList", learnerList);
            map.put("lesson", lesson);
            map.put("subjectName", subjectName);
            map.put("noOfLearners", noOfLearners);
            map.put("notesByLesson", notesByLesson);
            map.put("notesStatus", notesStatus);
            map.put("day", day);

            return new ModelAndView(map, "teachLesson.handlebars");
         }, new HandlebarsTemplateEngine());


         post("/teacher/:teacherId/day/:dayId/lesson/:lessonId", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long teacherId = Long.parseLong(req.params("teacherId"));
            Long lessonId = Long.parseLong(req.params("lessonId"));
            Long dayId = Long.parseLong(req.params("dayId"));
            String lessonNotes = req.queryParams("notes");
            String title = req.queryParams("title");

            Notes notes = new Notes();
            notes.setTitle(title);
            notes.setNotes(lessonNotes);
            notes.setLessonId(lessonId);

            // Add lesson notes
            notesDao.add(notes);

            List<Learner> attendingLearners = learnerDao.getLearnersAttending(lessonId);
            Teacher teacher = teacherDao.getById(teacherId);

            map.put("lessonId", lessonId);

            res.redirect("/teacher/" + teacherId + "/day/" + dayId + "/lesson/"  + lessonId);

            return new ModelAndView(map, "teachLesson.handlebars");
         }, new HandlebarsTemplateEngine());

         get("/teacher/:teacherId/day/:dayId/lesson/:lessonId/teach", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long teacherId = Long.parseLong(req.params("teacherId"));
            Long lessonId = Long.parseLong(req.params("lessonId"));
            Long dayId = Long.parseLong(req.params("dayId"));

            //SHOULD THROW AN EXCEPTION IF INPUT IS EMPTY
            Lesson lesson = lessonDao.getById(lessonId);
            Long notesId = notesDao.getIdByLessonId(lessonId);

            List<Learner> attendingLearners = learnerDao.getLearnersAttending(lessonId);
            Teacher teacher = teacherDao.getById(teacherId);

            // ALL LEARNERS GET 3 TOKENS
            for (Learner learner: attendingLearners) {
               learner.increaseTokens(3);
               learnerDao.update(learner.getId(), learner);

               // Add notes for the learner
               notesDao.addLearnerNotes(learner.getId(), notesId);
            }

            // TEACHER SHOULD ALSO GET 5 TOKENS FOR TEACHING
            teacher.increaseTokens(5);
            teacherDao.update(teacherId, teacher);

            map.put("lessonId", lessonId);

            res.redirect("/teacher/" + teacherId + "/day/" + dayId + "/lesson/"  + lessonId);

            return new ModelAndView(map, "teachLesson.handlebars");
         }, new HandlebarsTemplateEngine());

         get("/teacher/:teacherId/lesson/:lessonId/cancel", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long teacherId = Long.parseLong(req.params("teacherId"));
            Long lessonId = Long.parseLong(req.params("lessonId"));
            List<Learner> attendingLearners = learnerDao.getLearnersAttending(lessonId);
            Teacher teacher = teacherDao.getById(teacherId);
            Lesson lesson = lessonDao.getById(lessonId);
            Long dayId = lesson.getDayId();

            // ALL LEARNERS DEDUCT 3 TOKENS
            for (Learner learner: attendingLearners) {
               learner.decreaseTokens(3);
               lessonDao.saveCancelledLesson(lessonId);
               learnerDao.update(learner.getId(), learner);
               teacherDao.cancelLesson(learner.getId(), lessonId);
            }

            // TEACHER SHOULD ALSO DEDUCT 5 TOKENS FOR TEACHING
            teacher.decreaseTokens(5);
            teacherDao.update(teacherId, teacher);

            map.put("lessonId", lessonId);

            res.redirect("/teacher/" + teacherId + "/day/" + dayId + "/lesson/"  + lessonId);

            return new ModelAndView(map, "teachLesson.handlebars");
         }, new HandlebarsTemplateEngine());


         get("/teacher/:teacherId/day/:dayId/cafeteria", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long teacherId = Long.parseLong(req.params("teacherId"));
            Long dayId = Long.parseLong(req.params("dayId"));
            Day day = dayDao.getById(dayId);

            Teacher teacher = teacherDao.getById(teacherId);
            List<Product> products = productDao.getAll();

            List<Product> buyerHistoryItems = productDao.getBuyerHistory(teacherId);

            map.put("buyerHistoryItems", buyerHistoryItems);
            map.put("teacher", teacher);
            map.put("products", products);
            map.put("day", day);

            //res.redirect("/learner/" + learnerId + "/lessons");

            return new ModelAndView(map, "teacherBuy.handlebars");
         }, new HandlebarsTemplateEngine());

         get("/teacher/:teacherId/day/:dayId/product/:productId/buy", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long teacherId = Long.parseLong(req.params("teacherId"));
            Long productId = Long.parseLong(req.params("productId"));
            Long dayId = Long.parseLong(req.params("dayId"));

            Teacher teacher = teacherDao.getById(teacherId);
            Product product = productDao.getById(productId);

            if (teacher.getTokens() < product.getCost()) {
               throw new OutOfTokensException("DO NOT HAVE ENOUGH TOKENS");
            }

            teacher.decreaseTokens(product.getCost());
            teacherDao.update(teacher.getId(), teacher);

            // Add product to sold items
            productDao.addToSales(teacher.getId(), teacher.getFirstName(), teacher.getLastName(), product.getId(), dayId);
            productDao.addToBuyerHistory(teacherId, productId, dayId);

            res.redirect("/teacher/" + teacherId + "/day/" + dayId + "/cafeteria");

            return new ModelAndView(map, "teacherBuy.handlebars");
         }, new HandlebarsTemplateEngine());


         get("/teacher/:teacherId/day/:dayId/product/:productId/delete", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long teacherId = Long.parseLong(req.params("teacherId"));
            Long dayId = Long.parseLong(req.params("dayId"));
            Long productId = Long.parseLong(req.params("productId"));

            productDao.deleteFromBuyerHistory(teacherId, productId);

            res.redirect("/teacher/" + teacherId + "/day/" + dayId + "/cafeteria");

            return new ModelAndView(map, "teacher.handlebars");
         }, new HandlebarsTemplateEngine());








         // REPORTS

         get("/manager/:managerId/summary", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long managerId = Long.parseLong(req.params("managerId"));


            System.out.println("WELCOME TO MANAGER SUMMARY...");
            List<SoldProduct> dailySales = productDao.getDailyProductsSold(2L);
            List<SoldProduct> allSales = productDao.getAllProductsSold();

            map.put("dailySales", dailySales);
            map.put("allSales", allSales);

            //res.redirect("/learner/" + learnerId + "/lessons");

            return new ModelAndView(map, "managerSummary.handlebars");
         }, new HandlebarsTemplateEngine());


         get("/principal/:principalId/report", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Long principalId = Long.parseLong(req.params("principalId"));


            System.out.println("WELCOME TO PRINCIPAL REPORT...");
            List<Lesson> lessons = lessonDao.getBySubjectAndDay(1L, 1L);
            List<Lesson> cancelledLessons = lessonDao.getCancelledLesson();

//            List<SoldProduct> allSales = productDao.getAllProductsSold();
//
            map.put("lessons", lessons);
            map.put("cancelledLessons", cancelledLessons);
//            map.put("allSales", allSales);

            //res.redirect("/learner/" + learnerId + "/lessons");

            return new ModelAndView(map, "principalReport.handlebars");
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
