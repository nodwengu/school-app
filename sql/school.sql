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


INSERT INTO grade (grade_name) VALUES ('Grade 10');
INSERT INTO grade (grade_name) VALUES ('Grade 11');
INSERT INTO grade (grade_name) VALUES ('Grade 12');
INSERT INTO grade (grade_name) VALUES ('Grade 9');

INSERT INTO days (day_name) VALUES ('Monday');
INSERT INTO days (day_name) VALUES ('Tuesday');
INSERT INTO days (day_name) VALUES ('Wednesday');
INSERT INTO days (day_name) VALUES ('Thursday');
INSERT INTO days (day_name) VALUES ('Friday');