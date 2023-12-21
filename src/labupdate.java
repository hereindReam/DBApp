import java.sql.*;
import javax.swing.*;

public class labupdate
{
    static String showDialog()
    {
        String message = "请输入要更改的工资";
        String title = "sample database";
        return JOptionPane.showInputDialog(null,message,title);
    }

    static
    {
        try
    {

        Class.forName ("com.ibm.db2.jdbc.jcc.DB2Driver");
    }
    catch (Exception e)
    {   System.out.println ("\n  Error loading DB2 Driver...\n");
        System.out.println (e);
        System.exit(1);
    }
    }


    static void update(String input) throws Exception
    {
        String name = "";
        java.lang.String deptno  = "";
        short id = 0;
        double salary = 0;
        String job = "";
        short NumEmp = 0;
        String sqlstmt = "UPDATE STAFF SET SALARY = SALARY * 1.05 WHERE DEPT = ?";
        String s = " ";
        int mydeptno = 0;
        int SQLCode = 0;
        String SQLState = "     ";
        /*BufferedReader in = new BufferedReader( new InputStreamReader (System.in));*/


        /*  Establish connection and set default context  */
        System.out.println("Connect statement follows:");

        Connection sample = DriverManager.getConnection("jdbc:db2:sample","db2admin","student");
        System.out.println("Connect completed");

        sample.setAutoCommit(false);


        /*   Print instruction lines                       */
        System.out.println("This program will update the salaries for a department");
        System.out.println("\n");
        System.out.println("Please enter a department number: \n ");

        /*  Get the department number from the input data */


        /*s = in.readLine();*/
        /*deptno = s.substring(0,2);*/
        deptno  = input.substring(0,2);
        mydeptno = Integer.parseInt(deptno);

        /*  Issue Select statement  */
        System.out.println("Statement stmt follows");
        try
        {

            PreparedStatement pstmt = sample.prepareStatement( sqlstmt );


            pstmt.setInt(1, mydeptno);

            int updateCount = pstmt.executeUpdate();

            System.out.println("\nNumber of rows updated: " + updateCount);
        }  // end try
        catch ( SQLException x )
        {

            SQLCode = x.getErrorCode();

            SQLState = x.getSQLState();

            String Message = x.getMessage();
            System.out.println("\nSQLCODE:  " + SQLCode );
            System.out.println("\nSQLSTATE: " + SQLState);
            System.out.println("\nSQLERRM:  " + Message);
        }

        System.exit(0);
    } // end main

    public static void main(String[] args) throws Exception {
        update(showDialog());
    }

}  // end of kegstaff class





