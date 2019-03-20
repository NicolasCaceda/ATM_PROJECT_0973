import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class DbHandler {

  Scanner userInput = new Scanner(System.in);

  //The usual database stuff
  private static final String DATABASE_URI = "jdbc:sqlite:Database/ATM_Management.db";
  private Connection conn = null;

  //Not all of these are needed i just wanted them
  PreparedStatement preparedStatementInsert = null;
  Statement statementUpdate = null;
  PreparedStatement preparedStatementUpdate1 = null;
  PreparedStatement preparedStatementUpdate2 = null;
  PreparedStatement preparedStatementUpdate3 = null;
  PreparedStatement preparedStatementSelect = null;

  //constructor connects to the database
  public DbHandler() {
    try {
      conn = DriverManager.getConnection(DATABASE_URI);
      conn.setAutoCommit(false);
      System.out.println("Connected to " + conn.getMetaData().getURL());
      System.out.println("-----------------------------------");
    } catch (SQLException e) {
      System.out.println("Error could not connect.");
      System.out.println(e.getMessage());
    }
  }

  //inserts to atm
  //if theres an error then it rolls back.
  void insertATM(int atmId, int bankId, int atmLocation, String locationName, int balance)
      throws SQLException {
    try {
      String insertATMSQL = "INSERT INTO ATM(atm_id, bank_id, atm_location," +
          " location_name, balance, num_of_tran)" +
          " VALUES (?, ?, ?, ?, ?, 0);";
      preparedStatementInsert = conn.prepareStatement(insertATMSQL);

      preparedStatementInsert.setInt(1, atmId);
      preparedStatementInsert.setInt(2, bankId);
      preparedStatementInsert.setInt(3, atmLocation);
      preparedStatementInsert.setString(4, locationName);
      preparedStatementInsert.setInt(5, balance);

      preparedStatementInsert.addBatch();
      System.out.println("ARE YOU 100% SURE?");
      if (isCertain()) {
        preparedStatementInsert.executeBatch();
        conn.commit();
        preparedStatementInsert = null;
      } else {
        conn.rollback();
        conn.close();
      }
    } catch (SQLException e) {
      System.out.println("Batch Error - rolling back");
      conn.rollback();
      conn.close();
    }

  }

  //is this atm valid to use?
  boolean isValidNewATMId(int id) throws SQLException {
    String checkQuery = "Select atm_id From ATM where atm_id = ?";
    preparedStatementSelect = conn.prepareStatement(checkQuery);
    preparedStatementSelect.setInt(1, id);
    ResultSet rs = preparedStatementSelect.executeQuery();

    if (rs.next()) {
      preparedStatementSelect = null;

      return false;
    }
    preparedStatementSelect = null;
    return true;
  }


  //Insert account
  //What i should have done is run this and insert member at the same exact time to
  //make sure there is no error for an account to exist and no member
  //but oh well hindsight 20/20 i guess.
  void insertAccount(int acct_id, int bank_id, String acct_type, int balance, boolean is_active)
      throws SQLException {
    try {
      String insertAccountSQL = "INSERT INTO Account(acct_id, bank_id," +
          " acct_type, balance, is_active)" +
          " VALUES (?, ?, ?, ?, ?);";
      preparedStatementInsert = conn.prepareStatement(insertAccountSQL);

      preparedStatementInsert.setInt(1, acct_id);
      preparedStatementInsert.setInt(2, bank_id);
      preparedStatementInsert.setString(3, acct_type);
      preparedStatementInsert.setInt(4, balance);
      preparedStatementInsert.setBoolean(5, is_active);

      preparedStatementInsert.addBatch();
      System.out.println("ARE YOU 100% SURE?");
      if (isCertain()) {
        preparedStatementInsert.executeBatch();
        conn.commit();
        preparedStatementInsert = null;
      } else {
        conn.rollback();
        conn.close();
      }
    } catch (SQLException e) {
      System.out.println("Batch Error - rolling back");
      conn.rollback();
      conn.close();
    }
  }

  //Insert member
  void insertMember(int memId, int acctId, String[] name, int ssn, int phone, String email,
      String address, Date birthdate)
      throws SQLException {
    try {
      String fName = name[0];
      String lName = name[name.length - 1];

      String insertMemberSQL = "INSERT INTO Member(mem_id, acct_id, mem_fname," +
          " mem_lname, ssn, phone, email, address, birthdate)" +
          " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
      preparedStatementInsert = conn.prepareStatement(insertMemberSQL);

      preparedStatementInsert.setInt(1, memId);
      preparedStatementInsert.setInt(2, acctId);
      preparedStatementInsert.setString(3, fName);
      preparedStatementInsert.setString(4, lName);
      preparedStatementInsert.setInt(5, ssn);
      preparedStatementInsert.setInt(6, phone);
      preparedStatementInsert.setString(7, email);
      preparedStatementInsert.setString(8, address);
      preparedStatementInsert.setDate(9, birthdate);

      preparedStatementInsert.addBatch();
      System.out.println("ARE YOU 100% SURE?");
      if (isCertain()) {
        preparedStatementInsert.executeBatch();
        conn.commit();
        preparedStatementInsert = null;
      } else {
        conn.rollback();
        conn.close();
      }
    } catch (SQLException e) {
      System.out.println("Batch Error - rolling back");
      conn.rollback();
      conn.close();
    }
  }

  //Select the totals from all banks
  void selectTotals() throws SQLException {
    String updateTotals = "UPDATE Bank"
        + "   SET total = ("
        + "           SELECT sum(balance) "
        + "             FROM Account"
        + "            WHERE Account.bank_id = Bank.bank_id"
        + "       )";

    statementUpdate = conn.createStatement();
    statementUpdate.executeUpdate(updateTotals);
    statementUpdate = null;

    String selectTotals = "SELECT bank_name, total FROM Bank";
    preparedStatementSelect = conn.prepareStatement(selectTotals);
    ResultSet rs = preparedStatementSelect.executeQuery();
    preparedStatementSelect = null;

    while (rs.next()) {
      System.out.println(rs.getString("bank_name") + ": $" + rs.getInt("total"));
    }
  }

  //is the database connection open?
  boolean isOpen() throws SQLException {
    return !conn.isClosed();
  }

  //IS CERTAIN?
  boolean isCertain() {
    while (true) {
      System.out.println("Are you sure you want to do this? (y/n)");
      String certainty = userInput.nextLine();
      switch (certainty.toLowerCase()) {
        case "y":
          return true;
        case "n":
          return false;
        default:
          System.out.println("You're not passing until you're certain.");
          break;
      }
    }
  }

  //View money from account
  public String viewMoney(int acctId) throws SQLException {
    String selectTotals = "SELECT balance FROM Account WHERE acct_id = ?";
    preparedStatementSelect = conn.prepareStatement(selectTotals);
    preparedStatementSelect.setInt(1, acctId);
    ResultSet rs = preparedStatementSelect.executeQuery();
    if (rs.next()) {
      return "$" + rs.getString("balance") + " in your account.";
    } else {
      return "No account found. Please check your account id or create an account.";
    }
  }

  //Random atm instead of asking the user what atm they are at
  public ResultSet getRandomATM() throws SQLException {
    ResultSet rs;
    String selectATM = "SELECT * FROM ATM ORDER BY RANDOM() LIMIT 1";
    preparedStatementSelect = conn.prepareStatement(selectATM);
    return rs = preparedStatementSelect.executeQuery();
  }

  //withdraw from atm and update bank
  public void withdraw(int withdrawAmount, int acctId, ResultSet randomATMRs) throws SQLException {
    try {
      String updateAcctBalance = "update Account set balance = (select Balance FROM ACCOUNT where acct_id = ?) - ? where acct_id = ?";
      preparedStatementUpdate1 = conn.prepareStatement(updateAcctBalance);
      preparedStatementUpdate1.setInt(1, acctId);
      preparedStatementUpdate1.setInt(2, withdrawAmount);
      preparedStatementUpdate1.setInt(3, acctId);
      preparedStatementUpdate1.addBatch();

      String updateATMBalance = "update ATM set balance = ?, num_of_tran = (select num_of_tran From ATM WHERE atm_id = ?) + 1 where atm_id = ?";
      preparedStatementUpdate2 = conn.prepareStatement(updateATMBalance);
      preparedStatementUpdate2.setInt(1, randomATMRs.getInt("balance") - withdrawAmount);
      preparedStatementUpdate2.setInt(2, randomATMRs.getInt("atm_id"));
      preparedStatementUpdate2.setInt(3, randomATMRs.getInt("atm_id"));
      preparedStatementUpdate2.addBatch();

      String updateTotals = "UPDATE Bank"
          + "   SET total = ("
          + "           SELECT sum(balance) "
          + "             FROM Account"
          + "            WHERE Account.bank_id = Bank.bank_id"
          + "       )";

      preparedStatementUpdate3 = conn.prepareStatement(updateTotals);
      preparedStatementUpdate3.addBatch();

      System.out.println("ARE YOU 100% SURE?");
      if (isCertain()) {
        preparedStatementUpdate1.executeBatch();
        preparedStatementUpdate2.executeBatch();
        preparedStatementUpdate3.executeBatch();
        conn.commit();
        preparedStatementUpdate1 = null;
      } else {
        conn.rollback();
        conn.close();
      }
    } catch (SQLException e) {
      System.out.println("Batch Error - rolling back");
      conn.rollback();
      conn.close();
    }
  }

  //view the money of a user's bank
  public String viewBankMoney(int acctId) throws SQLException {
    String selectStatement = "Select total from Bank where bank_id = (select bank_id From Account where acct_id = ?)";
    preparedStatementSelect = conn.prepareStatement(selectStatement);
    preparedStatementSelect.setInt(1, acctId);
    ResultSet rs = preparedStatementSelect.executeQuery();
    return String.valueOf(rs.getInt("total"));
  }

  //closes the database
  public void closeDb() throws SQLException {
    System.out.println("Now leaving Atm Manager. Thank you for choosing us.");
    conn.close();
  }
}
