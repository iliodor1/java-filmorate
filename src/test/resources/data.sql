merge into mpa (id, name)
    values (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17');

merge into genres (id, name)
    values (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');

INSERT INTO users (email, login, name, birthday)
VALUES ( 'user1@gmail.com', 'user1', 'name1',  '1985-12-12');

INSERT INTO users (email, login, name, birthday)
VALUES ( 'user2@gmail.com', 'user2', 'user2',  '1950-12-12');

INSERT INTO users (email, login, name, birthday)
VALUES ( 'user3@gmail.com', 'user3', 'name1',  '2010-12-12');

INSERT INTO users (email, login, name, birthday)
VALUES ( 'user4@gmail.com', 'user4', 'user4',  '2000-12-12');

INSERT INTO films (name, description, release_date, duration, mpa_id)
VALUES ('name1', 'description', '1977-01-01', 100, 1);

INSERT INTO films (name, description, release_date, duration, mpa_id)
VALUES ('name3', 'description', '1980-02-03', 100, 2);
