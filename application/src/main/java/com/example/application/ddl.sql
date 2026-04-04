CREATE TABLE IF NOT EXISTS genres(
                                     genre_id  SERIAL PRIMARY KEY ,
                                     genre_name VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS authors(
                                      author_id SERIAL PRIMARY KEY ,
                                      name VARCHAR(50),
                                      paternal VARCHAR(50),
                                      surname VARCHAR(50)

);

CREATE TABLE IF NOT EXISTS books(
                                    book_id SERIAL PRIMARY KEY,
                                    title VARCHAR(100),
                                    genre_id INT NOT NULL,
                                    author_id INT NOT NULL,
                                    year INT NOT NULL,
                                    status CHAR NOT NULL CHECK ( status IN ('I', 'O') ), -- I - in stock, O - out of stock
                                    price BIGINT NOT NULL,
                                    last_date_purchase DATE,
                                    admission_date DATE,

                                    FOREIGN KEY  (genre_id) REFERENCES genres(genre_id),
                                    FOREIGN KEY  (author_id) REFERENCES authors(author_id)

);

CREATE TABLE IF NOT EXISTS customers(
                                        customer_id SERIAL PRIMARY KEY ,
                                        name VARCHAR(50),
                                        surname VARCHAR(50),
                                        email VARCHAR(75)
);


CREATE TABLE IF NOT EXISTS  orders(
                                      order_id SERIAL PRIMARY KEY ,
                                      customer_id INT NOT NULL,
                                      completion_date DATE,
                                      status CHAR NOT NULL CHECK ( status IN ('N', 'D', 'C') ) , -- New, Done, Cancelled

                                      FOREIGN KEY  (customer_id) REFERENCES customers(customer_id)
);

CREATE TABLE IF NOT EXISTS  order_books(
                                           order_book_id SERIAL PRIMARY KEY ,
                                           order_id INT NOT NULL,
                                           book_id INT NOT NULL,

                                           FOREIGN KEY  (order_id) REFERENCES orders(order_id),
                                           FOREIGN KEY (book_id) REFERENCES  books(book_id)

);


CREATE TABLE IF NOT EXISTS  requests(
                                        request_id SERIAL PRIMARY KEY ,
                                        book_id INT NOT NULL,
                                        order_id INT NOT NULL,

                                        FOREIGN KEY (order_id) REFERENCES orders(order_id),
                                        FOREIGN KEY  (book_id) REFERENCES  books(book_id)


);