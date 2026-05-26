INSERT INTO COURIER (FIRST_NAME, LAST_NAME, AVAILABLE, CITY)
VALUES ('Ivan', 'Ivanov', true, 'Plovdiv');

INSERT INTO COURIER (FIRST_NAME, LAST_NAME, AVAILABLE, CITY)
VALUES ('Dragan', 'Petkanov', true, 'Sofia');

INSERT INTO WAREHOUSE (CITY)
VALUES ('Plovdiv');

INSERT INTO WAREHOUSE (CITY)
VALUES ('Sofia');

INSERT INTO CUSTOMER (FIRST_NAME, LAST_NAME, USERNAME, PHONE_NUMBER, CITY)
VALUES ('Ivan',
        'Ivanov',
        'ivan.ivanov',
        '000',
        'Plovdiv');

INSERT INTO CUSTOMER (FIRST_NAME, LAST_NAME, USERNAME, PHONE_NUMBER, CITY)
VALUES ('Georgi',
        'Ivanov',
        'georgi.ivanov',
        '000',
        'Sofia');

INSERT INTO DELIVERY (CREATED_AT,
                      CUSTOMER_ID,
                      COURIER_ID,
                      WAREHOUSE_ID,
                      DELIVERY_STATUS,
                      ESTIMATED_ARRIVAL_AT
)
VALUES ('2026-05-10 10:22:58',
        1,
        1,
        1,
        'CREATED',
        '2026-05-13 10:22:58'
       );
