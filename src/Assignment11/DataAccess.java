package Assignment11;

import java.sql.*;

public class DataAccess {

    public static void main(String[] args) {
        String resume = null;
        String empnum = "000130";
        int startper, startper1, startdpt = 0;//personal Info start/  /Department start
        PreparedStatement stmt1, stmt2, stmt3 = null;
        String sql1, sql2, sql3 = null;
        String empno, resumefmt = null;
        Clob resumelob = null;
        ResultSet rs1, rs2, rs3 = null;

        Connection con;
        try {
            con = DriverManager.getConnection("jdbc:db2://192.168.62.128:50000/SAMPLE","db2admin","student");
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }


        sql1 = "SELECT POSSTR(RESUME,'Personal') "
                + "FROM EMP_RESUME "+
                "WHERE EMPNO = ? AND RESUME_FORMAT = 'ascii' ";

        try {
            stmt1 = con.prepareStatement (sql1);
            stmt1.setString ( 1, empnum);
            rs1 = stmt1.executeQuery();
            rs1.next();
            startper = rs1.getInt(1);
            while (rs1.next()) {
                startper = rs1.getInt(1);
            } // end while
            sql2 = "SELECT POSSTR(RESUME,'Department') "
                    + "FROM EMP_RESUME "
                    + "WHERE EMPNO = ? AND RESUME_FORMAT = 'ascii' ";
            stmt2 = con.prepareStatement (sql2);
            stmt2.setString ( 1, empnum);
            rs2 = stmt2.executeQuery();
            while (rs2.next()) {
                startdpt = rs2.getInt(1);
            } // end while
            startper1 = startper - 1;
            sql3 = "SELECT EMPNO, RESUME_FORMAT, "
                    + "SUBSTR(RESUME,1,?)|| SUBSTR(RESUME,?) AS RESUME "//merge 1 and 3
                    + "FROM EMP_RESUME "
                    + "WHERE EMPNO = ? AND RESUME_FORMAT = 'ascii' ";
            stmt3 = con.prepareStatement (sql3);
            stmt3.setInt (1, startper1);
            stmt3.setInt (2, startdpt);
            stmt3.setString ( 3, empnum);
            rs3 = stmt3.executeQuery();
            while (rs3.next()) {
                empno = rs3.getString(1);
                resumefmt = rs3.getString(2);
                resumelob = rs3.getClob(3);
                long len = resumelob.length();
                int len1 = (int)len;
                String resumeout = resumelob.getSubString(1, len1);
                System.out.println(empno+ resumefmt+ resumelob+ resumeout);
            } // end while
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


}
