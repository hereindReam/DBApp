package Assignment8;

import javax.swing.*;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
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
import java.util.Vector;

/*
模仿下面截图设计并实现向表TEMPL插入行的操作，GUI界面要求实现单行插入、多行插入、通过子查询插入的功能。
*/
public class LabCursor extends JFrame{

    /**数据库连接参数*/
    final String url = "jdbc:db2://192.168.62.128:50000/SAMPLE";
    final String name = "db2admin";
    final String password = "student";

    /**
     * GUI Component
     * */
//    private JFrame mainFrame;
    private final JPanel mainPanel;
    private JScrollPane scrollPane;
    private JTable table;
    private JTableHeader header;

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
    public LabCursor(){
        super("TEMPL");
        this.setSize(600,400);
        this.setLocation(200,100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //component


        /**
         * 插入按钮
         * */
        JButton bSingle = new JButton("单行插入");
        JButton bMul = new JButton("多行插入");
        JButton bSub = new JButton("子查询插入");

        showTable();

        mainPanel = new JPanel(new GridLayout(3,1));

        mainPanel.add(bSingle);
        mainPanel.add(bMul);
        mainPanel.add(bSub);

        this.setLayout(new BorderLayout());
        add(scrollPane,BorderLayout.CENTER);
        add(mainPanel,BorderLayout.EAST);

        //addListener
        bSingle.addActionListener(e -> {
            singleActionPerformed(e);
            showTable();
        });
        bMul.addActionListener(e -> {
            MulActionPerformed(e);
            showTable();
        });

        bSub.addActionListener(e -> {
            SubActionPerformed(e);
            showTable();
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

            Connection connection = DriverManager.getConnection(url,name,password);
            connection.setAutoCommit(true);
            // 查询数据库
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery("SELECT * FROM TEMPL ");

            // 创建 DefaultTableModel
            DefaultTableModel model = new DefaultTableModel();
            model.setRowCount(0);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnName(i));
            }

            // 将查询结果添加到 DefaultTableModel
            while (resultSet.next()) {
                Vector<Object> row = new Vector<Object>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(resultSet.getObject(i));
                }
                model.addRow(row);
            }

            // 创建 JTable
            table = new JTable(model);
            table.setCellSelectionEnabled(true);

            // 添加 TableModelListener
            model.addTableModelListener(new TableModelListener() {
                public void tableChanged(TableModelEvent e) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();
                    Object newValue = table.getValueAt(row, column);
                    // 更新数据库
                    try {
                        resultSet.absolute(row+1);
                        resultSet.updateObject(column+1,newValue);
                        resultSet.updateRow();
                        connection.commit();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }


                }
            });

            // 显示 JTable
            this.scrollPane = new JScrollPane(table);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // TODO: 2023/12/8  在插入后要更改表的内容

    private void SubActionPerformed(ActionEvent e) {
        /**subQuery insert*/

        JFrame frame = new JFrame("子查询插入");
        frame.setSize(370,250);
        /*frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);*/

        JPanel panel = new JPanel();
        frame.add(panel);
        panel.setLayout(null);
        JLabel sub = new JLabel("请输入子查询内容：");
        sub.setBounds(10,20,165,20);
        panel.add(sub);

        subQueryField = new JTextField();
        subQueryField.setBounds(10,50,165,50);
        panel.add(subQueryField);

        JButton button = new JButton("Insert");
        button.setBounds(10,120,80,25);
        panel.add(button);

        button.addActionListener(e1 -> {
            String sub1 = subQueryField.getText();

            String sql = "insert into TEMPL " + sub1;

            try {
                Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.executeUpdate();

                pstmt.close();
                conn.close();
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        });
        frame.setVisible(true);
    }

    public void MulActionPerformed(ActionEvent e){
        JFrame frame = new JFrame("多行插入");
        frame.setSize(500,450);
        /*frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);*/

        JPanel panel = new JPanel();
        frame.add(panel);
        panel.setLayout(null);

        String[] labels = {"编号", "名字","中间名", "姓氏", "员工公寓","电话号码","雇佣日期","职位","教育水平","性别","出生日期","月薪","奖金","佣金"};
        JLabel[] labelArray = new JLabel[labels.length];

        JTextArea[] textAreas = new JTextArea[labels.length];

        int i=0;
        for (; i < labels.length/2; i++) {
            labelArray[i] = new JLabel(labels[i]);
            labelArray[i].setBounds(10 , 20 + i * 80, 80, 70);
            panel.add(labelArray[i]);

            textAreas[i] = new JTextArea();
            textAreas[i].setBounds(100 , 20 + i * 80, 100, 70);
            panel.add(textAreas[i]);
        }

        for(;i < labels.length; i++){
            labelArray[i] = new JLabel(labels[i]);
            labelArray[i].setBounds(210 , 20 +( i-labels.length/2) * 80, 80, 70);
            panel.add(labelArray[i]);

            textAreas[i] = new JTextArea();
            textAreas[i].setBounds(300 , 20 + ( i-labels.length/2)* 80, 100, 70);
            panel.add(textAreas[i]);
        }
        JButton button = new JButton("Insert");
        button.setBounds(10, 600, 80, 25);
        panel.add(button);

        frame.setVisible(true);
        //添加事件处理
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[][] column = new String[labels.length][];

                for (int j = 0; j < labels.length; j++) {
                    column[j] = textAreas[j].getText().split("\\n");
                }

                String sql = "insert into templ values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                String pattern = "yyyy-MM-dd hh:mm:ss";
                SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

                try {
                    Connection connection = getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);

                    for (int n = 0; n < column[0].length; n++) {
                        for (int k = 0; k < labels.length; k++) {
                            if (n < column[k].length && !column[k][n].isEmpty()) {
                                preparedStatement.setString(k + 1, column[k][n]);
                            } else {
                                preparedStatement.setNull(k + 1, Types.CHAR);
                            }

                        }

                        Timestamp hireTime = null, birthTime = null;
                        BigDecimal salary = null;
                        BigDecimal bonus = null;
                        BigDecimal comm = null;

                        if (n < column[6].length &&!column[6][n].isEmpty()) {
                            Date parsedDate = (Date) dateFormat.parse(column[6][n]);
                            hireTime = new java.sql.Timestamp(parsedDate.getTime());
                        }

                        if (n < column[10].length&&!column[10][n].isEmpty()) {
                            Date parsedDate = (Date) dateFormat.parse(column[10][n]);
                            birthTime = new java.sql.Timestamp(parsedDate.getTime());
                        }

                        if (n < column[11].length&&!column[11][n].isEmpty()) {
                            salary = new BigDecimal(column[11][n]);
                        }

                        if (n < column[12].length&&!column[12][n].isEmpty()) {
                            bonus = new BigDecimal(column[12][n]);
                        }

                        if (n < column[13].length&&!column[13][n].isEmpty()) {
                            comm = new BigDecimal(column[13][n]);
                        }

                        preparedStatement.setTimestamp(7, hireTime);
                        preparedStatement.setTimestamp(11, birthTime);
                        preparedStatement.setBigDecimal(12, salary);
                        preparedStatement.setBigDecimal(13, bonus);
                        preparedStatement.setBigDecimal(14, comm);

                        preparedStatement.addBatch();
                    }
                    preparedStatement.executeBatch();

                    preparedStatement.close();
                    connection.close();
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                } catch (ParseException ex) {
                    throw new RuntimeException(ex);
                }

            }

        });
    }

    private void singleActionPerformed(ActionEvent e){
        JFrame frame = new JFrame("单行插入");
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
                    pstmt.close();
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
            LabCursor cursor = new LabCursor();
            cursor.setVisible(true);
        });
    }
}