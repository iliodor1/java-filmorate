# java-filmorate
Template repository for Filmorate project.

![img_4.png](https://lh3.googleusercontent.com/pw/AM-JKLWHWvoBkn-6mgD3V-debCY72Lzn-T2M-fQxvZdiLGjP1Mf5OUyryrPNLvzIJwtU_5FDijhdoBQDmnbuZZLpPIONlAVTasbEAsMt4EmTJeq8RL7P5I9jQgEBEAraU0czXN65iNhB3Eqh6Y-ooCYE63vr=w981-h659-no?authuser=0.png)

Примеры запросов:

Запрос на получение всех пользователей
```
SELECT * 
FROM user;
```
Запрос на получение названия фильма с id = 25
```
SELECT name 
FROM film
WHERE id = 25;
```
Запрос на получение списка пользователей (id, login, name), которым был отправлен запрос на дружбу, но дружба не была подтверждена
```
SELECT id,
       login,
       name
FROM user
WHERE id in
    (SELECT friend_id
     FROM friend
     WHERE friendship = false);
```
Запрос на получение списка фильмов с рейтингом R и NC-17
```
SELECT *
FROM film
WHERE mpa_id in ('R','NC-17');
```
Запрос на получение Топ 10 популярных фильмов
```
SELECT f.name
       COUNT(l.user_id) AS likes
FROM film AS f
LEFT JOIN like AS l ON f.id = l.film_id
GROUP BY f.name
ORDER BY likes DESC
LIMIT 10;
```