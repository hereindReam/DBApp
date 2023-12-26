package Assignment10;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
public class LabBLOB extends JFrame {
    /**
     * 以GUI的形式实现sample数据库中，表emp_photo的picture列的查询和插入。
     * */
    final String user = "db2admin";
    final String password = "student";
    private final JLabel[] label;//label shows pics

    private LabBLOB(){
        super("Picture");
        /*setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);*/
        setSize(450,350);
        setLocation(500,450);
        label = new JLabel[getCount()];
        for (int i = 0; i < label.length; i++) {
            label[i] = new JLabel();
        }

        setLayout(new BorderLayout());

        /*int row = (int) Math.sqrt(getCount());*/

        //replace 2
        JPanel picPanel = new JPanel(new GridLayout(2, 2));
        add(picPanel,BorderLayout.CENTER);
        JPanel actionPanel = new JPanel(new GridLayout(2, 1));
        add(actionPanel,BorderLayout.EAST);

        //query button
        JButton queryButton = new JButton("查询");
        queryButton.addActionListener(e -> queryActionListener());
        actionPanel.add(queryButton);

        // insert button
        JButton insertButton = new JButton("插入");
        insertButton.addActionListener(e -> uploadActionListener());
        actionPanel.add(insertButton);


        for (JLabel jLabel : label) {
            picPanel.add(jLabel);
        }
       /* mainFrame.pack();*/
    }

    private int getCount(){
        int result;
        try{
            Connection connection = DriverManager.getConnection("jdbc:db2://192.168.62.128:50000/SAMPLE",user,password);
            String countSQL = "SELECT COUNT(*) FROM EMP_PHOTO WHERE PHOTO_FORMAT = 'gif'";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(countSQL);

            rs.next();
            result = rs.getInt(1);

            rs.close();
            stmt.close();
            connection.close();
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
        return result;
    }

    private void queryActionListener(){
        try{
            Connection connection = DriverManager.getConnection("jdbc:db2://192.168.62.128:50000/SAMPLE",user,password);
            String picSQL = "SELECT * FROM EMP_PHOTO WHERE PHOTO_FORMAT = 'gif'";

            Statement stmt = connection.createStatement();

            //Query Part
            ResultSet rs = stmt.executeQuery(picSQL);
            int i = 0;
            while(rs.next()){
                byte[] imgBytes = rs.getBytes("PICTURE");
                ImageIcon icon = new ImageIcon(imgBytes);
                Image img = icon.getImage();
                //show in label
                label[i].setIcon(new ImageIcon(img));
                i++;
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    private void uploadActionListener(){
        JFrame insertFrame = new JFrame("Insert");
        insertFrame.setSize(350,300);
        insertFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JPanel insertPanel = new JPanel();
        JButton uploadButton = new JButton("上传图片");
        JTextField noField = new JTextField();
        JTextField formatField = new JTextField();
        JLabel noLabel = new JLabel("编号：");
        JLabel formatLabel = new JLabel("格式：");
        JButton OkButton = new JButton("确定");

        insertPanel.setLayout(null);
        noLabel.setBounds(10,20,60,30);
        noField.setBounds(70,20,115,30);
        formatLabel.setBounds(10,80,60,30);
        formatField.setBounds(70,80,115,30);
        uploadButton.setBounds(10,140,100,30);
        OkButton.setBounds(10,200,100,30);


        JLabel picLabel = new JLabel();
        picLabel.setBounds(200,20,100,100);
        insertPanel.add(picLabel);

        final File[] selectedFile = new File[1];
        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedFile[0] = fileChooser.getSelectedFile();
                ImageIcon icon = new ImageIcon(selectedFile[0].getAbsolutePath());
                Image image = icon.getImage().getScaledInstance(picLabel.getWidth(), picLabel.getHeight(), Image.SCALE_SMOOTH);
                picLabel.setIcon(new ImageIcon(image));
            }
        });

        OkButton.addActionListener(e -> {
            /*JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                insert(selectedFile,noField.getText(),formatField.getText());
            }*/
            insert(selectedFile[0],noField.getText(),formatField.getText());
        });


        insertPanel.add(OkButton);
        insertPanel.add(noLabel);
        insertPanel.add(formatLabel);
        insertPanel.add(noField);
        insertPanel.add(formatField);
        insertPanel.add(uploadButton);
        insertFrame.add(insertPanel);
        insertFrame.setVisible(true);
    }

    private void insert(File file,String no,String format){
        try{
            Connection connection = DriverManager.getConnection("jdbc:db2://192.168.62.128:50000/SAMPLE",user,password);
            FileInputStream fis = new FileInputStream(file);
            String insertSQL = "INSERT INTO EMP_PHOTO VALUES (?,?,?)";
            PreparedStatement pstmt = connection.prepareStatement(insertSQL);

            /*pstmt.setBinaryStream(3, fis, (int) file.length());*/
            pstmt.setBlob(3,fis);
            pstmt.setString(1,no);
            pstmt.setString(2,format);
            pstmt.executeUpdate();

            pstmt.close();
            fis.close();
            connection.close();

        } catch (SQLException | IOException e){
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            LabBLOB test = new LabBLOB();
            test.setVisible(true);
        });
    }
}
