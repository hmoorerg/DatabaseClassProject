COPY MENU
FROM '/home/henry/DATABASE/DatabaseClassProject/data/menu.csv'
WITH DELIMITER ';';

COPY USERS
FROM '/home/henry/DATABASE/DatabaseClassProject/data/users.csv'
WITH DELIMITER ';';

COPY ORDERS
FROM '/home/henry/DATABASE/DatabaseClassProject/data/orders.csv'
WITH DELIMITER ';';
ALTER SEQUENCE orders_orderid_seq RESTART 87257;

COPY ITEMSTATUS
FROM '/home/henry/DATABASE/DatabaseClassProject/data/itemStatus.csv'
WITH DELIMITER ';';

