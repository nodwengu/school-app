package net.school.dao;

import net.school.model.*;

import java.util.List;

public interface NotesDao {
   List<Notes> getAll();
   List<Notes> getAllByLessonId(Long lessonId);
   boolean add(Notes notes);
   boolean delete(Long notesId);
   boolean update(Long notesId, Notes notes);
   Notes getById(Long notesId);

   boolean addLearnerNotes(Long learnerId, Long notesId);
   Long getIdByLessonId(Long lessonId);
   List<Notes> getLearnerNotes(Long learnerId);
   List<Notes> getNotesByLesson(Long lessonId);

   boolean deleteLearnerNotes(Long learnerId, Long notesId);
   boolean updateSource(Long learnerId, Long notesId, String source);

}
