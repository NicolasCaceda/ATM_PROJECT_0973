import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Scanner;


public class AtmManager {

  //A bunch of variables I use
  Scanner userInput = new Scanner(System.in);
  DbHandler db;

  final Integer[] BANK_IDS = {1235, 1452, 1958, 2456,
      2778, 4123, 4222, 4888, 7245, 9875};
  private String bankChoices = "There are the bank IDs to select from:\n";

  final String[] ACCT_TYPES = {"SAVINGS", "CHECKING", "CD", "MONEY MARKET", "IRA"};
  private String accountChoices = "There are the account types to select from:\n";

  //Pretty much the "main" of my project it is the main menu because it is the atm
  public AtmManager() throws SQLException {
    db = new DbHandler();
    for (int i = 1; i < BANK_IDS.length; i++) {
      bankChoices += BANK_IDS[i] + "\n";
    }
    for (int i = 1; i < ACCT_TYPES.length; i++) {
      accountChoices += ACCT_TYPES[i] + "\n";
    }
    System.out.println("Hello and welcome to ATM Manager.");
    //The atm manager loop
    while (true) {
      if (!db.isOpen()) {
        System.out.println("System is now closed.");
        System.exit(0);
      }
      System.out.println("Please Select a mode. (Pick a number)");

      //The menu
      System.out.print("1 - Create ATM" +
          '\n' + "2 - Add Member And See Totals For All Banks" +
          '\n' + "3 - Withdraw And View Money" +
          '\n' + "4 - Exit Program" +
          '\n');

      //The menu selections
      switch (userInteger()) {
        case 1:
          System.out.println("-----------------------------------");
          createATM();
          System.out.println("-----------------------------------");
          break;
        case 2:
          System.out.println("-----------------------------------");
          addMember();
          System.out.println("-----------------------------------");
          seeTotals();
          System.out.println("-----------------------------------");
          break;
        case 3:
          System.out.println("-----------------------------------");
          withdrawAndViewMoney();
          System.out.println("-----------------------------------");
          break;
        case 4:
          userInput.close();
          db.closeDb();
          System.exit(0);
          break;
        default:
          System.out.println("Please Select a valid integer.");
          System.out.println("-----------------------------------");
      }
    }
  }

  //A method to see all the totals.
  private void seeTotals() throws SQLException {
    db.selectTotals();
  }

  //Method to add members
  //It gets user data and then passes it to the database
  private void addMember() throws SQLException {
    System.out.println("Creating a member.");

    int memId, acctId, ssn, phone;
    String email, name, address;

    memId = (int) (Math.random() * 125 + Math.random() * 50 + 1000);
    acctId = (int) (Math.random() * 125 + Math.random() * 50 + 1000);
    createAccount(acctId);

    System.out.print("State your full name: ");
    name = userInput.nextLine();

    System.out.print("State your email: ");
    email = userInput.nextLine();

    System.out.print("State your address: ");
    address = userInput.nextLine();

    System.out.print("Sate your ssn: ");
    ssn = userInteger();

    System.out.print("Sate your phone number:");
    phone = userInteger();

    String birthdayAsString = "";
    while (true) {
      if (birthdayAsString.matches("^\\d{4}-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$")) {
        break;
      }
      System.out.print("State your birthdate as yyyy-mm-dd: ");
      birthdayAsString = userInput.nextLine();
      Date birthdate = Date.valueOf(birthdayAsString);

    }
    Date birthdate = Date.valueOf(birthdayAsString);

    if (isCertain()) {
      //Time to put it into the database.
      db.insertMember(memId, acctId, name.split(" "), ssn, phone, email, address, birthdate);
    }
  }

  //Method to create an account
  //It gets user data and then passes it to the database
  private void createAccount(int acctId) throws SQLException {
    System.out.println("Creating an account");

    int bankId = 0, balance;
    String acctType = "";
    //Why are you making an account if you wont be active???
    boolean isActive = true;

    while (true) {
      if (Arrays.asList(BANK_IDS).contains(bankId)) {
        break;
      }
      System.out.print(bankChoices);
      bankId = userInteger();
    }

    System.out.println("The account balance will be: ");
    balance = userInteger();

    while (true) {
      if (Arrays.asList(ACCT_TYPES).contains(acctType)) {
        break;
      }
      System.out.print(accountChoices);
      acctType = userInput.nextLine().toUpperCase();
    }
    System.out.println("Are these values okay with you?: " +
        '\n' + "acct id: " + acctId +
        '\n' + "Bank Id: " + bankId +
        '\n' + "Balance: $" + balance +
        '\n' + "Account Type: " + acctType);
    if (isCertain()) {
      //passes it to the database
      db.insertAccount(acctId, bankId, acctType, balance, isActive);
    }
  }

  //Method to withdraw and view money.
  //It gets user data and then passes it to the database
  void withdrawAndViewMoney() throws SQLException {
    System.out.println("Now Viewing Money and Possibly Withdrawing.");

    int acctId;
    //User input is annoying just give me an atm
    ResultSet randomATMRs = db.getRandomATM();
    System.out.println(randomATMRs.getString("location_name"));

    System.out.print("Please state your account ID: ");
    acctId = userInteger();

    System.out.println("You have " + db.viewMoney(acctId));

    System.out.println("Do you wish to withdraw?");
    //if yes then dont leave.
    if (!isCertain()) {
      System.out.println("Alrighty, see ya.");
      return;
    }

    int withdrawAmount;

    System.out.println("How much do you wish to withdraw? ");
    withdrawAmount = userInteger();

    //to the database
    db.withdraw(withdrawAmount, acctId, randomATMRs);

    //Shows the money in your account and your bank in general
    System.out.println("You now have " + db.viewMoney(acctId));
    System.out.println("Your Bank Now has $" + db.viewBankMoney(acctId) + ".");

  }

  //Method to create an atm
  //It gets user data and then passes it to the database
  void createATM() throws SQLException {
    System.out.println("Now creating ATM.");

    int atmId, bankId = 0, balance, atmLocation;
    String locationName;

    //random atm id because why not
    atmId = (int) (Math.random() * 125 + Math.random() * 50 + 1000);
    while (!db.isValidNewATMId(atmId)) {
      atmId = (int) (Math.random() * 125 + Math.random() * 50 + 1000);
    }

    while (true) {
      if (Arrays.asList(BANK_IDS).contains(bankId)) {
        break;
      }
      System.out.print(bankChoices);
      bankId = userInteger();
    }

    //random atm location because why not
    atmLocation = atmId + (int) (Math.random() * 125 + Math.random() * 50 + 1000);

    System.out.print("Starting Balance of the ATM: ");
    balance = userInteger();

    System.out.print("Name of the location for the ATM: ");
    locationName = userInput.nextLine();

    System.out.println("Are these values okay with you?: " +
        '\n' + "Atm Id: " + atmId +
        '\n' + "Bank Id: " + bankId +
        '\n' + "Balance: $" + balance +
        '\n' + "Atm Location: " + atmLocation +
        '\n' + "Location Name: " + locationName);
    if (isCertain()) {
      //passes to database.
      db.insertATM(atmId, bankId, atmLocation, locationName, balance);
    }
  }

  //Gets an integer from the user because this is better.
  int userInteger() {
    int returnedInteger;
    while (true) {
      try {
        returnedInteger = userInput.nextInt();
        userInput.nextLine();
        return returnedInteger;
      } catch (Exception e) {
        System.out.println("Please select a valid integer.");
        userInput.nextLine();
      }
    }
  }


  //IS CERTAIN WHAT ELSE IS THERE TO SAY?
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
}
