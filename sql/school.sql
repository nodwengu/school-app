CREATE TABLE teacher (
   id SERIAL NOT NULL PRIMARY KEY,
   first_name TEXT NOT NULL,
   last_name TEXT NOT NULL,
   email TEXT NOT NULL,
   tokens INT NOT NULL
);

CREATE TABLE learner (
   id SERIAL NOT NULL PRIMARY KEY,
   first_name TEXT NOT NULL,
   last_name TEXT NOT NULL,
   email TEXT NOT NULL,
   tokens INT NOT NULL,
   grade_id INT NOT NUll
);

CREATE TABLE subject (
   id SERIAL NOT NULL PRIMARY KEY,
   subject_name TEXT NOT NULL
);

CREATE TABLE teacher_subject (
   id SERIAL NOT NULL PRIMARY KEY,
   teacher_id INT NOT NULL,
   subject_id INT NOT NULL,
   FOREIGN KEY (teacher_id) REFERENCES teacher(id),
   FOREIGN KEY (subject_id) REFERENCES subject(id)
);

CREATE TABLE learner_subject (
    id SERIAL NOT NULL PRIMARY KEY,
    learner_id INT NOT NULL,
    subject_id INT NOT NULL,
    FOREIGN KEY (learner_id) REFERENCES learner(id),
    FOREIGN KEY (subject_id) REFERENCES subject(id)
);

CREATE TABLE days (
    id SERIAL NOT NULL PRIMARY KEY,
    day_name TEXT NOT NULL
);

CREATE TABLE grade (
    id SERIAL NOT NULL PRIMARY KEY,
    grade_name TEXT NOT NULL
);

CREATE TABLE lesson (
   id SERIAL NOT NULL PRIMARY KEY,
   subject_id INT NOT NULL,
   lesson_name TEXT NOT NULL,
   grade_id INT NOT NULL,
   day_id INT NOT NULL,
   time TEXT NOT NULL,
   FOREIGN KEY (subject_id) REFERENCES subject(id),
   FOREIGN KEY (grade_id) REFERENCES grade(id),
   FOREIGN KEY (day_id) REFERENCES days(id)
);


CREATE TABLE learner_lesson_attendant (
   id SERIAL NOT NULL PRIMARY KEY,
   learner_id INT NOT NULL,
   lesson_id INT NOT NULL,
   FOREIGN KEY(learner_id) REFERENCES learner(id),
   FOREIGN KEY(lesson_id) REFERENCES lesson(id)
);


INSERT INTO grade (grade_name) VALUES ('Grade 10');
INSERT INTO grade (grade_name) VALUES ('Grade 11');
INSERT INTO grade (grade_name) VALUES ('Grade 12');
INSERT INTO grade (grade_name) VALUES ('Grade 9');

INSERT INTO days (day_name) VALUES ('Monday');
INSERT INTO days (day_name) VALUES ('Tuesday');
INSERT INTO days (day_name) VALUES ('Wednesday');
INSERT INTO days (day_name) VALUES ('Thursday');
INSERT INTO days (day_name) VALUES ('Friday');

 String sql = "SELECT ln.id ln_id, ln.first_name ln_first_name, ln.last_name ln_last_name, ln.email ln_email, ln.tokens ln_tokens, " +
              "ls.id ls_id, ls.learner_id ls_learner_id, ls.subject_id ls_subject_id, " +
              "s.id s_id, s.subject_name s_subject_name, " +
              "l.id l_id, l.lesson_name l_lesson_name, l.subject_id l_subject_id, l.time l_time, " +
              "t.id t_id, t.first_name t_first_name, t.last_name t_last_name, t.email t_email, t.tokens t_tokens " +
              "FROM learner ln " +
              "INNER JOIN learner_subject ls " +
              "ON ls.learner_id = ln.id " +
              "INNER JOIN subject s " +
              "ON ls.subject_id = s.id " +
              "INNER JOIN lesson l " +
              "ON l.subject_id = s.id " +
              "INNER JOIN teacher_subject ts " +
              "ON s.id = ts.subject_id " +
              "INNER JOIN teacher t " +
              "ON ts.teacher_id = t.id " +
              "WHERE ln.id = " + theId;

SELECT l.id, l.first_name, l.last_name, l.email, l.tokens,
ls.id, ls.learner_id, ls.subject_id,
s.id s_id, s.subject_name s_subject_name,
ln.id, ln.lesson_name, ln.subject_id, ln.time, ln.grade_id, ln.day_id,
t.id, t.first_name, t.last_name, t.email, t.tokens
FROM learner As l
INNER JOIN learner_subject As ls
ON l.id = ls.learner_id
INNER JOIN subject As s
ON ls.subject_id = s.id
INNER JOIN lesson As ln
ON s.id = ln.subject_id
INNER JOIN teacher_subject As ts
ON s.id = ts.subject_id
INNER JOIN teacher As t
ON ts.teacher_id = t.id
WHERE l.id = 6 and day_id = 1;


day/:dayId/learner/:learnerId/
