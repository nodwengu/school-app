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
   grade_id INT NOT NUll,
   FOREIGN KEY (teacher_id) REFERENCES teacher(id),
   FOREIGN KEY (subject_id) REFERENCES subject(id),
   FOREIGN KEY (grade_id) REFERENCES grade(id)
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

CREATE TABLE lessons_taught (
   id SERIAL NOT NULL PRIMARY KEY,
   lesson_id INT NOT NULL,
   FOREIGN KEY(lesson_id) REFERENCES lesson(id)
);

CREATE TABLE cancelled_lessons (
   id SERIAL NOT NULL PRIMARY KEY,
   lesson_id INT NOT NULL,
   FOREIGN KEY(lesson_id) REFERENCES lesson(id)
);


CREATE TABLE notes (
   id SERIAL NOT NULL PRIMARY KEY,
   title TEXT NOT NULL,
   lesson_id INT NOT NULL,
   notes TEXT NOT NULL,
   FOREIGN KEY(lesson_id) REFERENCES lesson(id)
);

CREATE TABLE learner_notes (
   id SERIAL NOT NULL PRIMARY KEY,
   source TEXT NOT NULL DEFAULT 'attended',
   learner_id INT NOT NULL,
   notes_id INT NOT NULL,
   FOREIGN KEY(learner_id) REFERENCES learner(id),
   FOREIGN KEY(notes_id) REFERENCES notes(id)
);

CREATE TABLE product (
   id SERIAL NOT NULL PRIMARY KEY,
   product_name TEXT NOT NULL,
   cost INT NOT NULL
);

CREATE TABLE sold_product (
   id SERIAL NOT NULL PRIMARY KEY,
   buyer_id INT NOT NULL,
   product_id INT NOT NULL,
   day_id INT NOT NULL,
   FOREIGN KEY(product_id) REFERENCES product(id),
   FOREIGN KEY(day_id) REFERENCES days(id)
);

CREATE TABLE buyer_history (
   id SERIAL NOT NULL PRIMARY KEY,
   buyer_id INT NOT NULL,
   product_id INT NOT NULL,
   day_id INT NOT NULL,
   FOREIGN KEY(product_id) REFERENCES product(id),
   FOREIGN KEY(day_id) REFERENCES days(id)
);

CREATE TABLE manager (
   id SERIAL NOT NULL PRIMARY KEY,
   first_name TEXT NOT NULL,
   last_name TEXT NOT NULL,
   email TEXT NOT NULL
);

CREATE TABLE sold_product (
   id SERIAL NOT NULL PRIMARY KEY,
   buyer_id INT NOT NULL,
   buyer_name TEXT NOT NULL,
   buyer_surname TEXT NOT NULL,
   product_id INT NOT NULL,
   day_id INT NOT NULL,
   FOREIGN KEY(product_id) REFERENCES product(id),
   FOREIGN KEY(day_id) REFERENCES days(id)
);

INSERT INTO product (product_name, cost) VALUES ('Breakfast', 4);
INSERT INTO product (product_name, cost) VALUES ('Afternoon Snack', 3);
INSERT INTO product (product_name, cost) VALUES ('Lunch', 6);
INSERT INTO product (product_name, cost) VALUES ('Drink', 2);



INSERT INTO grade (grade_name) VALUES ('Grade 10');
INSERT INTO grade (grade_name) VALUES ('Grade 11');
INSERT INTO grade (grade_name) VALUES ('Grade 12');
INSERT INTO grade (grade_name) VALUES ('Grade 9');

INSERT INTO days (day_name) VALUES ('Monday');
INSERT INTO days (day_name) VALUES ('Tuesday');
INSERT INTO days (day_name) VALUES ('Wednesday');
INSERT INTO days (day_name) VALUES ('Thursday');
INSERT INTO days (day_name) VALUES ('Friday');

INSERT INTO subject (subject_name) VALUES ('Mathematics');
INSERT INTO subject (subject_name) VALUES ('Biology');
INSERT INTO subject (subject_name) VALUES ('English');
INSERT INTO subject (subject_name) VALUES ('Economics');
INSERT INTO subject (subject_name) VALUES ('Accounting');

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


ublic | days                   | table    | qltgjstwngqxmo | 16 kB      |
 public | days_id_seq            | sequence | qltgjstwngqxmo | 8192 bytes |
 public | grade                  | table    | qltgjstwngqxmo | 16 kB      |
 public | grade_id_seq           | sequence | qltgjstwngqxmo | 8192 bytes |
 public | learner                | table    | qltgjstwngqxmo | 16 kB      |
 public | learner_id_seq         | sequence | qltgjstwngqxmo | 8192 bytes |
 public | learner_subject        | table    | qltgjstwngqxmo | 0 bytes    |
 public | learner_subject_id_seq | sequence | qltgjstwngqxmo | 8192 bytes |
 public | lesson                 | table    | qltgjstwngqxmo | 8192 bytes |
 public | lesson_id_seq          | sequence | qltgjstwngqxmo | 8192 bytes |
 public | subject_id_seq         | sequence | qltgjstwngqxmo | 8192 bytes |


 lesson
 subject
 notes
 learner_notes
 learner
 --GET BY LEANER ID FOR THAT LEANER

 --WHAT IS THE RELATIONSHIP BETWEEN LEARNER AND SUBJECTS
 learner
 learner_subject
 subject
 --Filter by grade_id
 WHERE ln.grade_id = gradeFromArgument

 -- GET CLASSMATE INFO
 lessons - 'lesson_name'
 subject - 'subject_name'
 notes - 'title', 'notes'

 select ls.lesson_name, subject_name, n.title, n.notes, ln.id, ln.first_name
 from lesson As ls
 inner join subject As s
 on ls.subject_id = s.id
 inner join notes As n
 on n.lesson_id = ls.id
 inner join learner_subject As lns
 on s.id = lns.subject_id
 inner join learner As ln
 on lns.learner_id = ln.id
 where ln.id = 18;

 school=> select * from lesson;
  id | subject_id |       lesson_name       | grade_id | day_id | time
 ----+------------+-------------------------+----------+--------+-------
   9 |          3 | Reading skills          |        1 |      1 | 08:00
  10 |          1 | Algebra                 |        1 |      1 | 09:00
  11 |          1 | Exponents               |        1 |      2 | 09:00
  12 |          1 | Number patterns         |        1 |      3 | 09:00
  13 |          1 | Trigonometry            |        1 |      5 | 09:00
  14 |          2 | Chemistry               |        1 |      1 | 10:00
  15 |          2 | Biochemistry            |        1 |      2 | 10:00
  17 |          2 | Genetics                |        1 |      4 | 10:00
  18 |          2 | Cell reproduction       |        1 |      5 | 10:00
  19 |          1 | Equations               |        1 |      4 | 09:00
  20 |          3 | Punctuation             |        1 |      2 | 08:00
  21 |          3 | Nouns                   |        1 |      3 | 08:00
  22 |          3 | Comprehension           |        1 |      4 | 08:00
  23 |          3 | Pronouns                |        1 |      5 | 08:00
  24 |          5 | Inventory systems       |        1 |      1 | 11:00
  25 |          5 | Bank reconciliation     |        1 |      2 | 11:00
  26 |          5 | Assets                  |        1 |      3 | 11:00
  27 |          5 | Financial statements    |        1 |      4 | 11:00
  28 |          5 | Cash flow statements    |        1 |      5 | 11:00
  29 |          4 | The circular flow model |        1 |      1 | 12:00
  30 |          4 | Business cycles         |        1 |      2 | 12:00
  31 |          4 | Role of public  sector  |        1 |      3 | 12:00
  32 |          4 | Foreign exchange        |        1 |      4 | 12:00
  33 |          4 | Free trade              |        1 |      5 | 11:00
  34 |          2 | Photosynthesis          |        1 |      3 | 10:00
  39 |          1 | Test lesson             |        2 |      1 | 05:00


SELECT p.id, p.product_name, p.cost, d.day_name,
l.id, l.first_name, l.last_name,
t.id, t.first_name, t.last_name
FROM product As p
INNER JOIN sold_product As sp
ON p.id = sp.product_id
INNER JOIN days As d
ON sp.day_id = d.id
INNER JOIN learner As l
ON sp.buyer_id = l.id
INNER JOIN teacher As t
ON sp.buyer_id = t.id
WHERE sp.day_id = 1;


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


SELECT p.id, p.product_name, p.cost, d.day_name,
l.id, l.first_name, l.last_name,
FROM product As p
INNER JOIN sold_product As sp
ON p.id = sp.product_id
INNER JOIN days As d
ON sp.day_id = d.id
INNER JOIN learner As l
ON sp.buyer_id = l.id
WHERE sp.day_id = 1;




