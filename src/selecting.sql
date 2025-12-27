--Найти номер модели, скорость и размер жесткого диска для всех ПК стоимостью менее 500 долларов.
SELECT model, speed, hd FROM pc WHERE price < 500::money;

--Найти производителей принтеров. Вывести поля: maker.
-- сделала уникальную выборку
SELECT DISTINCT maker from product WHERE type = 'Printer';

-- Найти номер модели, объем памяти и размеры экранов ноутбуков, цена которых превышает 1000 долларов.
SELECT model, ram, screen FROM laptop WHERE price > 1000::money;

--Найти все записи таблицы Printer для цветных принтеров.
SELECT * FROM printer WHERE color='y';

-- Найти номер модели, скорость и размер жесткого диска для ПК, имеющих скорость cd 12x или 24x и цену менее 600 долларов.
SELECT model, speed, hd FROM pc WHERE (cd = '12x' OR cd = '24x')  AND price < 600::money;

-- Указать производителя и скорость для тех ноутбуков, которые имеют жесткий диск объемом не менее 100 Гбайт.
SELECT pr.maker, l.speed FROM product AS pr
    INNER JOIN laptop AS l ON pr.model = l.model
    WHERE l.hd >= 100;

-- Найти номера моделей и цены всех продуктов (любого типа), выпущенных производителем B (латинская буква).
WITH needed_model AS (SELECT model FROM product WHERE maker = 'B')
SELECT p.model, p.price FROM printer AS p WHERE p.model IN (SELECT model FROM needed_model)
UNION ALL
SELECT p.model, p.price FROM laptop AS p WHERE p.model IN (SELECT model FROM needed_model)
UNION ALL
SELECT p.model, p.price FROM pc AS p WHERE p.model IN (SELECT model FROM needed_model);

--Найти производителя, выпускающего ПК, но не ноутбуки.
-- у меня таких два
-- A - принтеры и компьютеры и laptop
-- С - компьютеры
-- B - принтеры
-- D - ноутбуки

SELECT DISTINCT maker  FROM product
WHERE  maker NOT IN  (
    SELECT maker FROM product  AS p
     INNER JOIN laptop AS l ON p.model = l.model
     )
AND maker IN
    (SELECT  maker FROM product as p
   INNER JOIN pc On pc.model = p.model);

-- Найти производителей ПК с процессором не менее 450 Мгц. Вывести поля: maker.
SELECT DISTINCT maker FROM product AS p
INNER JOIN pc ON pc.model = p.model
WHERE pc.speed >=450;

--Найти принтеры, имеющие самую высокую цену. Вывести поля: model, price.
SELECT model, price FROM printer WHERE price = (SELECT MAX(price) FROM printer);

--Найти среднюю скорость ПК.
SELECT AVG(speed) FROM pc;

-- Найти среднюю скорость ноутбуков, цена которых превышает 1000 долларов.
SELECT AVG(speed)  FROM laptop WHERE price > 1000::money;

-- Найти среднюю скорость ПК, выпущенных производителем A.
SELECT AVG(pc.speed) FROM pc
    LEFT JOIN product AS p ON  p.model = pc.model
    WHERE    p.maker = 'A';

-- Для каждого значения скорости процессора найти среднюю стоимость ПК с такой же скоростью. Вывести поля: скорость, средняя цена.
SELECT AVG(price::numeric), speed FROM pc GROUP BY speed;

-- Найти размеры жестких дисков, совпадающих у двух и более PC. Вывести поля: hd.
SELECT  hd FROM pc
GROUP BY hd
HAVING COUNT(*) > 1;


--Найти пары моделей PC, имеющих одинаковые скорость процессора и RAM. В результате каждая пара указывается только один раз, т.е. (i,j), но не (j,i), Порядок вывода полей: модель с большим номером, модель с меньшим номером, скорость, RAM.
SELECT p1.model, p2.model, p1.speed, p2.ram FROM pc AS p1
JOIN pc AS p2 ON p1.ram = p2.ram AND p1.speed = p2.speed AND p1.model > p2.model
ORDER BY p1.model DESC, p2.model;


-- Найти модели ноутбуков, скорость которых меньше скорости любого из ПК.
-- Вывести поля: type, model, speed.
SELECT product.type, l.model, l.speed FROM laptop AS l
LEFT JOIN product ON product.model = l.model
WHERE l.speed < (SELECT MIN(speed) FROM pc)

-- Найти производителей самых дешевых цветных принтеров. Вывести поля: maker, price.
-- не понимаю, что значит самые дешевые - какое количество или от какой суммы, поэтому просто отсортировала
SELECT pr.maker, p.price FROM printer AS p
LEFT JOIN product as pr ON p.model = pr.model
WHERE p.color = 'y'
ORDER BY price;
-- если самого дешевого то LIMIT 1;

-- Для каждого производителя найти средний размер экрана выпускаемых им ноутбуков. Вывести поля: maker, средний размер экрана.
SELECT pr.maker , AVG(l.screen) FROM product as pr
INNER JOIN laptop AS l ON l.model = pr.model
GROUP BY pr.maker;

-- Найти производителей, выпускающих по меньшей мере три различных модели ПК. Вывести поля: maker, число моделей.
SELECT maker , COUNT(model) FROM product AS pr
WHERE pr.type = 'PC'
GROUP BY maker
HAVING COUNT(model) >=3
;

-- Найти максимальную цену ПК, выпускаемых каждым производителем. Вывести поля: maker, максимальная цена.

SELECT max(pc.price) , p.maker FROM pc
JOIN product AS p ON p.model  = pc.model
GROUP BY p.maker ;


-- Для каждого значения скорости процессора ПК, превышающего 600 МГц, найти среднюю цену ПК с такой же скоростью. Вывести поля: speed, средняя цена.
SELECT speed, AVG(price::numeric) FROM pc
WHERE speed > 600
GROUP BY speed ;

-- Найти производителей, которые производили бы как ПК, так и ноутбуки со скоростью не менее 750 МГц. Вывести поля: maker
SELECT DISTINCT maker FROM product
WHERE maker IN
      (SELECT p.maker FROM product AS p
                      JOIN pc ON p.model = pc.model
                      WHERE pc.speed >= 750)
AND maker IN
(SELECT p.maker FROM product AS p
                         JOIN laptop AS pc ON p.model = pc.model
 WHERE pc.speed >= 750);

-- Перечислить номера моделей любых типов, имеющих самую высокую цену по всей имеющейся в базе данных продукции.

WITH max_prices AS (
    SELECT model, price FROM pc WHERE price = (SELECT MAX(price::numeric)::money FROM pc)
                         UNION
    (SELECT model, price FROM laptop WHERE price = (SELECT MAX(price::numeric)::money FROM laptop))
    UNION
    (SELECT model, price FROM printer WHERE price = (SELECT MAX(price::numeric)::money FROM printer))

)

SELECT  model FROM max_prices
WHERE price  = (SELECT MAX(price::numeric) from max_prices)::money;

-- Найти производителей принтеров, которые производят ПК с наименьшим объемом RAM и с самым быстрым процессором среди всех ПК, имеющих наименьший объем RAM. Вывести поля: maker
WITH
  min_ram AS (
      SELECT model FROM pc WHERE ram = (SELECT min(ram) FROM pc )
      )
,
    apr_model AS (

    SELECT model FROM pc
    WHERE model in (SELECT model FROM min_ram) AND
        speed = (SELECT max(speed) FROM pc WHERE model IN (SELECT model FROM min_ram))

), apr_makers AS (
    SELECT maker FROM product WHERE model IN (SELECT model FROM apr_model)
)
SELECT DISTINCT maker FROM product
WHERE type = 'Printer'
  AND maker IN (SELECT maker FROm apr_makers )




