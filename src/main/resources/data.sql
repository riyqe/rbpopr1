-- Инициализация демо-данных
-- Пользователи
INSERT INTO USERS (id, username, password, role)
VALUES (1, 'teacher', 'password', 'TEACHER');
INSERT INTO USERS (id, username, password, role)
VALUES (2, 'student', 'password', 'STUDENT');
ALTER TABLE USERS ALTER COLUMN ID RESTART WITH 3;

-- Студенты
INSERT INTO STUDENTS (id, name, email, user_id)
VALUES (1, 'Alice', 'alice@example.com', 2),
       (2, 'Bob', 'bob@example.com', NULL);
ALTER TABLE STUDENTS ALTER COLUMN ID RESTART WITH 3;

-- Курсы
INSERT INTO COURSES (id, title, description, teacher_id)
VALUES (1, 'RBPO', 'Basics of securing apps', 1),
       (2, 'Database Basics', 'Intro to SQL', 1);
ALTER TABLE COURSES ALTER COLUMN ID RESTART WITH 3;

-- пароль = password, BCrypt (strength=10)
UPDATE users SET password='$2a$10$EixZaYRU1zW63HzOJBr6Nu/vJyJrKxVDZxNGGJJVipX6VJqY6VUXO' WHERE username='teacher';
UPDATE users SET password='$2a$10$EixZaYRU1zW63HzOJBr6Nu/vJyJrKxVDZxNGGJJVipX6VJqY6VUXO' WHERE username='student';