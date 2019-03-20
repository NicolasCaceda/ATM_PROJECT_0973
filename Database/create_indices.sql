CREATE UNIQUE INDEX acct_type ON AcctType (
    acct_type
);

CREATE INDEX birthdate ON Member (
    birthdate
);

CREATE INDEX atm_location ON AtmManager (
    atm_location
);
