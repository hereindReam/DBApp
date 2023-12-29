package Assignment9; /********************************************************************/
/* LABADDBATCH.JAVA                                                 */
/* This program will demonstrate how to use the addbatch() method   */
/*                                                                  */
/* Last updated = 01/01/2003                                        */
/********************************************************************/
/* Import statements                                                */
/********************************************************************/

import java.sql.*;
import java.io.*;
import java.util.*;
import java.math.*;


public class labaddbatch
{
/**********************************************************************/
    /* Register the class with the DB2 Driver                             */
    /**********************************************************************/
    static
    {   try
    {   Class.forName ("com.ibm.db2.jcc.DB2Driver");
    }
    catch (Exception e)
    {   System.out.println ("\n  Error loading DB2 Driver...\n");
        System.out.println (e);
        System.exit(1);
    }
    }

/**********************************************************************/
    /* Main routine                                                       */
    /**********************************************************************/
    public static void main(String[] args) throws Exception
    {
        int outID = 0;
        String outname = " ";
        String outjob = " ";
        float outsalary = 0;
        int updaterowcount = 0;

        try {

            /*  Establish connection and set default context  */
            System.out.println("Connect statement follows:");
            /********************* ???????????????? ****************************/
            /* (1) Connect to the database SAMPLE.  Instantiate the connection */
            /*     object named sample.  Use the userid udba and the password  */
            /*     of udba                                                     */
            /*******************************************************************/
            Connection sample = DriverManager.getConnection("jdbc:db2://192.168.62.128:50000/SAMPLE","db2admin","student" );
            System.out.println("Connect completed");
            /*********************   ?????????????? ****************************/
            /*  (2) Using the object sample turn autocommit off                */
            /***************************************labTable****************************/
            sample.setAutoCommit(false);

            System.out.println("\nAutocommit set off");
            /*********************   ?????????????? ****************************/
            /*  (3) Instantiate the Statement object named  stmt               */
            /*******************************************************************/

            /*String test = "select COUNT(*) from ROD ";
            Statement st = sample.createStatement();
            ResultSet resultSet = st.executeQuery(test);
            resultSet.next();
            System.out.println(resultSet.getString(1));*/

            Statement stmt = sample.createStatement();

            System.out.println("\n Batch Statements begin ");

            /*********************   ?????????????? ****************************/
            /*  (4) Add an INSERT statement to list of the commands associated */
            /*      with the object stmt                                       */
            /*      Insert a row into the UDBA.DEPARTMENT with the values      */
            /*      'BT6', 'BATCH6 NEWYORK','BBBBB1','BTT','NEW YORK CITY6'    */
            /*******************************************************************/

            stmt.addBatch("INSERT INTO DB2ADMIN.DEPARTMENT " +
                    "VALUES ('BT6','BATCH6 NEWYORK','000070','D21','NEW YORK CITY6')");    // (4)
            /********************  ?????????????????? **************************/
            /*  (5) Add an INSERT statement to list of the commands associated */
            /*      with the object stmt                                       */
            /*      Insert a row into the UDBA.DEPARTMENT with the values      */
            /*      'BT7', 'BATCH7 NEWYORK','BBBBB2','BT2','NEW YORK CITY7'    */
            /*******************************************************************/

            stmt.addBatch("INSERT INTO DB2ADMIN.DEPARTMENT " +
                    "VALUES ('BT7','BATCH7 NEWYORK','000020','B01','NEW YORK CITY7')");    // (5)
            /********************  ?????????????????? **************************/
            /*  (6) Add an INSERT statement to list of the commands associated */
            /*      with the object stmt                                       */
            /*      Insert a row into the UDBA.DEPARTMENT with the values      */
            /*      'BT8', 'BATCH8 NEWYORK','BBBBB3','BT3','NEW YORK CITY8'    */
            /*******************************************************************/

            stmt.addBatch("INSERT INTO DB2ADMIN.DEPARTMENT " +
                    "VALUES ('BT8','BATCH8 NEWYORK','000030','C01','NEW YORK CITY8')");    // (6)

            /********************  ?????????????????? **************************/
            /*  (7) Add an INSERT statement to list of the commands associated */
            /*      with the object stmt                                       */
            /*      Insert a row into the UDBA.DEPARTMENT with the values      */
            /*      'BT9', 'BATCH9 NEWYORK','BBBBB4','BT4','NEW YORK CITY9'    */
            /*******************************************************************/

            stmt.addBatch("INSERT INTO DB2ADMIN.DEPARTMENT " +
                    "VALUES ('BT9','BATCH9 NEWYORK','000150','D11','NEW YORK CITY9')");    // (7)

            /********************  ?????????????????? **************************/
            /*  (8) Add an INSERT statement to list of the commands associated */
            /*      with the object stmt                                       */
            /*      Insert a row into the UDBA.DEPARTMENT with the values      */
            /*      'BTA', 'BATCH10 NEWYORK','BBBBB5','BT5','NEW YORK CITY10'  */
            /*******************************************************************/

            stmt.addBatch("INSERT INTO DB2ADMIN.DEPARTMENT " +
                    "VALUES ('BTA','BATCH10 NEWYORK','000280','E11','NEW YORK CITY10')");  // (8)
            System.out.println("\n Batch statements completed executeBatch follows");
            /********************  ?????????????????? **************************/
            /*  (9) Code a statement that will execute the commands in the     */
            /*      batch.  Save the number of rows in the table that are      */
            /*      added to the table for each SQL statement in the batch     */
            /*******************************************************************/

            int [] updateCounts = stmt.executeBatch();                                           // (9)
            for (int i = 0; i < updateCounts.length; i++) {
                System.out.println("\nUpdate row count " + updateCounts[i] );
            }
            /******************** ???????????????????? *************************/
            /* (10) Commit the logical unit of work.  Use the connection       */
            /*      object named sample                                        */
            /*******************************************************************/
            sample.commit();                                                                     // (10)
        }   // end of try
        catch (BatchUpdateException e) {
            SQLException nextException = e.getNextException();
            while (nextException != null) {
                System.out.println("Error message: " + nextException.getMessage());
                System.out.println("SQL state: " + nextException.getSQLState());
                System.out.println("Error code: " + nextException.getErrorCode());
                nextException = nextException.getNextException();
            }
        }
        catch (SQLException e)
        {
            System.out.println("\n SQLState: " + e.getSQLState() + " SQLCode: " + e.getErrorCode());
            System.out.println("\n Message " + e);
            System.out.println("\n Get Error Message: " + e.getMessage());
        }
    }   // End Main
}  // End Program