INSERT INTO authors ( name, paternal, surname)  VALUES ('Иван', 'Александрович', 'Гончаров');
INSERT INTO authors ( name, paternal, surname)  VALUES ('Лев', 'Николаевич', 'Толстой');
INSERT INTO authors ( name, paternal, surname)  VALUES ('Михаил', 'Афанасьевич', 'Булгаков');

INSERT INTO genres ( genre_name) VALUES ('фэнтази');
INSERT INTO genres ( genre_name) VALUES ('история');
INSERT INTO genres ( genre_name) VALUES ('классика');


INSERT INTO  books ( title, genre_id, author_id, year, status, price)  VALUES (
       'Война и Мир', 2,2,1870, 'I',2000 );
INSERT INTO  books ( title, genre_id, author_id, year, status, price)  VALUES (
                  'Мастер и Маргарита', 3,3,1930, 'I',2200 );
INSERT INTO  books ( title, genre_id, author_id, year, status, price)  VALUES (
      'Обломов', 3,1,1850, 'O',1500 );
INSERT INTO  books ( title, genre_id, author_id, year, status, price)  VALUES (
              'Воскресенье', 3,2,1870, 'O',2000 );


