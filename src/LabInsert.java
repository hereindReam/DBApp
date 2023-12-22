import javax.swing.*;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.JTable;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.sql.*;

/*
模仿下面截图设计并实现向表TEMPL插入行的操作，GUI界面要求实现单行插入、多行插入、通过子查询插入的功能。
*/
public class LabInsert extends JFrame{

    /**数据库连接参数*/
    final String url = "jdbc:db2://192.168.62.128:50000/SAMPLE";
    final String name = "db2admin";
    final String password = "student";

    /**
     * GUI Component
     * */
//    private JFrame mainFrame;
    private JPanel mainPanel;
    private JScrollPane scrollPane;
    private JTable table;
    private JTableHeader header;

    /**
     * 插入按钮
     * */
    private JButton bSingle;
    private JButton bMul;
    private JButton bSub;

    /**
     * 单行插入文本框
     */
    private JTextField singleInsertField1;//empno
    private JTextField singleInsertField2;//firstnme
    private JTextField singleInsertField3;//lastname
    private JTextField singleInsertField4;//edlevel

    /**
     * 多行插入
     * */
    private JTextArea mulNo;
    private JTextArea mulFirstname;
    private JTextArea mulLastname;
    private JTextArea mulEdLevel;

    /**
     * 子查询插入
     * */
    private JTextField subQueryField;


    private Connection getConnection() throws ClassNotFoundException, SQLException {
        // Load the DB2 driver
        Class.forName("com.ibm.db2.jcc.DB2Driver");

        // Get a connection to the database
        return DriverManager.getConnection(url, name, password);

    }
     public LabInsert(){
        super("TEMPL Insert GUI");
        this.setSize(600,400);
        this.setLocation(200,100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //component


        this.scrollPane = new JScrollPane(table);
        this.bSingle = new JButton("单行插入");
        this.bMul = new JButton("多行插入");
        this.bSub = new JButton("子查询插入");

        showTable();

        mainPanel = new JPanel(new GridLayout(3,1));

        mainPanel.add(bSingle);
        mainPanel.add(bMul);
        mainPanel.add(bSub);

        this.setLayout(new BorderLayout());

        add(scrollPane,BorderLayout.CENTER);
        add(mainPanel,BorderLayout.EAST);




        //addListener
        this.bSingle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                singleActionPerformed(e);
            }
        });
        this.bMul.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MulActionPerformed(e);
            }
        });

        this.bSub.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SubActionPerformed(e);
            }
        });
    }

    private DefaultTableModel buildTableModel(ResultSet resultSet) throws SQLException {


        ResultSetMetaData metaData = resultSet.getMetaData();

        // 获取列数
        int columnCount = metaData.getColumnCount();

        // 创建表格模型
        DefaultTableModel tableModel = new DefaultTableModel();

        // 添加列名
        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
            tableModel.addColumn(metaData.getColumnLabel(columnIndex));
        }

        // 添加数据行
        while (resultSet.next()) {
            Object[] rowData = new Object[columnCount];
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                rowData[columnIndex - 1] = resultSet.getObject(columnIndex);
            }
            tableModel.addRow(rowData);
        }

        return tableModel;
    }

    private void showTable(){
        try {
            Connection connection = getConnection();

/*//            获取结果集条数，作为data的索引数
            // TODO: 2023/12/8 can this be a function or I just put it here?
            int rowCount = 0;
            String query = "SELECT COUNT(*) FROM templ";
            Statement stm = connection.createStatement();
            ResultSet resultSet = stm.executeQuery(query);
            if (resultSet.next()) {
                rowCount = resultSet.getInt(1);
            }*/

            String sql = "select * from templ";
            Statement stm = connection.createStatement();
            ResultSet rs = stm.executeQuery(sql);

           /* ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            int count = 0;*/

/*
//            数据
            Object[][] data = new Object[rowCount][columnCount];

            while(rs.next()){
                for (int i = 0; i < columnCount; i++) {
                    data[count][i] = rs.getObject(i);
                }
            }
*/

            DefaultTableModel model = buildTableModel(rs);
            table = new JTable(model);
            scrollPane = new JScrollPane(table);

            getContentPane().add(scrollPane, BorderLayout.CENTER);

            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setVisible(true);

            connection.close();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    // TODO: 2023/12/8  在插入后要更改表的内容

    private void SubActionPerformed(ActionEvent e) {
        /**subQuery insert*/

        JFrame frame = new JFrame("Database SubQuery Insert");
        frame.setSize(370,200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);
        panel.setLayout(null);

        subQueryField = new JTextField();
        subQueryField.setBounds(10,20,165,50);
        panel.add(subQueryField);

        JButton button = new JButton("Insert");
        button.setBounds(10,80,80,25);
        panel.add(button);

        subQueryField = new JTextField("子查询",20);
        panel.add(subQueryField);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sub = subQueryField.getText();

                String sql = "insert into templ" + sub;

                try {
                    Connection conn = getConnection();
                    Statement statement = conn.createStatement();
                    statement.executeUpdate(sql);

                    conn.close();
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
        frame.setVisible(true);
    }

    public void MulActionPerformed(ActionEvent e){
        JFrame frame = new JFrame("Database Multi Insert");
        frame.setSize(370,200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);
        panel.setLayout(null);

        mulNo = new JTextArea();
        mulNo.setBounds(10,20,100,200);
        panel.add(mulNo);

        mulFirstname = new JTextArea();
        mulFirstname.setBounds(120,20,100,200);
        panel.add(mulFirstname);

        mulLastname = new JTextArea();
        mulLastname.setBounds(230,20,100,200);
        panel.add(mulLastname);

        mulEdLevel = new JTextArea();
        mulEdLevel.setBounds(340,20,100,200);
        panel.add(mulEdLevel);

        JButton button = new JButton("Insert");
        button.setBounds(10, 250, 80, 25);
        panel.add(button);

        //添加事件处理
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] empno = mulNo.getText().split("\\n");
                String[] firstname = mulFirstname.getText().split("\\n");
                String[] lastname = mulLastname.getText().split("\\n");
                String[] edlevel = mulEdLevel.getText().split("\\n");

                String sql = "insert into templ(empno,firstnme,lastname,edlevel) values (?,?,?,?)";

                try {
                    Connection connection = getConnection();

                    PreparedStatement preparedStatement = connection.prepareStatement(sql);

                    for (int i = 0; i < empno.length; i++) {
                        int no = Integer.parseInt(empno[i]);
                        int level = Integer.parseInt(edlevel[i]);

                        preparedStatement.setInt(1,no);
                        preparedStatement.setString(2,firstname[i]);
                        preparedStatement.setString(3,lastname[i]);
                        preparedStatement.setInt(4,level);

                        preparedStatement.addBatch();
                    }

                    preparedStatement.executeBatch();

                    connection.close();
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        frame.setVisible(true);
    }

    private void singleActionPerformed(ActionEvent e){
        JFrame frame = new JFrame("Database Insert Example");
        frame.setSize(350, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);
        panel.setLayout(null);

        singleInsertField1 = new JTextField("编号",20);
        singleInsertField1.setBounds(10,20,165,25);
        panel.add(singleInsertField1);

        singleInsertField2 = new JTextField("名字",20);
        singleInsertField2.setBounds(10,50,165,25);
        panel.add(singleInsertField2);

        singleInsertField3 = new JTextField("姓氏",20);
        singleInsertField3.setBounds(10,80,165,25);
        panel.add(singleInsertField3);

        singleInsertField4 = new JTextField("教育水平",20);
        singleInsertField4.setBounds(10,110,165,25);
        panel.add(singleInsertField4);

        JButton button = new JButton("Insert");
        button.setBounds(10, 140, 80, 25);
        panel.add(button);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String empno = singleInsertField1.getText();
                String first = singleInsertField2.getText();
                String last = singleInsertField3.getText();
                String level = singleInsertField4.getText();

                int no = Integer.parseInt(empno);
                int edlevel = Integer.parseInt(level);
                try {
                    Connection conn = getConnection();
                    String sql = "insert into templ(empno,firstnme,lastname,edlevel) values(?,?,?,?)";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, no);
                    pstmt.setString(2, first);
                    pstmt.setString(3, last);
                    pstmt.setInt(4, edlevel);
                    pstmt.executeUpdate();
                    conn.close();
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        frame.setVisible(true);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            LabInsert labInsert = new LabInsert();
            labInsert.setVisible(true);
        });
    }
}

