CREATE  TABLE IF NOT EXISTS product (
    maker  VARCHAR(10) NOT NULL,
    model VARCHAR(50) PRIMARY KEY,
    type  VARCHAR(50) NOT NULL CHECK (type in ('PC', 'Laptop', 'Printer'))


    );

CREATE TABLE IF NOT EXISTS laptop(
    code    SERIAL  PRIMARY KEY ,
    model VARCHAR(50) NOT NULL,
    speed SMALLINT NOT NULL,
    ram SMALLINT NOT NULL,
    hd REAL NOT NULL,
    price MONEY,
    screen SMALLINT NOT NULL,

    FOREIGN KEY (model) REFERENCES product(model)
    );

CREATE TABLE IF NOT EXISTS printer(
  code SERIAL PRIMARY KEY,
  model  VARCHAR(50) NOT NULL,
    color CHAR(1) NOT NULL CHECK (color IN ('y', 'n')), -- y -color , n - without color
    type VARCHAR(10) NOT NULL CHECK (type in ('Laser','Jet', 'Matrix')),
    price MONEY,
    FOREIGN KEY (model) REFERENCES product(model)

    );

CREATE TABLE IF NOT EXISTS pc (
  code SERIAL PRIMARY KEY,
  model VARCHAR(50) NOT NULL ,
    speed SMALLINT NOT NULL,
    ram SMALLINT NOT NULL,
    hd REAL NOT NULL,
    cd VARCHAR(10) NOT NULL,
    price MONEY,
    FOREIGN KEY (model) REFERENCES product(model)
    );