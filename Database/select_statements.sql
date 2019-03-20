/*The number of members with email accounts ending in “edu” and the percentage of “edu” emails.*/
SELECT count( * ) AS Ending_edu,
       round(100.0 * count( * ) / (
                                      SELECT count( * ) 
                                        FROM Member
                                  ), 2) AS Percentage
  FROM Member
 WHERE lower(email) LIKE '%.edu';


/*The number of banks in each state listed in bank database (grouped by state)*/
Select state, count(*) as amount from Bank group by state;

/*The total amount of money held by ATMs at Publix® locations*/
SELECT sum(balance) AS total
  FROM ATM
 WHERE lower(location_name) LIKE '%publix%';


/*The maximum and minimum amounts of money in all the ATMs*/
SELECT max(balance),
       MIN(balance) 
  FROM ATM;


/*The percentage of transactions completed in FL*/
SELECT Round(100.0 * Sum(ATM.num_of_tran) / (
                                        SELECT Sum(num_of_tran) 
                                          FROM ATM
                                    ), 2)
       AS percentage_fl
  FROM Bank
       INNER JOIN
       ATM ON ATM.bank_id = Bank.bank_id
 WHERE bank.state = 'FL';
 
/*The average number of transactions for each AtmManager (grouped by AtmManager ids)*/
SELECT atm_id,
       AVG(num_of_tran) AS Average_tran
  FROM ATM
 GROUP BY atm_id;
