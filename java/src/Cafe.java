/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Cafe {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
         new InputStreamReader(System.in));

   public String CurrentlyloggedInUser;

   /**
    * Creates a new instance of Cafe
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Cafe(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try {
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      } catch (Exception e) {
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      } // end catch
   }// end Cafe

   /**
    * Method to execute an update SQL statement. Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate(String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement();

      // issues the update instruction
      stmt.executeUpdate(sql);

      // close the instruction
      stmt.close();
   }// end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT). This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult(String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery(query);

      /*
       ** obtains the metadata object for the returned result set. The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData();
      int numCol = rsmd.getColumnCount();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()) {
         if (outputHeader) {
            for (int i = 1; i <= numCol; i++) {
               System.out.print(rsmd.getColumnName(i) + "\t");
            }
            System.out.println();
            outputHeader = false;
         }
         for (int i = 1; i <= numCol; ++i)
            System.out.print(rs.getString(i) + "\t");
         System.out.println();
         ++rowCount;
      } // end while
      stmt.close();
      return rowCount;
   }// end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT). This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult(String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery(query);

      /*
       ** obtains the metadata object for the returned result set. The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData();
      int numCol = rsmd.getColumnCount();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result = new ArrayList<List<String>>();
      while (rs.next()) {
         List<String> record = new ArrayList<String>();
         for (int i = 1; i <= numCol; ++i)
            record.add(rs.getString(i));
         result.add(record);
      } // end while
      stmt.close();
      return result;
   }// end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT). This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery(String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery(query);

      int rowCount = 0;

      // iterates through the result set and count nuber of results.
      while (rs.next()) {
         rowCount++;
      } // end while
      stmt.close();
      return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
      Statement stmt = this._connection.createStatement();

      ResultSet rs = stmt.executeQuery(String.format("Select currval('%s')", sequence));
      if (rs.next())
         return rs.getInt(1);
      return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup() {
      try {
         if (this._connection != null) {
            this._connection.close();
         } // end if
      } catch (SQLException e) {
         // ignored.
      } // end try
   }// end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login
    *             file>
    */
   public static void main(String[] args) {
      if (args.length != 3) {
         System.err.println(
               "Usage: " +
                     "java [-classpath <classpath>] " +
                     Cafe.class.getName() +
                     " <dbname> <port> <user>");
         return;
      } // end if

      Greeting();
      Cafe esql = null;
      try {
         // use postgres JDBC driver.
         Class.forName("org.postgresql.Driver").newInstance();
         // instantiate the Cafe object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Cafe(dbname, dbport, user, "");

         boolean keepon = true;
         while (keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()) {
               case 1:
                  CreateUser(esql);
                  break;
               case 2:
                  authorisedUser = LogIn(esql);
                  break;
               case 9:
                  keepon = false;
                  break;
               default:
                  System.out.println("Unrecognized choice!");
                  break;
            }// end switch
            if (authorisedUser != null) {
               boolean usermenu = true;
               while (usermenu) {
                  System.out.println("MAIN MENU");
                  System.out.println("---------");
                  System.out.println("1. Goto Menu");
                  System.out.println("2. Update Profile (manager only)");
                  System.out.println("3. Place an Order");
                  System.out.println("4. Update an Order");
                  System.out.println(".........................");
                  System.out.println("9. Log out");
                  switch (readChoice()) {
                     case 1:
                        Menu(esql);
                        break;
                     case 2:
                        if (AllowOnlyManager(esql)) {
                           UpdateProfile(esql);
                        }
                        break;
                     case 3:
                        PlaceOrder(esql);
                        break;
                     case 4:
                        UpdateOrder(esql);
                        break;
                     case 9:
                        usermenu = false;
                        break;
                     default:
                        System.out.println("Unrecognized choice!");
                        break;
                  }
               }
            }
         } // end while
      } catch (Exception e) {
         System.err.println(e.getMessage());
      } finally {
         // make sure to cleanup the created table and close the connection.
         try {
            if (esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup();
               System.out.println("Done\n\nBye !");
            } // end if
         } catch (Exception e) {
            // ignored.
         } // end try
      } // end try
   }// end main

   public static void Greeting() {
      System.out.println(
            "\n\n*******************************************************\n" +
                  "              User Interface      	               \n" +
                  "*******************************************************\n");
   }// end Greeting

   /*
    * Reads the users choice given from the keyboard
    * 
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         } catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         } // end try
      } while (true);
      return input;
   }// end readChoice

   /*
    * Creates a new user with privided login, passowrd and phoneNum
    **/
   public static void CreateUser(Cafe esql) {
      try {
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user phone: ");
         String phone = in.readLine();

         String type = "Customer";
         String favItems = "";

         String query = String.format(
               "INSERT INTO USERS (phoneNum, login, password, favItems, type) VALUES ('%s','%s','%s','%s','%s')", phone,
               login, password, favItems, type);

         esql.executeUpdate(query);
         System.out.println("User successfully created!");
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }// end CreateUser

   /*
    * Check log in credentials for an existing user
    * 
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Cafe esql) {
      try {
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE login = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
         if (userNum > 0) 
         {
            esql.CurrentlyloggedInUser = login;
            return login;
         }
         return null;
      } catch (Exception e) {
         System.err.println(e.getMessage());
         return null;
      }
   }// end

   // Rest of the functions definition go in here

   public static void Menu(Cafe esql) {
      boolean usermenu = true;
      while (usermenu) {
         System.out.println("RESTAURANT MENU");
         System.out.println("---------");
         System.out.println("1. Search by itemName");
         System.out.println("2. Search by type");
         System.out.println("3. Add item (managers only)");
         System.out.println("4. Delete item (managers only)");
         System.out.println("5. Update item (managers only)");
         System.out.println(".........................");
         System.out.println("9. Exit menu");
         switch (readChoice()) {
            case 1:
               SearchMenuByName(esql);
               break;
            case 2:
               SearchMenuByType(esql);
               break;
            case 3:

               if (AllowOnlyManager(esql))
               {
                  AddItem(esql);
               }
               break;
            case 4:
               if (AllowOnlyManager(esql))
               {
                  DeleteItem(esql);
               }
               break;
            case 5:
               UpdateItem(esql);
               break;
            case 9:
               usermenu = false;
               break;
            default:
               System.out.println("Unrecognized choice!");
               break;
         }
      }
   }
   
   private static boolean AllowOnlyManager(Cafe esql) 
   {
      
      try {
         if (IsManager(esql))
         {
            return true;
         }
         else
         {
            System.out.println("Unauthorized: You are not a manager!");
            return false;
         }
      } catch (Exception e) {
         System.err.println(e.getMessage());
         return false;
      }
   }

   private static boolean IsManager(Cafe esql) throws SQLException {
      
      String query = String.format("SELECT * FROM USERS WHERE login = '%s' AND type='Manager'", esql.CurrentlyloggedInUser);
      int userNum = esql.executeQuery(query);
      if (userNum > 0) 
      {
         return true;
      }
      return false;
   }

   private static void DeleteItem(Cafe esql) {
      try {
         System.out.println("Searching menu by Name");
         System.out.print("\tEnter menu item name: ");
         String menuItemName = in.readLine();
         String query = String.format("DELETE FROM MENU WHERE itemName = '%s'", menuItemName);
         esql.executeUpdate(query);
         System.out.println("Item with name " + menuItemName + " deleted");
      } catch (Exception e) {
         System.err.println(e.getMessage());
         return;
      }
   }
   
   private static void UpdateItem(Cafe esql) {
      try {
         System.out.println("Searching menu by Name");
         System.out.print("\tEnter the menu item's current name: ");
         String menuItemName = in.readLine();
         System.out.print("\tEnter new item price: ");
         String newItemPrice = in.readLine();
         System.out.print("\tEnter new item description: ");
         String newItemDescription = in.readLine();
         System.out.print("\tEnter new image url: ");
         String newImageURL = in.readLine();
         String query = String.format("UPDATE MENU SET price = '%s', description = '%s', imageURL = '%s' WHERE itemName = '%s'", newItemPrice, newItemDescription, newImageURL, menuItemName);
         esql.executeUpdate(query);
         System.out.println("Item with name " + menuItemName + " updated");
      } catch (Exception e) {
         System.err.println(e.getMessage());
         return;
      }
   }
   private static void AddItem(Cafe esql) {
      try {
         System.out.print("\tEnter item name: ");
         String itemName = in.readLine();
         System.out.print("\tEnter item type: ");
         String itemType = in.readLine();
         System.out.print("\tEnter item price: ");
         String itemPrice = in.readLine();
         System.out.print("\tEnter item description: ");
         String itemDescription = in.readLine();
         System.out.print("\tEnter item image url: ");
         String imageURL = in.readLine();
         String query = String.format("INSERT INTO MENU (itemName, type, price, description, imageURL) VALUES ('%s','%s','%s','%s','%s')", itemName, itemType, itemPrice, itemDescription, imageURL);
         esql.executeUpdate(query);
         System.out.println("Item with name " + itemName + " added");
      } catch (Exception e) {
         System.err.println(e.getMessage());
         return;
      }
   }


   private static void PrintMenuItem(List<String> record) {
      String name = record.get(0).trim();
      String typeString = record.get(1).trim();
      double price = Double.parseDouble(record.get(2).trim());
      String description = record.get(3).trim();
      String imageURL = record.get(4).trim();

      Locale locale = new Locale("en","US");
      NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

      System.out.println("-----RECORD FOUND-----");
      System.out.println("Name: " + name);
      System.out.println("Type: " + typeString);
      System.out.println("Price: " + currencyFormatter.format(price));
      System.out.println("Description: " + description);
      System.out.println("imageURL: " + imageURL);
      System.out.println("-----END OF RECORD-----");
   }

   public static void SearchMenuByName(Cafe esql) {
      try {
         System.out.println("Searching menu by Name");
         System.out.print("\tEnter menu item name: ");
         String menuItemName = in.readLine();
         String query = String.format("SELECT * FROM MENU WHERE itemName = '%s'", menuItemName);
         List<List<String>> data = esql.executeQueryAndReturnResult(query);
         for (List<String> record : data) {
            PrintMenuItem(record);
         }
      
      } catch (Exception e) {
         System.err.println(e.getMessage());
         return;
      }
   }

   public static void SearchMenuByType(Cafe esql) {
      try {
         System.out.println("Searching menu by Type");
         System.out.print("\tEnter menu item type: ");
         String menuItemType = in.readLine();
         String query = String.format("SELECT * FROM MENU WHERE type = '%s'", menuItemType);
         List<List<String>> data = esql.executeQueryAndReturnResult(query);
         for (List<String> record : data) {
            PrintMenuItem(record);
         }
      
      } catch (Exception e) {
         System.err.println(e.getMessage());
         return;
      }

   }

   public static void UpdateProfile(Cafe esql) {
      try {

         System.out.println("Updating user by login");
         System.out.print("\tEnter login of the user you want to update: ");
         String userName = in.readLine();

         // Get the new phone number:
         System.out.print("\tEnter the user's new phone number: ");
         String newPhoneNumber = in.readLine();

         // Get the new password:
         System.out.print("\tEnter the user's new password: ");
         String newPassword = in.readLine();

         // Get the new favItems:
         System.out.print("\tEnter the user's new favItems: ");
         String newFavItems = in.readLine();

         // Get the new type:
         System.out.print("\tEnter the user's new type: ");
         String newType = in.readLine();

         String query = String.format("UPDATE Users SET phoneNum = '%s', password = '%s', favItems = '%s', type = '%s' WHERE login = '%s'", newPhoneNumber, newPassword, newFavItems, newType, userName);
         esql.executeUpdate(query);
         System.out.println("User with name " + userName + " updated");
      } catch (Exception e) {
         System.err.println(e.getMessage());
         return;
      }
   }

   public static void PlaceOrder(Cafe esql) {
      try {
         boolean isManager = IsManager(esql);

         // Insert using this schema:
         //CREATE TABLE Orders(
         // orderid serial UNIQUE NOT NULL,
         // login char(50), 
         // paid boolean,
         // timeStampRecieved timestamp NOT NULL,
         // total real NOT NULL,
         // PRIMARY KEY(orderid));
         
         // Insert an order
         System.out.println("Placing an order");
         String query = String.format("INSERT INTO Orders (login, paid, timeStampRecieved, total) VALUES ('%s', '%s', '%s', '%s') RETURNING orderid", esql.CurrentlyloggedInUser, false, new Timestamp(System.currentTimeMillis()), 0);
         int id = Integer.parseInt(esql.executeQueryAndReturnResult(query).get(0).get(0));
         System.out.println("Creating new order with orderid:  " + id);

         

      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static void UpdateOrder(Cafe esql) {

   }

}// end Cafe
