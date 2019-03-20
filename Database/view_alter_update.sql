CREATE VIEW active_users AS
    SELECT m.mem_id
      FROM Member m
           INNER JOIN
           Account a ON a.acct_id = m.acct_id
     WHERE a.is_active = 'true';

CREATE VIEW bank_totals AS
    SELECT (a.balance / 100.0) 
      FROM Account a
           INNER JOIN
           Bank b ON b.bank_id = a.bank_id
     GROUP BY b.bank_id;

ALTER TABLE bank ADD COLUMN total INTEGER;

UPDATE Bank
   SET total = (
           SELECT sum(balance) 
             FROM Account
            WHERE Account.bank_id = Bank.bank_id
       );
