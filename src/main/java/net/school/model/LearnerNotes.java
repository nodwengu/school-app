package net.school.model;

public class LearnerNotes {
   private Long id;
   private String source;
   private Long learnerId;
   private Long notesId;

   public LearnerNotes() {}

   public LearnerNotes(Long id, String source, Long learnerId, Long notesId) {
      this.id = id;
      this.source = source;
      this.learnerId = learnerId;
      this.notesId = notesId;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public void setSource(String source) {
      this.source = source;
   }

   public void setLearnerId(Long learnerId) {
      this.learnerId = learnerId;
   }

   public void setNotesId(Long notesId) {
      this.notesId = notesId;
   }

   public Long getId() {
      return id;
   }

   public String getSource() {
      return source;
   }

   public Long getLearnerId() {
      return learnerId;
   }

   public Long getNotesId() {
      return notesId;
   }


   @Override
   public String toString() {
      return "LearnerNotes{" +
              "id=" + id +
              ", source='" + source + '\'' +
              ", learnerId=" + learnerId +
              ", notesId=" + notesId +
              '}';
   }
}
