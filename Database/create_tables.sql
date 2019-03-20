PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS Bank (
    bank_id        INTEGER  NOT NULL
                            PRIMARY KEY AUTOINCREMENT,
    bank_name      VARCHAR  NOT NULL,
    street_address TEXT     NULL,
    state          CHAR (2) NOT NULL,
        -- If bank is online it wouldn't have street but would still be founded in State
    zip            INTEGER  NOT NULL,
    location_id    INTEGER  UNIQUE
                            NOT NULL
);

CREATE TABLE IF NOT EXISTS AcctType (
    acct_type VARCHAR NOT NULL
                    PRIMARY KEY
    -- Current types: Savings, Checking, CD, Mutual
);

CREATE TABLE IF NOT EXISTS Account (
    acct_id   INTEGER NOT NULL
                      PRIMARY KEY AUTOINCREMENT,
    bank_id   INTEGER NOT NULL,
    acct_type VARCHAR NOT NULL,
    balance   INTEGER DEFAULT 0,
        -- Always use an integer for money if percision is needed
        -- To save values * 100; to display / 100
    is_active BOOLEAN NOT NULL
                      DEFAULT true,
    FOREIGN KEY (
        bank_id
    )
    REFERENCES Bank (bank_id),
    FOREIGN KEY (
        acct_type
    )
    REFERENCES AcctType (acct_type) 
);

CREATE TABLE IF NOT EXISTS Member (
    mem_id    INTEGER      NOT NULL
                           PRIMARY KEY AUTOINCREMENT,
    acct_id   INTEGER      NOT NULL
                           UNIQUE,
    mem_fname VARCHAR (50) NOT NULL,
    mem_lname VARCHAR (50) NOT NULL,
    ssn       INTEGER,
    phone     INTEGER,
        -- International members would not have SSNs
    email     VARCHAR (80) UNIQUE,
        -- You should make this long in Java (international numbers are big)
    address   TEXT,
    birthdate DATE         NOT NULL,
        -- Easier to store international and varying addresses as single field
        -- Unless you plan on using them for a lot more analysis
    FOREIGN KEY (
        acct_id
    )
    REFERENCES Account (acct_id) 
);

CREATE TABLE IF NOT EXISTS AtmManager (
    atm_id        INTEGER NOT NULL
                          PRIMARY KEY AUTOINCREMENT,
    bank_id       INTEGER NOT NULL,
    atm_location  INTEGER UNIQUE
                          NOT NULL,
    location_name VARCHAR NOT NULL,
    balance       INTEGER DEFAULT 0,
    num_of_tran   INTEGER DEFAULT 0,
    FOREIGN KEY (
        bank_id
    )
    REFERENCES Bank (bank_id)
);

CREATE TABLE IF NOT EXISTS ATM_transaction (
    tran_id     INTEGER   NOT NULL
                          PRIMARY KEY AUTOINCREMENT,
    atm_id      INTEGER   NOT NULL,
    mem_id      INTEGER   NOT NULL,
    tran_amount INTEGER,
    tran_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        -- Integer DEFAULT (strftime('%s','now')),
    FOREIGN KEY (
        atm_id
    )
    REFERENCES AtmManager (atm_id),
    FOREIGN KEY (
        mem_id
    )
    REFERENCES Member (mem_id) 
);
