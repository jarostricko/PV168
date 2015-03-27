CREATE TABLE "CARS" (
    "ID" INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "LICENCE_PLATE" VARCHAR(8),
    "MODEL" VARCHAR(32),
    "RENTAL_PAYMENT" DECIMAL(10,2),
    "STATUS" BOOLEAN
);

CREATE TABLE "CUSTOMERS" (
    "ID" INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "FULL_NAME" VARCHAR(60),
    "ADDRESS" VARCHAR(100),
    "PHONE_NUMBER" VARCHAR(32)
);

CREATE TABLE "LEASES" (
    "ID" INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "CUSTOMER" INTEGER NOT NULL REFERENCES CUSTOMERS(ID),
    "CAR" INTEGER NOT NULL REFERENCES CARS(ID),
    "PRICE" DECIMAL(10,2),
  "START_DATE" DATE,
  "END_DATE"   DATE
);