package net.school.impl;

import net.school.dao.TeacherDao;
import net.school.model.*;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class TeacherDaoImpl implements TeacherDao {
   private Jdbi jdbi;
   private List<Teacher> teachers;
   private List<Lesson> teacherLessons = new ArrayList<>();
   private List<Teacher> subjectsList = new ArrayList<>();
   private List<Lesson> lessonsByDay = new ArrayList<>();

   public TeacherDaoImpl(Jdbi jdbi) {
      this.jdbi = jdbi;
   }

   public TeacherDaoImpl() {}

   @Override
   public List<Teacher> getAll() {
      return jdbi.withHandle( (handle) -> handle.createQuery("SELECT id, first_name, last_name, email, tokens FROM teacher")
           .mapToBean(Teacher.class)
           .list() );
   }

   @Override
   public boolean addTeacher(Teacher teacher) {
      jdbi.useHandle( (handle) -> handle.execute("INSERT INTO teacher (first_name, last_name, email, tokens) VALUES(?, ?, ?, ?)",
              teacher.getFirstName(),
              teacher.getLastName(),
              teacher.getEmail(),
              teacher.getTokens() )
      );
      return true;
   }

   public int testDivide(int x, int y) {
      return x / y;
   }

   @Override
   public Teacher getById(Long id) {
        return jdbi.withHandle( (handle) -> handle.createQuery("SELECT id, first_name, last_name, email, tokens FROM teacher WHERE id = :id")
           .bind("id", id)
           .mapToBean(Teacher.class)
           .findOnly() );
   }

   @Override
   public boolean selectSubject(Long teacherId, Long subjectId, Long gradeId) {
      jdbi.useHandle( handle -> handle.execute("INSERT INTO teacher_subject(teacher_id, subject_id, grade_id) VALUES(?,?,?)",
              teacherId, subjectId, gradeId) );
      return true;
   }

   @Override
   public boolean delete(Long teacherId) {
      jdbi.useHandle(handle -> handle.execute("DELETE FROM teacher WHERE id = ?", teacherId));
      return false;
   }

   @Override
   public boolean update(Long id, Teacher teacher) {
      String sql = "UPDATE teacher SET first_name=?, last_name=?, email=?, tokens=? WHERE id=?";

      jdbi.useTransaction(handle -> handle.createUpdate(sql)
              .bind(0, teacher.getFirstName())
              .bind(1, teacher.getLastName())
              .bind(2, teacher.getEmail())
              .bind(3, teacher.getTokens())
              .bind(4, teacher.getId())
              .execute() );

      return true;
   }

   @Override
   public List<Lesson> getTeacherLessons(Long theId) {
      String sql = "SELECT t.id t_id, t.first_name t_first_name, t.last_name t_last_name, t.email t_email, t.tokens t_tokens, " +
              "ts.id ts_id, ts.teacher_id ts_teacher_id, ts.subject_id ts_subject_id, " +
              "s.id s_id, s.subject_name s_subject_name, " +
              "d.id d_id, d.day_name d_day_name, " +
              "g.id g_id, g.grade_name g_grade_name, " +
              "l.id l_id, l.lesson_name l_lesson_name, l.subject_id l_subject_id, l.time l_time, l.grade_id l_grade_id, l.day_id l_day_id " +
              "FROM teacher t " +
              "INNER JOIN teacher_subject ts " +
              "ON ts.teacher_id = t.id " +
              "INNER JOIN subject s " +
              "ON ts.subject_id = s.id " +
              "INNER JOIN lesson l " +
              "ON l.subject_id = s.id " +
              "INNER JOIN days d " +
              "ON d.id = l.day_id " +
              "INNER JOIN grade g " +
              "ON g.id = l.grade_id " +
              "WHERE t.id = " + theId;

      return jdbi.withHandle( handle -> {
         teacherLessons = handle.createQuery(sql)
              .registerRowMapper(BeanMapper.factory(Teacher.class, "t"))
              .registerRowMapper(BeanMapper.factory(TeacherSubject.class, "ts"))
              .registerRowMapper(BeanMapper.factory(Subject.class, "s"))
              .registerRowMapper(BeanMapper.factory(Lesson.class, "l"))
                 .registerRowMapper(BeanMapper.factory(Day.class, "d"))
                 .registerRowMapper(BeanMapper.factory(Grade.class, "g"))
              .reduceRows(new LinkedHashMap<Long, Lesson>(), (map, rowView) -> {
                 Lesson lesson = map.computeIfAbsent(
                         rowView.getColumn("l_id", Long.class),
                         id -> rowView.getRow(Lesson.class)
                 );

                 if (rowView.getColumn("ts_id", Long.class) != null)
                    lesson.addTeacherSubject(rowView.getRow(TeacherSubject.class));

                 if (rowView.getColumn("s_id", Long.class) != null)
                    lesson.addSubject(rowView.getRow(Subject.class));

                 if (rowView.getColumn("l_id", Long.class) != null)
                    lesson.addLesson(rowView.getRow(Lesson.class));

                 if (rowView.getColumn("d_id", Long.class) != null)
                    lesson.addDay(rowView.getRow(Day.class));

                 if (rowView.getColumn("g_id", Long.class) != null)
                    lesson.addGrade(rowView.getRow(Grade.class));

                 return map;

              })
              .values()
              .stream()
              .collect(toList());

         return teacherLessons;
      });
   }

   @Override
   public List<Teacher> getAllSubjects(Long myId) {
      String sql = "SELECT t.id t_id, t.first_name t_first_name, t.last_name t_last_name, t.email t_email, t.tokens t_tokens, " +
              "ts.id ts_id, ts.teacher_id ts_teacher_id, ts.subject_id ts_subject_id, " +
              "s.id s_id, s.subject_name s_subject_name " +
              "FROM teacher t " +
              "INNER JOIN teacher_subject ts " +
              "ON ts.teacher_id = t.id " +
              "INNER JOIN subject s " +
              "ON ts.subject_id = s.id " +
              "WHERE t.id = " + myId;

      return jdbi.withHandle( handle -> {
         subjectsList = handle.createQuery(sql)
                 .registerRowMapper(BeanMapper.factory(Teacher.class, "t"))
                 .registerRowMapper(BeanMapper.factory(TeacherSubject.class, "ts"))
                 .registerRowMapper(BeanMapper.factory(Subject.class, "s"))
                 .reduceRows(new LinkedHashMap<Long, Teacher>(), (map, rowView) -> {
                    Teacher teacher = map.computeIfAbsent(
                            rowView.getColumn("t_id", Long.class),
                            id -> rowView.getRow(Teacher.class)
                    );

                    if (rowView.getColumn("ts_id", Long.class) != null)
                       teacher.addTeacherSubject(rowView.getRow(TeacherSubject.class));

                    if (rowView.getColumn("s_id", Long.class) != null)
                       teacher.addSubject(rowView.getRow(Subject.class));

                    return map;

                 })
                 .values()
                 .stream()
                 .collect(toList());

         return subjectsList;
      });
   }

   @Override
   public boolean removeTeacherSubject(Long id) {
      jdbi.useHandle(handle -> handle.execute("DELETE FROM teacher_subject WHERE subject_id = ?",id));
      return true;
   }

   @Override
   public boolean cancelLesson(Long learnerId, Long lessonId) {
      jdbi.useHandle(handle -> handle.execute("DELETE FROM learner_lesson_attendant WHERE learner_id=? AND lesson_id=?",
              learnerId, lessonId)
      );
      return true;
   }


   @Override
   public List<Lesson> getLessonsByDay(Long teacherId, Long dayId) {
      String sql = "SELECT t.id t_id, t.first_name t_first_name, t.last_name t_last_name, t.email t_email, t.tokens t_tokens, " +
              "ts.id ts_id, ts.teacher_id ts_teacher_id, ts.subject_id ts_subject_id, " +
              "s.id s_id, s.subject_name s_subject_name, " +
              "d.id d_id, d.day_name d_day_name, " +
              "g.id g_id, g.grade_name g_grade_name, " +
              "ts.id ts_id, ts.teacher_id ts_teacher_id, ts.subject_id ts_subject_id, ts.grade_id ts_grade_id, " +
              "l.id l_id, l.lesson_name l_lesson_name, l.subject_id l_subject_id, l.time l_time, l.day_id l_day_id, l.grade_id l_grade_id " +
              "FROM teacher t " +
              "INNER JOIN teacher_subject ts " +
              "ON ts.teacher_id = t.id " +
              "INNER JOIN subject s " +
              "ON ts.subject_id = s.id " +
              "INNER JOIN lesson l " +
              "ON l.subject_id = s.id " +
              "INNER JOIN grade g " +
              "ON l.grade_id = g.id " +
              "INNER JOIN days d " +
              "ON l.day_id = d.id " +
//              "WHERE t.id = " + teacherId;
              "WHERE t.id = " + teacherId + " AND l.day_id = " + dayId;
//              "ORDER BY l.time ASC";

      return jdbi.withHandle( handle -> {
         lessonsByDay = handle.createQuery(sql)
                 .registerRowMapper(BeanMapper.factory(TeacherSubject.class, "ts"))
                 .registerRowMapper(BeanMapper.factory(Subject.class, "s"))
                 .registerRowMapper(BeanMapper.factory(Teacher.class, "t"))
                 .registerRowMapper(BeanMapper.factory(Grade.class, "g"))
                 .registerRowMapper(BeanMapper.factory(Day.class, "d"))
                 .registerRowMapper(BeanMapper.factory(Lesson.class, "l"))
                 .reduceRows(new LinkedHashMap<Long, Lesson>(), (map, rowView) -> {
                    Lesson lesson = map.computeIfAbsent(
                            rowView.getColumn("l_id", Long.class),
                            id -> rowView.getRow(Lesson.class)
                    );

                    if (rowView.getColumn("g_id", Long.class) != null)
                       lesson.addGrade(rowView.getRow(Grade.class));

                    if (rowView.getColumn("d_id", Long.class) != null)
                       lesson.addDay(rowView.getRow(Day.class));

                    if (rowView.getColumn("s_id", Long.class) != null)
                       lesson.addSubject(rowView.getRow(Subject.class));

//                    if (rowView.getColumn("t_id", Long.class) != null) {
//                       lesson.addTeacher(rowView.getRow(Teacher.class));
//                    }

                    return map;

                 })
                 .values()
                 .stream()
                 .collect(toList());

         return lessonsByDay;
      });
   }
}
