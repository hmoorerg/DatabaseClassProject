COPY MENU
FROM '/DatabaseClassProject/data/menu.csv'
WITH DELIMITER ';';

COPY USERS
FROM '/DatabaseClassProject/data/users.csv'
WITH DELIMITER ';';

COPY ORDERS
FROM '/DatabaseClassProject/data/orders.csv'
WITH DELIMITER ';';
ALTER SEQUENCE orders_orderid_seq RESTART 87257;

COPY ITEMSTATUS
FROM '/DatabaseClassProject/data/itemStatus.csv'
WITH DELIMITER ';';

