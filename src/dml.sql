--PRODUCT_-------------------------------------------------------
-- Printers from A maker
INSERT INTO product (model, maker, type) VALUES ('printer x1', 'A', 'Printer');
INSERT INTO product (model, maker, type) VALUES ('printer x2', 'A', 'Printer');
INSERT INTO product (model, maker, type) VALUES ('printer x3', 'A', 'Printer');
INSERT INTO product (model, maker, type) VALUES ('printer x4', 'A', 'Printer');

-- Printer from A make
INSERT INTO product (model, maker, type) VALUES ('printer xx1', 'B', 'Printer');
INSERT INTO product (model, maker, type) VALUES ('printer xx2', 'B', 'Printer');
--PC from A
INSERT INTO product (model, maker, type) VALUES ('pc c0', 'A', 'PC');
INSERT INTO product (model, maker, type) VALUES ('pc c1', 'A', 'PC');
INSERT INTO product (model, maker, type) VALUES ('pc c2', 'A', 'PC');
--PC from C
INSERT INTO product (model, maker, type) VALUES ('pc cc2', 'C', 'PC');
INSERT INTO product (model, maker, type) VALUES ('pc cc1', 'C', 'PC');
INSERT INTO product (model, maker, type) VALUES ('pc cc3', 'C', 'PC');
INSERT INTO product (model, maker, type) VALUES ('pc cc4', 'C', 'PC');
--LAPTOP
INSERT INTO product (model, maker, type) VALUES ('laptop l1', 'A', 'Laptop');
INSERT INTO product (model, maker, type) VALUES ('laptop l2', 'D', 'Laptop');

--PC----------------------------------------
INSERT INTO pc ( model, speed, ram, hd, cd, price) VALUES ('pc c0', '750', 16, '512', '12x', 800);
INSERT INTO pc ( model, speed, ram, hd, cd, price) VALUES ('pc c1', '750', 16, '512', '12x', 800);
INSERT INTO pc ( model, speed, ram, hd, cd, price) VALUES ('pc c2', '900', 32, '512', '24x', 900);
INSERT INTO pc ( model, speed, ram, hd, cd, price) VALUES ('pc cc1', '300', 16, '256', '8x', 400);
INSERT INTO pc ( model, speed, ram, hd, cd, price) VALUES ('pc cc2', '850', 64, '1000', '24x', 1000);
INSERT INTO pc ( model, speed, ram, hd, cd, price) VALUES ('pc cc3', '700', 16, '512', '12x', 500);
INSERT INTO pc ( model, speed, ram, hd, cd, price) VALUES ('pc cc4', '700', 16, '512', '12x', 500);

--LAPTOP-------------
INSERT INTO laptop( model, speed, ram, hd, price, screen) VALUES('laptop l1', '750', 8, '512',1200, 13);
INSERT INTO laptop( model, speed, ram, hd, price, screen) VALUES('laptop l2', '200', 8, '256',900, 14);

--PRINTERS-----------
INSERT INTO printer ( model, color, type, price)  VALUES ('printer x1', 'y', 'Laser', 2000);
INSERT INTO printer ( model, color, type, price)  VALUES ('printer x2', 'y', 'Jet', 2500);
INSERT INTO printer ( model, color, type, price)  VALUES ('printer x3', 'n', 'Matrix', 1900);
INSERT INTO printer ( model, color, type, price)  VALUES ('printer xx1', 'n', 'Jet', 2200);
INSERT INTO printer ( model, color, type, price)  VALUES ('printer xx2', 'n', 'Laser', 1950);
INSERT INTO printer ( model, color, type, price)  VALUES ('printer x4', 'y', 'Matrix', 2500);





