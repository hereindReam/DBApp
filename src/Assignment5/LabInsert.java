package Assignment5;

import com.ibm.db2.jcc.DB2SimpleDataSource;

import javax.swing.*;

import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.JTable;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
    private JTextField[] singleInsertField;
    private JTextField singleInsertField1;//empno
    private JTextField singleInsertField2;//firstnme
    private JTextField singleInsertField3;//lastname
    private JTextField singleInsertField4;//edlevel

    /**
     * 多行插入
     * */
    private JTextArea[] mulInsertField;
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
        /*mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        bSingle.setAlignmentX(Component.CENTER_ALIGNMENT);
        bMul.setAlignmentX(Component.CENTER_ALIGNMENT);
        bSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        bSingle.setBorder(new EmptyBorder(10, 0, 10, 0)); // 10 pixels gap above and below the button
        bMul.setBorder(new EmptyBorder(10, 0, 10, 0)); // 10 pixels gap above and below the button
        bSub.setBorder(new EmptyBorder(10, 0, 10, 0)); // 10 pixels gap above and below the button
*/
        mainPanel.add(bSingle);
        mainPanel.add(bMul);
        mainPanel.add(bSub);

        this.setLayout(new BorderLayout());
        add(scrollPane,BorderLayout.CENTER);
        add(mainPanel,BorderLayout.EAST);

        /*// Set button positions
        bSingle.setBounds(20, 20, 100, 30);
        bMul.setBounds(20, 70, 100, 30);
        bSub.setBounds(20, 120, 100, 30);*/

        //addListener
        this.bSingle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                singleActionPerformed(e);
                showTable();
            }
        });
        this.bMul.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MulActionPerformed(e);
                showTable();
            }
        });

        this.bSub.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SubActionPerformed(e);
                showTable();
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

            String sql = "select * from templ";
            Statement stm = connection.createStatement();
            ResultSet rs = stm.executeQuery(sql);

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
        frame.setSize(350, 500);
        /*frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);*/

        JPanel panel = new JPanel();
        frame.add(panel);
        panel.setLayout(null);

        String[] labels = {"编号", "名字","中间名", "姓氏", "员工公寓","电话号码","雇佣日期","职位","教育水平","性别","出生日期","月薪","奖金","佣金"};
        JTextField[] fields = new JTextField[labels.length];

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBounds(0, 0, 350, 200);
        frame.add(scrollPane);

        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setBounds(10, 20 + i * 30, 80, 25);
            panel.add(label);

            fields[i] = new JTextField(20);
            fields[i].setBounds(100, 20 + i * 30, 165, 25);
            panel.add(fields[i]);
        }

        JButton button = new JButton("Insert");
        button.setBounds(10, 20 + labels.length * 30, 80, 25);
        panel.add(button);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //"编号", "名字","中间名", "姓氏", "员工公寓","电话号码",
                // "雇佣日期","职位","教育水平","性别","出生日期","月薪","奖金","佣金"
                String[] column = new String[labels.length];
                for (int i = 0; i < labels.length; i++) {
                    column[i] = fields[i].getText();
                }

                Timestamp hireTime = null,birthTime = null;

                BigDecimal salary = null;
                if (!column[11].isEmpty()) {
                    salary = new BigDecimal(column[11]);
                }

                BigDecimal bonus = null;
                if (!column[12].isEmpty()) {
                    bonus = new BigDecimal(column[12]);
                }

                BigDecimal comm = null;
                if (!column[13].isEmpty()) {
                    comm = new BigDecimal(column[13]);
                }
                //TODO how to avoid hard-code?
                String pattern = "yyyy-MM-dd hh:mm:ss";
                SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                try {
                    //special process
                    if (!column[6].isEmpty()) {
                        Date parsedDate = (Date) dateFormat.parse(column[6]);
                        hireTime = new java.sql.Timestamp(parsedDate.getTime());
                    }
                    if (!column[10].isEmpty()) {
                        Date parsedDate = (Date) dateFormat.parse(column[10]);
                        birthTime = new java.sql.Timestamp(parsedDate.getTime());
                    }
                } catch (ParseException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    Connection conn = getConnection();
                    String sql = "insert into templ values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    for (int i = 0; i < labels.length; i++) {
                        if (!column[i].isEmpty()) {
                            pstmt.setString(i+1,column[i]);
                        }else{
                            pstmt.setNull(i+1,Types.CHAR);
                        }

                    }

                    pstmt.setTimestamp(7,hireTime);
                    pstmt.setTimestamp(11,birthTime);
                    pstmt.setBigDecimal(12,salary);
                    pstmt.setBigDecimal(13,bonus);
                    pstmt.setBigDecimal(14,comm);

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

