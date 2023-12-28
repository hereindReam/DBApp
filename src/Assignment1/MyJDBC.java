package Assignment1;

import java.sql.*;
import java.io.*;
import java.lang.*;

class MyJDBC{
    static{
        try{
            Class.forName("COM.ibm.db2.jdbc.app.DB2Driver");
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        try{
            Connection con = null;
            String url = "jdbc:db2:sample";
            if(args.length == 0){
                con = DriverManager.getConnection(url);
            }else if(args.length == 2){
                String userid = args[0];
                String passwd = args[1];
                con = DriverManager.getConnection(url,userid,passwd);
            }else{
                throw new Exception("\n Usage: java MyJDBC[,user,password]\n");
            }

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery
                    ("select empno, lastname "+
                            "from employee "+
                            "where salary > 40000");
            while ( rs.next()){
                System.out.println("empno = " + rs.getString(1) +
                        " lastname = " + rs.getString(2));
            }
            rs.close();
            stmt.close();
            con.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
