CREATE INDEX index1
ON orders
(timeStampRecieved);

CREATE INDEX index2
ON ItemStatus
( orderid );

CREATE INDEX index3
ON Menu
( itemName );

CREATE INDEX index4
ON menu
( type );


CREATE INDEX index5
ON orders
(orderid);

CREATE INDEX index6
ON users
( login );

CREATE INDEX index7
ON users
(password);

CREATE INDEX index8
ON users
(type);

CREATE INDEX index9
ON orders
(paid);

CREATE INDEX index10
ON orders
(login);

CREATE INDEX index11
ON orders
(orderid);

CREATE INDEX index12
ON ItemStatus
( itemName );