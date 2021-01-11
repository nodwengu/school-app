package net.school.impl;

import net.school.dao.NotesDao;
import net.school.model.*;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class NotesDaoImpl implements NotesDao {
   private Jdbi jdbi;
   private List<Notes> notes = new ArrayList<>();
   private List<Notes> notesByLesson = new ArrayList<>();

   public NotesDaoImpl(Jdbi jdbi) {
      this.jdbi = jdbi;
   }

   @Override
   public boolean add(Notes notes) {
      jdbi.useHandle(handle -> handle.execute("INSERT INTO notes(title, notes, lesson_id) VALUES(?, ?, ?)",
              notes.getTitle(), notes.getNotes(), notes.getLessonId())
      );
      return true;
   }

   @Override
   public List<Notes> getAll() {
      return jdbi.withHandle(handle -> handle.createQuery("SELECT id, title, notes, lesson_id FROM notes")
              .mapToBean(Notes.class)
              .list()
      );
   }

   @Override
   public List<Notes> getAllByLessonId(Long lessonId) {
      return jdbi.withHandle(handle -> handle.createQuery("SELECT id, title, notes, lesson_id FROM notes WHERE lesson_id=?")
              .bind(0, lessonId)
              .mapToBean(Notes.class)
              .list()
      );
   }

   @Override
   public boolean update(Long notesId, Notes notes) {
      return false;
   }

   @Override
   public Notes getById(Long notesId) {
      return jdbi.withHandle(handle -> handle.createQuery("SELECT id, title, lesson_id, notes FROM notes WHERE id = ?")
         .bind(0, notesId)
         .mapToBean(Notes.class)
         .findOnly()
      );
   }


   @Override
   public boolean delete(Long notesId) {
      return false;
   }

   @Override
   public boolean addLearnerNotes(Long learnerId, Long notesId) {
      jdbi.useHandle(handle -> handle.execute("INSERT INTO learner_notes(learner_id, notes_id) VALUES(?, ?)",
              learnerId, notesId)
      );
      return true;
   }

   @Override
   public Long getIdByLessonId(Long lessonId) {
       return jdbi.withHandle(handle -> handle.createQuery("SELECT id FROM notes WHERE lesson_id = ?")
              .bind(0, lessonId)
              .mapTo(Long.class)
              .findOnly());
   }

   @Override
   public List<Notes> getLearnerNotes(Long learnerId) {
      String sql = "SELECT ls.id ls_id, ls.lesson_name ls_lesson_name, ls.grade_id ls_grade_id, ls.day_id ls_day_id," +
              "s.id s_id, s.subject_name s_subject_name, " +
              "n.id n_id, n.title n_title, n.notes n_notes, " +
              "lnn.id lnn_id, lnn.source lnn_source, " +
              "ln.id ln_id " +
              "FROM lesson ls " +
              "INNER JOIN subject s " +
              "ON ls.subject_id = s.id " +
              "INNER JOIN notes n " +
              "ON ls.id = n.lesson_id " +
              "INNER JOIN learner_notes lnn " +
              "ON n.id = lnn.notes_id " +
              "INNER JOIN learner ln " +
              "ON lnn.learner_id = ln.id " +
              "WHERE ln.id = " + learnerId;

      return jdbi.withHandle(handle -> {
         notes = handle.createQuery(sql)
                 .registerRowMapper(BeanMapper.factory(Lesson.class, "ls"))
                 .registerRowMapper(BeanMapper.factory(Subject.class, "s"))
                 .registerRowMapper(BeanMapper.factory(LearnerNotes.class, "lnn"))
                 .registerRowMapper(BeanMapper.factory(Learner.class, "ln"))
                 .registerRowMapper(BeanMapper.factory(Notes.class, "n"))

                 .reduceRows(new LinkedHashMap<Long, Notes>(), (map, rowView) -> {
                    Notes notes = map.computeIfAbsent(rowView.getColumn("n_id", Long.class), id -> rowView.getRow(Notes.class));

                    if (rowView.getColumn("ls_id", Long.class) != null)
                       notes.addLesson(rowView.getRow(Lesson.class));

                    if (rowView.getColumn("s_id", Long.class) != null)
                       notes.addSubject(rowView.getRow(Subject.class));

                    if (rowView.getColumn("lnn_id", Long.class) != null)
                       notes.addLearnerNotes(rowView.getRow(LearnerNotes.class));

                    if (rowView.getColumn("ln_id", Long.class) != null)
                       notes.addLearner(rowView.getRow(Learner.class));

                    return map;

                 })
                 .values()
                 .stream()
                 .collect(toList());
         return notes;
      });
   }

   @Override
   public boolean deleteLearnerNotes(Long learnerId, Long notesId) {
      jdbi.useHandle(handle -> handle.execute("DELETE FROM learner_notes WHERE learner_id=? AND notes_id=?",
              learnerId, notesId));
      return true;
   }

   @Override
   public boolean updateSource(Long learnerId, Long notesId, String source) {
      String sql = "UPDATE learner_notes SET source=? WHERE learner_id=? AND notes_id=?";

      jdbi.useTransaction(handle -> handle.createUpdate(sql)
              .bind(0, source)
              .bind(1, learnerId)
              .bind(2, notesId)
              .execute() );

      return true;
   }


   @Override
   public List<Notes> getNotesByLesson(Long lessonId) {
      String sql = "SELECT ls.id ls_id, ls.lesson_name ls_lesson_name, ls.grade_id ls_grade_id, ls.day_id ls_day_id," +
              "s.id s_id, s.subject_name s_subject_name, " +
              "n.id n_id, n.title n_title, n.notes n_notes, " +
              "lnn.id lnn_id, lnn.source lnn_source, " +
              "ln.id ln_id " +
              "FROM lesson ls " +
              "INNER JOIN subject s " +
              "ON ls.subject_id = s.id " +
              "INNER JOIN notes n " +
              "ON ls.id = n.lesson_id " +
              "INNER JOIN learner_notes lnn " +
              "ON n.id = lnn.notes_id " +
              "INNER JOIN learner ln " +
              "ON lnn.learner_id = ln.id " +
              "WHERE ls.id = " + lessonId;

      return jdbi.withHandle(handle -> {
         notes = handle.createQuery(sql)
                 .registerRowMapper(BeanMapper.factory(Lesson.class, "ls"))
                 .registerRowMapper(BeanMapper.factory(Subject.class, "s"))
                 .registerRowMapper(BeanMapper.factory(LearnerNotes.class, "lnn"))
                 .registerRowMapper(BeanMapper.factory(Learner.class, "ln"))
                 .registerRowMapper(BeanMapper.factory(Notes.class, "n"))

                 .reduceRows(new LinkedHashMap<Long, Notes>(), (map, rowView) -> {
                    Notes notes = map.computeIfAbsent(rowView.getColumn("n_id", Long.class), id -> rowView.getRow(Notes.class));

                    if (rowView.getColumn("ls_id", Long.class) != null)
                       notes.addLesson(rowView.getRow(Lesson.class));

                    if (rowView.getColumn("s_id", Long.class) != null)
                       notes.addSubject(rowView.getRow(Subject.class));

                    if (rowView.getColumn("lnn_id", Long.class) != null)
                       notes.addLearnerNotes(rowView.getRow(LearnerNotes.class));

                    if (rowView.getColumn("ln_id", Long.class) != null)
                       notes.addLearner(rowView.getRow(Learner.class));

                    return map;

                 })
                 .values()
                 .stream()
                 .collect(toList());
         return notes;
      });
   }
}
